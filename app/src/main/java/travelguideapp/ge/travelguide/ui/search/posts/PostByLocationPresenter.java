package travelguideapp.ge.travelguide.ui.search.posts;

import org.jetbrains.annotations.NotNull;

import travelguideapp.ge.travelguide.model.request.PostByHashtagRequest;
import travelguideapp.ge.travelguide.model.request.PostByLocationRequest;
import travelguideapp.ge.travelguide.model.response.PostResponse;
import travelguideapp.ge.travelguide.network.ApiService;
import travelguideapp.ge.travelguide.network.RetrofitManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class PostByLocationPresenter {

    private final PostByLocationListener searchPostListener;
    private final ApiService apiService;

    PostByLocationPresenter(PostByLocationListener searchPostListener) {
        this.searchPostListener = searchPostListener;
        this.apiService = RetrofitManager.getApiService();
    }

    void getPostsByLocation(PostByLocationRequest postByLocationRequest) {
        apiService.getPostsByLocation(postByLocationRequest).enqueue(postCallback());
    }

    void getPostsByHashtag(PostByHashtagRequest postByHashtagRequest) {
        apiService.getPostsByHashtag(postByHashtagRequest).enqueue(postCallback());
    }

    private Callback<PostResponse> postCallback() {
        return new Callback<PostResponse>() {
            @Override
            public void onResponse(@NotNull Call<PostResponse> call, @NotNull Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus() == 0) {
                        if (response.body().getPosts().size() > 0)
                            searchPostListener.onGetPosts(response.body().getPosts());
                    } else {
                        searchPostListener.onGetPostError(response.message());
                    }
                } else {
                    searchPostListener.onGetPostError(response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostResponse> call, @NotNull Throwable t) {
                searchPostListener.onGetPostError(t.getMessage());
            }
        };
    }

}
