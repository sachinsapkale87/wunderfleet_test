package com.sachin.wunderfleet.net;

public class WebAPI {

    public static final String BASE_URL = "https://s3.eu-central-1.amazonaws.com/wunderfleet-recruiting-dev/";
    public static final String GET_CAR_OBJECTS_JSON_ALL = BASE_URL + "cars.json";
    public static final String GET_CAR_OBJECTS_JSON_BY_ID = BASE_URL + "{carid}";
}
