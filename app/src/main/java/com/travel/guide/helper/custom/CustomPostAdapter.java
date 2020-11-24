package com.travel.guide.helper.custom;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.travel.guide.R;
import com.travel.guide.model.response.PostResponse;
import com.travel.guide.ui.home.home.HomeFragmentListener;

import java.util.List;

public class CustomPostAdapter extends RecyclerView.Adapter {
    private List<PostResponse.Posts> posts;
    private RequestManager requestManager;
    private HomeFragmentListener listener;

    public CustomPostAdapter(HomeFragmentListener listener, RequestManager requestManager) {
        this.listener = listener;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CustomPostHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_story, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((CustomPostHolder) viewHolder).onBind(posts.get(position), requestManager, listener, position);
        loadMoreCallback(position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<PostResponse.Posts> posts) {
        if (this.posts != null && this.posts.size() != 0)
            this.posts.addAll(posts);
        else {
            this.posts = posts;
            notifyDataSetChanged();
        }
    }

    private void loadMoreCallback(int position) {
        if (position == posts.size() - 2) {
            listener.onLazyLoad(posts.get(position + 1).getPost_id());
        }
    }
}
