package travelguideapp.ge.travelguide.ui.login.signIn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import travelguideapp.ge.travelguide.R;
import travelguideapp.ge.travelguide.base.BaseActivity;
import travelguideapp.ge.travelguide.base.HomeParentActivity;
import travelguideapp.ge.travelguide.helper.AuthManager;
import travelguideapp.ge.travelguide.helper.ClientManager;
import travelguideapp.ge.travelguide.helper.MyToaster;
import travelguideapp.ge.travelguide.helper.SystemManager;
import travelguideapp.ge.travelguide.helper.language.GlobalLanguages;
import travelguideapp.ge.travelguide.model.customModel.AuthModel;
import travelguideapp.ge.travelguide.utility.GlobalPreferences;
import travelguideapp.ge.travelguide.model.request.LoginRequest;
import travelguideapp.ge.travelguide.model.request.VerifyEmailRequest;
import travelguideapp.ge.travelguide.model.response.AuthWithFirebaseResponse;
import travelguideapp.ge.travelguide.model.response.LoginResponse;
import travelguideapp.ge.travelguide.model.response.VerifyEmailResponse;
import travelguideapp.ge.travelguide.ui.home.HomePageActivity;
import travelguideapp.ge.travelguide.helper.HelperUI;
import travelguideapp.ge.travelguide.ui.login.signUp.SignUpFireBaseActivity;
import travelguideapp.ge.travelguide.ui.login.password.ForgotPasswordActivity;
import travelguideapp.ge.travelguide.ui.login.signUp.SignUpActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

import travelguideapp.ge.travelguide.enums.LoadWebViewBy;

public class SignInActivity extends BaseActivity implements SignInListener {

    private SignInPresenter signInPresenter;
    private CallbackManager callbackManager;


    private TextView enterMailHead;
    private TextView enterPasswordHead;
    private TextView and;

    private LottieAnimationView animationView;
    private FrameLayout frameLayout;
    private EditText enterEmail, enterPassword;
    private LoginButton signBtnFacebook;
    private ImageButton passwordStateBtn;

    private final static int GOOGLE_SIGN_IN = 0;
    private boolean passwordHidden = true;
    private int platformId;
    private String key, name;

    public static Intent getRedirectIntent(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInPresenter = new SignInPresenter(this);

        initUI();

        verifyEmail(getIntent());

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("travelguideapp.ge.travelguide", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        verifyEmail(intent);
    }

    private void verifyEmail(Intent intent) {
        try {
            if (!intent.hasExtra("USED_INTENT")) {
                Uri uri = intent.getData();
                if (uri != null) {
                    List<String> params = uri.getPathSegments();

                    String id = params.get(params.size() - 2);
                    String signature = uri.getQueryParameter("signature");

                    if (signature != null && id != null) {
                        signInPresenter.verify(GlobalPreferences.getAccessToken(this), new VerifyEmailRequest(id, signature));
                        intent.putExtra("USED_INTENT", true);
//                    Log.e("email", signature + " " + id);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initUI() {
        try {
            animationView = findViewById(R.id.animation_view_sign);
            frameLayout = findViewById(R.id.frame_sign_loader);
            signBtnFacebook = findViewById(R.id.login_button_facebook);
            callbackManager = CallbackManager.Factory.create();

            enterMailHead = findViewById(R.id.enter_mail_head);

            enterPasswordHead = findViewById(R.id.enter_password_head);

            enterEmail = findViewById(R.id.enter_email);
            enterEmail.setOnFocusChangeListener(this::onFocusChange);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterEmail.setFocusedByDefault(false);
            }

            enterPassword = findViewById(R.id.enter_password);
            enterPassword.setOnFocusChangeListener(this::onFocusChange);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPassword.setFocusedByDefault(false);
            }

            TextView notHaveAccount = findViewById(R.id.not_have_account);
            TextView connectWith = findViewById(R.id.connect_with);

            ImageView lineLeft = findViewById(R.id.line_left);
            ImageView lineRight = findViewById(R.id.line_right);
            LinearLayout terms = findViewById(R.id.linear_terms);

            TextView registerTxt = findViewById(R.id.register_now);
            registerTxt.setOnClickListener(v -> {
                onFocusChange(v, false);
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            });

            TextView forgotPassword = findViewById(R.id.forgot_password_sign_in);
            forgotPassword.setOnClickListener(v -> {
                onFocusChange(v, false);
                Intent intent1 = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent1);
            });

            and = findViewById(R.id.and);

            TextView termsOfServices = findViewById(R.id.terms_of_services);
            termsOfServices.setOnClickListener(v -> HelperUI.startWebActivity(SignInActivity.this, LoadWebViewBy.TERMS, ""));

            TextView privacyPolicy = findViewById(R.id.privacy_policy);
            privacyPolicy.setOnClickListener(v -> HelperUI.startWebActivity(SignInActivity.this, LoadWebViewBy.POLICY, ""));

            SignInButton signBtnGoogle = findViewById(R.id.sign_in_button_google);
            signBtnGoogle.setSize(SignInButton.SIZE_ICON_ONLY);

            Button googleBtn = findViewById(R.id.google);
            googleBtn.setOnClickListener(v -> signInWithGoogle());

            Button signInBtn = findViewById(R.id.sign_in_button_main);
            signInBtn.setOnClickListener(v -> {
                onFocusChange(v, false);
                signInWithAccount();
            });

            Button facebookBtn = findViewById(R.id.facebook);
            facebookBtn.setOnClickListener(v -> signInWithFacebook());

            passwordStateBtn = findViewById(R.id.password_visibility);
            passwordStateBtn.setOnClickListener(v -> {
                if (passwordHidden) {
                    passwordStateBtn.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.ic_password_hide));
                    enterPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    passwordHidden = false;
                } else {
                    passwordStateBtn.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.ic_password_show));
                    enterPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordHidden = true;
                }
            });

            HelperUI.loadAnimation(enterMailHead, R.anim.anim_swipe_bottom, 0);
            HelperUI.loadAnimation(enterEmail, R.anim.anim_swipe_bottom, 50);
            HelperUI.loadAnimation(enterPasswordHead, R.anim.anim_swipe_bottom, 100);
            HelperUI.loadAnimation(enterPassword, R.anim.anim_swipe_bottom, 150);
            HelperUI.loadAnimation(passwordStateBtn, R.anim.anim_swipe_bottom, 150);
            HelperUI.loadAnimation(forgotPassword, R.anim.anim_swipe_bottom, 200);
            HelperUI.loadAnimation(notHaveAccount, R.anim.anim_swipe_bottom, 250);
            HelperUI.loadAnimation(registerTxt, R.anim.anim_swipe_bottom, 250);
            HelperUI.loadAnimation(signInBtn, R.anim.anim_swipe_bottom, 300);
            HelperUI.loadAnimation(lineLeft, R.anim.anim_swipe_left, 400);
            HelperUI.loadAnimation(lineRight, R.anim.anim_swipe_right, 400);
            HelperUI.loadAnimation(connectWith, R.anim.anim_swipe_bottom, 350);
            HelperUI.loadAnimation(facebookBtn, R.anim.anim_swipe_left, 450);
            HelperUI.loadAnimation(googleBtn, R.anim.anim_swipe_right, 450);
            HelperUI.loadAnimation(terms, R.anim.anim_swipe_bottom, 500);

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }


    }

    private void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void signInWithAccount() {

        int white = getResources().getColor(R.color.white, null);
        String email;
        String password;

        email = HelperUI.checkEditTextData(enterEmail, enterMailHead, getString(R.string.email_or_phone_number), HelperUI.WHITE, HelperUI.BACKGROUND_DEF_WHITE, this);

        password = HelperUI.checkEditTextData(enterPassword, enterPasswordHead, getString(R.string.password), HelperUI.WHITE, HelperUI.BACKGROUND_DEF_WHITE, this);

        if (email != null) {
            HelperUI.setBackgroundDefault(enterEmail, enterMailHead, getString(R.string.email_or_phone_number), white, HelperUI.BACKGROUND_DEF_WHITE);
            if (password != null && HelperUI.checkPassword(password)) {
                HelperUI.setBackgroundDefault(enterPassword, enterPasswordHead, getString(R.string.password), white, HelperUI.BACKGROUND_DEF_WHITE);
                showLoading(true);
                signInPresenter.singIn(new LoginRequest(email, password));
            } else {
                HelperUI.setBackgroundWarning(enterPassword, enterPasswordHead, getString(R.string.password), this);
            }
        } else {
            HelperUI.setBackgroundWarning(enterEmail, enterMailHead, getString(R.string.email_or_phone_number), this);
        }
    }

    private void signInWithGoogle() {

        GlobalPreferences.saveLoginType(this, GlobalPreferences.GOOGLE);

        Intent signInIntent = ClientManager.googleSignInClient(this).getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);

    }

    private void signInWithFacebook() {
        GlobalPreferences.saveLoginType(this, GlobalPreferences.FACEBOOK);

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY);
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));
//        signBtnFacebook.performClick();
//        signBtnFacebook.callOnClick();
        signBtnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showLoading(true);
                signInPresenter.authWithFb(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                MyToaster.getToast(SignInActivity.this, getString(R.string.cancel));
            }

            @Override
            public void onError(FacebookException error) {
                MyToaster.getToast(SignInActivity.this, error.getLocalizedMessage());
            }
        });
    }

    public void showLoading(boolean show) {
        if (show) {
            frameLayout.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            animationView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onVerify(VerifyEmailResponse verifyEmailResponse) {
        AuthManager.persistAuthState(this, new AuthModel(verifyEmailResponse.getAccess_token(), verifyEmailResponse.getUser().getId(), verifyEmailResponse.getUser().getRole(), GlobalPreferences.TRAVEL_GUIDE));
        startActivity(HomePageActivity.getRedirectIntent(this));
    }

    @Override
    public void onError(String message) {
        try {
            showLoading(false);
            MyToaster.getToast(this, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFireBaseAuthSignIn(AuthWithFirebaseResponse authWithFirebaseResponse) {
        GlobalPreferences.saveAccessToken(this, authWithFirebaseResponse.getAccess_token());
        GlobalPreferences.saveUserRole(this, authWithFirebaseResponse.getUser().getRole());
        GlobalPreferences.saveUserId(this, authWithFirebaseResponse.getUser().getId());
        showLoading(false);
        startActivity(HomePageActivity.getRedirectIntent(this));
    }

    @Override
    public void onFireBaseSignUp(String token, int platform, String name) {
        this.key = token;
        this.platformId = platform;
        this.name = name;
    }

    @Override
    public void onFireBaseAuthSignUp() {
        Intent intent = new Intent(this, SignUpFireBaseActivity.class);
        intent.putExtra("platform", platformId);
        intent.putExtra("key", key);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    @Override
    public void onSign(LoginResponse loginResponse) {
        showLoading(false);
        AuthManager.persistAuthState(this, new AuthModel(loginResponse.getAccess_token(), loginResponse.getUser().getId(), loginResponse.getUser().getRole(), GlobalPreferences.TRAVEL_GUIDE));
        startActivity(HomePageActivity.getRedirectIntent(this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {

            showLoading(true);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            signInPresenter.authWithGoogle(task);
        }

    }


    @Override
    protected void onDestroy() {
        if (signInPresenter != null) {
            signInPresenter = null;
        }
        super.onDestroy();
    }

}
