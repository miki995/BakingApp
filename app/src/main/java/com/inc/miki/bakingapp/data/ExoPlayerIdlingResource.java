package com.inc.miki.bakingapp.data;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows for testing of the ExoPlayer in Espresso UI tests.
 */

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

    /**
     * Sets the new idle state, if idleNow is true, it pings the {@link ResourceCallback}.
     * @param idleNow false if there are pending operations, true if idle.
     */
    public void setIdleState(boolean idleNow) {
        this.idleNow.set(idleNow);
        if (idleNow && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}
