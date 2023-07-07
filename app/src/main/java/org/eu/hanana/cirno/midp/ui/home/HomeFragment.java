package org.eu.hanana.cirno.midp.ui.home;

import static org.eu.hanana.cirno.midp.Helper.FILE_PICKER_REQUEST_CODE;
import static org.eu.hanana.cirno.midp.Helper.getpermision;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.StorageChooser;

import org.eu.hanana.cirno.midp.CAudioRecorder;
import org.eu.hanana.cirno.midp.Helper;
import org.eu.hanana.cirno.midp.MainActivity;
import org.eu.hanana.cirno.midp.R;
import org.eu.hanana.cirno.midp.databinding.FragmentHomeBinding;

import android.app.FragmentManager;

import java.io.CharArrayReader;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        binding.button6.setOnClickListener(v -> {
            if (binding.editTextText.getText().toString().equals("")){
                MainActivity.mainActivity.show_toast(getString(R.string.emptyV));
                return;
            }
            if (!getpermision(Manifest.permission.RECORD_AUDIO,getActivity()))
                return;

            if (Helper.cAudioRecorder!=null&&Helper.cAudioRecorder.isRecording) {
                Helper.stopRec();
                binding.button6.setText(R.string.record);
                Helper.records.put(binding.editTextText.getText().toString(),Helper.cAudioRecorder.recordedAudioAsBytes);
                binding.editTextText.setText("");
            }else {
                Helper.startRec();
                binding.button6.setText(R.string.stop);
            }
            binding.button7.setEnabled(!Helper.cAudioRecorder.isRecording);
            MainActivity.mainActivity.binding.getRoot().findViewById(R.id.toolbar).setEnabled(!Helper.cAudioRecorder.isRecording);

        });
        binding.button7.setOnClickListener(v -> {
            if (binding.editTextText.getText().toString().equals("")){
                MainActivity.mainActivity.show_toast(getString(R.string.emptyV));
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!Helper.getpermision(Manifest.permission.READ_MEDIA_AUDIO,getActivity()))
                    if (!Helper.getpermision(Manifest.permission.READ_EXTERNAL_STORAGE,getActivity())) {
                        MainActivity.mainActivity.show_toast(MainActivity.mainActivity.getApplicationContext().getString(R.string.nper));
                        return;
                    }
            }else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
                    if (!Helper.getpermision(Manifest.permission.READ_EXTERNAL_STORAGE, getActivity())) {
                        MainActivity.mainActivity.show_toast(MainActivity.mainActivity.getApplicationContext().getString(R.string.nper));
                        return;
                    }
                } else {
                    if (!Helper.getpermision(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,getActivity())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        getContext().startActivity(intent);
                        return;
                    }
                }
            };
            Helper.getpermision(Manifest.permission.WRITE_EXTERNAL_STORAGE,getActivity());
            MainActivity.mainActivity.show_toast(MainActivity.mainActivity.getApplicationContext().getString(R.string.cfile));

            Content c = new Content();
            c.setCreateLabel(getActivity().getString(R.string.Create));
            c.setInternalStorageText(getActivity().getString(R.string.InternalStorage));
            c.setCancelLabel(getActivity().getString(R.string.Cancel));
            c.setSelectLabel(getActivity().getString(R.string.Select));
            c.setOverviewHeading(getActivity().getString(R.string.ChooseDrive));
            c.setFreeSpaceText(getActivity().getString(R.string.free));

            StorageChooser chooser = new StorageChooser.Builder()
                    .withActivity(MainActivity.mainActivity)
                    .withFragmentManager(getActivity().getFragmentManager())
                    .withMemoryBar(true)
                    .allowCustomPath(true)
                    .setType(StorageChooser.FILE_PICKER)
                    .disableMultiSelect()
                    .filter(StorageChooser.FileType.AUDIO)
                    .withContent(c)
                    .build();
// Show dialog whenever you want by
            chooser.show();
// get path that the user has chosen
            chooser.setOnSelectListener(path -> {
                Log.e("SELECTED_PATH", path);

            });
            binding.editTextText.setText("");
        });
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}