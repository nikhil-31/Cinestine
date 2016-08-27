package in.nikhil.cinestine.Network;

import android.app.Application;
import android.content.Context;

/**
 * Created by nikhil on 20-08-2016.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
    public static MyApplication getsInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}