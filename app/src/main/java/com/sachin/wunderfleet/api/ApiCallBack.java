package com.sachin.wunderfleet.api;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ApiCallBack implements Observer {

    private OnApiResponseListner onApiResponseListner;
    private int requestCode = 0;

    public ApiCallBack(OnApiResponseListner onApiResponseListner, int requestCode) {
        this.onApiResponseListner = onApiResponseListner;
        this.requestCode = requestCode;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Object t) {
        if (onApiResponseListner != null)
            onApiResponseListner.onResponseComplete(t, requestCode, 200);
        else {
            onApiResponseListner.onResponseError("Error", requestCode, 4444);
        }
    }

    @Override
    public void onError(Throwable t) {
        if (onApiResponseListner != null)
            onApiResponseListner.onResponseError(t.getMessage() + "", requestCode, 3333);
    }

    @Override
    public void onComplete() {

    }
}
