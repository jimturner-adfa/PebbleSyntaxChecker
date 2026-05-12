#!/bin/bash
DIR=$1
DIR=${DIR%/}
LANGUAGE=$2
CDIR=${DIR%/}.copy
echo *****Cloning $DIR into $CDIR
rm -rf $CDIR
cp -r $DIR $CDIR
echo ****Switching to $CDIR
pushd $CDIR > /dev/null
FULL_PATH=$(realpath "$(pwd)")
export PEBBLE_PREFIX="$FULL_PATH"
echo *****Processing .peb files in $CDIR
if [[ $LANGUAGE == "java" ]]; then
    echo *****Removing all kotlin source files from $CDIR
    find . -name "*.kt.peb" -exec rm {} \;
else
    echo *****Removing all java source files from $CDIR
    find . -name "*.java.peb" -exec rm {} \;
fi
find . -name "*.peb" -exec java -jar ~/.local/lib/DryRun.jar $(pwd) {} \;
chmod a+x ./gradlew
echo *****Building app in $CDIR
./gradlew assembleDebug
echo ********DONE**********
popd

