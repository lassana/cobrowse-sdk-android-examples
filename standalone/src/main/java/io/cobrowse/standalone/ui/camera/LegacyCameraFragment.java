package io.cobrowse.standalone.ui.camera;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import io.cobrowse.standalone.R;

public class LegacyCameraFragment extends Fragment {
    private final int REQUEST_CODE_PERMISSIONS = 20;
    private final String[] REQUIRED_PERMISSIONS = new String [] { Manifest.permission.CAMERA };

    private LegacyCameraViewModel mViewModel;
    private CameraView cameraView;

    public static LegacyCameraFragment newInstance() {
        return new LegacyCameraFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_legacy_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView = view.findViewById(R.id.fragment_legacy_camera_preview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LegacyCameraViewModel.class);
        if (!allPermissionsGranted()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allPermissionsGranted()) {
            cameraView.start();
        }
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                if (isResumed()) {
                    cameraView.start();
                }
            } else {
                Toast.makeText(getActivity(),
                        R.string.fragment_camera_error_permissions_not_granter,
                        Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getView()).popBackStack();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String next : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), next) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}