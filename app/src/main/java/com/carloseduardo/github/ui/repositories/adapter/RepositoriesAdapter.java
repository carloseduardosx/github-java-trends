package com.carloseduardo.github.ui.repositories.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.carloseduardo.github.R;
import com.carloseduardo.github.data.model.Owner;
import com.carloseduardo.github.data.model.Repository;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.ViewHolder> {

    private final List<Repository> repositories;

    public RepositoriesAdapter(List<Repository> repositories) {

        this.repositories = repositories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repository_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Repository repository = repositories.get(position);
        Owner owner = repository.getOwner();

        holder.repositoryTitle.setText(repository.getName());
        holder.repositoryDescription.setText(repository.getDescription());
        holder.repositoryForks.setText(String.valueOf(repository.getForksCount()));
        holder.repositoryStarts.setText(String.valueOf(repository.getStargazersCount()));
        holder.repositoryOwnerName.setText(owner.getLogin());
        Picasso.with(holder.repositoryOwnerImage.getContext())
                .load(owner.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_white_24dp)
                .error(R.drawable.ic_account_circle_white_24dp)
                .fit()
                .into(holder.repositoryOwnerImage);
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    public void appendItems(@NonNull List<Repository> repositories) {

        this.repositories.addAll(repositories);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout repositoryContainer;
        private AppCompatTextView repositoryTitle;
        private AppCompatTextView repositoryDescription;
        private AppCompatTextView repositoryForks;
        private AppCompatTextView repositoryStarts;
        private LinearLayout repositoryOwnerContainer;
        private CircleImageView repositoryOwnerImage;
        private AppCompatTextView repositoryOwnerName;

        ViewHolder(View itemView) {
            super(itemView);
            repositoryContainer = (RelativeLayout) itemView.findViewById(R.id.repository_container);
            repositoryTitle = (AppCompatTextView) repositoryContainer.findViewById(R.id.repository_title);
            repositoryDescription = (AppCompatTextView) repositoryContainer.findViewById(R.id.repository_description);
            repositoryForks = (AppCompatTextView) repositoryContainer.findViewById(R.id.repository_forks);
            repositoryStarts = (AppCompatTextView) repositoryContainer.findViewById(R.id.repository_stars);
            repositoryOwnerContainer = (LinearLayout) repositoryContainer.findViewById(R.id.repository_owner_container);
            repositoryOwnerImage = (CircleImageView) repositoryOwnerContainer.findViewById(R.id.repository_owner_img);
            repositoryOwnerName = (AppCompatTextView) repositoryOwnerContainer.findViewById(R.id.repository_owner_name);
        }
    }
}