package travelguideapp.ge.travelguide.ui.search;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import travelguideapp.ge.travelguide.R;
import travelguideapp.ge.travelguide.base.BaseActivity;
import travelguideapp.ge.travelguide.helper.MyToaster;
import travelguideapp.ge.travelguide.model.request.FullSearchRequest;
import travelguideapp.ge.travelguide.model.request.SearchHashtagRequest;
import travelguideapp.ge.travelguide.model.response.FollowerResponse;
import travelguideapp.ge.travelguide.model.response.FullSearchResponse;
import travelguideapp.ge.travelguide.model.response.HashtagResponse;
import travelguideapp.ge.travelguide.model.response.PostResponse;
import travelguideapp.ge.travelguide.ui.search.go.GoFragment;
import travelguideapp.ge.travelguide.ui.search.hashtag.HashtagsFragment;
import travelguideapp.ge.travelguide.ui.search.posts.SearchPostFragment;
import travelguideapp.ge.travelguide.ui.search.user.UsersFragment;
import travelguideapp.ge.travelguide.utility.GlobalPreferences;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SearchActivity extends BaseActivity implements SearchListener {

    private ImageButton searchBtn, backBtn, clearTextBtn;
    private TabLayout tabLayout;
    private EditText searchField;
    private ConstraintLayout loader;

    private SearchPresenter searchPresenter;

    private SearchPostFragment searchPostsFragment;
    private HashtagsFragment searchHashtagsFragment;
    private UsersFragment searchUsersFragment;
    private GoFragment searchGoFragment;

    private List<HashtagResponse.Hashtags> hashtags;
    private List<FullSearchResponse.Users> users;
    private List<PostResponse.Posts> posts;

    private String searchedText;
    private boolean isLazyLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchPresenter = new SearchPresenter(this);
        initUI();
    }

    private void initUI() {
        ViewPager viewPager = findViewById(R.id.search_view_pager);

        tabLayout = findViewById(R.id.search_tabs);

        loader = findViewById(R.id.search_loader);

        searchField = findViewById(R.id.search_edit_text_second);

        backBtn = findViewById(R.id.search_back_btn_second);
        backBtn.setOnClickListener(v -> finish());

        clearTextBtn = findViewById(R.id.search_clear_searched_text_btn);
        clearTextBtn.setOnClickListener(v -> clearPreviousText());

        searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(v -> startSearch());

        setViewPager(viewPager);

    }

    private void clearPreviousText() {
        try {
            if (searchField.getText().toString() != null) {
                if (!searchField.getText().toString().isEmpty()) {
                    searchField.getText().clear();
                    searchField.clearFocus();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLoader(boolean show) {
        try {
            if (show) {
                loader.setVisibility(View.VISIBLE);
                clearTextBtn.setClickable(false);
                searchBtn.setClickable(false);
                backBtn.setClickable(false);
            } else {
                clearTextBtn.setClickable(true);
                searchBtn.setClickable(true);
                backBtn.setClickable(true);
                loader.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startSearch() {
        try {
            if (searchField.getText().toString() != null && !searchField.getText().toString().isEmpty()) {
                String accessToken = GlobalPreferences.getAccessToken(this);
                String requestText = searchField.getText().toString();
                getKeyboard(false, searchField);
                getLoader(true);
                searchField.clearFocus();
//                searchPresenter.getHashtags(accessToken, new SearchHashtagRequest(requestText));
//                searchPresenter.getFollowers(accessToken, new SearchFollowersRequest(requestText));
                searchPresenter.fullSearch(accessToken, new FullSearchRequest(requestText));
                this.searchedText = requestText;
                isLazyLoad = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setViewPager(ViewPager viewPager) {
        searchHashtagsFragment = new HashtagsFragment();
        searchUsersFragment = new UsersFragment();
        searchPostsFragment = new SearchPostFragment();
//        searchGoFragment = new GoFragment();

        SearchPagerAdapter searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        searchPagerAdapter.addFragment(searchPostsFragment, getString(R.string.posts_tab));
        searchPagerAdapter.addFragment(searchUsersFragment, getString(R.string.users_tab));
        searchPagerAdapter.addFragment(searchHashtagsFragment, getString(R.string.hashtags_tab));
//        searchPagerAdapter.addFragment(searchGoFragment, getString(R.string.go_tab));
        viewPager.setAdapter(searchPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
//                    case 0:
//                        Log.e("asdzxc", String.valueOf(tab.getId()));
//                        break;
//                    case 1:
//                        Log.e("asdzxc", String.valueOf(tab.getId()));
//                        break;
//                    case 2:
//                        Log.e("asdzxc", String.valueOf(tab.getId()));
//                        break;
//                    case 3:
//                        Log.e("asdzxc", String.valueOf(tab.getId()));
//                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }

    @Override
    public void onGetHashtags(List<HashtagResponse.Hashtags> hashtags) {
        try {
            this.hashtags.addAll(hashtags);
            searchHashtagsFragment.setHashtags(this.hashtags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetUsers(List<FollowerResponse.Followers> followers) {
//        try {
//            if (this.followers == null) {
//                this.followers = followers;
//                searchUsersFragment.initUsersRecycler(this.followers);
//                getLoader(false);
//            } else {
//                this.followers.addAll(followers);
//                searchUsersFragment.setUsers(this.followers);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            getLoader(false);
//            this.followers = followers;
//            searchUsersFragment.setUsers(this.followers);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onGetSearchedData(FullSearchResponse fullSearchResponse) {

        getLoader(false);

        try {
            if (fullSearchResponse.getUsers() != null && fullSearchResponse.getUsers().size() > 0) {
                this.users = fullSearchResponse.getUsers();
                searchUsersFragment.setUsers(this.users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (fullSearchResponse.getHashtags() != null && fullSearchResponse.getHashtags().size() > 0) {
                this.hashtags = fullSearchResponse.getHashtags();
                searchHashtagsFragment.setHashtags(this.hashtags);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (fullSearchResponse.getPosts() != null && fullSearchResponse.getPosts().size() > 0) {
                this.posts = fullSearchResponse.getPosts();
                searchPostsFragment.setPosts(this.posts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getHashtagsNextPage(int page) {
        try {
            searchPresenter.getHashtags(GlobalPreferences.getAccessToken(this), new SearchHashtagRequest(searchedText, page));
            isLazyLoad = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FullSearchResponse.Users> getUsers() {
        if (users == null)
            return null;
        else
            return users;
    }

    public List<HashtagResponse.Hashtags> getHashtags() {
        if (hashtags == null)
            return null;
        else
            return hashtags;
    }

    public List<PostResponse.Posts> getPosts() {
        if (posts == null)
            return null;
        else
            return posts;
    }

    @Override
    public void onError(String message) {
        getLoader(false);
        MyToaster.getErrorToaster(this, message);
    }

    @Override
    public void onAuthenticationError(String message) {
        super.onAuthenticateError(message);
    }

    @Override
    public void onConnectionError() {
        super.onConnectionError();
    }

    @Override
    protected void onDestroy() {
        if (searchPresenter != null)
            searchPresenter = null;
        super.onDestroy();
    }

}
