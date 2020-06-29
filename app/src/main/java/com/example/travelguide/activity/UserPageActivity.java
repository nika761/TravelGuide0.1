package com.example.travelguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.travelguide.R;
import com.example.travelguide.adapter.BottomNavigationPageAdapter;
import com.example.travelguide.fragments.UserHomeFragment;
import com.example.travelguide.fragments.UserProfileFragment;
import com.example.travelguide.model.User;
import com.example.travelguide.utils.UtilsPref;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

public class UserPageActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private Bundle userDataForFragments;
    private BottomNavigationView bottomNavigationView;

    private ViewPager navViewPager;
    private BottomNavigationPageAdapter navViewPagerAdapter;

    public UserPageActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        ongGetUserDate();
        initBtmNav();
        initGoogleSignClient();
    }

    public void hideBottomNavigation(Boolean visible) {
        if (visible)
            bottomNavigationView.setVisibility(View.VISIBLE);
        else
            bottomNavigationView.setVisibility(View.GONE);
    }

    private void ongGetUserDate() {
        User loggedUser = (User) getIntent().getSerializableExtra("loggedUser");
        if (loggedUser != null) {
            userDataForFragments = new Bundle();
            userDataForFragments.putSerializable("user", loggedUser);
        }
    }

    private void initBtmNav() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.bot_nav_home);
        bottomNavigationView.setItemIconSize(60);

//        navViewPager = findViewById(R.id.user_page_view_pager);
//        navViewPagerAdapter = new BottomNavigationPageAdapter(getSupportFragmentManager());


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {

        switch (item.getItemId()) {
            case R.id.bot_nav_home:
                loadFragment(new UserHomeFragment(), userDataForFragments, R.id.user_page_frg_container, false);
                break;

            case R.id.bot_nav_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.bot_nav_add:
                Toast.makeText(this, "Add", Toast.LENGTH_LONG).show();
                break;

            case R.id.bot_nav_ntf:
                Toast.makeText(this, "Notification", Toast.LENGTH_LONG).show();
                break;

            case R.id.bot_nav_profile:
                loadFragment(new UserProfileFragment(), userDataForFragments, R.id.user_page_frg_container, false);
                break;
        }
        return true;
    };

    public void loadFragment(Fragment currentFragment, Bundle data, int fragmentID, boolean backStack) {
        currentFragment.setArguments(data);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        if (backStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.replace(fragmentID, currentFragment).commit();
    }

    private void initGoogleSignClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(this), gso);
    }

    public void signOutFromFbAccount() {
        LoginManager.getInstance().logOut();
//        if (user != null) {
//            UtilsPref.deleteUser(this, user);
//        }
        finish();
    }

    public void signOutFromGoogleAccount() {

        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//            if (user != null) {
//                UtilsPref.deleteUser(this, user);
//            }
        }).addOnCanceledListener(this, () -> {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(this, e -> {
            e.printStackTrace();
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
        });

        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
