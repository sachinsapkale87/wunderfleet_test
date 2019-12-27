package com.sachin.wunderfleet.net.rxtask;

import com.sachin.wunderfleet.net.ApiCallBack;
import com.sachin.wunderfleet.net.ApiClientSingleton;
import com.sachin.wunderfleet.net.OnApiResponseListner;
import com.sachin.wunderfleet.net.RequestCode;
import com.sachin.wunderfleet.net.RetryWithDelay;
import com.sachin.wunderfleet.net.callinterface.ApiCall;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class ApiTask {

    private final ApiCall apiCall;
    private ApiTask apiTask;

    public ApiTask() {
        Retrofit retrofit = ApiClientSingleton.getRetrofitInstance();
        apiCall = retrofit.create(ApiCall.class);
    }

    public ApiTask getApiTask() {
        return (apiTask == null) ? apiTask = new ApiTask() : apiTask;
    }

    public Observable<?> getAllCarObjects(OnApiResponseListner onApiResponseListner) {
        Observable<?> callApi = apiCall.getAllCarObjects()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(0, 3000));
        callApi.subscribe(new ApiCallBack(onApiResponseListner, RequestCode.GET_CAR_OBJECTS_ALL));
        return callApi;
    }

    public Observable<?> getCarObjectsById(String car_id,OnApiResponseListner onApiResponseListner) {
        Observable<?> callApi = apiCall.getCarObjectsById(car_id)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(0, 3000));
        callApi.subscribe(new ApiCallBack(onApiResponseListner, RequestCode.GET_CAR_OBJECTS_BY_ID));
        return callApi;
    }

    private RequestBody getPart(String value) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/json"), value);
        return requestFile;
    }
}