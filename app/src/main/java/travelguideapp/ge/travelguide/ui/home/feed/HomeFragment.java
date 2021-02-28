package travelguideapp.ge.travelguide.ui.home.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.airbnb.lottie.LottieAnimationView;

import travelguideapp.ge.travelguide.R;
import travelguideapp.ge.travelguide.base.BaseApplication;
import travelguideapp.ge.travelguide.base.HomeParentActivity;
import travelguideapp.ge.travelguide.enums.LoadWebViewBy;
import travelguideapp.ge.travelguide.helper.DialogManager;
import travelguideapp.ge.travelguide.helper.HelperUI;
import travelguideapp.ge.travelguide.helper.MyToaster;
import travelguideapp.ge.travelguide.custom.CustomTimer;
import travelguideapp.ge.travelguide.custom.customPost.CustomPostAdapter;
import travelguideapp.ge.travelguide.custom.customPost.CustomPostRecycler;
import travelguideapp.ge.travelguide.custom.CustomProgressBar;
import travelguideapp.ge.travelguide.model.customModel.ReportParams;
import travelguideapp.ge.travelguide.model.parcelable.LoadPostParams;
import travelguideapp.ge.travelguide.model.customModel.PostView;
import travelguideapp.ge.travelguide.model.parcelable.SearchPostParams;
import travelguideapp.ge.travelguide.model.request.ChooseGoRequest;
import travelguideapp.ge.travelguide.model.request.DeleteStoryRequest;
import travelguideapp.ge.travelguide.model.request.FavoritePostRequest;
import travelguideapp.ge.travelguide.model.request.FollowRequest;
import travelguideapp.ge.travelguide.model.request.PostByLocationRequest;
import travelguideapp.ge.travelguide.model.request.PostByUserRequest;
import travelguideapp.ge.travelguide.model.request.SetPostFavoriteRequest;
import travelguideapp.ge.travelguide.model.request.SetPostViewRequest;
import travelguideapp.ge.travelguide.model.request.SetStoryLikeRequest;
import travelguideapp.ge.travelguide.model.request.SharePostRequest;
import travelguideapp.ge.travelguide.model.response.DeleteStoryResponse;
import travelguideapp.ge.travelguide.ui.home.comments.CommentFragment;
import travelguideapp.ge.travelguide.ui.home.comments.RepliesFragment;
import travelguideapp.ge.travelguide.utility.GlobalPreferences;
import travelguideapp.ge.travelguide.model.request.PostRequest;
import travelguideapp.ge.travelguide.model.response.PostResponse;
import travelguideapp.ge.travelguide.model.response.SharePostResponse;
import travelguideapp.ge.travelguide.ui.home.HomePageActivity;
import travelguideapp.ge.travelguide.ui.login.signIn.SignInActivity;

import java.util.ArrayList;
import java.util.List;

import static travelguideapp.ge.travelguide.ui.home.comments.CommentFragment.CommentFragmentType.COMMENT;

public class HomeFragment extends Fragment implements HomeFragmentListener, CommentFragment.LoadCommentFragmentListener {

    public static HomeFragment getInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    private HomeFragmentPresenter presenter;
    private PostAdapter postAdapter;

    private LottieAnimationView loader;
    private ConstraintLayout loaderContainer;
    private ConstraintLayout commentFragmentContainer;
    private RecyclerView postRecycler;
    private CustomProgressBar customProgressBar;

    private ArrayList<Integer> reports = new ArrayList<>();

    private int customerUserId;

    private LoadPostParams.Source loadSource;
    private CustomPostRecycler customPostRecycler;
    private CustomPostAdapter customPostAdapter;

    private List<PostResponse.Posts> posts;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.setStatusBarColor(getResources().getColor(R.color.black, null));
            setSystemBarTheme(getActivity(), true);
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(customPostRecycler);

        return view;
    }

    private void setSystemBarTheme(final Activity activity, final boolean pIsDark) {
        try {
            final int lFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
            activity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            try {
                LoadPostParams postDataLoad = getArguments().getParcelable(LoadPostParams.INTENT_KEY_LOAD);
                this.loadSource = postDataLoad.getLoadSource();
                switch (loadSource) {
                    case FAVORITES:

                    case SEARCH:

                    case MY_POSTS:
                        initRecyclerView(postDataLoad.getPosts(), true, postDataLoad.getScrollPosition());
                        break;

                    case CUSTOMER_POSTS:
                        this.customerUserId = postDataLoad.getUserId();
                        initRecyclerView(postDataLoad.getPosts(), true, postDataLoad.getScrollPosition());
                        break;

                    case FEED:
                        loaderContainer.setVisibility(View.VISIBLE);
                        presenter.getPosts(GlobalPreferences.getAccessToken(postRecycler.getContext()), new PostRequest(0));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initRecyclerView(List<PostResponse.Posts> posts, boolean scrollToPosition, int scrollPosition) {
        customPostRecycler.setPosts(posts);
        customPostAdapter = new CustomPostAdapter(this);
        customPostAdapter.setPosts(posts);
        customPostRecycler.setAdapter(customPostAdapter);
        try {
            if (scrollToPosition) {
                if (scrollPosition == 0)
                    customPostRecycler.post(() -> customPostRecycler.smoothScrollBy(0, 1));
                else {
                    customPostRecycler.post(() -> customPostRecycler.smoothScrollToPosition(scrollPosition));
                }
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

    @Override
    public void onGetPosts(List<PostResponse.Posts> posts) {
        try {
            if (customPostAdapter == null) {
                initRecyclerView(posts, false, 0);
                this.posts = posts;
            } else {
                this.posts.addAll(posts);
                customPostAdapter.setPosts(this.posts);
                customPostRecycler.setPosts(this.posts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChoose(int postId, SearchPostParams.SearchBy searchBy) {
        try {
            ((HomeParentActivity) getActivity()).startSearchPostActivity(new SearchPostParams(postId, searchBy));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHashtagChoose(String hashtag, SearchPostParams.SearchBy searchBy) {
        try {
            ((HomeParentActivity) getActivity()).startSearchPostActivity(new SearchPostParams(hashtag, searchBy));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFollowChoose(int userId) {
        presenter.follow(GlobalPreferences.getAccessToken(postRecycler.getContext()), new FollowRequest(userId));
    }

    @Override
    public void onFollowAdded() {
        /*Supposedly TO-DO : update recycler view and adapter by this response : Now updating by local changes, for each item  **/
    }

    @Override
    public void onFollowRemoved() {
        /*Supposedly TO-DO : update recycler view and adapter by this response : Now updating by local changes, for each item  **/
    }

    @Override
    public void onFavoriteChoose(int post_id) {
        presenter.setPostFavorite(GlobalPreferences.getAccessToken(postRecycler.getContext()), new SetPostFavoriteRequest(post_id));
    }

    @Override
    public void onFavoriteAdded() {
        /*Supposedly TO-DO : update recycler view and adapter by this response : Now updating by local changes, for each item  **/
    }

    @Override
    public void onFavoriteRemoved() {
        /*Supposedly TO-DO : update recycler view and adapter by this response : Now updating by local changes, for each item  **/
    }

    @Override
    public void onGoChoose(String url, int post_id) {
        try {
            presenter.goChoosed(GlobalPreferences.getAccessToken(postRecycler.getContext()), new ChooseGoRequest(post_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HelperUI.startWebActivity(getActivity(), LoadWebViewBy.GO, url);
    }

    @Override
    public void onShareChoose(String postLink, int post_id) {
        try {
            presenter.setPostShare(GlobalPreferences.getAccessToken(postRecycler.getContext()), new SharePostRequest(post_id));
            ((HomeParentActivity) postRecycler.getContext()).shareContent(postLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShared(SharePostResponse sharePostResponse) {
        /*Supposedly TO-DO : update ui by this response **/
    }


    @Override
    public void onStoryLikeChoose(int postId, int storyId) {
        presenter.setStoryLike(GlobalPreferences.getAccessToken(postRecycler.getContext()), new SetStoryLikeRequest(storyId, postId));
    }

    @Override
    public void onStoryLiked() {
        /*Supposedly TO-DO : update recycler view and adapter by this response : Now updating by local changes, for each item  **/
    }

    @Override
    public void onStoryUnLiked() {
        /*Supposedly TO-DO : update recycler view and adapter by this response : Now updating by local changes, for each item  **/
    }

    @Override
    public void onCommentChoose(int storyId, int postId) {
        try {
            Bundle data = new Bundle();
            data.putInt("storyId", storyId);
            data.putInt("postId", postId);
            loadCommentFragment(data, COMMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserChoose(int userId) {
        if (userId == GlobalPreferences.getUserId(postRecycler.getContext())) {
            try {
                ((HomePageActivity) getContext()).onProfileChoose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (customerUserId != 0) {
            try {
                getActivity().onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ((HomeParentActivity) getActivity()).startCustomerActivity(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReportChoose(int postId) {
        try {
            ((HomeParentActivity) getActivity()).openReportDialog(ReportParams.setParams(ReportParams.Type.POST, postId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChooseDeleteStory(int storyId, int postId, int position) {
        DialogManager.getAskingDialog(postRecycler.getContext(), getString(R.string.delete_story), () -> presenter.deleteStory(GlobalPreferences.getAccessToken(postRecycler.getContext()), new DeleteStoryRequest(postId, storyId)));
    }

    @Override
    public void onStoryDeleted(DeleteStoryResponse deleteStoryResponse) {
        try {
            Intent intent = new Intent(getContext(), HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLazyLoad(int fromPostId) {

        String accessToken = GlobalPreferences.getAccessToken(postRecycler.getContext());

        switch (loadSource) {

            case FAVORITES:
                presenter.getFavoritePosts(accessToken, new FavoritePostRequest(fromPostId));
                break;

            case MY_POSTS:
                presenter.getUserPosts(accessToken, new PostByUserRequest(GlobalPreferences.getUserId(postRecycler.getContext()), fromPostId));
                break;

            case CUSTOMER_POSTS:
                presenter.getUserPosts(accessToken, new PostByUserRequest(customerUserId, fromPostId));
                break;

            case FEED:
                presenter.getPosts(accessToken, new PostRequest(fromPostId));
                break;

            case SEARCH:
                presenter.getPostsByLocation(accessToken, new PostByLocationRequest(fromPostId));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("VideoPlayerRecyclerView", "onResume");
        CustomPostRecycler.feedLive = true;
        try {
            customPostRecycler.startPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("VideoPlayerRecyclerView", "onStart");
    }

    @Override
    public void onPause() {
//        try {
//            customPostRecycler.pausePlayer();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onPause();
        Log.e("VideoPlayerRecyclerView", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomPostRecycler.feedLive = false;
        try {
            customPostRecycler.pausePlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("VideoPlayerRecyclerView", "onStop");
    }

    @Override
    public void onDestroy() {
        Log.e("VideoPlayerRecyclerView", "onDestroy");
        try {
            List<PostView> views = CustomTimer.getPostViews();
            if (views.size() != 0)
                presenter.setPostViews(GlobalPreferences.getAccessToken(getContext()), new SetPostViewRequest(views));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (customPostRecycler != null)
            customPostRecycler.releasePlayer();

        if (presenter != null)
            presenter = null;

        super.onDestroy();
    }


    @Override
    public void stopLoader() {
        try {
            loaderContainer.setVisibility(View.GONE);
            loader.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAuthError(String message) {
        try {
            MyToaster.getToast(getContext(), message);
            Intent intent = new Intent(getContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String message) {
        try {
            stopLoader();
            MyToaster.getToast(postRecycler.getContext(), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadCommentFragment(Bundle dataForFragment, CommentFragment.CommentFragmentType commentFragmentType) {
        try {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            switch (commentFragmentType) {
                case COMMENT:
                    CommentFragment commentFragment = CommentFragment.getInstance(this);
                    commentFragment.setArguments(dataForFragment);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_fragment_slide_up, R.anim.anim_swipe_bottom);
                    fragmentTransaction.addToBackStack(CommentFragment.STACK);
                    fragmentTransaction.replace(R.id.post_comment_fragment_container, commentFragment, CommentFragment.TAG);
                    fragmentTransaction.commit();
                    break;

                case COMMENT_REPLY:
                    RepliesFragment repliesFragment = new RepliesFragment();
                    repliesFragment.setArguments(dataForFragment);
                    fragmentTransaction.addToBackStack(RepliesFragment.STACK);
                    fragmentTransaction.replace(R.id.post_comment_fragment_container, repliesFragment, RepliesFragment.TAG);
                    fragmentTransaction.commit();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommentCountChanged(int count) {
        try {
            customPostRecycler.setComments(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
