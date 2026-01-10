# Use Ubuntu 22.04 as the base image (amd64 for better Android SDK tool compatibility)
FROM --platform=linux/amd64 ubuntu:22.04

# Prevent interactive prompts during installation
ENV DEBIAN_FRONTEND=noninteractive

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wget \
    unzip \
    git \
    cmake \
    ninja-build \
    build-essential \
    libncurses5 \
    libbz2-1.0 \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV ANDROID_HOME="/usr/local/android-sdk"
ENV ANDROID_SDK_ROOT="/usr/local/android-sdk"
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"
ENV JAVA_OPTS="-Xmx2048m"

# Download and install Android Command Line Tools
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip \
    && unzip /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

# Accept licenses
RUN yes | sdkmanager --licenses

# Install SDK platforms, build tools, and NDK (Split for better caching and debugging)
RUN sdkmanager "platforms;android-34"
RUN sdkmanager "build-tools;34.0.0"
RUN sdkmanager "ndk;25.2.9519653"
RUN sdkmanager "platform-tools"

# Set project directory
WORKDIR /project

# Disable Gradle daemon for container build stability
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.workers.max=2 -Xmx2048m"

# Copy project files
COPY . .

# Grant execute permission for gradlew
RUN chmod +x gradlew

# Default command to build the app
CMD ["./gradlew", "assembleDebug"]
