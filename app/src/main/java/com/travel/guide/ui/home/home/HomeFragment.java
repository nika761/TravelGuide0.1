package com.travel.guide.ui.home.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.travel.guide.R;
import com.travel.guide.enums.GetPostsFrom;
import com.travel.guide.helper.customView.CustomPostAdapter;
import com.travel.guide.helper.customView.CustomPostRecycler;
import com.travel.guide.helper.customView.CustomProgressBar;
import com.travel.guide.model.request.FavoritePostRequest;
import com.travel.guide.model.request.FollowRequest;
import com.travel.guide.model.request.PostByUserRequest;
import com.travel.guide.model.request.SetPostFavoriteRequest;
import com.travel.guide.model.request.SetStoryLikeRequest;
import com.travel.guide.model.request.SharePostRequest;
import com.travel.guide.model.response.FollowResponse;
import com.travel.guide.model.response.SetPostFavoriteResponse;
import com.travel.guide.model.response.SetStoryLikeResponse;
import com.travel.guide.utility.GlobalPreferences;
import com.travel.guide.model.request.PostRequest;
import com.travel.guide.model.response.PostResponse;
import com.travel.guide.model.response.SharePostResponse;
import com.travel.guide.ui.home.HomePageActivity;
import com.travel.guide.ui.home.customerUser.CustomerProfileActivity;
import com.travel.guide.ui.login.signIn.SignInActivity;

import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.travel.guide.network.ApiEndPoint.ACCESS_TOKEN_BEARER;

public class HomeFragment extends Fragment implements HomeFragmentListener {

    private static final int SHARING_REQUEST_CODE = 1;

    private HomeFragmentPresenter presenter;
    private PostAdapter postAdapter;

    private LottieAnimationView loader;
    private ConstraintLayout loaderContainer;
    private RecyclerView postRecycler;
    private CustomProgressBar customProgressBar;

    private int postId;
    private int customerUserId;

    private GetPostsFrom getPostsFrom;
    private CustomPostRecycler customPostRecycler;
    private CustomPostAdapter customPostAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        presenter = new HomeFragmentPresenter(this);

        loader = view.findViewById(R.id.loader_post);
        loaderContainer = view.findViewById(R.id.loader_constraint);

        customProgressBar = view.findViewById(R.id.post_progress_container);

        postRecycler = view.findViewById(R.id.recycler_story);
        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(postRecycler);

        customPostRecycler = view.findViewById(R.id.testing_recycler);
        customPostRecycler.setLayoutManager(new LinearLayoutManager(customPostRecycler.getContext()));
        customPostRecycler.setHomeFragmentListener(this);
        customPostRecycler.setCustomProgressBar(customProgressBar);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(customPostRecycler);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(postRecycler.getContext());

        Bundle params = new Bundle();
        params.putString("post_name", "pirveli posti");
        params.putString("full_text", "pirveli posti testi");
        firebaseAnalytics.logEvent("share_image", params);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            getPostsFrom = (GetPostsFrom) getArguments().getSerializable("PostShowType");
            int scrollPosition = getArguments().getInt("postPosition");
            if (getPostsFrom != null) {
                switch (getPostsFrom) {
                    case FAVORITES:
                        List<PostResponse.Posts> favoritePosts = (List<PostResponse.Posts>) getArguments().getSerializable("favoritePosts");
                        initRecyclerView(favoritePosts, true, scrollPosition);
                        break;

                    case MY_POSTS:
                        List<PostResponse.Posts> myPosts = (List<PostResponse.Posts>) getArguments().getSerializable("my_posts");
                        initRecyclerView(myPosts, true, scrollPosition);
                        break;

                    case CUSTOMER_POSTS:
                        this.customerUserId = getArguments().getInt("customer_user_id");
                        List<PostResponse.Posts> customerPosts = (List<PostResponse.Posts>) getArguments().getSerializable("customer_posts");
                        initRecyclerView(customerPosts, true, scrollPosition);
                        break;

                    case FEED:
                        loaderContainer.setVisibility(View.VISIBLE);
                        presenter.getPosts(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new PostRequest(0));
                        break;
                }
            }
        }
    }

    private void initRecyclerView(List<PostResponse.Posts> posts, boolean scrollToPosition, int scrollPosition) {
        customPostRecycler.setPosts(posts);
        customPostAdapter = new CustomPostAdapter(this, initGlide());
        customPostAdapter.setPosts(posts);
        customPostRecycler.setAdapter(customPostAdapter);
        try {
            if (scrollToPosition) {
                if (scrollPosition == 0)
                    customPostRecycler.post(() -> customPostRecycler.smoothScrollBy(0, 1));
                else
                    customPostRecycler.post(() -> customPostRecycler.smoothScrollToPosition(scrollPosition));
            } else {
                customPostRecycler.post(() -> customPostRecycler.smoothScrollBy(0, 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        postRecycler.setLayoutManager(layoutManager);
//
//        postAdapter = new CustomPostAdapter(this);
//        postAdapter.setPosts(posts);
//        postRecycler.setAdapter(postAdapter);
//
//        if (scrollToPosition) {
//            postRecycler.post(() -> postRecycler.scrollToPosition(scrollPosition));
//        }
//
//        postRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
////                if (dy > 0) {
////                    visibleItemCount = layoutManager.getChildCount();
////                    totalItemCount = layoutManager.getItemCount();
////                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
////
////                    Log.e("dasdasdaczxczxczxczx", visibleItemCount + " " + totalItemCount + " " + pastVisibleItems);
////
////                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
////                    }
////                }
//
//                int firstVisibleItem = layoutManager.findLastVisibleItemPosition();
//
//                if (firstVisibleItem != oldPosition) {
//
//                    CustomPostAdapter.CustomPostHolder postHolder = ((CustomPostAdapter.CustomPostHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem));
//
//                    if (postHolder != null) {
//                        postHolder.storyRecycler.post(() -> postHolder.storyRecycler.smoothScrollToPosition(0));
//                        postHolder.iniStory(firstVisibleItem);
//                    }
//
//                    CustomPostAdapter.CustomPostHolder oldHolder = ((CustomPostAdapter.CustomPostHolder) recyclerView.findViewHolderForAdapterPosition(oldPosition));
//
//                    if (oldHolder != null) {
////                        oldHolder.delete(0);
//                        oldHolder.storyView.removeAllViews();
//                    }
//
//                    oldPosition = firstVisibleItem;
//                }
//            }
//
////            @Override
////            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
////                super.onScrollStateChanged(recyclerView, newState);
////
////                switch (newState) {
////
////                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
////
////                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
////                        break;
////
////                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
////                        if (oldHolder != null) {
////                            oldHolder.delete(0);
////                            oldHolder.storyView.removeAllViews();
////                        }
////                        break;
////
////                }
////            }
//        });

    }

    private RequestManager initGlide() {
//        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.bg_confirmation)
//                .error(R.drawable.bg_confirmation);

        return Glide.with(customPostRecycler.getContext());
    }

    @Override
    public void onGetPosts(List<PostResponse.Posts> posts) {
//
//        if (postAdapter == null) {
//            initRecyclerView(posts, false, 0);
//        } else {
//            postAdapter.setPosts(posts);
//        }

        if (customPostAdapter == null) {
            initRecyclerView(posts, false, 0);
        } else {
            customPostAdapter.setPosts(posts);
            customPostRecycler.setPosts(posts);
        }
    }

    @Override
    public void onFollowChoose(int userId) {
        presenter.follow(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new FollowRequest(userId));
    }

    @Override
    public void onFollowSuccess(FollowResponse followResponse) {
        switch (followResponse.getStatus()) {
            case 0:
            case 1:
                Log.e("PostEmotion", followResponse.getMessage() + followResponse.getStatus());
                break;
        }
    }


    @Override
    public void onFavoriteChoose(int post_id, int position) {
        presenter.setPostFavorite(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new SetPostFavoriteRequest(post_id));
    }

    @Override
    public void onFavoriteSuccess(SetPostFavoriteResponse setPostFavoriteResponse) {
        switch (setPostFavoriteResponse.getStatus()) {
            case 0:
            case 1:
                Log.e("PostEmotion", setPostFavoriteResponse.getMessage() + setPostFavoriteResponse.getStatus());
                break;
        }
    }


    @Override
    public void onShareChoose(String postLink, int post_id) {
        this.postId = post_id;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, postLink);
        startActivityForResult(sharingIntent, SHARING_REQUEST_CODE);
    }

    @Override
    public void onShareSuccess(SharePostResponse sharePostResponse) {
        Log.e("PostEmotion", " " + sharePostResponse.getStatus());
    }


    @Override
    public void onStoryLikeChoose(int postId, int storyId, int position) {
        presenter.setStoryLike(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new SetStoryLikeRequest(storyId, postId));
    }

    @Override
    public void onStoryLiked(SetStoryLikeResponse setStoryLikeResponse) {
        switch (setStoryLikeResponse.getStatus()) {
            case 0:
            case 1:
                Log.e("PostEmotion", setStoryLikeResponse.getMessage() + setStoryLikeResponse.getStatus());
                break;
        }
    }

    @Override
    public void onCommentChoose(int storyId, int postId) {
        if (getContext() != null)
            ((HomePageActivity) getContext()).loadCommentFragment(storyId, postId);
    }

    @Override
    public void onUserChoose(int userId) {
        if (userId == GlobalPreferences.getUserId(postRecycler.getContext())) {
            if (getContext() != null)
                ((HomePageActivity) getContext()).onProfileChoose();
        } else {
            Intent intent = new Intent(postRecycler.getContext(), CustomerProfileActivity.class);
            intent.putExtra("id", userId);
            postRecycler.getContext().startActivity(intent);
        }

    }

    @Override
    public void onLazyLoad(int fromPostId) {

        switch (getPostsFrom) {

            case FAVORITES:
                presenter.getFavoritePosts(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new FavoritePostRequest(fromPostId));
                break;

            case MY_POSTS:
                presenter.getUserPosts(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new PostByUserRequest(GlobalPreferences.getUserId(postRecycler.getContext()), fromPostId));
                break;

            case CUSTOMER_POSTS:
                presenter.getUserPosts(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new PostByUserRequest(customerUserId, fromPostId));
                break;

            case FEED:
                presenter.getPosts(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new PostRequest(fromPostId));
                break;
        }

        Log.e("lazyLoad", "from post id " + fromPostId);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                presenter.setPostShare(ACCESS_TOKEN_BEARER + GlobalPreferences.getAccessToken(postRecycler.getContext()), new SharePostRequest(postId));
            }
        }
    }

    @Override
    public void onStop() {
        if (customPostRecycler != null)
            customPostRecycler.releasePlayer();

        if (presenter != null)
            presenter = null;

        super.onStop();
    }

    @Override
    public void onPause() {
        Log.e("zxczxc","paused");
        super.onPause();
    }

    @Override
    public void stopLoader() {
        loaderContainer.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
    }

    @Override
    public void onAuthError() {
        Intent intent = new Intent(postRecycler.getContext(), SignInActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onError(String message) {
        stopLoader();
        Toast.makeText(postRecycler.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
