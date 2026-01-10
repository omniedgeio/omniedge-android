# OmniEdge Android App

**Bringing intranet to the internet with Zero-Config Mesh VPNs.**

Join your devices to a virtual network instantly, securely, and seamlessly.

[Website](https://connect.omniedge.io) • [Download](https://github.com/omniedgeio/omniedge-android/releases) • [Documentation](https://omniedge.io/docs)

![OmniEdge Android](https://user-images.githubusercontent.com/93888/171181733-a413b5f3-7daf-4fea-bd9e-ca9c5a6f9e03.png)

## Features

-   **Zero Config:** Connect instantly without complex setup.
-   **Secure:** End-to-end encryption for all traffic.
-   **Browser Login:** Seamless authentication via your web browser.
-   **Cross-Platform:** Works with OmniEdge on macOS, Windows, Linux, and iOS.

## Installation

Download the latest APK from the [Releases Page](https://github.com/omniedgeio/omniedge-android/releases).

## Development

### Prerequisites

-   **JDK:** Java 17 is required.
-   **Android SDK:** Android API 34 (UpsideDownCake).
-   **Docker:** Recommended for consistent builds.

### Building with Docker (Recommended)

To build the APK without setting up a local Android environment:

1.  Clone the repository:
    ```bash
    git clone https://github.com/omniedgeio/omniedge-android.git
    cd omniedge-android
    ```

2.  Build the Docker image:
    ```bash
    docker build -t omniedge-android-build .
    ```

3.  Run the container and extract the APK:
    ```bash
    docker run --name omniedge-build-container omniedge-android-build
    docker cp omniedge-build-container:/project/app/build/outputs/apk/debug/app-debug.apk .
    docker rm omniedge-build-container
    ```

### Building Locally

1.  Open the project in **Android Studio** (Koala or newer recommended).
2.  Sync Gradle (ensure JDK 17 is selected in Gradle settings).
3.  Run `./gradlew assembleDebug` or build directly from the IDE.

## Contributing

We welcome contributions! Please feel free to submit a Pull Request.

## License

This project is open-source. See the [LICENSE](LICENSE) file for details.

## Credits

OmniEdge Android is built upon [Hin2n](https://github.com/switch-iot/hin2n). Special thanks to [switchwang](https://github.com/switch-st) and [zhangbz](https://github.com/zhangbz).
