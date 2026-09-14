# Cobrowse Full Device Remote Control Demo

A minimal Kotlin + XML-layout app showcasing the [full-device remote control](https://docs.cobrowse.io/sdk-features/full-device-capabilities/full-device-remote-control#android)
feature of the Cobrowse SDK. It displays live states for:

- the Cobrowse session (none, pending, authorizing, active in-app, active full device, ended),
- remote control (off, requested, rejected, active in-app, active full device),
- the Cobrowse accessibility service (disabled in the app manifest, enabled in the
  app but disabled in system settings, enabled and running).

## Running

```sh
./gradlew :full-device-demo:installDebug
```

The app starts Cobrowse with the `trial` license. Tap **Create session**, enter the
displayed code at <https://cobrowse.io/dashboard>, then upgrade the session to full
device from the agent side. Use **Open accessibility settings** to enable the
Cobrowse accessibility service, which is required for full-device remote control.
