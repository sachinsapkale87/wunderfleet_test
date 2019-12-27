package com.sachin.wunderfleet.net.callinterface;

import com.sachin.wunderfleet.model.CarModel;
import com.sachin.wunderfleet.net.WebAPI;
import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiCall {

    @GET(WebAPI.GET_CAR_OBJECTS_JSON_ALL)
    Observable<List<CarModel>> getAllCarObjects();

    @GET(WebAPI.GET_CAR_OBJECTS_JSON_BY_ID)
    Observable<CarModel> getCarObjectsById(@Path("carid") String carId);
}