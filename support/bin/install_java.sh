#!/bin/bash

# Copyright (c) UChicago Argonne, LLC. All rights reserved.
# See LICENSE file.

# Installs Eclipse Temurin JDK into java/$CDB_HOST_ARCH (version set in versions.sh).

CDB_HOST_ARCH=$(uname -sm | tr -s '[:upper:][:blank:]' '[:lower:][\-]')

currentDir=`pwd`
cd `dirname $0`/.. && topDir=`pwd`
. $topDir/bin/versions.sh

srcDir=$topDir/src
buildDir=$topDir/build
javaInstallDir=$topDir/java/$CDB_HOST_ARCH

case $CDB_HOST_ARCH in
    linux-x86_64)  temurinOs=linux; temurinArch=x64 ;;
    linux-aarch64) temurinOs=linux; temurinArch=aarch64 ;;
    darwin-arm64)  temurinOs=mac;   temurinArch=aarch64 ;;
    darwin-x86_64) temurinOs=mac;   temurinArch=x64 ;;
    *) echo "Error: unsupported host architecture $CDB_HOST_ARCH"; exit 1 ;;
esac

# e.g. 25.0.5.1+2 -> tag jdk-25.0.5.1%2B2, file suffix 25.0.5.1_2
javaReleaseTag="jdk-${JAVA_VERSION/+/%2B}"
javaFileVersion=${JAVA_VERSION/+/_}
javaFileName="OpenJDK${JAVA_FEATURE_VERSION}U-jdk_${temurinArch}_${temurinOs}_hotspot_${javaFileVersion}.tar.gz"
DOWNLOAD_URL="https://github.com/adoptium/temurin${JAVA_FEATURE_VERSION}-binaries/releases/download/$javaReleaseTag/$javaFileName"

if command -v sha256sum > /dev/null; then
    SHA256_CMD="sha256sum"
else
    SHA256_CMD="shasum -a 256"
fi

mkdir -p $srcDir
cd $srcDir
if [ ! -f $javaFileName ]; then
    echo "Retrieving $DOWNLOAD_URL"
    curl -f -L -o $javaFileName $DOWNLOAD_URL || { rm -f $javaFileName; exit 1; }
    curl -f -L -o $javaFileName.sha256.txt $DOWNLOAD_URL.sha256.txt || { rm -f $javaFileName*; exit 1; }
fi

if [ ! -f $javaFileName ]; then
    echo "Error: file $srcDir/$javaFileName does not exist"
    exit 1
fi

if [ -f $javaFileName.sha256.txt ]; then
    echo "Verifying checksum of $javaFileName"
    $SHA256_CMD -c $javaFileName.sha256.txt || exit 1
fi

mkdir -p $buildDir
cd $buildDir
rm -rf jdk-*

echo "Installing JDK $JAVA_VERSION ($temurinOs/$temurinArch)"
tar zxf $srcDir/$javaFileName || exit 1
rm -rf $javaInstallDir
mkdir -p `dirname $javaInstallDir`
# macOS tarballs nest the JDK under Contents/Home; flatten so java/$CDB_HOST_ARCH/bin works everywhere.
if [ -d jdk-*/Contents/Home ]; then
    mv jdk-*/Contents/Home $javaInstallDir
    rm -rf jdk-*
else
    mv jdk-* $javaInstallDir
fi

$javaInstallDir/bin/java -version || exit 1
