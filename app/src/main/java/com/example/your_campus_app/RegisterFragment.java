package com.example.your_campus_app;

import static android.app.Activity.RESULT_OK;
import static com.example.your_campus_app.constants.Constants.COLLECTION_INSTITUTIONS;
import static com.example.your_campus_app.constants.Constants.COLLECTION_USERS;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.your_campus_app.databinding.FragmentRegisterBinding;
import com.example.your_campus_app.model.UserProfileModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private CollectionReference profileRef;
    private CollectionReference institutionRef;

    private Uri imageUri;

    static final int GALLERY_REQ_CODE = 5432;

    String type = "School";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        profileRef = db.collection(COLLECTION_USERS);
        institutionRef = db.collection(COLLECTION_INSTITUTIONS);

        loadInstitutions();

        binding.tvRedirectLogin.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_registerFragment_to_loginFragment)
        );

        binding.rgInstitutionType.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        RadioButton radioButton = view.findViewById(checkedId);

                        if (radioButton != null) {
                            type = radioButton.getText().toString();

                            Toast.makeText(
                                    getContext(),
                                    "Selected: " + type,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });

        binding.civAddProfilePhoto.setOnClickListener(v -> browsePhoto());

        binding.btnRegister.setOnClickListener(v -> register());

        return view;
    }

    private void loadInstitutions() {

        institutionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {

            List<String> institutions = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                String name = documentSnapshot.getString("name");

                if (name != null) {
                    institutions.add(name);
                }
            }

            ArrayAdapter<String> instAdapter =
                    new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.select_dialog_item,
                            institutions
                    );

            binding.actvInstitution.setThreshold(0);
            binding.actvInstitution.setAdapter(instAdapter);
        });
    }

    private void browsePhoto() {

        Intent iGallery = new Intent(Intent.ACTION_PICK);

        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(iGallery, GALLERY_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && requestCode == GALLERY_REQ_CODE
                && data != null) {

            imageUri = data.getData();

            binding.civAddProfilePhoto.setPadding(0, 0, 0, 0);

            binding.civAddProfilePhoto.setImageURI(imageUri);
        }
    }

    private void register() {

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String fullName = binding.etFullName.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String institution = binding.actvInstitution.getText().toString().trim();

        boolean hasError = false;

        if (email.isEmpty()) {
            binding.etEmail.setError("Email cannot be empty");
            hasError = true;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password cannot be empty");
            hasError = true;
        }

        if (fullName.isEmpty()) {
            binding.etFullName.setError("Name cannot be empty");
            hasError = true;
        }

        if (mobile.isEmpty()) {
            binding.etMobile.setError("Mobile cannot be empty");
            hasError = true;
        }

        if (institution.isEmpty()) {
            binding.actvInstitution.setError("Institution cannot be empty");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        ProgressDialog dialog =
                ProgressDialog.show(getContext(), "", "Registering...", true);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    dialog.dismiss();

                    if (task.isSuccessful()) {

                        String uid = task.getResult().getUser().getUid();

                        UserProfileModel userProfile =
                                new UserProfileModel(
                                        uid,
                                        fullName,
                                        mobile,
                                        type,
                                        institution,
                                        "",
                                        email
                                );

                        createAccount(userProfile);

                    } else {

                        Toast.makeText(
                                getContext(),
                                "" + task.getException().getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void createUserProfile(UserProfileModel userProfile) {
        profileRef.document(userProfile.getUid())
                .set(userProfile)
                .addOnSuccessListener(unused -> {
                    binding.pbRegLoading.setVisibility(View.GONE);
                    Toast.makeText(
                            getContext(),
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (getView() != null) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_registerFragment_to_loginFragment);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.pbRegLoading.setVisibility(View.GONE);
                    Toast.makeText(
                            getContext(),
                            "Firestore Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private String getFileExtension(Uri uri) {
        if (uri == null) return "jpg";
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension != null ? extension : "jpg";
    }

    private void createAccount(UserProfileModel userProfile) {
        binding.pbRegLoading.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child("profile_photos")
                    .child(userProfile.getUid() + "_" + System.currentTimeMillis() + "." + getFileExtension(imageUri));

            // putFile ব্যবহার করা বেশি নিরাপদ এবং সহজ
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String profilePhotoUrl = uri.toString();
                                    // অবজেক্টে ইউআরএল সেট করা হচ্ছে
                                    userProfile.setProfilePhotoUrl(profilePhotoUrl);
                                    // ইউআরএল সেট হওয়ার পর প্রোফাইল তৈরি করা হচ্ছে
                                    createUserProfile(userProfile);
                                })
                                .addOnFailureListener(e -> {
                                    binding.pbRegLoading.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        binding.pbRegLoading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            // যদি ছবি না থাকে তবে সরাসরি প্রোফাইল তৈরি হবে
            createUserProfile(userProfile);
        }
    }
}