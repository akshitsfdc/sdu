package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.MainActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.service.StorageService;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.UIUtils;
import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateUserDataFragment extends BaseFragment {


    private boolean isPhotoUpdate;
    private String oldName;
    private View progressIndicatorLayout;
    private View profilePictureLayout, nameOuterLayout, parent;
    private Button pictureSelectButton, uploadPictureButton, nameUpdateButton;
    private EditText nameField;
    private StorageService storageService;
    private ImageView profileImage;
    private ProgressBar uploadProgressbar;
    private String downloadUrl;
    private Uri profilePicUri;
    private UIUtils uiUtils;
    private ActivityResultLauncher<Intent> cropperLauncher;



    public UpdateUserDataFragment(AppCompatActivity activity, boolean isPhotoUpdate, String oldName){
        this.isPhotoUpdate = isPhotoUpdate;
        this.oldName = oldName;
        currentActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cropperLauncher =
                registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            if(result.getData() != null){
                                Uri uri = result.getData().getData();
                                if(uri != null){
                                    startCrop(uri);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_update_user_data, container, false);
        objectCreations(view);
        objectInitializations();
        setEvenListeners();
        return view;
    }

    private void startCrop(Uri uri){
        try {
            currentActivity.getCacheDir().delete();
        }catch (Exception e){
            e.printStackTrace();
        }

        String destinationFileName = ""+new Date().getTime();
        destinationFileName +=".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(currentActivity.getCacheDir(), destinationFileName)));

        uCrop.withMaxResultSize(512, 512);
        uCrop.withAspectRatio(1, 1);
        uCrop.withOptions(getCropOption());
        uCrop.start(currentActivity, UCrop.REQUEST_CROP);


    }
    private UCrop.Options getCropOption(){
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(70);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setShowCropGrid(false);
        options.setCircleDimmedLayer(true);

        return options;
    }
    @Override
    protected void objectCreations(View view) {

        progressIndicatorLayout = view.findViewById(R.id.progressIndicatorLayout);
        profilePictureLayout = view.findViewById(R.id.profilePictureLayout);
        nameOuterLayout = view.findViewById(R.id.nameOuterLayout);
        pictureSelectButton = view.findViewById(R.id.pictureSelectButton);

        uploadPictureButton = view.findViewById(R.id.uploadPictureButton);
        nameUpdateButton = view.findViewById(R.id.nameUpdateButton);
        nameField = view.findViewById(R.id.nameField);
        profileImage = view.findViewById(R.id.profileImage);
        uploadProgressbar = view.findViewById(R.id.uploadProgressbar);
        parent = view.findViewById(R.id.parent);

        storageService = new StorageService();
        uiUtils = new UIUtils(currentActivity);

        MainActivity homeActivity = (MainActivity)currentActivity;
        uiUtils.setSnakebarView(homeActivity.getSnakBarView(view.findViewById(R.id.snakebarLayout)));
    }

    @Override
    protected void objectInitializations() {
        if(isPhotoUpdate){
            profilePictureLayout.setVisibility(View.VISIBLE);
            nameOuterLayout.setVisibility(View.GONE);
        }else {
            profilePictureLayout.setVisibility(View.GONE);
            nameOuterLayout.setVisibility(View.VISIBLE);
            nameField.setText(oldName);
        }

    }

    @Override
    protected void setEvenListeners() {

        pictureSelectButton.setOnClickListener(v -> {
            try{
                openImageSelector();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        uploadPictureButton.setOnClickListener(v -> {
            uploadProfilePicture();
        });
        nameUpdateButton.setOnClickListener(v -> {
            updateName();
        });
        parent.setOnClickListener(v -> {
                hideSelf();
        });

    }
    private void hideSelf(){

        try {
            ((BaseActivity)currentActivity).routing.goBack();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void openImageSelector(){
        cropperLauncher.launch(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"));
    }
   public void setUri(Uri uri){
       profilePicUri = uri;
       profileImage.setImageURI(profilePicUri);
   }
    private boolean validateName(){
        boolean valid = true;
        String name = nameField.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            valid = false;
            nameField.setError("Required");
        }else {
            nameField.setError(null);
        }
        return valid;
    }
    private boolean validatePicture(){
        boolean valid = true;
        MainActivity homeActivity = (MainActivity)currentActivity;

        if(profilePicUri == null){
            valid = false;
            uiUtils.showLongErrorSnakeBar("Please select picture first");
        }
        return valid;
    }
    private void disableButtons(){
        uploadPictureButton.setEnabled(false);
        nameUpdateButton.setEnabled(false);
    }
    private void enableButtons(){
        uploadPictureButton.setEnabled(true);
        nameUpdateButton.setEnabled(true);
        nameUpdateButton.setText("Update Now");
        uploadPictureButton.setText("Upload Picture");
    }

    private void updateProfilePicture(String downloadUrl){

        MainActivity homeActivity = (MainActivity)currentActivity;
        FirebaseUser firebaseUser =  homeActivity.fireAuthService.getCurrentUser();

        homeActivity.fireAuthService.updatePhotoUrl(firebaseUser, downloadUrl)
        .addOnSuccessListener(aVoid -> {
            uiUtils.showLongSuccessSnakeBar("Profile picture updated successfully");
            enableButtons();
            homeActivity.fireAuthService.reloadCurrentUser();
            profilePicUri = null;
            savePicUrlToUserData(downloadUrl);
            hideSelf();
        })
        .addOnFailureListener(e -> {
            uiUtils.showShortErrorSnakeBar("Some error occurred please try again");
            enableButtons();
        });
    }
    private void savePicUrlToUserData(String picUrl){
        BaseActivity baseActivity = (BaseActivity) currentActivity;
        Map<String, Object> map = new HashMap<>();
        map.put("photoUrl", picUrl);
        baseActivity.fireStoreService.updateData("user_data", baseActivity.fireAuthService.getUserId() , map);
    }
    private void saveNameToUserData(String name){
        BaseActivity baseActivity = (BaseActivity) currentActivity;
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        baseActivity.fireStoreService.updateData("user_data", baseActivity.fireAuthService.getUserId() , map);
    }
    private void uploadProfilePicture(){

        if(!validatePicture()){
            return;
        }

        MainActivity homeActivity = (MainActivity)currentActivity;
        String picName = "profile_pic_"+homeActivity.fireAuthService.getUserId();

        String filePath = "profile_pictures/"+picName;

        disableButtons();
        progressIndicatorLayout.setVisibility(View.VISIBLE);
        storageService.uploadFile(
                filePath,
                homeActivity.utils.compressImageSmall(profilePicUri, currentActivity))
        .addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            uploadProgressbar.setProgress((int) progress);
        })
        .addOnCompleteListener(task -> {

            uploadPictureButton.setText("Updating Profile");
            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                downloadUrl = uri.toString();
                updateProfilePicture(downloadUrl);
            })
            .addOnFailureListener(e -> {
                enableButtons();
            });
            progressIndicatorLayout.setVisibility(View.GONE);
        })
        .addOnFailureListener(e -> {
            uiUtils.showShortErrorSnakeBar("Some error occurred please try again");
            progressIndicatorLayout.setVisibility(View.GONE);
            enableButtons();
        });

    }
    private void updateName(){
        if(!validateName()){
            return;
        }
        String name = nameField.getText().toString().trim();
        MainActivity homeActivity = (MainActivity)currentActivity;
        FirebaseUser firebaseUser =  homeActivity.fireAuthService.getCurrentUser();
        disableButtons();
        nameUpdateButton.setText("Updating name");

        homeActivity.fireAuthService.updateDisplayName(firebaseUser, name)
                .addOnSuccessListener(aVoid -> {
                    uiUtils.showLongSuccessSnakeBar("Your name has been updated successfully");
                    enableButtons();

                    homeActivity.fireAuthService.reloadCurrentUser();
                    nameField.setText("");
                    saveNameToUserData(name);
                    hideSelf();

                })
                .addOnFailureListener(e -> {
                    uiUtils.showShortErrorSnakeBar("Some error occurred please try again");
                    enableButtons();
                });
    }
}