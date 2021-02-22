package travelguideapp.ge.travelguide.ui.webView.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

import travelguideapp.ge.travelguide.R;
import travelguideapp.ge.travelguide.helper.MyToaster;
import travelguideapp.ge.travelguide.helper.SystemManager;
import travelguideapp.ge.travelguide.utility.GlobalPreferences;
import travelguideapp.ge.travelguide.model.request.AboutRequest;
import travelguideapp.ge.travelguide.model.response.AboutResponse;

import java.util.Objects;

public class AboutFragment extends Fragment implements AboutListener {

    private Button cancelBtn;
    private LottieAnimationView animationView;
    private TextView aboutHeader;
    private WebView aboutTextWebView;
    private AboutPresenter aboutPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        cancelBtn = view.findViewById(R.id.cancel_btn_about);
        cancelBtn.setText(getString(R.string.back_btn));
        cancelBtn.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        animationView = view.findViewById(R.id.animation_view_about);
        aboutHeader = view.findViewById(R.id.about_header);
        aboutTextWebView = view.findViewById(R.id.about_text);
        aboutTextWebView.getSettings();
        aboutTextWebView.setBackgroundColor(getResources().getColor(R.color.whiteNone, null));

        aboutPresenter = new AboutPresenter(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAnimation(cancelBtn, R.anim.anim_swipe_left, 50);

        aboutPresenter.sendAboutRequest(new AboutRequest(String.valueOf(GlobalPreferences.getLanguageId(cancelBtn.getContext()))));
    }

    private void loadAnimation(View target, int animationId, int offset) {
        Animation animation = AnimationUtils.loadAnimation(cancelBtn.getContext(), animationId);
        animation.setStartOffset(offset);
        target.startAnimation(animation);
    }

    private void updateUI(AboutResponse.About about) {
        aboutHeader.setText(about.getAbout_title());
        aboutTextWebView.loadData("<html><body>" + about.getAbout_text() + "</body></html>", "text/html", "UTF-8");
        animationView.setVisibility(View.GONE);
    }


    @Override
    public void onGetAbout(AboutResponse.About about) {
        updateUI(about);
    }

    @Override
    public void onGetError(String message) {
        MyToaster.getToast(cancelBtn.getContext(), message);
    }

    @Override
    public void onDestroy() {
        if (aboutPresenter != null) {
            aboutPresenter = null;
        }
        super.onDestroy();
    }
}
