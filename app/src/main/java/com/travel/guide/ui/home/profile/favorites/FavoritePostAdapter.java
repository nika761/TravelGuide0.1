package com.travel.guide.ui.home.profile.favorites;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.guide.R;
import com.travel.guide.helper.HelperMedia;
import com.travel.guide.model.response.PostResponse;

import java.util.List;

public class FavoritePostAdapter extends RecyclerView.Adapter<FavoritePostAdapter.PostViewHolder> {
    private List<PostResponse.Posts> posts;
    private FavoritePostListener favoritePostListener;

    private int itemWidth;

    FavoritePostAdapter(FavoritePostListener favoritePostListener) {
        this.favoritePostListener = favoritePostListener;
    }

    @NonNull
    @Override
    public FavoritePostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritePostAdapter.PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritePostAdapter.PostViewHolder holder, int position) {
        holder.loadMoreCallback(position);

        holder.postImage.getLayoutParams().width = itemWidth;
        HelperMedia.loadPhoto(holder.postImage.getContext(), posts.get(position).getCover(), holder.postImage);
        holder.reactions.setText(String.valueOf(posts.get(position).getPost_reactions()));
        holder.nickName.setText(posts.get(position).getNickname());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    void setPosts(List<PostResponse.Posts> posts) {
//        if (this.posts != null && this.posts.size() != 0) {
//            this.posts.addAll(posts);
//            notifyDataSetChanged();
//        } else {
//            this.posts = posts;
//            notifyDataSetChanged();
//        }

        this.posts = posts;
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView reactions;
        TextView nickName;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);

            reactions = itemView.findViewById(R.id.favorite_post_reactions);

            nickName = itemView.findViewById(R.id.item_customer_post_nick);

            postImage = itemView.findViewById(R.id.favorite_post_cover);

            setClickListeners();
        }

        void loadMoreCallback(int position) {
            if (position == posts.size() - 1) {
                favoritePostListener.onLazyLoad(posts.get(position).getPost_id());
            }
        }

        void setClickListeners() {
            postImage.setOnClickListener(v -> favoritePostListener.onPostChoose(posts.get(getLayoutPosition()).getPost_id()));
        }

    }
}

