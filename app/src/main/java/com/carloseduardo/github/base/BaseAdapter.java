package com.carloseduardo.github.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH>{

    protected List<T> items;

    public BaseAdapter(List<T> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void appendItems(@NonNull List<T> items) {

        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setContent(@NonNull List<T> items) {

        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }
}