package com.example.travelguide.ui.opening.adapter.recycler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;
import com.example.travelguide.helper.HelperPref;
import com.example.travelguide.ui.opening.interfaces.ILanguageActivity;
import com.example.travelguide.model.response.LanguagesResponse;

import java.util.List;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.LanguageViewHolder> {

    private List<LanguagesResponse.Language> languageList;
    private Context context;
    private ILanguageActivity iLanguageActivity;

    public LanguagesAdapter(Context context, ILanguageActivity iLanguageActivity) {
        this.context = context;
        this.iLanguageActivity = iLanguageActivity;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_languages);
        animation.setDuration(position * 500);
        holder.itemView.startAnimation(animation);
        holder.languageFullAdapter.setText(languageList.get(position).getNative_full());
        holder.languageSmallAdapter.setText(languageList.get(position).getNative_short());
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public void setLanguageList(List<LanguagesResponse.Language> getLanguagesList) {
        this.languageList = getLanguagesList;
        notifyDataSetChanged();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView languageFullAdapter, languageSmallAdapter;

        LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            iniUI();
            setClickListeners();
        }

        void iniUI() {
            languageFullAdapter = itemView.findViewById(R.id.language_full_adapter);
            languageSmallAdapter = itemView.findViewById(R.id.language_small_adapter);
        }

        void setClickListeners() {
            languageFullAdapter.setOnClickListener(this);
            languageSmallAdapter.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.language_full_adapter:
                    languageFullAdapter.setTextColor(Color.parseColor("#F3BC1E"));
                    HelperPref.saveLanguageId(context, languageList.get(getLayoutPosition()).getId());
                    iLanguageActivity.onChooseLanguage();
                    break;
            }
        }
    }
}
