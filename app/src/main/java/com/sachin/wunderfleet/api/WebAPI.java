package com.sachin.wunderfleet.api;

public class WebAPI {

    public static final String BASE_URL = "https://s3.eu-central-1.amazonaws.com/wunderfleet-recruiting-dev/";
    public static final String ENDPOINT_ALL = BASE_URL + "cars.json";
    public static final String ENDPOINT_BY_ID = BASE_URL + "cars/{carid}";
    public static final String QUICK_RENT_URL = "https://4i96gtjfia.execute-api.eu-central-1.amazonaws.com/default/wunderfleet-recruiting-mobile-dev-quick-rental";
}
