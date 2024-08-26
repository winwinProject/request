package com.reactnativechangeicon;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.ComponentName;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = ChangeIconModule.NAME)
public class ChangeIconModule extends ReactContextBaseJavaModule {
    public static final String NAME = "ChangeIcon";
    private final String packageName;

    public ChangeIconModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.packageName = reactContext.getPackageName();
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void changeIcon(String iconName, Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("ACTIVITY_NOT_FOUND", "Activity is null");
            return;
        }

        String newIconName = (iconName == null || iconName.isEmpty()) ? "Default" : iconName;
        String activeClass;

        if (newIconName.equals("Default")) {
            activeClass = packageName + ".MainActivityDefault";
        } else {
            activeClass = packageName + ".MainActivity" + newIconName;
        }

        try {
            // Disable all icons
            disableAllIcons(activity);

            // Enable the new icon
            activity.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(packageName, activeClass),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            promise.resolve(newIconName);
        } catch (Exception e) {
            promise.reject("ICON_CHANGE_ERROR", "Failed to change icon: " + e.getMessage());
        }
    }

    private void disableAllIcons(Activity activity) {
        String[] iconNames = {
                ".MainActivityDefault",
                ".MainActivitylogo_1",
                ".MainActivitylogo_2"
        };

        for (String name : iconNames) {
            String className = packageName + name;
            activity.getPackageManager().setComponentEnabledSetting(
                    new ComponentName(packageName, className),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}