package com.Zakovskiy.lwaf.news.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.ShortPost;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.news.NewsActivity;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private FragmentManager fragmentManager;
    private List<ShortPost> posts;
    public PostsAdapter(Context context, FragmentManager fragmentManager, List<ShortPost> posts) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.posts = posts;
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
        ShortPost post = getItem(position);
        PostViewHolder postHolder = (PostViewHolder) holder;
        try {
            postHolder.bind(post);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private ShortPost getItem(int position) {return this.posts.get(position);}

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
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.userAvatar);
            author = itemView.findViewById(R.id.postAuthor);
            date = itemView.findViewById(R.id.postDate);
            title = itemView.findViewById(R.id.postTitle);
            content = itemView.findViewById(R.id.postContent);
            likes = itemView.findViewById(R.id.likes);
            dislikes = itemView.findViewById(R.id.dislikes);
        }

        public void bind(ShortPost post) throws IOException, XmlPullParserException {
            avatar.setUser(post.author);
            author.setText(post.author.nickname);
            date.setText(TimeUtils.getTime(post.time * 1000));
            title.setText(post.title);
            content.setText(post.content);
            likes.setText(post.likes);
            dislikes.setText(post.dislikes);
        }
    }
}
