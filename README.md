# RemoteLogcatLibrary
An Android library that boots **an internal web server to display Android Logcat in your browser**. You will never need to connect your cable again to debug or watch logs.

## Why?
* Does anyone on your team, who is not Android developer, need to view logs? Maybe back-end developers? They test webservices in Postman, SoapUI, Advanced REST Client ... but they do not obtain the same results as with the APP? Why not test the APP directly? You only have to write logs to see them in the browser.
* Android Logcat shows raw data.
* You can integrate this ligthweight library and just run it into debug builds.

## How it works?
When you add this library to your Android application, it enables a web server to allow access to getting Android Logcat info in real-time. You only need run some browser and request a query to IP direction of your device (like http://192.168.0.128:8080). Note that is necesary share the same network. Once you get the fist connection to */log* path, automatically the content be reload frequently providing the latest changes. Remember that you can also filter the content using the search engine.

## How can you setup it?
You only need to follow these 3 steps:
 1) Add [this .AAR library](https://github.com/mipegir/RemoteLogcatLibrary/raw/master/downloads/remotelogcat_v1.1.aar) to your **app/libs** folder.
 2) Add these lines to the **app/build.gradle** file.
```java
    repositories {
        ...
        flatDir {
            dirs 'libs' 
        }
    }
    ...
    dependencies {
        compile fileTree(include: ['*.jar'], dir: 'libs')
        ...
        //RemoteLogcat
        compile(name: "remotelogcat_v1.1", ext: 'aar')
}
```
 3) Code this lines in your custom *Application class or Activity*.
```java
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) { //avoid execution on release, it is only for testing purpoise
            RemoteLogcatServer logcatServer 
                    = new RemoteLogcatServer(
                        8080,  //port to open connection
                        5000,  //page reload rate (ms)
                        getApplicationContext()
                     );
            logcatServer.startServer();
        }
    }
```

When you runs your APP, a default Android notification shows IP Address of your mobile phone to start conections. If you touch it, your browser opens welcome page to show tracked info from your Android Logcat.

I hope you enjoy it! Byee

# Hightlighting
* Automatic highlighting for JSON objects found in Logcat
* Automatic bold hightlighting for *Retrofit* request and response tags
* Manual bold hightlighting using *#RemoteLogcatBold#* tags

### Custom bold hightlighting
```java
 private static final String TAG_REMOTE_LOGCAT_BOLD = "#RemoteLogcatBold#";
 Log.d("your_tag", TAG_REMOTE_LOGCAT_BOLD + "something to hightlight");
```

![Screen Shot](https://github.com/mipegir/RemoteLogcatLibrary/raw/master/downloads/screenshots/desktop_screenshot_bold_higtligting.PNG)

### Automatic highlighting for JSON objects: collapsed object
![Screen Shot](https://github.com/mipegir/RemoteLogcatLibrary/raw/master/downloads/screenshots/desktop_screenshot_colapsed_json_object.png)

### Automatic highlighting for JSON objects: expanded object
To expand the content formatting a pretty JSON object you must click on the header tag. In this case: "D/OkHttp" label.

![Screen Shot](https://github.com/mipegir/RemoteLogcatLibrary/raw/master/downloads/screenshots/desktop_screenshot_expanded_json_object.png)

# Other screen shots

## From mobile phone (local connection)
![Screen Shot](https://github.com/mipegir/RemoteLogCatLibrary/raw/master/downloads/screenshots/mobile_screenshot_remote_logcat_android.png)

![Screen Shot](https://github.com/mipegir/RemoteLogCatLibrary/raw/master/downloads/screenshots/mobile_screenshot2_remote_logcat_android.png)

*Sorry, currently the content is not mobile responsive*

## From desktop browser (remote connection)
![Screen Shot](https://github.com/mipegir/RemoteLogcatLibrary/raw/master/downloads/screenshots/desktop_screenshot_remote_logcat_android.PNG)
