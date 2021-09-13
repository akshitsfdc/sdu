package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.PictureFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.UpdateUserDataFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.helpers.UnityAdsListeners;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AdsInfoModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CurrentDownloadingModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserDataModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.yalantis.ucrop.UCrop;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle toggle;
    public static boolean DOWNLOAD_IN_PROGRESS = false;
    public static int CURRENT_DOWNLOAD_PROGRESS = 0;
    public static CurrentDownloadingModel CURRENT_DOWNLOADING_MODEL;

    private Uri mInvitationUrl;
    protected NavigationView navigationView;
    protected View headerView;

    private View profileNavView;
    private View loginNavView;
    private View loginButton;
    private ImageView profileImage;
    private TextView name;
    private TextView userId;
    private ImageView primeIndicator;
    private TextView editProfile;
    private  MenuItem  logout, login, supportView;
    private UpdateUserDataFragment updateUserDataFragment;
    private HomeActivity homeBaseActivity;
    private Dialog dialog;
    private boolean initCompleted;



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        headerView = navigationView.getHeaderView(0);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        logout = nav_Menu.findItem(R.id.nav_logout);
        login = nav_Menu.findItem(R.id.nav_login);
        supportView = nav_Menu.findItem(R.id.nav_support);

        navigationView.setNavigationItemSelectedListener(this);

        initNavigationView();

        initClassWithData();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void initClassWithData(){


        if (homeBaseActivity == null){
            return;
        }

        if(!initCompleted && SplashActivity.APP_INFO == null && SplashActivity.USER_DATA == null){
            loadAppInfoData();
        }else if(!initCompleted && SplashActivity.APP_INFO != null && SplashActivity.USER_DATA != null) {
            checkUpdate();
            changePrime();
            initCompleted = true;
        }else if(!initCompleted && SplashActivity.APP_INFO != null && SplashActivity.USER_DATA == null){
            loadAppInfoData();
        }else if(!initCompleted && SplashActivity.APP_INFO == null && SplashActivity.USER_DATA != null){
            loadAppInfoData();
        }


    }

    protected void setBaseActivity(AppCompatActivity baseActivity){
        if(baseActivity instanceof  HomeActivity){
            homeBaseActivity = (HomeActivity) baseActivity;
            showBannerAd();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

       if (id == R.id.nav_home) {
            //startPollFishSurvey();
           if (!(this instanceof HomeActivity)) {
               navigateToHome();
           }
        }else if(id == R.id.nav_all){
           navigateToSoftPuranaDashBoard("all");
       }else if(id == R.id.nav_puran){
                navigateToSoftPuranaDashBoard("purans");
        }else if(id == R.id.nav_veda){
               navigateToSoftPuranaDashBoard("vedas");
       }else if(id == R.id.nav_hdBook){
               navigateToSoftPuranaDashBoard("hd");
       }else if(id == R.id.nav_festive){
               navigateToSoftPuranaDashBoard("festive");

       }else if(id == R.id.nav_geeta){
               navigateToSoftPuranaDashBoard("geetamrit");
       }else if(id == R.id.nav_others){
               navigateToSoftPuranaDashBoard("more");
       }else if(id == R.id.nav_prime){
           if (!(this instanceof PrimeActivity)) {
               navigateToPrimeActivity();
           }
       }else if(id == R.id.nav_favorite){
           navigateToSoftPuranaDashBoard(getString(R.string.favorite_key));
       }else if(id == R.id.nav_offline){
           navigateToSoftPuranaDashBoard(getString(R.string.offline_key));
       }else if(id == R.id.nav_share){
           sendInvitation();
       }else if(id == R.id.daily_motivation){
           routing.appendParams("fromNotification", false);
           routing.navigate(TodayMotivationActivity.class, false);
       }else if(id == R.id.nav_contact){
           if (!(this instanceof ContactUsActivity)) {
               navigateToContactUs();
           }
       }else if(id == R.id.nav_updesh){
           if (!(this instanceof UpdeshActivity)) {
               navigateToUpdesh();
           }
       }else if(id == R.id.nav_support){
           if (!(this instanceof SupportUsActivity)) {
               navigateToSupportUs();
           }
       }else if(id == R.id.nav_logout){
           logout();
       }else if(id == R.id.nav_login){
           routing.navigate(LoginActivity.class, false);
       }

        return true;
    }
    private void logout(){
        FirebaseUser user = fireAuthService.getCurrentUser();
        if(user != null){
            fireAuthService.logoutUser();
            drawer.closeDrawers();
            SplashActivity.USER_DATA = null;
            routing.navigate(HomeActivity.class, true);

        }
    }
    private void navigateToSupportUs(){
        routing.navigate(SupportUsActivity.class, false);
        //finish();

    }
    private void navigateToUpdesh(){
        routing.navigate(UpdeshActivity.class, false);
        //finish();

    }
    private void navigateToContactUs(){
        routing.navigate(ContactUsActivity.class, false);
        //finish();

    }

    private void navigateToHome(){

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        super.onBackPressed();

    }
    @Override
    public void onBackPressed() {


        boolean frgRem = removeFragmentFirst();

        if(frgRem){
            return;
        }

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if(homeBaseActivity != null){
                exitWarning();
            }else {
                super.onBackPressed();
            }
        }
    }


    private void navigateToPrimeActivity(){
        routing.clearParams();
        routing.appendParams("fromHome", true);
        routing.navigate(PrimeActivity.class, true);
    }
    public void navigateToSoftPuranaDashBoard(String type){

       routing.clearParams();
       routing.appendParams("singleBook", false);
       routing.appendParams("fromHome", false);
       routing.appendParams("type", type);
       routing.navigate(SoftPuranaDashboardActivity.class, false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try{
            if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
                Uri result =   UCrop.getOutput(data);

                if(updateUserDataFragment != null){
                    updateUserDataFragment.setUri(result);
                }

            } else if (requestCode == UCrop.RESULT_ERROR) {
                uiUtils.showShortErrorSnakeBar("Something went wrong, try again later!");
            }
        }catch (Exception e){
            e.printStackTrace();
//            fileUtils.showShortToast("Something went wrong. Try again");
        }
    }
    private void changePrime(){

        try{
            if(SplashActivity.USER_DATA == null || SplashActivity.APP_INFO == null){
                return;
            }
            if(SplashActivity.USER_DATA.isPrimeMember()){
                headerView.findViewById(R.id.primeIndicator).setVisibility(View.VISIBLE);
            }else{
                headerView.findViewById(R.id.primeIndicator).setVisibility(View.GONE);
            }

            if(!SplashActivity.USER_DATA.isPrimeMember() && SplashActivity.APP_INFO.isAllowPrime()){
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.nav_prime).setVisible(true);
            }else if(SplashActivity.USER_DATA.isPrimeMember() || !SplashActivity.APP_INFO.isAllowPrime()){
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.nav_prime).setVisible(false);
            }
//            if(homeBaseActivity != null){
//                homeBaseActivity.initBanner();
//            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }
    private void initNavigationView(){

        profileNavView = headerView.findViewById(R.id.profileNavView);
        loginNavView = headerView.findViewById(R.id.loginNavView);
        loginButton = headerView.findViewById(R.id.loginButton);
        profileImage = headerView.findViewById(R.id.profileImage);
        name = headerView.findViewById(R.id.name);
        userId = headerView.findViewById(R.id.userId);
        primeIndicator = headerView.findViewById(R.id.primeIndicator);
        editProfile = headerView.findViewById(R.id.editProfile);

        loginButton.setOnClickListener(v -> {
            routing.navigate(LoginActivity.class, false);
        });
        editProfile.setOnClickListener(v -> {
            openUpdateUserFragment(true);
        });
        profileImage.setOnClickListener(v -> {
            openImageFragment();
        });
        name.setOnClickListener(v -> {
            openUpdateUserFragment(false);
        });

        updateProfileData();
    }
    private void openImageFragment(){
        FirebaseUser user = fireAuthService.getCurrentUser();
        if(user != null && SplashActivity.USER_DATA != null){
            routing.openFragmentOver(new PictureFragment(this,  getPhotoUrl() ), getString(R.string.picture_tag));
        }else {
            routing.openFragmentOver(new PictureFragment(this,  "" ), "picture_frg");
        }
    }
    private void openUpdateUserFragment(boolean pictureUpdate){

        FirebaseUser user = fireAuthService.getCurrentUser();


        if(user != null && SplashActivity.USER_DATA != null){

            if(pictureUpdate){
                updateUserDataFragment = new UpdateUserDataFragment(this, pictureUpdate, getPhotoUrl());
                routing.openFragmentOver(updateUserDataFragment, getString(R.string.user_data_update_tag));
            }else {
                updateUserDataFragment = new UpdateUserDataFragment(this, pictureUpdate, user.getDisplayName());
                routing.openFragmentOver(updateUserDataFragment, getString(R.string.user_data_update_tag));
            }

        }
    }
    public void updateProfileData(){

        FirebaseUser user = fireAuthService.getCurrentUser();

        if(user == null){

            profileNavView.setVisibility(View.GONE);
            loginNavView.setVisibility(View.VISIBLE);

            logout.setVisible(false);
            login.setVisible(true);

        }else{


            logout.setVisible(true);
            login.setVisible(false);

            if(SplashActivity.USER_DATA == null){
                return;
            }
            String nameStr = SplashActivity.USER_DATA.getName();

            if(utils.checkValidString(nameStr)){
                name.setText(nameStr);
            }else {
                name.setText(getString(R.string.default_on_name));
            }

            String phoneStr = SplashActivity.USER_DATA.getPhone();

            if(utils.checkValidString(phoneStr)){
                userId.setText(phoneStr);
            }else {
                String emailStr = user.getEmail();
                userId.setText(emailStr);
            }

            showRemoteImage( getPhotoUrl(), profileImage);
            changePrime();
        }
    }

    private String getPhotoUrl(){
        FirebaseUser user = fireAuthService.getCurrentUser();
        String photoUrl = SplashActivity.USER_DATA.getPhotoUrl();
        if(user != null){
            return photoUrl==null?"":photoUrl;
        }else {
            return "";
        }

    }


//    public void createLink() {
//
//        String link = "https://allpuranasinhindi.page.link";
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse(link))
//                .setDomainUriPrefix("https://allpuranasinhindi.page.link")
//                .setAndroidParameters(
//                        new DynamicLink.AndroidParameters.Builder("com.akshit.akshitsfdc.allpuranasinhindi")
//                                .setMinimumVersion(1)
//                                .build())
//                .setIosParameters(
//                        new DynamicLink.IosParameters.Builder("com.akshit.akshitsfdc.allpuranasinhindi")
//                                .setAppStoreId("123456789")
//                                .setMinimumVersion("1.0.1")
//                                .build())
//                .buildShortDynamicLink()
//                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
//                    @Override
//                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
//                        mInvitationUrl = shortDynamicLink.getShortLink();
//                        Log.d(TAG, "onSuccess: mInvitationUrl : \n"+mInvitationUrl);
//                        sendInvitation();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                new FileUtils(MainActivity.this).showShortToast("Something went wrong try again later!");
//                e.printStackTrace();
//            }
//        });
//        // [END ddl_referral_create_link]
//    }

//    for ads
    private void showBannerAd(){

        if(SplashActivity.ADS_INFO_MODEL == null){
            loadAdsData();
        }else {
            if(SplashActivity.ADS_INFO_MODEL.isShowGoogleAds()){
                if(homeBaseActivity != null){
                    homeBaseActivity.initBanner();
                }
            }else if(SplashActivity.ADS_INFO_MODEL.isShowUnityAds()){
                initUnity();
            }

        }
    }
    private void loadAdsData(){

        Log.d(TAG, "loadAdsData: >> loadAdsData");

        fireStoreService.getData("app_info", "ads_info")
        .addOnSuccessListener(documentSnapshot -> {
            SplashActivity.ADS_INFO_MODEL = documentSnapshot.toObject(AdsInfoModel.class);
            if(SplashActivity.ADS_INFO_MODEL == null){
                return;
            }

            if(SplashActivity.ADS_INFO_MODEL.isShowUnityAds()){
                initUnity();
            }else if(SplashActivity.ADS_INFO_MODEL.isShowGoogleAds()){
                initGoogleAds();
            }

        })
        .addOnFailureListener(e -> {
            Log.d(TAG, "loadAdsData: "+e.getMessage());
        });
    }

    public void initUnity(){
        if(UnityAds.isInitialized()){
            if(homeBaseActivity != null){
                homeBaseActivity.initBanner();
            }
            return;
        }

        UnityAds.initialize(this, getString(R.string.unity_game_id), false, new UnityAdsListeners(){
            @Override
            public void onInitializationComplete() {
                if(homeBaseActivity != null){
                    homeBaseActivity.initBanner();
                }
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
                Log.d(TAG, "onInitializationFailed: >> "+s);
            }
        });

    }
    public void initGoogleAds(){

        MobileAds.initialize(this, initializationStatus -> {
            if(homeBaseActivity != null){
                homeBaseActivity.initBanner();
            }
        });
    }


    public void sendInvitation() {

        try {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey I am using this amazing app: "+getString(R.string.app_name));
            String shareMessage = "Hey I am using this amazing app: \n\nInstall "+getString(R.string.app_name)+" app from store and login.\n\nThis app provides a huge library of Hindu Spiritual books at one place.\n\nThis is very portable to use, you can read any book anytime at one place.\n\n\n"
                    ;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
            new FileUtils(MainActivity.this).showShortToast("Something went wrong try again later!");
        }
    }


    public boolean removeFragmentFirst(){

        boolean fragmentRemoved;

        fragmentRemoved = routing.hideFragment(getString(R.string.picture_tag));
        if(!fragmentRemoved){
            fragmentRemoved = routing.hideFragment(getString(R.string.user_data_update_tag));
        }
        if(!fragmentRemoved){
            fragmentRemoved = routing.hideFragment(getString(R.string.search_tag));
        }

        return fragmentRemoved;
    }

    private void loadAppInfoData(){

        if(SplashActivity.APP_INFO == null || !initCompleted){

            showLoading();
            fireStoreService.snapshotOnDocument("app_info", "meta_data_new", (value, error) -> {

                hideLoading();
                if (error != null) {

                    return;
                }
                if (value != null && value.exists()) {
                    SplashActivity.APP_INFO = value.toObject(AppInfo.class);
                    changePrime();
                    if(SplashActivity.APP_INFO != null && SplashActivity.APP_INFO.isShowSupportUs()){
                        supportView.setVisible(true);
                    }else {
                        supportView.setVisible(false);
                    }
                    if(!initCompleted){
                        checkUpdate();
                        initCompleted = true;
                    }

                }
            });
        }

        FirebaseUser user = fireAuthService.getCurrentUser();

        Log.d(TAG, "loadAppInfoData: SplashActivity.USER_DATA : "+SplashActivity.USER_DATA+" user : "+user);

        if(SplashActivity.USER_DATA == null && user != null){

            fireStoreService.snapshotOnDocument("user_data", user.getUid(), (value, error) -> {

                Log.d(TAG, "loadAppInfoData: snapshotOnDocument >> called");
                if (error != null) {
                    return;
                }

                if (value != null && value.exists()) {



                    if(SplashActivity.USER_DATA == null){
                        SplashActivity.USER_DATA = value.toObject(UserDataModel.class);
                    }else {
                        UserDataModel userDataModel = value.toObject(UserDataModel.class);
                        if(userDataModel != null){
                            utils.updateUser(userDataModel);
                        }

                    }
                    if(SplashActivity.USER_DATA.isPrimeMember()){
                        if(homeBaseActivity != null){
                            homeBaseActivity.hideAds();
                        }
                    }
                    checkTokenUpdate(SplashActivity.USER_DATA);

                    updateProfileData();


                }
            });
        }


    }
    private void checkTokenUpdate(UserDataModel userDataModel){
        fireAuthService.getDeviceToken()
                .addOnSuccessListener(token -> {
                    if(!TextUtils.equals(userDataModel.getDeviceToken(), token)){
                        Map<String, Object> map = new HashMap<>();
                        map.put("deviceToken",token);
                        fireStoreService.updateData("user_data", userDataModel.getuId(), map);
                    }
                });
    }
    private void exitWarning(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        killTheApp();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void killTheApp(){
        super.onBackPressed();
    }

    private void checkUpdate(){

        if(SplashActivity.APP_INFO == null){
            return;
        }

        String version = "bad";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }


        final String finalVersion = version;

        if(homeBaseActivity != null){
            homeBaseActivity.setBanner(finalVersion);
        }


        if(SplashActivity.APP_INFO.isCheckVersionPopup()){
            if(!TextUtils.equals(finalVersion, SplashActivity.APP_INFO.getLatestVersion()) || SplashActivity.APP_INFO.isAbortApp()) {
                showUpdateDialog(SplashActivity.APP_INFO);
            }
        }

    }
    private void showUpdateDialog(AppInfo appInfoDB){

        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(appInfoDB.getHeader());
            builder.setMessage(appInfoDB.getFeature());

            builder.setPositiveButton(appInfoDB.getOkText(), (dialog, which) -> {

                if(appInfoDB.isAbortApp()){
                    killTheApp();
                }else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("market://details?id=com.akshit.akshitsfdc.allpuranasinhindi")));
                    dialog.dismiss();
                }
            });
            if(!appInfoDB.isAbortApp()){
                builder.setNegativeButton(appInfoDB.getCancelText(), (dialog, which) -> {
                    if(appInfoDB.isForceUpdate()){
                        dialog.cancel();
                        killTheApp();
                    }else{
                        dialog.cancel();
                    }
                });
            }

            try{
                dialog = builder.create();

                if(appInfoDB.isForceUpdate()){
                    dialog.setCancelable(false);
                }else {
                    dialog.setCancelable(true);
                }
                dialog.show();
            }catch(Exception e){
                e.printStackTrace();
            }

            if(appInfoDB.isAbortApp()){
                dialog.setCancelable(false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
                );
                if (noConnectivity) {
                    uiUtils.showShortErrorSnakeBar("You are disconnected!");
                    internetConnected = false;
                    if(!initCompleted && homeBaseActivity != null){
                        homeBaseActivity.noInternet();
                    }
                } else {
                    internetConnected = true;
//                    findViewById(R.id.internetLostView).setVisibility(View.GONE);
                    if(!initCompleted && homeBaseActivity != null){
                        loadAppInfoData();
                    }
                    if(homeBaseActivity != null){
                        homeBaseActivity.internetAvailable();
                    }

                }
            }
        }
    };
}
