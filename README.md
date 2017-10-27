# RemoteLogCatLibrary
An Android library that setup internal an server to **show Android LogCat in your browser**. You will never need to connect your cable again to debug or watch logs.

## How it works?
When you add this library with your Android APP, it enables a web server to allow access to getting Android Logcat info in real-time. You only need run some browser and request a query setting IP direction of your device (like http://192.168.0.128). Note that is necesary share the same network.

## How can you setup it?
You only need follow only these 3 steps:
 1) Add [this .AAR library](https://github.com/mipegir/RemoteLogCatLibrary/raw/master/downloads/remotelogcat_v1.0.aar) to your **app/libs** folder.
 2) Add lines to **app/build.gradle** file.
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
        //RemoteLogCat
        compile(name: "remotelogcat_v1.0", ext: 'aar')
}
```
 3) Code this lines in custom *Application class or some Activity*.
```java
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) { //avoid execution on release, it is only for testing purpoise
            RemoteLogCatServer logCatServer 
                    = new RemoteLogCatServer(
                        8080,  //port to open connection
                        5000,  //page reload rate (ms)
                        getApplicationContext()
                     );
            logCatServer.startViewer();
        }
    }
```

When you runs your APP, a default Android notification shows IP Address of your mobile phone to start conections. If you touch it, your browser opens welcome page to show tracked info from your Android logCat.

I hope you enjoy it! Byee
