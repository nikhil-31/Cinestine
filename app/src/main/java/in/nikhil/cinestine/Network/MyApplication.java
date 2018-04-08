package in.nikhil.cinestine.Network;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by nikhil on 20-08-2016.
 */
public class MyApplication extends Application {

  private static MyApplication sInstance;

  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
    RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
    Realm.setDefaultConfiguration(configuration);
  }

  public static MyApplication getsInstance() {
    return sInstance;
  }

  public static Context getAppContext() {
    return sInstance.getApplicationContext();
  }
}
