![Alt text](image.png)
# ScanDoc Android SDK

ScanDoc is a modern document scanning solution offering comprehensive global coverage across 100+ countries, accommodating over 300 document types. Thanks to its flexibility, ScanDoc streamlines onboarding new documents in under two hours. Furthermore, ScanDoc’s user-friendly solution seamlessly integrates across all devices, making document scanning effortless and accurate for businesses worldwide.

This repository contains a minimal example of integrating our Flutter module into a native Android app.

##  Quick Start

### Integrate with Android Studio
The Android Studio IDE can help integrate our Flutter module.

#### Add the dependencies in your app

Android Studio supports add-to-app flows on Android Studio 2022.2 or later with the [Flutter plugin for IntelliJ](https://plugins.jetbrains.com/plugin/9212-flutter). To build your app, the Android Studio plugin configures your Android project to add your Flutter module as a dependency.

To depend on our AAR, the host app must be able to find these files.

To do that, edit settings.gradle in your host app so that it includes the local repository and the dependency (check our sample app for a working example):

```gradle
String storageUrl = System.env.FLUTTER_STORAGE_BASE_URL ?: "https://storage.googleapis.com"
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url 'http://maven.scandoc.ai/repository/maven-releases/'
            allowInsecureProtocol true
        }
        maven {
            url "$storageUrl/download.flutter.io"
        }
    }
}
```

#### Add the ScanDoc screen to your app

Add the following XML to your AndroidManifest.xml file under your application tag:
```xml
<activity
  android:name="io.flutter.embedding.android.FlutterActivity"
  android:theme="@style/LaunchTheme"
  android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
  android:hardwareAccelerated="true"
  android:windowSoftInputMode="adjustResize"
  />
```

Add the following permissions:
```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-feature android:name="android.hardware.camera" android:required="true"/>
<uses-permission android:name="android.permission.INTERNET" />
```

Add a scan button that launches the ScanDoc activity:
```kotlin
Button(onClick = {
    val flutterEngine = FlutterEngine(this)

    // use your API key and Client name
    val apiKey = "<api_key>";
    val clientName = "<client_name>"
    flutterEngine.navigationChannel.setInitialRoute("/scan?APIKey=$apiKey&ClientName=$clientName")

    flutterEngine
        .dartExecutor
        .executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

    FlutterEngineCache.getInstance().put("5", flutterEngine)

    val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "scandoc.ai/result")
    channel.setMethodCallHandler { call, result ->
        when (call.method) {
            // this is called when the scan has successfully completed
            "scanResult" -> {
                var scanResult = call.argument<Map<String, Any>>("ScanDocData")
                Log.d("TEST", "Scan successful, printing extracted data")
                Log.d("TEST", scanResult.toString())
                result.success(true)
            }
        }
    }
    val intent = FlutterActivity
        .withCachedEngine("5")
        .build(this)
    startActivity(intent)
}) {
    Text(text = "Scan")
}
```

Finished! You can now scan more than 1000+ Document Types!

## Minimum requirements
### Android Version
ScanDoc requires Android API level 21 or newer.

### Camera
Camera video preview resolution also matters. In order to perform successful scans, camera preview resolution must be at least 720p. Note that camera preview resolution is not the same as video recording resolution.

### Processor architecture
ScanDoc is distributed with ARMv7 and ARM64 native library binaries.

## Contact
Feel free to contact us on our [Website](https://scandoc.ai/resources/contact/)

