package com.travel.guide.ui.language;

import com.travel.guide.model.request.LanguageStringsRequest;
import com.travel.guide.model.response.LanguageStringsResponse;
import com.travel.guide.model.response.LanguagesResponse;
import com.travel.guide.network.ApiService;
import com.travel.guide.network.RetrofitManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguagePresenter {
    private LanguageListener languageListener;
    private LanguageListener.SplashListener splashListener;
    private ApiService service;

    LanguagePresenter(LanguageListener languageListener) {
        this.languageListener = languageListener;
        this.service = RetrofitManager.getApiService();
    }

    public LanguagePresenter(LanguageListener.SplashListener splashListener) {
        this.splashListener = splashListener;
        this.service = RetrofitManager.getApiService();
    }

    public void sentLanguageRequest() {
        service.getLanguages().enqueue(new Callback<LanguagesResponse>() {
            @Override
            public void onResponse(Call<LanguagesResponse> call, Response<LanguagesResponse> response) {
                if (response.isSuccessful()) {
                    splashListener.onGetLanguages(response.body());
                }
            }

            @Override
            public void onFailure(Call<LanguagesResponse> call, Throwable t) {

            }
        });
    }

    void getLanguageStrings(LanguageStringsRequest languageStringsRequest) {
        service.getLanguageStrings(languageStringsRequest).enqueue(new Callback<LanguageStringsResponse>() {
            @Override
            public void onResponse(Call<LanguageStringsResponse> call, Response<LanguageStringsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languageListener.onGetStrings(response.body());
                } else {
                    languageListener.onGetError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LanguageStringsResponse> call, Throwable t) {
                languageListener.onGetError(t.getMessage());
            }
        });
    }
}
