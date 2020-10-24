package io.cobrowse.standalone.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import io.cobrowse.standalone.R;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.fragment_home_button_6_digits_code).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), io.cobrowse.ui.CobrowseActivity.class);
            getActivity().startActivity(intent);
        });
        root.findViewById(R.id.fragment_home_button_open_camera).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        ? R.id.action_nav_home_to_nav_camera
                        : R.id.action_nav_home_to_nav_legacy_camera);
        });
        return root;
    }
}