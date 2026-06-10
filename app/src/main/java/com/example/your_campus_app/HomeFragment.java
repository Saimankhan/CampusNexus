package com.example.your_campus_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.your_campus_app.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeFragment extends Fragment {



    public HomeFragment() {
        // Required empty public constructor
    }

   private FirebaseUser user;
    private FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        user= FirebaseAuth.getInstance().getCurrentUser();

        if(user==null){
            MainActivity.navigateToNewStartDestination(
                    getActivity(),
                    R.id.action_homeFragment_to_loginFragment,
                    R.id.loginFragment
            );
        }
       binding.addNews.setOnClickListener((new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_addNewsFragment);
           }
       }));
        // Inflate the layout for this fragment
        return view;
    }
}