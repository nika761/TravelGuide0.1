package travelguideapp.ge.travelguide.ui.home.home;

import travelguideapp.ge.travelguide.enums.SearchPostBy;
import travelguideapp.ge.travelguide.model.response.DeleteStoryResponse;
import travelguideapp.ge.travelguide.model.response.FollowResponse;
import travelguideapp.ge.travelguide.model.response.PostResponse;
import travelguideapp.ge.travelguide.model.response.SetPostFavoriteResponse;
import travelguideapp.ge.travelguide.model.response.SetStoryLikeResponse;
import travelguideapp.ge.travelguide.model.response.SharePostResponse;

import java.util.List;

public interface HomeFragmentListener {

    void stopLoader();


    void onStoryLikeChoose(int postId, int storyId);

    void onStoryLiked();

    void onStoryUnLiked();


    void onGetPosts(List<PostResponse.Posts> posts);

    void onLocationChoose(int postId, SearchPostBy searchPostBy);


    void onFollowChoose(int userId);

    void onFollowAdded();

    void onFollowRemoved();


    void onFavoriteChoose(int post_id);

    void onFavoriteAdded();

    void onFavoriteRemoved();


    void onGoChoose(String url);


    void onShareChoose(String postLink, int post_id);

    void onShared(SharePostResponse sharePostResponse);


    void onCommentChoose(int storyId, int postId);

    void onUserChoose(int userId);


    void onChooseDeleteStory(int storyId, int postId, int position);

    void onStoryDeleted(DeleteStoryResponse deleteStoryResponse);


    void onLazyLoad(int fromPostId);

    void onError(String message);


    void onAuthError(String message);

}
