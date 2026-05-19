#!/bin/bash
DIR=$1
DIR=${DIR%/}
LANGUAGE=$2
CDIR=${DIR%/}.copy
if [[ -z "$TERMUX_VERSION" ]]; then
    PARAM=""
    JAR="~/.local/lib/DryRun.jar"
    GRADLE="./gradlew"
else
    PARAM="-Pandroid.aapt2FromMavenOverride=/data/data/com.itsaky.androidide/files/home/android-sdk/build-tools/35.0.0/aapt2 assembleDebug"
    JAR="$(pwd)/DryRun.jar"
    GRADLE="sh ./gradlew"
fi
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
# Load files into an array safely
mapfile -d $'\0' my_files < <(find . -name "*.peb" -print0)
# Process the array
for file in "${my_files[@]}"; do
    echo "*****Updating: $file"
    java -jar $JAR $(pwd) $file
    if [ $? -eq 0 ]; then
	echo "***Update successful!"
    else
	echo "***Update failed with exit code $?"
	exit -1
    fi
done
chmod a+x ./gradlew
echo *****Building app in $CDIR
$GRADLE $PARAM assembleDebug
echo ********DONE**********
popd

