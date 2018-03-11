<p align="center">
    <img src="https://i.imgur.com/PSc8AQu.png" width="150" /> 
</p> 

<center>
<h1>Catch Me If You Can</h1>
</center>

## Contents

[About](#about)<br/>
[Examples of Functionality](#examples-of-functionality)<br/>
[Running the Server, Database and Server](#running-the-server-database-and-server)</br>
[Testing](#testing)</br>
[Contributors](#contributors)</br>


## About

Catch Me If You Can is an Android game app. The motive for creating the game was to make the dull experience of trying to meet up with friends a fun experience. The app features: user accounts, customisable user profiles, text chat, Google Maps integration, in-app notifications, and basic augmented reality.

The team also decided to tackle making this app through Agile methodology with 3 major milestones, tracking assignments and responsibilities through [Zenhub](zenhub.com). 

A highly detailed requirements document of the application explaining the target users, product description, and a detailed list of features and how they should work: **https://goo.gl/WPsNSg** 

README produced by [vikramgk](https://github.com/vikramgk)

## Examples of Functionality

<center>


| <center>Register & Logging In</center> |  <center>Chat</center> | <center>Adding a Friend</center> | <center>Changing your Profile Details</center> |
| ------------- | ------------- | ------------- | ------------- |
| <img src="https://i.imgur.com/RaCq7oZ.gif" width="200" /> | <img src="https://imgur.com/L97s5jx.gif" width="200" />  |  <img src="https://imgur.com/mXWVFRS.gif" width="200" />  | <img src="https://imgur.com/211OrtT.gif" width="200" />  |


<!--| Starting a Game | Joining a Game |
| ------------- | ------------- |
| To Be Added  | To Be Added  |

| Augmented Reality | Game Over |
| ------------- | ------------- |
| To Be Added  | To Be Added  |

-->

</center>

## Requirements

### System Requirements

- A Device/Emulator running Android 5.0 Lollipop or higher
- A Stable Internet Connection
- A Accelerometer
- A Gyroscope
- A Camera

### Application Dependencies:
- Cardview-v7 25.3.1
- Gradle 1.0.0
- Google Services 3.0.0
- Google Play Services 9.6.0
- Gson 2.8.0
- Okhttp 3.6.0
- Mockito 2.4.0
- PowerMock 4.17
- jUnit 4.12
- Connection Port 8888 unblocked
- node.js

## Running the Server, Database and Server

#### Server
0. run `npm install` to install node_modules
1. `cd` into `catch-me-if-you-can/Server Program/Node.JS Web Socket Connection/ht`
2. run `$ node server.js`

#### Database
0. Download MySQL Server and MySQL Workbench
1. In workbench, create a new connection with TCP/IP
2. File > Open Model and locate `catch-me-if-you-can\Database Design\database_design.mwb`
3. Database > Forward Engineer and complete without changing any default settings for the connection your created

#### App
0. Open `catch-me-if-you-can\CatchMeIfYouCan` in Android Studio and install any missing dependencies (AS will suggest missing ones) and build project
1. Run in virtual machine or build apk to be installed on Android device

## Testing

### Testing Instructions:
1. Locate InstrumentedTestSuite.java to run all instrumented tests
  (`CatchMeIfYouCan\app\src\androidTest\java\com\comp30022\tarth\catchmeifyoucan\InstrumentedTestSuite.java`)
2. Locate UnitTestSuite.java to run all unit tests
  (`CatchMeIfYouCan\app\src\test\java\com\comp30022\tarth\catchmeifyoucan\UnitTestSuite.java`)
3. Individual test classes are located in their respective subfolders

4. Server testing is can be found by running the server


## Contributors

| [<img src="https://avatars3.githubusercontent.com/u/11909916?v=4" width="150" />](https://github.com/vikramgk)  | [<img src="https://avatars1.githubusercontent.com/u/531716?v=4" width="150" />](https://github.com/eyeonechi)  | [<img src="https://avatars2.githubusercontent.com/u/29011608?v=4" width="150" />](https://github.com/JussiSil) | [<img src="https://avatars1.githubusercontent.com/u/28945948?v=4" width="150" />](https://github.com/zirenxiao) | [<img src="https://avatars0.githubusercontent.com/u/30888620?v=4" width="150" />](https://github.com/minghaooo) |
| --- | --- | --- | --- | --- |
[Vikram GK](https://github.com/vikramgk) | [Ivan Chee](https://github.com/eyeonechi) | [Jussi Silventoinen](https://github.com/JussiSil) | [Ziren Xiao](https://github.com/zirenxiao) | [Minghao Wang](https://github.com/minghaooo) 

