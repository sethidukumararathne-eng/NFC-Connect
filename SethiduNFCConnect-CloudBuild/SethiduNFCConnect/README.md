# Sethidu NFC Connect

Android Studio source project for an HONOR 90 Lite HCE demonstration card.

## What it does
- Registers the NFC Forum Type 4 Tag NDEF application AID `D2760000850101`.
- Responds to SELECT and READ BINARY APDUs with a Type 4 Tag Capability Container and an NDEF URI record for `https://www.youtube.com/@ViolinSethidu`.
- Shows NFC/HCE status and a basic count of NDEF-file reads.

## Important limitation
This is an HCE card emulator, not a physical NFC tag. The receiving phone must actively recognize/read the emulated Type 4 Tag. Android does not guarantee that every receiving phone (or iPhone) will show a URL notification from an HCE card. This app cannot remotely create a notification on another phone. A physical NFC Forum NDEF tag is the broadly compatible way to trigger the normal tap-to-open URL experience.

## Build
1. Install Android Studio (current stable) and open this folder.
2. Allow Gradle sync; use JDK 17 if prompted.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Install the APK on the HONOR 90 Lite, enable NFC, and keep the phone unlocked for the demo.

The source is provided; no APK is included because it has not been compiled or tested on the HONOR 90 Lite in this environment.
