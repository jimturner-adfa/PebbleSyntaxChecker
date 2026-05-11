#!/bin/bash
DIR=$1
DIR=${DIR%/}
LANGUAGE=$2
CDIR=${DIR%/}.copy
rm -rf $CDIR
cp -r $DIR $CDIR
pushd $CDIR
FULL_PATH=$(realpath "$(pwd)")
export PEBBLE_PREFIX="$FULL_PATH"
find . -name "*.peb" -exec java -jar /sdcard/Download/DryRun.jar $(pwd) {} \;
if [[ $LANGUAGE == "java" ]]; then
    find . -name "*.kt" -exec rm {} \;
else
    find . -name "*.java" -exec rm {} \;
fi
chmod a+x ./gradlew
sh ./gradlew -Pandroid.aapt2FromMavenOverride=/data/data/com.itsaky.androidide/files/home/android-sdk/build-tools/35.0.0/aapt2 assembleDebug
popd

