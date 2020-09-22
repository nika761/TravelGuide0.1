package com.example.travelguide.ui.language;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;
import com.example.travelguide.model.response.LoginResponse;
import com.example.travelguide.ui.login.activity.SignInActivity;
import com.example.travelguide.ui.home.HomePageActivity;
import com.example.travelguide.helper.HelperPref;
import com.example.travelguide.ui.login.activity.SavedUserActivity;
import com.example.travelguide.model.response.LanguagesResponse;
import com.example.travelguide.model.User;

import java.util.List;


public class LanguageActivity extends AppCompatActivity implements LanguageListener {

    private Button startButton;
    private List<LoginResponse.User> currentUsers;
    private RecyclerView recyclerView;
    private LanguagePresenter languagePresenter;
    private List<LanguagesResponse.Language> languages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        recyclerView = findViewById(R.id.language_recycler);
        languagePresenter = new LanguagePresenter(this);
//        languagePresenter.sentLanguageRequest();
        String INTENT_LANGUAGES = "languages";
        languages = (List<LanguagesResponse.Language>) getIntent().getSerializableExtra(INTENT_LANGUAGES);
        if (languages != null) {
            iniRecyclerAdapter(languages);
        }
    }

    public void checkSavedUsers() {
//        currentUsers = HelperPref.getSavedUsers(this);
//        if (currentUsers.size() != 0) {
////            for (User currentUser : currentUsers) {
////                if (currentUser != null && currentUser.getLoginType() != null
////                        && currentUser.getLoginType().equals("google")) {
////                    startUserPageActivity(currentUser);
////                } else if (currentUser != null && currentUser.getLoginType() != null
////                        && currentUser.getLoginType().equals("facebook")) {
////                    startUserPageActivity(currentUser);
////                } else {
//            Intent userIntent = new Intent(this, SavedUserActivity.class);
//            startActivity(userIntent);
////                }
////            }
//        } else {
//            Intent signIntent = new Intent(this, SignInActivity.class);
//            startActivity(signIntent);
//        }
        Intent signIntent = new Intent(this, SignInActivity.class);
        startActivity(signIntent);

    }

    private void startUserPageActivity(User currentUser) {
        Intent userPageIntent = new Intent(this, HomePageActivity.class);
        userPageIntent.putExtra("name", currentUser.getName());
        userPageIntent.putExtra("lastName", currentUser.getLastName());
        userPageIntent.putExtra("email", currentUser.getEmail());
        userPageIntent.putExtra("url", currentUser.getUrl());
        userPageIntent.putExtra("id", currentUser.getId());
        userPageIntent.putExtra("loginType", currentUser.getLoginType());
        startActivity(userPageIntent);
    }

    private void iniRecyclerAdapter(List<LanguagesResponse.Language> updatedLanguagesList) {
        LanguagesAdapter adapter = new LanguagesAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setLanguageList(updatedLanguagesList);
    }

    @Override
    public void onGetLanguages(LanguagesResponse languagesResponse) {
        iniRecyclerAdapter(languagesResponse.getLanguage());
    }

    @Override
    public void onChooseLanguage() {
        checkSavedUsers();
    }

}
