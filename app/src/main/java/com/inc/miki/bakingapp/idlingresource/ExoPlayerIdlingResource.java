package com.inc.miki.bakingapp.idlingresource;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;


public class ExoPlayerIdlingResource implements IdlingResource {

    @Nullable
    private volatile ResourceCallback callback;
    private AtomicBoolean idleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return idleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }


    public void setIdleState(boolean idleNow) {
        this.idleNow.set(idleNow);
        if (idleNow && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
