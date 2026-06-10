package com.example.your_campus_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.your_campus_app.POST_TYPE;
import com.example.your_campus_app.R;
import com.example.your_campus_app.databinding.ItemViewPostBinding;
import com.example.your_campus_app.model.NewsPostModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsPostAdapter extends RecyclerView.Adapter<NewsPostAdapter.NewsPostViewHolder> {
    private Context mContext;
    private List<NewsPostModel> currItems;
    private POST_TYPE currentList;
    private FirebaseFirestore db;

    public NewsPostAdapter(Context mContext, List<NewsPostModel> currItems, POST_TYPE currentList) {
        this.mContext = mContext;
        this.currItems = currItems;
        this.currentList = currentList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public NewsPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsPostViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_view_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsPostViewHolder holder, int position) {
        NewsPostModel currItem = currItems.get(holder.getAdapterPosition());

        if (currItem.getImageUrl() == null || currItem.getImageUrl().isEmpty()) {
            holder.binding.itemViewIv.setImageResource(R.drawable.ic_image);
        } else {
            Glide.with(mContext).load(currItem.getImageUrl()).into(holder.binding.itemViewIv);
        }

        String title = currItem.getTitle();
        if (title != null && title.length() > 18) {
            title = title.substring(0, 18) + "...";
        }
        
        String desc = currItem.getDesc();
        if (desc != null && desc.length() > 110) {
            desc = desc.substring(0, 110) + "...";
        }

        holder.binding.tvTitle.setText(title);
        holder.binding.tvDesc.setText(desc);

        if (currItem.getDateTime() != null) {
            Date date = currItem.getDateTime().toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(date);
            holder.binding.tvDate.setText(formattedDate);
        }
        
        holder.binding.tvType.setText(currItem.getType());

        holder.itemView.setOnClickListener(view -> {
            // todo: navigate to post details
        });
    }

    @Override
    public int getItemCount() {
        return currItems.size();
    }

    public static class NewsPostViewHolder extends RecyclerView.ViewHolder {
        public ItemViewPostBinding binding;

        public NewsPostViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemViewPostBinding.bind(itemView);
        }
    }
}
