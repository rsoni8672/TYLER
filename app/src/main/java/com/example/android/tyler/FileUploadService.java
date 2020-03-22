package com.example.android.tyler;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FileUploadService {
    @Multipart
    @POST("predict_recording")
    Call<Audio> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name, @Query("text_message") String text_message);

    @POST("save_audio")
    Call<ResponseClass> save_audio(@Query("username") String username, @Query("filepath") String filename, @Query("emotion_audio") String emotion_audio);
}
