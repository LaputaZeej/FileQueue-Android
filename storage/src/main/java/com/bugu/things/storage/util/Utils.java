package com.bugu.things.storage.util;

import android.content.Context;

/**
 * Author by xpl, Date on 2021/2/5.
 */
public class Utils {
    public static boolean isPrivateDirectory(Context context, String path) {
        return true;
    }

    public static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }
}
