package travelguideapp.ge.travelguide.ui.home.profile.posts;

import travelguideapp.ge.travelguide.model.response.PostResponse;
import travelguideapp.ge.travelguide.ui.home.profile.BaseListener;

import java.util.List;

public interface UserPostListener extends BaseListener {

    void onGetPosts(List<PostResponse.Posts> posts);

    void onLazyLoad(int postId);

    void onPostChoose(int postId);

}
