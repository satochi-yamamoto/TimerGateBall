# Simple Dockerfile for building the Android project
FROM ubuntu:22.04 AS builder

ENV DEBIAN_FRONTEND=noninteractive

# Install required packages
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk wget unzip git && \
    rm -rf /var/lib/apt/lists/*

# Setup Android SDK
ENV ANDROID_SDK_ROOT=/opt/android-sdk
RUN mkdir -p $ANDROID_SDK_ROOT/cmdline-tools && \
    cd /tmp && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip && \
    unzip cmdline-tools.zip && \
    rm cmdline-tools.zip && \
    mv cmdline-tools $ANDROID_SDK_ROOT/cmdline-tools/latest && \
    yes | $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platforms;android-34" "build-tools;34.0.0" "platform-tools"

ENV PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/build-tools/34.0.0"

WORKDIR /workspace
COPY . /workspace

# Configure the Android SDK location for Gradle
RUN echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties \
    && chmod +x gradlew \
    && ./gradlew assembleDebug --no-daemon

FROM scratch AS export
COPY --from=builder /workspace/app/build/outputs/apk/debug/app-debug.apk /app-debug.apk
