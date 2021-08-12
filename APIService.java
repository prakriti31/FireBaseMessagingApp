package com.codewithharry.firebase.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers (
            {
            "Content-Type:application/json",
            "Authorization:key=AAAAwiG7inM:APA91bHMm61ULpbc157_13SgOnhsEdqducrsSOV3aL3WJycoVio24sVsdTkv6ES-n15BgBMDgttCGhnjlGrp4v6RPWilhBS41s14ipeqr2hTFpeE16VejmhwppeWT05T8Pcj3QTzgr"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
