package com.leon.counter_reading.infrastructure;

import android.graphics.Bitmap;

import com.leon.counter_reading.tables.ForbiddenDtoRequestMultiple;
import com.leon.counter_reading.tables.ForbiddenDtoResponses;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.tables.MultimediaUploadResponse;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.PasswordInfo;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.SimpleResponse;
import com.leon.counter_reading.view_models.LoginViewModel;
import com.leon.counter_reading.view_models.PerformanceViewModel;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IAbfaService {

    @POST("V1/Account/Login")
    Call<LoginViewModel> login(@Body LoginViewModel loginViewModel);

    @POST("V1/User/RegisterDevice")
    Call<LoginViewModel> register(@Body LoginViewModel loginViewModel);

    @POST("V1/Account/ChangePassword")
    Call<SimpleResponse> changePassword(@Body PasswordInfo passwordInfo);

    @POST("V1/Load/Data")
    Call<ReadingData> loadData(
            @Query("appVersionCode") int appVersionCode);

    @Multipart
    @POST("V1/Upload/Single")
    Call<Integer> fileUploadSingle(
            @Part MultipartBody.Part voice,
            @Part("OnOffLoadId") String OnOffLoadId,
            @Part("Description") String Description);

    @Multipart
    @POST("V1/Upload/Grouped")
    Call<MultimediaUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") RequestBody OnOffLoadId,
            @Part("Description") RequestBody Description);

    @Multipart
    @POST("V1/Upload/Grouped")
    Call<MultimediaUploadResponse> fileUploadGrouped(
            @Part("OnOffLoadId") RequestBody OnOffLoadId,
            @Part("Description") RequestBody Description);

    @Multipart
    @POST("V1/Upload/Grouped")
    Call<MultimediaUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") RequestBody OnOffLoadId);

    @Multipart
    @POST("V1/Upload/Multiple")
    Call<MultimediaUploadResponse> fileUploadMultiple(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") ArrayList<RequestBody> OnOffLoadId,
            @Part("Description") ArrayList<RequestBody> Description);

    @Multipart
    @POST("V1/Upload/Multiple")
    Call<MultimediaUploadResponse> voiceUploadMultiple(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") ArrayList<RequestBody> OnOffLoadId,
            @Part("Description") ArrayList<RequestBody> Description);

    @POST("V1/OffLoad/Data")
    Call<OnOffLoadDto.OffLoadResponses> OffLoadData(
            @Part("isFinal") RequestBody isFinal,
            @Part("offLoads") RequestBody offLoads,
            @Part("offLoadReports") RequestBody offLoadReports);


    @POST("V1/OffLoad/Data")
    Call<OnOffLoadDto.OffLoadResponses> OffLoadData(@Body OnOffLoadDto.OffLoadData offLoads);

    @POST("V1/OffLoad/ReportOnly")
    Call<OnOffLoadDto.OffLoadResponses> ReportOnly(@Body OnOffLoadDto.OffLoadData offLoads);

    @GET("V1/Apk/Last")
    Call<ResponseBody> getLastApk();

    @GET("V1/Apk/LastInfo")
    Call<LastInfo> getLastInfo();

    @Multipart
    @POST("V1/Forbidden/Single")
    Call<ForbiddenDtoResponses> singleForbidden(
            @Part ArrayList<MultipartBody.Part> files,
            @Part("zoneId") RequestBody zoneId,
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("activate") RequestBody activate,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    @Multipart
    @POST("V1/Forbidden/Single")
    Call<ForbiddenDtoResponses> singleForbidden(
            @Part("zoneId") RequestBody zoneId,
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("activate") RequestBody activate,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    @Multipart
    @POST("V1/Forbidden/Single")
    Call<ForbiddenDtoResponses> singleForbidden(
            @Part ArrayList<MultipartBody.Part> files,
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("activate") RequestBody activate,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    @Multipart
    @POST("V1/Forbidden/Single")
    Call<ForbiddenDtoResponses> singleForbidden(
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("activate") RequestBody activate,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    //    @Multipart
    @POST("V1/Forbidden/Multiple")
    Call<ForbiddenDtoResponses> multipleForbidden(@Body ForbiddenDtoRequestMultiple forbiddenDto);

    @POST("V1/List/Offloaded/MyKarkard")
    Call<PerformanceViewModel> myPerformance(@Body PerformanceViewModel performanceVM);

    @GET("V1/Account/CreateDNTCaptchaParams")
    Call<LoginViewModel> createDNTCaptchaParams();

    @GET("DNTCaptchaImage/Show?")
    Call<ResponseBody> showDNTCaptchaImage(@Query("data") String data, @Query("fileExtension") String fileExtension);
}

