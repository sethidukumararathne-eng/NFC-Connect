package com.sethidu.nfcconnect;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import java.nio.charset.StandardCharsets;

/** Emulates an NFC Forum Type 4 Tag NDEF application (AID D2760000850101). */
public class SethiduHostApduService extends HostApduService {
    private static final byte[] SELECT_NDEF_APP = hex("00A4040007D276000085010100");
    private static final byte[] SELECT_CC = hex("00A4000C02E103");
    private static final byte[] SELECT_NDEF = hex("00A4000C02E104");
    private static final byte[] OK = hex("9000");
    private static final byte[] NOT_FOUND = hex("6A82");
    private static final byte[] UNSUPPORTED = hex("6D00");
    private byte[] selectedFile = null;
    private byte[] ccFile;
    private byte[] ndefFile;

    @Override public void onCreate() {
        super.onCreate();
        // Capability Container for a Type 4 Tag, with a single NDEF file E104.
        ccFile = hex("000F20003B00340406E10400FF00FF");
        ndefFile = makeNdefFile("https://www.youtube.com/@ViolinSethidu");
    }

    @Override public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (apdu == null || apdu.length < 4) return UNSUPPORTED;
        if (equalsCommand(apdu, SELECT_NDEF_APP)) { selectedFile = null; return OK; }
        if (equalsCommand(apdu, SELECT_CC)) { selectedFile = ccFile; return OK; }
        if (equalsCommand(apdu, SELECT_NDEF)) { selectedFile = ndefFile; return OK; }
        // READ BINARY: 00 B0 P1 P2 Le
        if ((apdu[0] & 0xFF) == 0x00 && (apdu[1] & 0xFF) == 0xB0 && apdu.length >= 5) {
            if (selectedFile == null) return NOT_FOUND;
            int offset = ((apdu[2] & 0xFF) << 8) | (apdu[3] & 0xFF);
            int le = apdu[4] & 0xFF; if (le == 0) le = 256;
            if (offset > selectedFile.length) return hex("6B00");
            int length = Math.min(le, selectedFile.length - offset);
            byte[] response = new byte[length + 2];
            System.arraycopy(selectedFile, offset, response, 0, length);
            response[length] = (byte)0x90; response[length+1] = 0x00;
            if (selectedFile == ndefFile && offset == 0) getSharedPreferences("card", MODE_PRIVATE).edit().putInt("reads", getSharedPreferences("card", MODE_PRIVATE).getInt("reads",0)+1).apply();
            return response;
        }
        return UNSUPPORTED;
    }

    @Override public void onDeactivated(int reason) { selectedFile = null; }

    private static byte[] makeNdefFile(String url) {
        byte[] full = url.getBytes(StandardCharsets.UTF_8);
        byte[] prefix = "https://www.".getBytes(StandardCharsets.UTF_8);
        int start = url.startsWith("https://www.") ? prefix.length : 0;
        byte[] uri = new byte[1 + full.length - start];
        uri[0] = (byte)(start == 0 ? 0x00 : 0x04);
        System.arraycopy(full, start, uri, 1, full.length-start);
        // NDEF URI record: D1 01 payloadLength 'U' uriPayload
        byte[] record = new byte[4 + uri.length];
        record[0]=(byte)0xD1; record[1]=0x01; record[2]=(byte)uri.length; record[3]=0x55;
        System.arraycopy(uri,0,record,4,uri.length);
        byte[] file = new byte[2+record.length]; file[0]=(byte)((record.length >> 8)&0xFF); file[1]=(byte)(record.length&0xFF);
        System.arraycopy(record,0,file,2,record.length); return file;
    }
    private static boolean equalsCommand(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        for (int i=0;i<a.length;i++) if (a[i]!=b[i]) return false;
        return true;
    }
    private static byte[] hex(String s) {
        byte[] out = new byte[s.length()/2];
        for (int i=0;i<out.length;i++) out[i]=(byte)Integer.parseInt(s.substring(i*2,i*2+2),16);
        return out;
    }
}
