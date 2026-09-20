package com.shaunwalsh.compustocker.session;

import android.content.Context;
import android.content.SharedPreferences;
import com.shaunwalsh.compustocker.model.User;

public class SessionManager {
    private static final String PREFS = "session_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_STARTED = "started_at";
    private static final long TIMEOUT_MS = 30 * 60 * 1000;

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void startSession(User user) {
        prefs.edit()
                .putInt(KEY_USER_ID, user.getId())
                .putString(KEY_USERNAME, user.getUsername())
                .putString(KEY_ROLE, user.getRole())
                .putLong(KEY_STARTED, System.currentTimeMillis())
                .apply();
    }

    public boolean isLoggedIn() {
        int id = prefs.getInt(KEY_USER_ID, -1);
        long started = prefs.getLong(KEY_STARTED, 0);
        if (id < 0 || started == 0) {
            return false;
        }
        if (System.currentTimeMillis() - started > TIMEOUT_MS) {
            clear();
            return false;
        }
        return true;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}