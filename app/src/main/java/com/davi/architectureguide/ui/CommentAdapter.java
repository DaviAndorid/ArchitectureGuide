
package com.davi.architectureguide.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.davi.architectureguide.R;
import com.davi.architectureguide.databinding.CommentItemBinding;
import com.davi.architectureguide.db.entity.CommentEntity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends ListAdapter<CommentEntity, CommentAdapter.CommentViewHolder> {

    @Nullable
    private final CommentClickCallback mCommentClickCallback;

    CommentAdapter(@Nullable CommentClickCallback commentClickCallback) {
        super(new AsyncDifferConfig.Builder<>(new DiffUtil.ItemCallback<CommentEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull CommentEntity old,
                    @NonNull CommentEntity comment) {
                return old.getId() == comment.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull CommentEntity old,
                    @NonNull CommentEntity comment) {
                return old.getId() == comment.getId()
                        && old.getPostedAt().equals(comment.getPostedAt())
                        && old.getProductId() == comment.getProductId()
                        && TextUtils.equals(old.getText(), comment.getText());
            }
        }).build());
        mCommentClickCallback = commentClickCallback;
    }

    @Override
    @NonNull
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.comment_item,
                        parent, false);
        binding.setCallback(mCommentClickCallback);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.binding.setComment(getItem(position));
        holder.binding.executePendingBindings();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        final CommentItemBinding binding;

        CommentViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
