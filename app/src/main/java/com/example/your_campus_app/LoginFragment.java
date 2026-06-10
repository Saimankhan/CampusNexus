package com.example.your_campus_app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.your_campus_app.databinding.FragmentLoginBinding;
import com.example.your_campus_app.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentLoginBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
            }
        });
        binding.btnRedirectRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        auth =FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        binding.btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetPassword();
            }
        });

        return view;
    } // <-- এখানে onCreateView মেথডটি ক্লোজ করা হয়েছে
    private void resetPassword(){
        String email=binding.etEmail.getText().toString().trim();
        if(email.isEmpty()){
            binding.etEmail.setError("Enter the emial that you want toreset password");

        }
        else{
            ProgressDialog dialog=ProgressDialog.show(getContext(),"","Sending reset link ..",true);
            auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.hide();
                    Toast.makeText(getContext(), "Reset Link sent to your email", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.hide();
                    Toast.makeText(getContext(),"Error"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    public void login( ){
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {

            if (email.isEmpty()) {
                binding.etEmail.setError("email cannot be empty");
            }

            if (password.isEmpty()) {
                binding.etPassword.setError("password cannot be empty");
            }

        } else {
            ProgressDialog  dialog=ProgressDialog.show(getContext(),"","Logging in",true);
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                    dialog.hide();

                    Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_homeFragment);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.hide();
                    Toast.makeText(getContext(), "Login failed"+e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

    } // <-- এখানে login মেথডটি ক্লোজ করা হয়েছে

} // <-- এখানে ক্লাসটি ক্লোজ করা হয়েছে