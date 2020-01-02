package com.sachin.wunderfleet.api;

public interface OnApiResponseListner<T> {
    
    /**
     * On response complete.
     *
     * @param clsGson the cls gson
     * @param requestCode the request code
     */
    public void onResponseComplete(T clsGson, int requestCode, int responseCode);

    /**
     * On response error.
     *
     * @param errorMessage the error message
     * @param requestCode the request code
     */
    public void onResponseError(String errorMessage, int requestCode, int responseCode);
}