package travelguideapp.ge.travelguide.ui.home.feed;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import travelguideapp.ge.travelguide.R;
import travelguideapp.ge.travelguide.custom.CustomProgressBar;
import travelguideapp.ge.travelguide.model.response.PostResponse;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<PostResponse.Posts> posts;
    private HomeFragmentListener homeFragmentListener;

    private PostHolder postHolder;
    private int postHolderPosition;

    PostAdapter(HomeFragmentListener homeFragmentListener) {
        this.homeFragmentListener = homeFragmentListener;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        this.postHolder = holder;
        this.postHolderPosition = position;

        holder.initStoryAdapter(position);

        holder.loadMoreCallback(position);
    }

    void setPosts(List<PostResponse.Posts> posts) {

        if (this.posts != null && this.posts.size() != 0)
            this.posts.addAll(posts);

        else {
            this.posts = posts;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder implements CustomProgressBar.StoryListener, StoryPlayingListener {

        private LinearLayoutManager layoutManager;
        public RecyclerView storyRecycler;
        public StoryAdapter storyAdapter;

        public CustomProgressBar customProgressBar;

        int oldPosition;

        PostHolder(@NonNull View itemView) {
            super(itemView);
            storyRecycler = itemView.findViewById(R.id.post_recycler_new);

            SnapHelper helper = new PagerSnapHelper();
            helper.attachToRecyclerView(storyRecycler);

            customProgressBar = itemView.findViewById(R.id.story_container_new_new);

            customProgressBar.setListener(this);

        }

        void iniStory(int position) {
            customProgressBar.setStorySize(posts.get(position).getPost_stories().size());
//            int duration = posts.get(position).getPost_stories().get(0).getSecond();
//            storyView.start(0, duration + 2000);
        }

        void delete(int position) {
//            StoryAdapter.StoryHolder oldHolder = ((StoryAdapter.StoryHolder) storyRecycler.findViewHolderForAdapterPosition(position));
//            oldHolder.removeVideoView();

            StoryAdapter.StoryHolder storyHolder = ((StoryAdapter.StoryHolder) storyRecycler.findViewHolderForAdapterPosition(position));
            if (storyHolder != null) {
                storyHolder.frameLayout.releaseExoPlayer();
                storyHolder.frameLayout.removeAllViews();
            }
        }

//        void play(int position) {
//            StoryAdapter.StoryHolder storyHolder = ((StoryAdapter.StoryHolder) storyRecycler.findViewHolderForAdapterPosition(position));
//            if (storyHolder != null) {
//                storyHolder.playVideo(position);
//            }
//        }


        @Override
        public void storyFinished(int finishedPosition) {
            if (layoutManager.findLastVisibleItemPosition() == customProgressBar.size - 1) {
                storyRecycler.post(() -> storyRecycler.smoothScrollToPosition(0));
            } else {
                storyRecycler.post(() -> storyRecycler.smoothScrollToPosition(finishedPosition + 1));
            }
        }

        void loadMoreCallback(int position) {
            if (position == posts.size() - 2) {
                homeFragmentListener.onLazyLoad(posts.get(position).getPost_id());
            }
        }

        void initStoryAdapter(int position) {

            layoutManager = new LinearLayoutManager(storyRecycler.getContext(), RecyclerView.HORIZONTAL, false);
            storyRecycler.setLayoutManager(layoutManager);

            storyAdapter = new StoryAdapter(homeFragmentListener, this);
            storyAdapter.setStories(posts.get(position).getPost_stories());
            storyAdapter.setCurrentPost(posts.get(position));
            storyAdapter.setCustomProgressBar(customProgressBar);

            storyRecycler.setAdapter(storyAdapter);

            storyRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    switch (newState) {

                        case AbsListView.OnScrollListener.SCROLL_STATE_FLING:

                        case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                            customProgressBar.stop(true);
                            break;

                        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                            customProgressBar.stop(false);
                            break;

                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItem = layoutManager.findLastVisibleItemPosition();

                    if (oldPosition != firstVisibleItem) {
                        customProgressBar.start(firstVisibleItem, posts.get(getLayoutPosition()).getPost_stories().get(firstVisibleItem).getSecond() + 2000);
                        oldPosition = firstVisibleItem;
                    }
                }

            });

            Log.e("lazyLoad", "post id " + posts.get(position).getPost_id());

        }

        @Override
        public void onGetStoryHolder(StoryAdapter.StoryHolder storyHolder, int storyHolderPosition) {
//            homeFragmentListener.onGetHolder(storyRecycler, storyHolder, storyHolderPosition, postHolder, postHolderPosition);
        }
    }

}



