package com.seemile.launcher.net;

import rx.Observable;

/**
 * Created by whuthm on 2016/3/10.
 */
public interface Engine<T> {

    public Observable<T> start();

    public void stop();

}
