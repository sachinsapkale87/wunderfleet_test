package com.sachin.wunderfleet.api.callinterface;

import com.sachin.wunderfleet.model.CarModel;
import com.sachin.wunderfleet.model.QuickRentResponseModel;
import com.sachin.wunderfleet.api.WebAPI;

import java.util.HashMap;
import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiCall {

    @GET(WebAPI.ENDPOINT_ALL)
    Observable<List<CarModel>> getAllCarObjects();

    @GET(WebAPI.ENDPOINT_BY_ID)
    Observable<CarModel> getCarObjectsById(@Path("carid") int carId);

    @POST(WebAPI.QUICK_RENT_URL)
    Observable<QuickRentResponseModel> callQuickRent(
            @Body HashMap<String, Object> hashMap
    );
}