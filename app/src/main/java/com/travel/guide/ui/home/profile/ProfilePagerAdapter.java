package com.travel.guide.ui.home.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.travel.guide.enums.GetPostsFrom;
import com.travel.guide.ui.home.profile.favorites.FavoritePostFragment;
import com.travel.guide.ui.home.profile.posts.UserPostsFragment;
import com.travel.guide.ui.home.profile.tours.UserToursFragment;

import java.util.ArrayList;

import static com.travel.guide.enums.GetPostsFrom.CUSTOMER_POSTS;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private GetPostsFrom getPostsFrom;
    private int customerUserId;

    public ProfilePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (getPostsFrom == CUSTOMER_POSTS) {
            Bundle data = new Bundle();
            data.putInt("customer_user_id", customerUserId);
            data.putSerializable("request_type", getPostsFrom);
            switch (position) {
                case 0:
                    UserPostsFragment userPostsFragment = new UserPostsFragment();
                    userPostsFragment.setArguments(data);
                    return userPostsFragment;
                case 1:
                    UserToursFragment userToursFragment = new UserToursFragment();
                    userToursFragment.setArguments(data);
                    return userToursFragment;
            }
        } else {
            switch (position) {
                case 0:
                    return new UserPostsFragment();
                case 1:
                    return new FavoritePostFragment();
                case 2:
                    return new UserToursFragment();
            }
        }
        return fragments.get(0);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    public void setGetPostsFrom(GetPostsFrom getPostsFrom) {
        this.getPostsFrom = getPostsFrom;
    }

    public void setCustomerUserId(int customerUserId) {
        this.customerUserId = customerUserId;
    }

}
