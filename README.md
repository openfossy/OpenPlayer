# Open Player (Open Player)

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-B125EA?logo=kotlin&logoColor=white)
[![Downloads](https://img.shields.io/github/downloads/OpenFossy/OpenPlayer/total?logo=github)](https://github.com/OpenFossy/OpenPlayer/releases)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Open Player is a high-performance, native Android video player built with a focus on absolute playback smoothness, clean aesthetics, and extensive user customization.

---

Originally built as an ExoPlayer-based application, Open Player has been completely re-engineered under the hood to utilize the **mpv-android** engine. This architectural shift merges our minimalist Material Design UI with the raw decoding power of MPV, delivering unmatched format compatibility, hardware acceleration, and seamless video handling.

> ## Why MPV?

The transition from ExoPlayer to `is.xyz.mpv` allows Open Player to offer a truly desktop-class media experience on mobile. It brings native hardware decoding (`mediacodec`), superior subtitle rendering, and real-time color enhancement capabilities without sacrificing battery life or UI responsiveness.

> ## Key Features

> > ### Advanced Playback Engine

- **Dynamic Decoder Selection:** Instantly switch between Auto, Hardware (HW/HW+), and Software (SW) decoding on the fly.
- **Smart Audio Boost:** Amplify low-volume videos safely up to 200%.
- **Rich Subtitle Support:** Cycle tracks, adjust synchronization delays, customize fonts, and tweak scaling/offsets directly from the player.
- **Smart Enhance Mode:** Real-time hardware-level adjustments for Video Brightness, Contrast, Saturation, Gamma, and Hue.

> > ### Clean, Native UI

- **Material Design 3:** fully integrated with Android's Dynamic Color palette.
- **AMOLED & Dark Themes:** True black modes for battery saving and comfortable nighttime viewing.
- **Unobtrusive Overlays:** Transparent navigation bars, auto-hiding controls, and configurable quick-action buttons.
- **Smooth Navigation:** Jetpack Compose-driven UI for a fluid, jank-free browsing experience.

> > ### Deep Customization & Gestures

- **Multi-finger Gestures:** Configure 2-finger and 3-finger taps for rapid actions (Play/Pause, Fast Play, etc.).
- **Screen Edge Controls:** Slide to adjust brightness and volume, with customizable sensitivity.
- **Layout Editor:** Customize top and bottom control panels to fit your exact workflow.
- **Multiple Finger Gestures:** Configurable seek durations and tap-to-speed parameters.

> ## Building the Project

> > ### Prerequisites

- Android Studio (Latest Stable or Ladybug)
- JDK 17+
- Android SDK API 34+

> > ### Clone & Build

```bash
git clone https://github.com/OpenFossy/OpenPlayer.git
cd OpenPlayer
./gradlew assembleRelease
```

## Acknowledgements

Special thanks to [**Ritesh Pandit (@Riteshp2001)**](https://github.com/Riteshp2001) and the [**mpvRx**](https://github.com/Riteshp2001/mpvRx) project for the inspiration and foundational work on:

- **yt-dlp Online Streaming Integration** - enabling seamless online video playback via yt-dlp within an MPV-based Android player.
- **MPV Config Editor** - the in-app mpv.conf editor concept that allows users to tweak the MPV engine directly from the UI.
- **Thumbnail Generation Integration** - the approach to generating and displaying video thumbnails within an MPV-backed player.

---

## Origin

OpenPlayer is based on the original [Nosved Player](https://github.com/DevSon1024/Nosved-Player) project by **[Devendra Sonawane](https://github.com/DevSon1024/)**.

The project has been adapted and is maintained as an independent open-source project under the **OpenFossy** organization.
