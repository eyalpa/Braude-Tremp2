package com.eyalpano.app.UserData;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tundealao on 29/03/15.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    static SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }
    public static void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putInt("idUsers", user.idUsers);
        userLocalDatabaseEditor.putString("telephone", user.telephone);
        userLocalDatabaseEditor.putString("pass", user.pass);
        userLocalDatabaseEditor.putString("fullname", user.fullname);
        userLocalDatabaseEditor.putString("waze", user.waze);
        userLocalDatabaseEditor.putString("comments", user.comments);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }
        int idUsers = userLocalDatabase.getInt("idUsers", 0);
        String telephone = userLocalDatabase.getString("telephone", null);
        String pass =  userLocalDatabase.getString("pass",null);
        String fullname = userLocalDatabase.getString("fullname",null);
        String waze = userLocalDatabase.getString("waze",null);
        String comments = userLocalDatabase.getString("comments", null);
        User user = new User( idUsers,  telephone,  pass,  fullname, waze, comments);
        if(idUsers == 0) return null;
        return user;
    }

    public boolean getUserLoggedIn() {
        return userLocalDatabase.getBoolean("loggedIn",false);
    }
}
