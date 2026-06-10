package com.example.your_campus_app;

import static com.example.your_campus_app.constants.Constants.COLLECTION_POSTS;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.your_campus_app.adapter.NewsPostAdapter;
import com.example.your_campus_app.databinding.FragmentNewsBinding;
import com.example.your_campus_app.model.NewsPostModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;
    private FirebaseFirestore db;

    private static final String TAG = "NewsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        // get news from firebase
        getDataFromFirebase();

        binding.srlNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromFirebase();
                binding.srlNews.setRefreshing(false);
            }
        });

        return view;
    }

    private void getDataFromFirebase() {
        db = FirebaseFirestore.getInstance();
        CollectionReference newsPostRef = db.collection(COLLECTION_POSTS);

        ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Loading news updates...", true);

        List<NewsPostModel> newsPosts = new ArrayList<>();

        newsPostRef.whereEqualTo("live", true)
                .whereEqualTo("rejected", false)
                .whereEqualTo("type", "news")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dialog.dismiss();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "No news found in database", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: Query returned empty result");
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            NewsPostModel currPost = document.toObject(NewsPostModel.class);
                            newsPosts.add(currPost);
                        }
                        binding.rvAllNews.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.rvAllNews.setAdapter(new NewsPostAdapter(getContext(), newsPosts, POST_TYPE.GENERAL_POST));
                    }
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Log.e(TAG, "Firestore Query Error: " + e.getMessage());
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}