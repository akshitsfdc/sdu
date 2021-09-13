package com.akshit.akshitsfdc.allpuranasinhindi.adapters;

import android.content.Intent;
import android.text.TextUtils;
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
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftBookViewActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.models.BookDisplaySliderModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayHorizontalRecyclerViewAdapter extends RecyclerView.Adapter<DisplayHorizontalRecyclerViewAdapter.ViewHolder>{

    private List<SoftCopyModel> softCopyModels;
    private AppCompatActivity activity;
    private String layoutType;
    private BaseActivity baseActivity;

    public DisplayHorizontalRecyclerViewAdapter(AppCompatActivity activity, List<SoftCopyModel> softCopyModels, String layoutType){

        this.activity = activity;
        this.softCopyModels = softCopyModels;
        this.layoutType = layoutType;
        this.baseActivity = (BaseActivity) activity;
    }

    public void extendList(List<SoftCopyModel> softCopyModels){
        this.softCopyModels.addAll(softCopyModels);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DisplayHorizontalRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;


        if(is1HLayout()){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_1h_layout, parent, false);
        }else if(is2HLayout()){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_2h_layout, parent, false);
        }else if(is3HLayout()){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_3h_layout, parent, false);
        }else if(isSquareLayout()){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_square_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_book_layout, parent, false);
        }


        return new DisplayHorizontalRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DisplayHorizontalRecyclerViewAdapter.ViewHolder holder, int position) {

        SoftCopyModel softCopyModel = softCopyModels.get(position);

        holder.title.setText(softCopyModel.getName());

        if(shouldShowHImage(softCopyModel)){
            baseActivity.showRemoteImage(softCopyModel.getHorizontalImage(),
                    holder.bookImage, R.drawable.book_placeholder, holder.progress);
        }else {
            baseActivity.showRemoteImage(softCopyModel.getCoverUri()==null?softCopyModel.getPicUrl():softCopyModel.getCoverUri().toString(),
                    holder.bookImage, R.drawable.book_placeholder, holder.progress);
        }


        if (softCopyModel.isFree()){
            holder.ribbonImage.setVisibility(View.GONE);
        }else {
            holder.ribbonImage.setVisibility(View.VISIBLE);
        }

        holder.bookImage.setOnClickListener(v -> navigateToView(softCopyModel));


    }

    private boolean shouldShowHImage(SoftCopyModel softCopyModel){
        if(is1HLayout() || is2HLayout() || is3HLayout()){
            if(softCopyModel.getHorizontalImage() == null){
                return false;
            }else if(softCopyModel.getHorizontalImage().length() == 0){
                return false;
            }else {
                return true;
            }
        }else {
            return false;
        }


    }

    private void navigateToView(SoftCopyModel softCopyModel){

        baseActivity.routing.clearParams();
        baseActivity.routing.appendParams("singleBook", true);
        baseActivity.routing.appendParams("softCopyModel", softCopyModel);
        baseActivity.routing.navigate(SoftPuranaDashboardActivity.class, false);

    }


    @Override
    public int getItemCount() {
        return this.softCopyModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView bookImage;
        private ProgressBar progress;
        private TextView title;
        private ImageView ribbonImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.bookImage);
            title = itemView.findViewById(R.id.title);
            progress = itemView.findViewById(R.id.progress);
            ribbonImage = itemView.findViewById(R.id.ribbonImage);
        }
    }

    private boolean is2HLayout(){

        List<String> typeList = Arrays.asList("2h1", "2h2", "2h3", "2h4", "2h5", "2h6", "2h7", "2h8", "2h9", "2h10");

        for (String s : typeList
             ) {
            if(TextUtils.equals(layoutType.toLowerCase(), s)){
                return true;
            }
        }
       return false;
    }
    private boolean is1HLayout(){

        List<String> typeList = Arrays.asList("1h1", "1h2", "1h3", "1h4", "1h5", "1h6", "2h7", "1h8", "1h9", "1h10");

        for (String s : typeList
        ) {
            if(TextUtils.equals(layoutType.toLowerCase(), s)){
                return true;
            }
        }
        return false;
    }
    private boolean is3HLayout(){

        List<String> typeList = Arrays.asList("3h1", "3h2", "3h3", "3h4", "3h5", "3h6", "3h7", "3h8", "3h9", "3h10");

        for (String s : typeList
        ) {
            if(TextUtils.equals(layoutType.toLowerCase(), s)){
                return true;
            }
        }
        return false;
    }
    private boolean isSquareLayout(){

        List<String> typeList = Arrays.asList("sqr1", "sqr2", "sqr3", "sqr4", "sqr5", "sqr6", "sqr7", "sqr8", "sqr9", "sqr10");

        for (String s : typeList
        ) {
            if(TextUtils.equals(layoutType.toLowerCase(), s)){
                return true;
            }
        }

        return false;
    }
}
