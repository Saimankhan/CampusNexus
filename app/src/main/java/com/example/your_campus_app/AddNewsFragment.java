package com.example.your_campus_app;

import static android.app.Activity.RESULT_OK;
import static com.example.your_campus_app.RegisterFragment.GALLERY_REQ_CODE;
import static com.example.your_campus_app.constants.Constants.COLLECTION_POSTS;
import static com.example.your_campus_app.constants.Constants.COLLECTION_USERS;
import static com.example.your_campus_app.constants.Constants.STORAGE_POST_IMAGE;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.your_campus_app.databinding.FragmentAddNewsBinding;
import com.example.your_campus_app.model.NewsPostModel;
import com.example.your_campus_app.model.UserProfileModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddNewsFragment extends Fragment {

    private static final String TAG = "AddNewsFragment";

    private FragmentAddNewsBinding binding;
    private FirebaseFirestore db;
    private Uri imageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddNewsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        binding.btnCancel.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_addNewsFragment_to_homeFragment));

        binding.btnPost.setOnClickListener(v -> generateNews());

        binding.ivNewsImg.setOnClickListener(v -> browsePhoto());

        return view;
    }

    private void generateNews() {
        String title = binding.etTitle.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            binding.etTitle.setError("title cannot be empty");
            return;
        }
        if (desc.isEmpty()) {
            binding.etDescription.setError("description cannot be empty");
            return;
        }

        binding.pbPostLoading.setVisibility(View.VISIBLE);
        binding.btnPost.setEnabled(false);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            binding.pbPostLoading.setVisibility(View.GONE);
            binding.btnPost.setEnabled(true);
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Timestamp timestamp = Timestamp.now();
        String postId = uid + "_" + System.currentTimeMillis();

        CollectionReference userRef = db.collection(COLLECTION_USERS);

        userRef.document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfileModel user = documentSnapshot.toObject(UserProfileModel.class);
                        
                        NewsPostModel newsPost = new NewsPostModel(
                                postId,
                                title,
                                desc,
                                "", // image will be set later
                                uid,
                                timestamp
                        );
                        
                        // পোস্টের টাইপ "news" সেট করা হচ্ছে যাতে ফিল্টার কাজ করে
                        newsPost.setType("news");
                        
                        // ইউজারের তথ্য থেকে ক্যাম্পাস সেট করা হচ্ছে
                        if (user != null) {
                            newsPost.setCampus(user.getInstitution());
                        }

                        if (imageUri != null) {
                            uploadPhoto(newsPost);
                        } else {
                            uploadNews(newsPost);
                        }
                    } else {
                        binding.pbPostLoading.setVisibility(View.GONE);
                        binding.btnPost.setEnabled(true);
                        Toast.makeText(getContext(), "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user: " + e.getMessage());
                    binding.pbPostLoading.setVisibility(View.GONE);
                    binding.btnPost.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to get user data", Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void browsePhoto() {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setType("image/*");
        startActivityForResult(iGallery, GALLERY_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.ivNewsImg.setPadding(0, 0, 0, 0);
            binding.ivNewsImg.setImageURI(imageUri);
        }
    }

    private void uploadPhoto(NewsPostModel newsPost) {
        StorageReference fileRef = FirebaseStorage.getInstance()
                .getReference()
                .child(STORAGE_POST_IMAGE)
                .child(newsPost.getPostId() + "." + getFileExtension(imageUri));

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            newsPost.setImageUrl(uri.toString());
                            uploadNews(newsPost);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Download URL error: " + e.getMessage());
                            uploadNews(newsPost); // URL না পেলেও পোস্ট সেভ করার চেষ্টা করবে
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload error: " + e.getMessage());
                    binding.pbPostLoading.setVisibility(View.GONE);
                    binding.btnPost.setEnabled(true);
                    Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadNews(NewsPostModel newsPost) {
        db.collection(COLLECTION_POSTS)
                .document(newsPost.getPostId())
                .set(newsPost)
                .addOnSuccessListener(unused -> {
                    binding.pbPostLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "News added!", Toast.LENGTH_SHORT).show();
                    if (isAdded()) {
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_addNewsFragment_to_homeFragment);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.pbPostLoading.setVisibility(View.GONE);
                    binding.btnPost.setEnabled(true);
                    Log.e(TAG, "Firestore error: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to add news", Toast.LENGTH_SHORT).show();
                });
    }
}
