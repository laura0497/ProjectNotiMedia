package com.delarosa.notimedia.model.Services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface RetrofitInterface {

    @GET("tekus/media/Arkbox.mp4")
    @Streaming
    Call<ResponseBody> downloadFile();
}
