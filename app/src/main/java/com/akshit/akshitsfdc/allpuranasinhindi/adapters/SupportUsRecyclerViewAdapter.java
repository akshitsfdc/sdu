package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SplashActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.PictureFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SupporterModel;

import java.util.Date;
import java.util.List;

public class SupportUsRecyclerViewAdapter extends RecyclerView.Adapter<SupportUsRecyclerViewAdapter.ViewHolder> {


    private AppCompatActivity activity;
    private List<SupporterModel> supporterModels;
    private BaseActivity baseActivity;

    public SupportUsRecyclerViewAdapter(AppCompatActivity activity, List<SupporterModel> supporterModels){
        this.activity = activity;
        this.supporterModels = supporterModels;
        this.baseActivity = (BaseActivity)activity;
    }


    @NonNull
    @Override
    public SupportUsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_us_list_layout, parent, false);
        return new SupportUsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupportUsRecyclerViewAdapter.ViewHolder holder, int position) {

        SupporterModel supporterModel = supporterModels.get(position);

        String amountText =  activity.getString(R.string.rs)+ " "+supporterModel.getAmount();
        holder.amount.setText(amountText);
        baseActivity.showRemoteImage(supporterModel.getPhotoUrl(), holder.profileImage, R.drawable.ic_profile_placeholder, holder.profileProgress);
        holder.name.setText(supporterModel.getName());
        holder.supportTime.setText(baseActivity.utils.getLocalTimeString(supporterModel.getSupportTimeStamp()));

        setEmailField(supporterModel, holder.email);

        if(supporterModel.getMsg() != null && supporterModel.getMsg().length() > 0){
            holder.userMessage.setText(supporterModel.getMsg());
        }
        if(supporterModel.isPrimeMember()){
            holder.primeIndicator.setVisibility(View.VISIBLE);
        }else {
            holder.primeIndicator.setVisibility(View.GONE);
        }

        holder.profileImage.setOnClickListener(v -> {
            if(supporterModel.getPhotoUrl() == null){
                openImage("");
            }else {
                openImage(supporterModel.getPhotoUrl());
            }

        });
    }

    private void setEmailField(SupporterModel supporterModel, TextView textView){
        if(baseActivity.utils.checkValidString(supporterModel.getEmail())){
            textView.setText(baseActivity.utils.getMaskedEmail(supporterModel.getEmail()));
        }else if(baseActivity.utils.checkValidString(supporterModel.getPhone())){
            textView.setText(baseActivity.utils.getMaskedPhoneNumber(supporterModel.getPhone()));
        }else {
            textView.setText("xxxxx@gmail.com");
        }
    }
    private void openImage(String url){

        baseActivity.routing.openFragmentOver(new PictureFragment(activity,  url ), activity.getString(R.string.picture_tag));

    }



    @Override
    public int getItemCount() {
        return this.supporterModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage, primeIndicator;
        private TextView name, email, supportTime, amount, userMessage;
        private ProgressBar profileProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            supportTime = itemView.findViewById(R.id.supportTime);
            amount = itemView.findViewById(R.id.amount);
            userMessage = itemView.findViewById(R.id.userMessage);
            profileProgress = itemView.findViewById(R.id.profileProgress);
            primeIndicator = itemView.findViewById(R.id.primeIndicator);
        }
    }
}
