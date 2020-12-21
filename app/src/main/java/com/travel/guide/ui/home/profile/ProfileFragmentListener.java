package com.travel.guide.ui.home.profile;

import com.travel.guide.model.response.ProfileResponse;

public interface ProfileFragmentListener {

    void onGetProfile(ProfileResponse.Userinfo userInfo);

    void onAuthError(String message);

    void onGetError(String message);

}
