package com.Zakovskiy.lwaf.news.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.post.PostInList;
import com.Zakovskiy.lwaf.news.PostActivity;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private FragmentManager fragmentManager;
    private List<PostInList> posts;
    private ABCActivity abc;
    public PostsAdapter(Context context, FragmentManager fragmentManager, List<PostInList> posts, ABCActivity abc) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.posts = posts;
        this.abc = abc;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostInList post = getItem(position);
        PostViewHolder postHolder = (PostViewHolder) holder;
        try {
            postHolder.bind(post);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private PostInList getItem(int position) {return this.posts.get(position);}

    @Override
    public int getItemCount() {return this.posts.size();}


    private class PostViewHolder extends RecyclerView.ViewHolder {
        private UserAvatar avatar;
        private TextView author;
        private TextView date;
        private TextView title;
        private TextView content;
        private TextView likes;
        private TextView dislikes;
        private View item;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.userAvatar);
            author = itemView.findViewById(R.id.itemPostAuthor);
            date = itemView.findViewById(R.id.itemPostDate);
            title = itemView.findViewById(R.id.itemPostTitle);
            content = itemView.findViewById(R.id.itemPostContent);
            likes = itemView.findViewById(R.id.postLikes);
            dislikes = itemView.findViewById(R.id.postDislikes);
            this.item = itemView;
        }

        public void bind(PostInList post) throws IOException, XmlPullParserException {
            avatar.setUser(post.author);
            author.setText(post.author.nickname);
            date.setText(TimeUtils.getDateAndTime(post.time*1000));
            title.setText(post.title);
            content.setText(post.content);
            likes.setText(String.valueOf(post.likes));
            dislikes.setText(String.valueOf(post.dislikes));
            this.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putSerializable("id", post.id);
                    abc.newActivity(PostActivity.class, b);
                }
            });
        }
    }
}
