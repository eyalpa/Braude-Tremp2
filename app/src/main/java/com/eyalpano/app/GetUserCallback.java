package com.eyalpano.app;

import com.eyalpano.app.UserData.User;

public interface GetUserCallback {

    /**
     * Invoked when background task is completed
     */

    public abstract void done(User returnedUser);
}
