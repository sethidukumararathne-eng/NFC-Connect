CLOUD BUILD (NO ANDROID STUDIO INSTALL REQUIRED)

1. In a browser, sign in to GitHub and create a new empty repository.
2. Upload the contents of this project folder to the repository (not the outer folder itself).
3. Open the repository's Actions tab.
4. Select "Build Sethidu NFC Connect APK" and click "Run workflow" (or wait for the push build).
5. When the run finishes successfully, open it and download the artifact named
   SethiduNFCConnect-debug-apk. It contains app-debug.apk.

This workflow compiles the debug APK on GitHub-hosted runners. A successful cloud
build verifies compilation, not real NFC behavior. Install and test on the HONOR
90 Lite and a second NFC phone before relying on it.

No GitHub account/repository is connected from this chat, so this workflow has
not been run and no APK has been produced here.
