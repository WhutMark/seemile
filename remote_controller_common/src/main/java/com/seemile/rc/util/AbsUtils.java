package com.seemile.rc.util;

import android.content.Context;

public abstract class AbsUtils {
    
    protected static Context sContext;

    AbsUtils() {
    }
    
    public static void init(Context context){
        sContext = context.getApplicationContext();
    }
    
}
