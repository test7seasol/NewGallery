package com.gallery.photos.editpic.ImageEDITModule.edit;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;


/* loaded from: classes.dex */
public class ArtRoom extends MultiDexApplication {
    private static ArtRoom mInstance;
    private static ArtRoom myApplication;
    public final String TAG = "ArtRoom";
    public Context sContext = null;


    public static ArtRoom getApplication() {
        return myApplication;
    }

    public static void setApplication(ArtRoom artRoom) {
        myApplication = artRoom;
    }

    public static synchronized ArtRoom getInstance() {
        ArtRoom artRoom;
        synchronized (ArtRoom.class) {
            synchronized (ArtRoom.class) {
                synchronized (ArtRoom.class) {
                    artRoom = mInstance;
                    myApplication = artRoom;
                }
                return artRoom;
            }
        }
    }

   /* static {
        System.loadLibrary("CGE");
        System.loadLibrary("CGEExt");
        System.loadLibrary("blur");
        System.loadLibrary("tensorflow_inference");

    }*/

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
        this.sContext = getApplicationContext();
        setApplication(this);
        mInstance = this;
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mInstance = this;

        Log.d("NativeLibraryLoader", "Loading native libraries...");
        try {
            System.loadLibrary("CGE");
            System.loadLibrary("CGEExt");
            System.loadLibrary("blur");
//            System.loadLibrary("tensorflow_inference");
            Log.d("NativeLibraryLoader", "Libraries loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.e("NativeLibraryLoader", "Failed to load native libraries", e);
        }


    }

    public static Context getContext() {
        return getContext();
    }

    @Override // androidx.multidex.MultiDexApplication, android.content.ContextWrapper
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
