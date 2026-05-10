#!/bin/bash
DIR=$1
LANGUAGE=$2
CDIR=$DIR.copy
cp -r $DIR $CDIR
pushd $CDIR
FULL_PATH=$(realpath "$CDIR")
export PEBBLE_PREFIX="$FULL_PATH"
find . -name "*.peb" -exec java -jar ~/.local/lib/DryRun.jar $(pwd) {} \;
if [[ $LANGUAGE == "java" ]]; then
    find . -name "*.kt" -exec rm {} \;
else
    find . -name "*.java" -exec rm {} \;
fi
chmod a+x ./gradlew
./gradlew assembleDebug
popd

