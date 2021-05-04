# Cobrowse.io - Android Standalone App

Cobrowse.io is 100% free and easy to try out in your own apps. Please see full documentation at [https://docs.cobrowse.io](https://docs.cobrowse.io).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

## Building

This is a demo application for the [Cobrowse.io](https://cobrowse.io/) screen sharing service. See the [Cobrowse.io SDK](https://github.com/cobrowseio/cobrowse-sdk-android-binary) repository for more information.

You can build the standalone app with Android Studio. When running, you can specify the license code obtained from [Cobrowse.io](https://cobrowse.io/) in the app settings.

## Add your license key

Please register an account and generate your free License Key at <https://cobrowse.io/dashboard/settings>.

This will associate sessions from your mobile app with your Cobrowse.io account.

## Firebase Cloud Messaging

The standalone app includes preconfigured Firebase push notifications (FCM). To enable it, add your `google-services.json` to the [standalone](/standalone) module.

Refer to the [Firebase docs](https://firebase.google.com/docs/android/setup) to see how to create a new Firebase project and generate your own `google-services.json`.

## Try it out

Once you have the app running in the Android emulator or on a physical device, navigate to <https://cobrowse.io/dashboard> to see your device listed. You can click the "Connect" button to initiate a Cobrowse session!

## Questions?
Any questions at all? Please email us directly at [hello@cobrowse.io](mailto:hello@cobrowse.io).

## Requirements

* API version 19 (4.4 KitKat) or later
