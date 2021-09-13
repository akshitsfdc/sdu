package com.akshit.akshitsfdc.allpuranasinhindi.helpers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.HomeActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SoftPuranaDashboardActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.DisplayHorizontalRecyclerViewAdapter;
import com.akshit.akshitsfdc.allpuranasinhindi.models.DisplayModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SoftCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.service.FireStoreService;
import com.akshit.akshitsfdc.allpuranasinhindi.service.SQLService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayRecyclerHelper {

    private RecyclerView recyclerView;
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private View displayLayout;
    private DisplayHorizontalRecyclerViewAdapter adapter;
    private DisplayModel displayModel;
    private boolean loadAllowed;
    private boolean listEnded;
    private FireStoreService fireStoreService;
    private DocumentSnapshot lastVisible;
    private ProgressBar loadMoreProgress;
    private View allButton;
    private List<SoftCopyModel> offlineSoftCopies;
    private SQLService sqlService;


    private static final String TAG = "DisplayRecyclerHelper";

    public DisplayRecyclerHelper(AppCompatActivity activity, DisplayModel displayModel) {

        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        this.fireStoreService = ((BaseActivity)activity).fireStoreService;
        this.sqlService = new SQLService(activity);
        this.displayModel = displayModel;
        initiateViews();
        setupRecyclerView();

    }

    public DisplayRecyclerHelper(AppCompatActivity activity, DisplayModel displayModel, List<SoftCopyModel> softCopyModels) {

        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        this.fireStoreService = ((BaseActivity)activity).fireStoreService;
        this.sqlService = new SQLService(activity);
        this.displayModel = displayModel;
        this.offlineSoftCopies = softCopyModels;
        initiateViews();
        setupRecyclerView();

    }


    private void initiateViews(){

        View parent = inflater.inflate(R.layout.display_recycler_view, null, false);

        View displayLayout = parent.findViewById(R.id.displayLayout);

        if(displayLayout.getParent() != null){
            ((ViewGroup)displayLayout.getParent()).removeView(displayLayout);
            this.displayLayout = displayLayout;
            this.recyclerView = this.displayLayout.findViewById(R.id.recycler_view);
            this.loadMoreProgress = this.displayLayout.findViewById(R.id.loadMoreProgress);
            this.allButton = this.displayLayout.findViewById(R.id.allButton);
            TextView headerText = this.displayLayout.findViewById(R.id.headerText);
            headerText.setText(displayModel.getDisplayHeader());

            this.allButton.setOnClickListener(v -> navigateToAll());
        }
    }

    private void navigateToAll(){

        BaseActivity baseActivity = ((BaseActivity)activity);

        baseActivity.routing.clearParams();
        baseActivity.routing.appendParams("singleBook", false);
        baseActivity.routing.appendParams("type", displayModel.getDisplayKey());
        baseActivity.routing.appendParams("fromHome", true);
        baseActivity.routing.appendParams("displayModel", displayModel);
        baseActivity.routing.navigate(SoftPuranaDashboardActivity.class, false);
    }

    private void setupRecyclerView(){

        if(recyclerView == null){
            return;
        }


        adapter = new DisplayHorizontalRecyclerViewAdapter(activity, new ArrayList<>(), displayModel.getDisplayKey().toLowerCase());

        this.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        this.recyclerView.setAdapter(adapter);

        hideLoading();

        if(this.offlineSoftCopies != null){
            adapter.extendList(offlineSoftCopies);
        }else {
            paginationScroll();
            loadDisplay();
        }
    }

    public void resetRecyclerView(List<SoftCopyModel> softCopyModels){
        adapter = new DisplayHorizontalRecyclerViewAdapter(activity, softCopyModels, displayModel.getDisplayKey().toLowerCase());
        this.recyclerView.setAdapter(adapter);
    }

    public int getLimit(){
        return displayModel.getLimit();
    }

    private void paginationScroll(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dx > 0) {

                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loadAllowed) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loadAllowed = false;
                            if(!listEnded){
                                loadDisplay();
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadDisplay(){


        Query query = getQuery();

        showLoading();
       // showPB(true);
        query.get()
                .addOnSuccessListener(documentSnapshots -> {

                    if(documentSnapshots.size() <= 0){
                        endList();
                        return;
                    }
                    if(documentSnapshots.getDocuments().size() < displayModel.getLimit()){
                        endList();
                    }else {
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1 );
                    }
                    List<SoftCopyModel> softCopyModels = new ArrayList<>();
                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                        if(document != null){
                            softCopyModels.add(document.toObject(SoftCopyModel.class));
                        }
                    }

                    if(softCopyModels.size() > 0){
                        adapter.extendList(softCopyModels);
                    }

                    loadAllowed = true;
                    hideLoading();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "loadDisplay: ", e);
                    e.printStackTrace();
                    hideLoading();
                });
    }
    private void endList(){
        lastVisible = null;
        listEnded = true;
        loadAllowed = false;
        hideLoading();
    }
    private Query getQuery(){

        String bookCollection = "digital_books";

        String displayKey = displayModel.getDisplayKey().toLowerCase();
        Query query;

        if(TextUtils.equals(displayKey, "new")){

            if(lastVisible != null){
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(displayModel.getLimit());
            }else {
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereEqualTo("isOneOfThePart", false)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .limit(displayModel.getLimit());
            }

        }else {
            if(lastVisible != null){
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereArrayContains("displayKeys", displayKey)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(displayModel.getLimit());
            }else {
                query = this.fireStoreService.getDB().collection(bookCollection)
                        .whereArrayContains("displayKeys", displayKey)
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .limit(displayModel.getLimit());
            }


        }

        return query;
    }


    private void showLoading(){
        if(loadMoreProgress != null){
            loadMoreProgress.setVisibility(View.VISIBLE);
        }
    }
    private void hideLoading(){
        if(loadMoreProgress != null){
            loadMoreProgress.setVisibility(View.GONE);
        }
    }
    public View getDisplayLayout(){
        return this.displayLayout;
    }
    public RecyclerView getRecyclerView(){
        return this.recyclerView;
    }





}
