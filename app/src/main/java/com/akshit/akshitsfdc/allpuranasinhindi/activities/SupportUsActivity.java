package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SupportUsRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.SupportUsFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.UpdateUserDataFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.AppInfo;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SupporterModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SupportUsActivity extends MainActivity implements PaymentResultListener {

    private ImageView bgImageView;
    private Button supportButton;
    private int selectedAmount;
    private String msg;
    private RecyclerView recyclerView;
    private ListenerRegistration supporterListener;
    private List<SupporterModel> supporterModels;
    private SupportUsRecyclerViewAdapter adapter;
    private boolean englishActive = false;
    private View infoCard;
    private TextView infoTextView;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(supporterListener != null){
            try {
                supporterListener.remove();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_us);

        uiUtils.setSnakebarView(getSnakBarView(findViewById(R.id.snakebarLayout)));

        initObject();
        setEventListeners();
        setupViews();

        loadSupporters();
    }

    private void initObject(){
        bgImageView = findViewById(R.id.bgImageView);
        supportButton = findViewById(R.id.supportButton);
        recyclerView = findViewById(R.id.recyclerView);
        infoCard = findViewById(R.id.infoCard);
        infoTextView = findViewById(R.id.infoTextView);
        supporterModels = new ArrayList<>();
        adapter = new SupportUsRecyclerViewAdapter(this, supporterModels);
        Checkout.preload(this);

    }
    private void setEventListeners(){
        supportButton.setOnClickListener(v -> {
            if(fireAuthService.isUserLoggedIn()){
                openSupportUsFragment();
            }else {
                routing.navigate(LoginActivity.class, false);
            }

        });
        infoCard.setOnClickListener(v -> {
            if(englishActive){
                infoTextView.setText(getString(R.string.support_us_hn));
                englishActive = false;
            }else {
                infoTextView.setText(getString(R.string.support_us_en));
                englishActive = true;
            }
        });
    }
    private void setupViews(){
        showRemoteImage(SplashActivity.APP_INFO.getSupportHeaderBg(), bgImageView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adapter);
    }
    private void openSupportUsFragment(){

        routing.openFragmentOver(new SupportUsFragment(this), getString(R.string.support_us_tag));
    }

    private void loadSupporters(){

        showLoading();
        supporterListener = fireStoreService.getRecentSupporters(SplashActivity.APP_INFO.getSupportersLimit(), (value, error) -> {
            hideLoading();
            if (error != null) {
                return;
            }


            if (value != null) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            addSupporterModel(dc.getDocument());
                            break;
                        case MODIFIED:
                            updateSupporterModel(dc.getDocument());
                            break;
                        case REMOVED:
                            removeSupporterModel(dc.getDocument());
                            break;
                    }
                }
                adapter.notifyDataSetChanged();


            }

        });
    }
    private void addSupporterModel(QueryDocumentSnapshot queryDocumentSnapshot){
        SupporterModel supporterModel = queryDocumentSnapshot.toObject(SupporterModel.class);
        supporterModels.add(supporterModel);
        Collections.sort(supporterModels, Collections.reverseOrder());
    }
    private void updateSupporterModel(QueryDocumentSnapshot queryDocumentSnapshot){
        SupporterModel supporterModel = queryDocumentSnapshot.toObject(SupporterModel.class);
        for (SupporterModel supporter :supporterModels
             ) {
            if(supporterModel.getSupportTimeStamp() == supporter.getSupportTimeStamp()){
                updateSupporter(supporter, supporterModel);
            }
        }
    }
    private void removeSupporterModel(QueryDocumentSnapshot queryDocumentSnapshot){
        SupporterModel supporterModel = queryDocumentSnapshot.toObject(SupporterModel.class);
        for (SupporterModel supporter :supporterModels
        ) {
            if(supporterModel.getSupportTimeStamp() == supporter.getSupportTimeStamp()){
                supporterModels.remove(supporter);
                return;
            }
        }
    }
    private void updateSupporter(SupporterModel supporterModel, SupporterModel supporterModelUpdate){

        supporterModel.setAmount(supporterModelUpdate.getAmount());//
        supporterModel.setMsg(supporterModelUpdate.getMsg());//
        supporterModel.setSupportTimeStamp(supporterModelUpdate.getSupportTimeStamp());//
        supporterModel.setName(supporterModelUpdate.getName());//
        supporterModel.setEmail(supporterModelUpdate.getEmail());//
        supporterModel.setCreatedTime(supporterModelUpdate.getCreatedTime());//
        supporterModel.setPhone(supporterModelUpdate.getPhone());//
        supporterModel.setPhotoUrl(supporterModelUpdate.getPhotoUrl());//
        supporterModel.setPrimeMember(supporterModelUpdate.isPrimeMember());//
        supporterModel.setuId(supporterModelUpdate.getuId());//
    }
    public void startPayment(int amount, String msg){
        this.selectedAmount = amount;
        this.msg = msg;

        final Checkout co = new Checkout();

        try {
            int total = this.selectedAmount;


//            co.setKeyID(SplashActivity.APP_INFO.getPaymentApiProduction());
            co.setKeyID(SplashActivity.APP_INFO.getPaymentApiSandbox());


            JSONObject options = new JSONObject();
//            options.put("key", SplashActivity.APP_INFO.getPaymentApiProduction());
            options.put("key", SplashActivity.APP_INFO.getPaymentApiSandbox());
            options.put("name", getString(R.string.app_name));

            options.put("currency", "INR");
            options.put("amount", String.valueOf(total*100));

            JSONObject preFill = new JSONObject();
            if(SplashActivity.USER_DATA != null){
                preFill.put("email", SplashActivity.USER_DATA.getEmail());
                preFill.put("contact", SplashActivity.USER_DATA.getPhone());
            }

            options.put("prefill", preFill);

            co.open(this, options);
        } catch (Exception e) {
            uiUtils.showShortErrorSnakeBar("Error in payment, please try again");
            e.printStackTrace();
        }
    }
    @Override
    public void onPaymentSuccess(String s) {

        showLoading();
        SupporterModel supporterModel = getSupporterModel(this.selectedAmount, this.msg);
        fireStoreService.setData("supporters", ""+supporterModel.getSupportTimeStamp(), supporterModel)
        .addOnSuccessListener(aVoid -> {
            uiUtils.showLongSuccessSnakeBar(getString(R.string.sanatan_welcome_msg));
            hideLoading();
        })
        .addOnFailureListener(e -> {
            uiUtils.showShortErrorSnakeBar(getString(R.string.sanatana_error_msg));
            hideLoading();
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        uiUtils.showShortErrorSnakeBar("Error occurred in payment, please try again");
    }

    private SupporterModel getSupporterModel(int amount, String msg){

        SupporterModel supporterModel = new SupporterModel();

        supporterModel.setAmount(amount);//
        supporterModel.setMsg(msg);//
        supporterModel.setSupportTimeStamp(new Date().getTime());//
        supporterModel.setName(SplashActivity.USER_DATA.getName());//
        supporterModel.setEmail(SplashActivity.USER_DATA.getEmail());//
        supporterModel.setCreatedTime(SplashActivity.USER_DATA.getCreatedTime());//
        supporterModel.setPhone(SplashActivity.USER_DATA.getPhone());//
        supporterModel.setPhotoUrl(SplashActivity.USER_DATA.getPhotoUrl());//
        supporterModel.setPrimeMember(SplashActivity.USER_DATA.isPrimeMember());//
        supporterModel.setuId(SplashActivity.USER_DATA.getuId());//

        return supporterModel;
    }
}