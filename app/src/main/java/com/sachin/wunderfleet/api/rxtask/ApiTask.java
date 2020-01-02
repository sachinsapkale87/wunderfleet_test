package com.sachin.wunderfleet.api.rxtask;

import com.sachin.wunderfleet.api.ApiCallBack;
import com.sachin.wunderfleet.api.ApiClientSingleton;
import com.sachin.wunderfleet.api.OnApiResponseListner;
import com.sachin.wunderfleet.api.RequestCode;
import com.sachin.wunderfleet.api.RetryWithDelay;
import com.sachin.wunderfleet.api.callinterface.ApiCall;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ApiTask {

    private final ApiCall apiCall;

    public ApiTask() {
        Retrofit retrofit = ApiClientSingleton.getRetrofitInstance();
        apiCall = retrofit.create(ApiCall.class);
    }

    public Observable<?> getAllCarObjects(OnApiResponseListner onApiResponseListner) {
        ApiClientSingleton.setToken("");
        Observable<?> callApi = apiCall.getAllCarObjects()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(0, 3000));
        callApi.subscribe(new ApiCallBack(onApiResponseListner, RequestCode.GET_CAR_OBJECTS_ALL));
        return callApi;
    }

    public Observable<?> getCarObjectsById(int car_id, OnApiResponseListner onApiResponseListner) {
        ApiClientSingleton.setToken("");
        Observable<?> callApi = apiCall.getCarObjectsById(car_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(0, 3000));
        callApi.subscribe(new ApiCallBack(onApiResponseListner, RequestCode.GET_CAR_OBJECTS_BY_ID));
        return callApi;
    }

    public Observable<?> quickRent(int id, OnApiResponseListner onApiResponseListner) {
        HashMap<String, Object> body = new HashMap<>();
        body.put("carId", id);
        ApiClientSingleton.setToken("df7c313b47b7ef87c64c0f5f5cebd6086bbb0fa");
        Observable<?> callApi = apiCall.callQuickRent(body)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(0, 3000));
        callApi.subscribe(new ApiCallBack(onApiResponseListner, RequestCode.POST_QUICK_RENT));
        return callApi;
    }

}