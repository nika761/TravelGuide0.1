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
import travelguideapp.ge.travelguide.model.request.SearchFollowersRequest;
import travelguideapp.ge.travelguide.model.request.SearchHashtagRequest;
import travelguideapp.ge.travelguide.model.response.FollowerResponse;
import travelguideapp.ge.travelguide.model.response.HashtagResponse;
import travelguideapp.ge.travelguide.ui.search.go.GoFragment;
import travelguideapp.ge.travelguide.ui.search.hashtag.HashtagsFragment;
import travelguideapp.ge.travelguide.ui.search.user.UsersFragment;
import travelguideapp.ge.travelguide.utility.GlobalPreferences;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SearchActivity extends BaseActivity implements SearchListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText searchField;
    private ConstraintLayout loader;
    private ImageButton searchBtn, backBtn, clearTextBtn;

    private SearchPresenter searchPresenter;

    private HashtagsFragment hashtagsFragment;
    private UsersFragment searchUsersFragment;

    private List<HashtagResponse.Hashtags> hashtags;
    private List<FollowerResponse.Followers> followers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initUI();
        setViewPager(viewPager);
        searchPresenter = new SearchPresenter(this);
    }

    private void initUI() {
        tabLayout = findViewById(R.id.search_tabs);
        viewPager = findViewById(R.id.search_view_pager);

        loader = findViewById(R.id.search_loader);

        searchField = findViewById(R.id.search_edit_text_second);

        backBtn = findViewById(R.id.search_back_btn_second);
        backBtn.setOnClickListener(v -> finish());

        clearTextBtn = findViewById(R.id.search_clear_searched_text_btn);
        clearTextBtn.setOnClickListener(v -> clearPreviousText());

        searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(v -> startSearch());

    }

    private void clearPreviousText() {
        try {
            if (searchField.getText().toString() != null) {
                if (!searchField.getText().toString().isEmpty())
                    searchField.getText().clear();
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
                searchPresenter.getHashtags(accessToken, new SearchHashtagRequest(requestText));
                searchPresenter.getFollowers(accessToken, new SearchFollowersRequest(requestText));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setViewPager(ViewPager viewPager) {
        hashtagsFragment = new HashtagsFragment();
        searchUsersFragment = new UsersFragment();
        SearchPagerAdapter searchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
//        searchPagerAdapter.addFragment(new SearchStoriesFragment(), "STORIES");
        searchPagerAdapter.addFragment(searchUsersFragment, "USERS");
        searchPagerAdapter.addFragment(hashtagsFragment, "HASHTAGS");
        searchPagerAdapter.addFragment(new GoFragment(), "GO !");
        viewPager.setAdapter(searchPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onGetHashtags(List<HashtagResponse.Hashtags> hashtags) {
//        try {
//            if (this.hashtags == null) {
//                this.hashtags = hashtags;
//                hashtagsFragment.initHashtagRecycler(this.hashtags);
//                getLoader(false);
//            } else {
//                this.hashtags.addAll(hashtags);
//                hashtagsFragment.setHashtags(this.hashtags);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            getLoader(false);
            this.hashtags = hashtags;
            hashtagsFragment.initHashtagRecycler(this.hashtags);
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
        try {
            getLoader(false);
            this.followers = followers;
            searchUsersFragment.initUsersRecycler(this.followers);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<FollowerResponse.Followers> getFollowers() {
        if (followers == null)
            return null;
        else
            return followers;
    }

    public List<HashtagResponse.Hashtags> getHashtags() {
        if (hashtags == null)
            return null;
        else
            return hashtags;
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
        /*Supposedly TO-DO : Show Error Message  **/
    }


}
