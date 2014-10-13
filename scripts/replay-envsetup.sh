#!/bin/bash


# Fix the CircleCI path
function ensureDependenciesAndCreateAVD(){
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"

  DEPS="$ANDROID_HOME/installed-dependencies"

  if [ ! -e $DEPS ]; then
    cp -r /usr/local/android-sdk-linux $ANDROID_HOME &&
    echo y | android update sdk -u -a -t android-8 &&
    echo y | android update sdk -u -a -t android-19 &&
    echo y | android update sdk -u -a -t platform-tools &&
    echo y | android update sdk -u -a -t build-tools-20.0.0 &&
    echo y | android update sdk -u -a -t sys-img-x86-android-19 &&
    #echo y | android update sdk -u -a -t addon-google_apis-google-18 && 
    echo no | android create avd -n testAVD -f -t android-19 --abi default/x86 &&
    touch $DEPS
  fi
}

function waitAVD {
    # http://blog.crowdint.com/2013/05/17/android-builds-on-travis-ci-with-maven.html
    (
    local bootanim=""
    local failcounter=0
    export PATH=$(dirname $(dirname $(which android)))/platform-tools:$PATH
    until [[ "$bootanim" =~ "stopped" ]]; do
        sleep 5
        bootanim=$(adb -e shell getprop init.svc.bootanim 2>&1)
        echo "emulator status=$bootanim"
    done
    )
}

function sonatypePublish{
    #http://benlimmer.com/2014/01/04/automatically-publish-to-sonatype-with-gradle-and-travis-ci/
    #http://www.survivingwithandroid.com/2014/05/android-guide-to-publish-aar-to-maven-gradle.html

    echo -e "Beginning publish task to Sonatype/MavenCentral"

    echo -e $SONATYPE_PRIVATE_KEY >> privatekey.gpg

    ./gradlew uploadArchives -PUSERNAME="${SONTAYPE_USERNAME}" -PPASSWORD="${SONATYPE_PASSWORD}" \
        -Psigning.keyId=parth@originate.com -Psigning.password="${SONTAYPE_PASSWORD}" -Psigning.secretKeyRingFile=privatekey.gpg

    RETVAL=$?

    if [ $RETVAL -eq 0 ]; then
        echo 'Completed publish!'
      else
        echo 'Publish failed.'
        return 1
      fi
    else
        echo 'Skipping Sonatype push'
    fi

}

