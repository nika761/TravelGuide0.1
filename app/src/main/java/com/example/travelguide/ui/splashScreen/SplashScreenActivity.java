package com.example.travelguide.ui.splashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelguide.R;
import com.example.travelguide.helper.HelperSystem;
import com.example.travelguide.ui.login.activity.SignInActivity;
import com.example.travelguide.ui.home.HomePageActivity;
import com.example.travelguide.ui.language.LanguageActivity;
import com.example.travelguide.ui.language.LanguageListener;
import com.example.travelguide.ui.login.activity.SavedUserActivity;
import com.example.travelguide.model.response.LanguagesResponse;
import com.example.travelguide.ui.language.LanguagePresenter;
import com.example.travelguide.helper.HelperPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity implements LanguageListener {

    private final int SPLASH_DISPLAY_LENGTH = 1300;
    private ImageView mainIconSun;
    private TextView mainLogoTxt, justGoTxt;
    private LanguagePresenter languagePresenter;
    private ArrayList<LanguagesResponse.Language> languages;
    private final String INTENT_LANGUAGES = "languages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        iniUI();
        setAnimation();
        checkNetwork();
    }

    private void checkNetwork() {
        if (HelperSystem.checkNetworkConnection(this)) {
            if (HelperPref.getLanguageId(this) != 0) {
//                if (HelperPref.getSavedUsers(this).size() > 0) {
//                    openSaved();
//                } else {
//                    openApp();
//                }
                openApp();
            } else {
                languagePresenter.sentLanguageRequest();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    public void iniUI() {
        mainIconSun = findViewById(R.id.main_icon_sun);
        mainLogoTxt = findViewById(R.id.main_logo_text);
        justGoTxt = findViewById(R.id.just_go);
        languagePresenter = new LanguagePresenter(this);
        new Thread(() -> Glide.get(this).clearDiskCache()).start();
        Glide.get(this).clearMemory();
    }

    private void setAnimation() {
        Animation sunAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_sun);
        Animation goAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_just_go);
        goAnimation.setStartOffset(50);
        sunAnimation.setStartOffset(50);
        mainIconSun.setAnimation(sunAnimation);
        justGoTxt.setAnimation(goAnimation);
    }

    private void stopAnimation(Animation animation, Animation animationSecond) {
        animation.cancel();
        animationSecond.cancel();
    }

    private void checkLastSignedUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            String personPhotoUrl;
            Uri personPhotoUri = account.getPhotoUrl();
            if (personPhotoUri != null) {
                personPhotoUrl = personPhotoUri.toString();
            } else {
                personPhotoUrl = null;
            }
            Intent intent = new Intent(SplashScreenActivity.this, HomePageActivity.class);
            intent.putExtra("name", account.getGivenName());
            intent.putExtra("lastName", account.getFamilyName());
            intent.putExtra("email", account.getEmail());
            intent.putExtra("url", personPhotoUrl);
            intent.putExtra("id", account.getId());
            intent.putExtra("loginType", "google");
            startActivity(intent);
        } else {
            Intent mainIntent = new Intent(SplashScreenActivity.this, SignInActivity.class);
            startActivity(mainIntent);
        }

    }


    private void startApplication(List<LanguagesResponse.Language> languages) {
        new Handler().postDelayed(() -> {
//            if (HelperPref.getLanguageId(this) != 0) {
//                checkLastSignedUser();
//            } else {
            Intent mainIntent = new Intent(SplashScreenActivity.this, LanguageActivity.class);
            mainIntent.setFlags(mainIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            mainIntent.putExtra(INTENT_LANGUAGES, (Serializable) languages);
            startActivity(mainIntent);
//            }

            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void openApp() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void openSaved() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, SavedUserActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onGetLanguages(LanguagesResponse languagesResponse) {
        if (languagesResponse.getStatus() == 0) {
            List<LanguagesResponse.Language> languages = languagesResponse.getLanguage();
            startApplication(languages);
        }
    }

    @Override
    public void onChooseLanguage() {

    }
}
