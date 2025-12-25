#!/bin/bash

# Function to check if a command is installed
check_command() {
    if ! command -v "$1" &> /dev/null; then
        echo "Error: $1 is not installed." >&2
        exit 1
    fi
}

check_command unzip
check_command zip

# Set the mappings version
MAPPINGS_VERSION="1.0.3"

# Navigate to the directory of the script
cd "$(dirname "$0")"

# Remove directories if they exist
rm -rf build_BTW/BTW_dev
rm -rf build_BTW/tmp/BTW_dev

# Run the gradlew script to download assets, this also does the Java and Java version checks
./gradlew --no-daemon downloadAssets
echo "Please wait..."

# Create the required directories
mkdir -p build_BTW/BTW_dev
mkdir -p custom_mappings
mkdir -p build_BTW/tmp/BTW_dev

# Run the gradlew script to download BTW and copy the BTW jar
./gradlew --no-daemon downloadAssets copyBtwJar
echo "Please wait..."

# Extract the mappings jar using unzip
unzip -o build_BTW/tmp/BTW_dev/BTW-CE-mappings.jar -d custom_mappings

# Delete the existing intermediary jar
rm -f "$HOME/.gradle/caches/fabric-loom/1.6.4/minecraft-merged-intermediary.jar"

# Run the Tiny Remapper
java -jar libs/tiny-remapper-0.8.6+local-fat.jar "$HOME/.gradle/caches/fabric-loom/1.6.4/minecraft-merged.jar" \
    "$HOME/.gradle/caches/fabric-loom/1.6.4/minecraft-merged-intermediary.jar" \
    "$HOME/.gradle/caches/fabric-loom/1.6.4/intermediary-v2.tiny" official intermediary > /dev/null

# Run Tiny Remapper on the extracted file
java -jar libs/tiny-remapper-0.8.6+local-fat.jar "build_BTW/tmp/BTW_dev/BTW-CE-Intermediary.jar" \
    "build_BTW/BTW_dev/BTW-CE-Intermediary.jar" custom_mappings/mappings/mappings.tiny intermediary named \
    "$HOME/.gradle/caches/fabric-loom/1.6.4/minecraft-merged-intermediary.jar" > /dev/null

# Extract another Minecraft jar using unzip
unzip -o "$HOME/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged/1.6.4-btw.community.mappings.1_6_4.$MAPPINGS_VERSION-v2/minecraft-merged-1.6.4-btw.community.mappings.1_6_4.$MAPPINGS_VERSION-v2.jar" -d build_BTW/BTW_dev

# Extract the BTW-CE-Intermediary.jar using unzip
unzip -o "build_BTW/BTW_dev/BTW-CE-Intermediary.jar" -d build_BTW/BTW_dev

# Remove the intermediary jar
rm -f build_BTW/BTW_dev/BTW-CE-Intermediary.jar

# Change into the build_BTW/BTW_dev directory
cd build_BTW/BTW_dev

# Create the zip archive
zip -r ../BTW_dev.zip *

# Move back to the build directory
cd ..

# Remove and recreate the BTW_dev directory
rm -rf BTW_dev
mkdir BTW_dev

# Move the zip file into the new directory
mv BTW_dev.zip BTW_dev/BTW_dev.zip

# Move the javadoc jar
mv tmp/BTW_dev/BTW-CE-Intermediary-javadoc.jar BTW_dev/BTW_dev-javadoc.jar

# Clean up the tmp directory
rm -rf tmp/BTW_dev

echo "Done!"