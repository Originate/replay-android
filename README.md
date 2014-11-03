#Replay.io Android Client Library
---
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/io.replay/replay-android/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.replay/replay-android) <br>
[![Circle CI](https://circleci.com/gh/Originate/replay-android/tree/master.png?style=shield&circle-token=d9bbccb7db1bfaa58c304a8ac313aa2338f92423)](https://circleci.com/gh/Originate/replay-android/tree/master)


[Replay.io](http://replay.io) is an analytics platform that wraps all those other analytics tools so you don't have to. One-touch integrations and smart analysis!


##Documentation
You can find extensive documentation at [http://dev.replay.io/docs/client_libraries/android_api.html](http://dev.replay.io/docs/client_libraries/android_api.html).

###Download
- Gradle (preferred)<br>
	In your `build.gradle`:

	```gradle
    dependencies{
    	compile 'io.replay:replay-android:+
    }
    ```
- Maven

	```xml
    <dependency>
      <groupId>io.replay</groupId>
      <artifactId>replay-android</artifactId>
      <version>0.9.2</version>
	</dependency>
    ```
- [JAR](https://github.com/Originate/replay-android/releases) or [.aar](https://maven-badges.herokuapp.com/maven-central/io.replay/replay-android)
- Git (last resort or if you need to make modifications)<br>
    In your terminal window:

	```bash
	git clone git@github.com:Originate/replay-android.git
  		    
    ```
    and then in your `build.gradle`:

    ```gradle
	dependencies{
        compile project(':replay-android')
    }
    ```

Compiling with Gradle is the best solution, mostly because bundling JARs is *so* 2005! The library supports API 10+, so hack away!<br>
*(Note: If you really need API 8+, you can definitely retrofit the code to support API 8 instead of 10.)*
*(Note 2: This code does not contain a `build.xml`, so unless you manually create one, you will not be able to use replay-android as a library project in Eclipse/ADT.)*


###Build
The library targets/compiles against API 19, so make sure you're rocking a Build Tools Version of at least 19.1.0 - also obviously make sure that you've downloaded the Android SDK for API 19. 

###Test

Running the test suite is easy:
```bash
./gradlew clean assembleDebug   #compile/build library
./gradlew assembleDebugTest     #compile test suite
./gradlew connectedAndroidTest  #requires connected Android device; runs test suite
```

---

###License
```
TODO
```
