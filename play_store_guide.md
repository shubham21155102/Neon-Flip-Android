# Google Play Store Publishing Guide

Publishing an app to the Google Play Store involves a few key phases: preparing your app build, setting up your developer account, filling out your store listing, and finally submitting for review.

## Phase 1: Prepare Your App for Release

1. **Check Versioning:**
   Ensure your `versionCode` and `versionName` in `app/build.gradle.kts` are set correctly. Every time you upload an update in the future, you must increase the `versionCode`.
   
   ```kotlin
   defaultConfig {
       applicationId = "com.neonflip"
       minSdk = 24
       targetSdk = 35
       versionCode = 1
       versionName = "1.0"
   }
   ```

2. **Create a Keystore to Sign Your App:**
   Android requires all apps to be digitally signed with a certificate (Keystore) before they can be installed or published.
   - In Android Studio, go to the top menu menu and click **Build > Generate Signed Bundle / APK**.
   - Select **Android App Bundle** and click Next.
   - Under the "Key store path", click **Create new...**.
   - Choose a safe location on your computer to save this `.jks` file (DO NOT lose this file or its passwords; you need it to update your app later).
   - Fill out the passwords and certificate details (your name, organization, etc.), and click **OK**.

3. **Generate the App Bundle (`.aab`):**
   - Continuing from the previous step, select your newly created keystore and enter the passwords. 
   - Choose the **release** build variant.
   - Click **Finish**. Android Studio will build your release `.aab` file (usually located in `app/release/app-release.aab`).

## Phase 2: Google Play Console Setup

1. **Create a Developer Account:**
   - Go to the [Google Play Console](https://play.google.com/console/about/).
   - Sign in with your Google account.
   - Accept the developer agreement and pay the $25 USD one-time registration fee.
   - Verify your identity as requested by Google.

2. **Create Your App:**
   - Once in the console dashboard, click **Create app**.
   - Enter the App name (e.g., "NeonFlips"), select "App", and "Free", and accept the declarations.
   - Click **Create app** at the bottom right.

## Phase 3: Prepare the Store Listing

Navigate to **Grow > Store presence > Main store listing** on the left menu.

1. **Text Assets:**
   - **App Name** (max 30 characters).
   - **Short Description** (max 80 characters) - E.g., "A fast-paced, gravity-flipping neon arcade challenge!"
   - **Full Description** (max 4000 characters) - Explain the features, controls, and why it's fun.

2. **Graphics Assets:**
   - **App Icon**: `512x512 px` (You can resize the high-res one we generated!).
   - **Feature Graphic**: `1024x500 px`. A promotional banner that shows at the top of your store page.
   - **Screenshots**: Take at least 2-3 screenshots of your game in action.
   - *(Optional) Video trailer link.*

## Phase 4: App Content Declarations

Before you can publish, Google needs to know what's in your app. Scroll to the bottom of the Dashboard to the **"Set up your app"** tasks:
- **Privacy Policy**: Provide a URL to your privacy policy (you can host a simple text document on Google Sites or Firebase Hosting for free).
- **App Access**: Declare if any parts of the app are restricted.
- **Ads**: Declare if your app contains ads.
- **Content Rating**: Fill out a questionnaire about the game's violence, language, etc. (NeonFlips will likely be suitable for all ages).
- **Target Audience**: Select the ages your app targets. (If targeting children, you must comply with strict family policies).
- **Data Safety**: Declare if you are collecting user data.

## Phase 5: Release and Review

1. **Create a Release:**
   - Go to **Release > Production** (or "Testing > Internal testing" if you want to test it yourself first).
   - Click **Create new release**.
   - Upload your `app-release.aab` file from Phase 1.
   - Write your release notes (e.g., "Initial release!").

2. **Rollout to Review:**
   - Save the release.
   - Click **Review release**.
   - Look for any final warnings or errors. If none, click **Start rollout to Production**.

Your app is now in review! It typically takes Google 1 to 7 days to review a new app. Once approved, it will be live on the Play Store.
