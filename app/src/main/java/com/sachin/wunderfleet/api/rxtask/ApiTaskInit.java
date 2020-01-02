package com.sachin.wunderfleet.api.rxtask;


public class ApiTaskInit {

    private ApiTask apiTask;

    public ApiTask getApiTask() {
        return (apiTask == null) ? apiTask = new ApiTask() : apiTask;
    }

}
