# ⚡ Cyber Clock HUD Widget

[![Android 9+](https://img.shields.io/badge/Android-9%2B%20%7C%2014%20%7C%2015%20%7C%2016-00E5FF?style=for-the-badge&logo=android)](https://github.com/amintum)
[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0%20%2B%20Attribution-blue?style=for-the-badge)](LICENSE)

A cyberpunk-inspired, hardware-synced **Live Clock & System Telemetry Home Screen Widget** for Android. Displays live ticking seconds, full calendar date, and real-time **Battery % + Charging Indicator**, **RAM %**, and **Storage %** telemetry with sleek **3.8dp Cyber Cyan** progress bars and a smoked obsidian glass card.

---

## 🎨 Visual Layout & Architecture

```text
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│                         07:55:53 PM                         │
│                   Friday • 28 August 2026                   │
│                                                             │
│   ⚡ BATTERY 100%          RAM 58%             STORAGE 14%  │
│   ▓▓▓▓▓▓▓▓▓▓▓             ▓▓▓▓▓▓░             ▓▓░░░░░      │  ← 3.8dp Cyber Cyan Bars
└─────────────────────────────────────────────────────────────┘
```

---

## ⚡ Features
* ⏱️ **0ms Latency Hardware Clock**: Uses Android's host-rendered `TextClock` RemoteViews for live, battery-efficient seconds ticking without background battery drain.
* 🌌 **Translucent Obsidian Glass Card**: Light black translucent backdrop (`#9908090C`) with a crisp `1.2dp` Cyber Cyan glowing border and rounded corners.
* 📊 **3-Column Hardware Telemetry**:
  1. 🔋 **Battery % + Charging State**: 100% accurate native hardware broadcast tracking with Cyber Cyan progress bar.
  2. 🧠 **RAM %**: Real-time `ActivityManager.MemoryInfo` kernel RAM usage tracking.
  3. 💾 **Storage %**: Real-time internal partition storage tracking.
* 📏 **Refined 3.8dp Progress Bars**: Sleek Cyber Cyan bars with smooth rounded caps (`2.0dp` radius).
* 📐 **Tight 1-Row Ribbon Profile**: Sized perfectly for 4×1 and 5×1 launcher grid rows without wasted touch space.

---

## 📥 Installation & Usage

### 🔹 Option 1: Standalone User Install (Without ROM Modification)
You can install and use this widget on any Android phone (Android 9 to 16):

1. **Install the APK**:
   ```bash
   adb install releases/CyberClockHudWidget.apk
   # OR download and tap to install via your file manager
   ```
2. **Add Widget to Home Screen**:
   * Long-press on any empty area of your Home Screen.
   * Tap **Widgets** → scroll to **Cyber Clock Hud widget**.
   * Drag it to your home screen and resize it horizontally to full width.

---

### 🔹 Option 2: Full ROM / GSI Integration (For ROM Builders)
To integrate the widget as a standalone system app that connects with `Launcher3` and appears **pre-placed on the home screen by default** on first boot / factory reset:

1. **Place APK into Privileged Partition**:
   ```text
   /system/priv-app/CyberClockHudWidget/CyberClockHudWidget.apk
   # OR
   /system/system_ext/priv-app/CyberClockHudWidget/CyberClockHudWidget.apk
   ```
2. **Place Permissions Whitelist**:
   Copy `permissions/privapp-permissions-cyberhud.xml` to:
   ```text
   /system/etc/permissions/privapp-permissions-cyberhud.xml
   ```
3. **Configure Launcher3 Default Workspace**:
   In Launcher3's `res/xml/default_workspace_*.xml` grid profiles, add:
   ```xml
   <appwidget
       launcher:packageName="com.amintum.clock.cyberhudwidget"
       launcher:className="com.amintum.clock.cyberhudwidget.CyberHUDWidgetProvider"
       launcher:screen="0"
       launcher:x="0"
       launcher:y="0"
       launcher:spanX="5"
       launcher:spanY="1" />
   ```

---

## 🛠️ Building from Source
This repository contains complete Java source code and resources. Build using the included script:
```bash
python build_apk.py
# OR double-click build.bat on Windows
```

---

## 📜 License & Mandatory Credit
This project is licensed under **CC BY-NC-SA 4.0 with Mandatory Attribution**.
* **Credit**: If you use, fork, or integrate this app/code into any ROM or project, you **MUST** credit **Amintum / BestGSI** prominently.
* Commercial use requires explicit permission.

Developed with ❤️ by **[Amintum](https://github.com/amintum)** for **BestGSI**.
