package com.example.travelguide.ui.home.comments;

import com.example.travelguide.model.request.AddCommentRequest;
import com.example.travelguide.model.request.CommentRequest;
import com.example.travelguide.model.request.LikeCommentRequest;
import com.example.travelguide.model.response.AddCommentResponse;
import com.example.travelguide.model.response.CommentResponse;
import com.example.travelguide.model.response.LikeCommentResponse;
import com.example.travelguide.network.ApiService;
import com.example.travelguide.network.RetrofitManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CommentPresenter {
    private CommentListener commentListener;
    private ApiService apiService;

    CommentPresenter(CommentListener commentListener) {
        this.commentListener = commentListener;
        this.apiService = RetrofitManager.getApiService();
    }

    void getComments(String accessToken, CommentRequest commentRequest) {
        apiService.getStoryComments(accessToken, commentRequest).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 0)
                        commentListener.onGetComments(response.body());
                    else
                        commentListener.onError(response.body().getStatus() + " ");
                } else {
                    commentListener.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                commentListener.onError(t.getMessage());
            }
        });
    }

    void addComment(String accessToken, AddCommentRequest addCommentRequest) {
        apiService.addStoryComment(accessToken, addCommentRequest).enqueue(new Callback<AddCommentResponse>() {
            @Override
            public void onResponse(Call<AddCommentResponse> call, Response<AddCommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 0) {
                        commentListener.onAddComment(response.body());
                    } else {
                        commentListener.onError(response.body().getStatus() + " ");
                    }
                } else {
                    commentListener.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<AddCommentResponse> call, Throwable t) {
                commentListener.onError(t.getMessage());
            }
        });
    }

    void likeComment(String accessToken, LikeCommentRequest likeCommentRequest) {
        apiService.likeStoryComment(accessToken, likeCommentRequest).enqueue(new Callback<LikeCommentResponse>() {
            @Override
            public void onResponse(Call<LikeCommentResponse> call, Response<LikeCommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentListener.onLikeSuccess(response.body());
                } else {
                    commentListener.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LikeCommentResponse> call, Throwable t) {
                commentListener.onError(t.getMessage());
            }
        });
    }


}
