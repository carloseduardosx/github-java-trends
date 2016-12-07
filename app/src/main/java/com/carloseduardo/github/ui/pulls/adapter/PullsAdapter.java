package com.carloseduardo.github.ui.pulls.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.carloseduardo.github.R;
import com.carloseduardo.github.base.BaseAdapter;
import com.carloseduardo.github.data.model.Owner;
import com.carloseduardo.github.data.model.Pull;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PullsAdapter extends BaseAdapter<PullsAdapter.ViewHolder, Pull> {

    public PullsAdapter(List<Pull> items) {
        super(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pull_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Pull pull = items.get(position);
        String body = pull.getBody();
        Owner user = pull.getUser();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        configurePullBody(holder, pull, body);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openPullIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pull.getHtmlUrl()));
                view.getContext().startActivity(openPullIntent);
            }
        });
        holder.pullTitle.setText(pull.getTitle());
        holder.pullDate.setText(dateFormat.format(pull.getCreatedAt()));
        holder.userName.setText(user.getLogin());
        Picasso.with(holder.userImage.getContext())
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_white_24dp)
                .error(R.drawable.ic_account_circle_white_24dp)
                .fit()
                .into(holder.userImage);
    }

    private void configurePullBody(ViewHolder holder, Pull pull, String body) {

        if (body == null || body.isEmpty()) {

            holder.pullBody.setVisibility(View.GONE);
        } else {

            holder.pullBody.setText(pull.getBody());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout pullsContainer;
        private AppCompatTextView pullTitle;
        private AppCompatTextView pullBody;
        private AppCompatTextView pullDate;
        private LinearLayout userContainer;
        private CircleImageView userImage;
        private AppCompatTextView userName;

        ViewHolder(View itemView) {
            super(itemView);
            pullsContainer = (RelativeLayout) itemView.findViewById(R.id.pull_container);
            pullTitle = (AppCompatTextView) pullsContainer.findViewById(R.id.pull_title);
            pullBody = (AppCompatTextView) pullsContainer.findViewById(R.id.pull_body);
            pullDate = (AppCompatTextView) pullsContainer.findViewById(R.id.pull_date);
            userContainer = (LinearLayout) pullsContainer.findViewById(R.id.pull_user_container);
            userImage = (CircleImageView) userContainer.findViewById(R.id.pull_user_img);
            userName = (AppCompatTextView) userContainer.findViewById(R.id.pull_username);
        }
    }
}