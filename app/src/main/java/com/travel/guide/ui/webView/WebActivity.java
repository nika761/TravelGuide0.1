package com.travel.guide.ui.webView;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.travel.guide.R;
import com.travel.guide.enums.LoadWebViewBy;
import com.travel.guide.helper.HelperUI;
import com.travel.guide.ui.webView.about.AboutFragment;
import com.travel.guide.ui.webView.go.GoFragment;
import com.travel.guide.ui.webView.policy.PolicyFragment;
import com.travel.guide.ui.webView.terms.TermsFragment;

import static com.travel.guide.helper.HelperUI.GO_URL;
import static com.travel.guide.helper.HelperUI.TYPE;

public class WebActivity extends AppCompatActivity {

    private final static int WEB_FRAGMENT_CONTAINER = R.id.terms_policy_fragment_container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_policy);

        checkRequestType();
    }

    private void checkRequestType() {
        LoadWebViewBy loadWebViewType = (LoadWebViewBy) getIntent().getSerializableExtra(TYPE);
        if (loadWebViewType != null) {
            switch (loadWebViewType) {
                case TERMS:
                    HelperUI.loadFragment(new TermsFragment(), null, WEB_FRAGMENT_CONTAINER, false, true, this);
                    break;

                case POLICY:
                    HelperUI.loadFragment(new PolicyFragment(), null, WEB_FRAGMENT_CONTAINER, false, true, this);
                    break;

                case ABOUT:
                    HelperUI.loadFragment(new AboutFragment(), null, WEB_FRAGMENT_CONTAINER, false, true, this);
                    break;

                case GO:
                    String url = getIntent().getStringExtra(GO_URL);
                    HelperUI.loadFragment(GoFragment.getInstance(url), null, WEB_FRAGMENT_CONTAINER, false, true, this);
                    break;
            }
        } else {
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }
}
