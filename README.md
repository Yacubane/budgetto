<h1 align="center">
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/app/src/main/res/drawable-xxxhdpi/logo.png" alt="Budgetto" width="200">
  <br>Budgetto<br>
</h1>

Spending Tracker Android application build with Material Design UI and Firebase

## :camera: Screenshots

<p align="center">
  <br>
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/screenshot1.jpg" alt="Budgetto" width="25%">
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/animation1.gif" alt="Budgetto" width="25%">
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/screenshot2.jpg" alt="Budgetto" width="25%">
</p>

## :star: Features

* Always synchronized with Firebase Realtime Database - you will never lose your data
* Offline mode (Data will be synchronized when you connect to internet)
* Pie chart of your expenses
* Monthly/weekly limit
* Custom categories
* Compare incomes/expenses in selected date range
* Custom currency support
* Made with Google's Material Design UI

## :hammer: Building

### Clone this repository
Start with cloning this repository and import project in Android Studio. An error with missing Google Play Services configuration will appear - google-services.json will be downloaded in next step.

<img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/import_error.png" width="400"/>
  
### Create new Firebase project
1. Log in to [Firebase console](https://console.firebase.google.com/)
2. Create new project

### Add database
1. From left menu select Develop > Database
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/database1.png" width="250"/>
2. Create new Realtime Database
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/database2.png" width="600"/>
3. Start database in test mode
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/database3.png" width="350"/>
  
> :warning: You will need to change this mode in future if you want to run on production.

### Add Google Sign-in authentication method
1. From left menu select Develop > Authentication
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/auth1.png" width="200"/>
2. Select "Sign-in method" tab and click on "Google"
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/auth2.png" width="200"/>
3. Fill in necessary data and enable this signing method
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/auth3.png" width="600"/>

### Connect Firebase with Android app project
1. Head to Project settings
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/add_app.png" width="500"/>
2. Add Android app
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/add_app2.png" width="500"/>
3. Fill in necessary data
  <img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/add_app3.png" width="500"/>
You can obtain SHA-1 of debug key in Linux by invoking this command:

```bash
keytool -list -v \
-alias androiddebugkey -keystore ~/.android/debug.keystore
```
You can also get SHA-1 key of keystore by invoking android > signingReport task in Gradle in Android Studio.
More info on how to get SHA-1 key [here](https://developers.google.com/android/guides/client-auth)
> :warning: You will need to delete this key if you want to run on production.

4. Download and copy google-services.json to project app/ folder
<img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/download_config.png" width="300"/>

### Compile & Run & Test
1. Compile and run app to verify Firebase configuration
<img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/add_app4.png" width="500"/>
You may now compile and run debug build on Android phone. You should be able to login and use every app feature.
<img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/app.png" width="200"/>
  
2. :confetti_ball: Done! :confetti_ball:
<img src="https://raw.githubusercontent.com/jakubdybczak/Budgetto/master/readme_assets/building/add_app5.png" width="500"/>

