#Replay.io Android Client Library
---
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/io.replay/replay-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.replay/replay-android) [![Circle CI](https://circleci.com/gh/Originate/replay-android/tree/master.png?style=badge&circle-token=d9bbccb7db1bfaa58c304a8ac313aa2338f92423)](https://circleci.com/gh/Originate/replay-android/tree/master)


[Replay.io](http://replay.io) is an analytics platform that wraps all those other analytics tools so you don't have to. One-touch integrations and smart analysis!


##Documentation
You can find extensive documentation at [http://docs.replay.io/client-libraries/android-api](http://docs.replay.io/client-libraries/android-api). 

###Download
- Gradle (preferred)
	```java
    compile 'io.replay:-replay-android:+
    ```
- Maven 
	```xml
    <dependency>
      <groupId>io.replay</groupId>
      <artifactId>replay-android</artifactId>
      <version>0.9.2</version>
	</dependency>
    ```
- [JAR/AAR](https://maven-badges.herokuapp.com/maven-central/io.replay/replay-android)

Compiling with Gradle is the best solution, mostly because bundling JARs is *so* 2005! The library supports API 10+, so hack away!

###Build
The library compiles/targets API 19, so make sure you're rocking a Build Tools Version of at least 19.1.0 - also obviously make sure that you've downloaded the Android SDK for API 19. 

###Test

Running the test suite is easy:
```bash
./gradlew clean assembleDebug 	#compile/build library
./gradlew assembleDebugTest 	  #compile test suite
./gradlew connectedAndroidTest	#requires connected Android device; runs test suite
```

---

###License
```
TODO
```