package com.pg.documentuploader.presentation;

import static com.pg.documentuploader.app.Constants.SHAREABLE_LINK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.auth.GoogleAuthException;
import com.pg.documentuploader.R;
import com.pg.documentuploader.app.callbacks.ActivityResultCallback;
import com.pg.documentuploader.app.callbacks.PermissionResultCallback;
import com.pg.documentuploader.databinding.ActivityMainBinding;
import com.pg.documentuploader.util.DialogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private MainViewModel mainViewModel;

    private ActivityResultLauncher<Intent> googleSigninLauncher;
    private ActivityResultLauncher<Intent> documentPickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        setFabClickListener();

        registerFileUploadingPermissionResult(this::openDocumentPicker);

        registerDocumentPickerLauncher(uri -> {
            try {
                handleSelectedDocument(uri);
            } catch (GoogleAuthException | IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /* registering document picker launcher */
    private void registerDocumentPickerLauncher(ActivityResultCallback callback) {
        documentPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    callback.onURISelected(data.getData());
                }
            }
        });
    }

    /**
     * registering uploading permission launcher
     */
    private void registerFileUploadingPermissionResult(PermissionResultCallback callback) {
        googleSigninLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                callback.onPermissionGranted();
                Toast.makeText(getBaseContext(), "Uploading Permission Granted", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestFileUploadingPermission() {
        googleSigninLauncher.launch(mainViewModel.getPermissionIntent(getBaseContext()));
    }


    /**
     * Setting up fab click listener
     */
    private void setFabClickListener() {
        binding.fab.setOnClickListener(view -> {
            if (mainViewModel.isUploadingPermissionGranted(getBaseContext())) openDocumentPicker();
            else requestFileUploadingPermission();
        });
    }

    /**
     * Launch activity so that, file with only mentioned mimeTypes files can be selected.
     */
    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        documentPickerLauncher.launch(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * It will start the worker and set observer on the worker to update UI.
     *
     * @param documentUri: will be sent to worker, so that it will get to know which file will be uploaded.
     */
    private void handleSelectedDocument(Uri documentUri) throws GoogleAuthException, IOException {
        UUID workID = mainViewModel.handleSelectedDocument(documentUri, getBaseContext());
        observerWorkersWithUUID(workID);
    }


    /**
     * It will observe the progress of the worker which is uploading the file.
     *
     * @param uuid: used to observe worker with the uuid.
     */
    private void observerWorkersWithUUID(UUID uuid) {
        LiveData<WorkInfo> workInfoLiveData = WorkManager.getInstance(this).getWorkInfoByIdLiveData(uuid);
        AlertDialog loadingDialog = DialogUtil.showProgressDialog(MainActivity.this);
        workInfoLiveData.observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();
                    switch (state) {
                        case ENQUEUED: {
                            loadingDialog.show();
                            break;
                        }
                        case SUCCEEDED: {
                            loadingDialog.dismiss();
                            try {
                                String fileLink = URLDecoder.decode(workInfo.getOutputData().getString(SHAREABLE_LINK), StandardCharsets.UTF_8.toString());
                                AlertDialog successDialog = DialogUtil.fileUploadedDialog(fileLink, MainActivity.this, (dialog1, which) -> startActivity(Intent.createChooser(mainViewModel.shareableLinkIntent(fileLink), "Share link")));
                                successDialog.show();
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                        case CANCELLED:
                        case FAILED: {
                            loadingDialog.dismiss();
                            break;
                        }
                        default: {
                            //no-op
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}



