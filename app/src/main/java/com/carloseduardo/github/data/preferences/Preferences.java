package com.carloseduardo.github.data.preferences;

import java.util.Date;

public interface Preferences {

    String getString(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    boolean getBoolean(String key);

    Date getDate(String key);

    void putString(String key, String value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putFloat(String key, float value);

    void putBoolean(String key, boolean value);

    void putDate(String key, Date date);

    void remove(String key);
}