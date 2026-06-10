package com.example.your_campus_app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.your_campus_app.databinding.ActivityMainBinding;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding mVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVB = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mVB.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            if (mVB.bottomNavBar != null) {
                NavigationUI.setupWithNavController(mVB.bottomNavBar, navController);
            }
            navController.addOnDestinationChangedListener(this);
        }
    }

    @Override
    public void onDestinationChanged(
            @NonNull NavController navController,
            @NonNull NavDestination navDestination,
            @Nullable Bundle bundle) {
        Map<String, NavArgument> args = navDestination.getArguments();

        new Handler(Looper.getMainLooper()).post(() -> {
            NavArgument hideBottomNavArg = args.get("hideBottomNav");
            boolean hideBottomNav = hideBottomNavArg != null && Objects.equals(hideBottomNavArg.getDefaultValue(), true);

            if (mVB.bottomNavBar != null) {
                mVB.bottomNavBar.setVisibility(hideBottomNav ? View.GONE : View.VISIBLE);
            }
        });
    }

    public static void navigateToNewStartDestination(Activity activity, Integer transition, Integer fragmentId) {
        try {
            NavController navController = Navigation.findNavController(activity, R.id.fragmentContainerView);
            if (!isValidFragmentId(navController, fragmentId))
                return;
            navController.navigate(transition);
            setStartDestination(navController, fragmentId);
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: " + e.getMessage());
        }
    }

    private static boolean isValidFragmentId(NavController navController, Integer fragmentId) {
        NavDestination currentDestination = navController.getCurrentDestination();
        return currentDestination != null && !Objects.equals(currentDestination.getId(), fragmentId);
    }

    public static void setStartDestination(NavController navController, Integer fragmentId) {
        NavInflater inflater = navController.getNavInflater();
        NavGraph navGraph = inflater.inflate(R.navigation.main_navigation_graph);
        navGraph.setStartDestination(fragmentId);
        navController.setGraph(navGraph);
        NavDestination currentDestination = navController.getCurrentDestination();
        if (currentDestination != null) {
            Log.d(TAG, "setStartDestination(): new start fragment: " + currentDestination.getLabel());
        }
    }
}
