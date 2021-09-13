package com.akshit.akshitsfdc.allpuranasinhindi.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.BaseActivity;
import com.akshit.akshitsfdc.allpuranasinhindi.activities.SupportUsActivity;

import java.util.ArrayList;
import java.util.List;


public class SupportUsFragment extends BaseFragment {

    private View parent;
    private AutoCompleteTextView amountPicker;
    private EditText msgEditText;
    private Button donateButton;
    private int selectedAmount;
    private String msg = "";
    public SupportUsFragment(AppCompatActivity activity){
        currentActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support_us, container, false);

        objectCreations(view);
        objectInitializations();
        setEvenListeners();

        return view;
    }

    @Override
    protected void objectCreations(View view) {
        parent = view.findViewById(R.id.parent);
        amountPicker = view.findViewById(R.id.amountPicker);
        msgEditText = view.findViewById(R.id.msgEditText);
        donateButton = view.findViewById(R.id.donateButton);
    }

    @Override
    protected void objectInitializations() {
        List<String> amountList = new ArrayList<String>();

        amountList.add("5000");
        amountList.add("1100");
        amountList.add("501");
        amountList.add("251");
        amountList.add("101");
        amountList.add("51");

        this.selectedAmount = Integer.parseInt( amountList.get(3));
        amountPicker.setText(amountList.get(3));

        amountPicker.setAdapter(new ArrayAdapter(currentActivity, R.layout.list_item, amountList));

        donateButton.setOnClickListener(v -> {
            startPayment();
        });
    }

    @Override
    protected void setEvenListeners() {
        parent.setOnClickListener(v -> {
            ((BaseActivity)currentActivity).routing.goBack();
        });
        amountPicker.setOnItemClickListener( (parent, view, position, id) -> {
            String selectedValue = ((TextView)view).getText().toString();
            this.selectedAmount = Integer.parseInt(selectedValue.trim());
        });
    }
    public void startPayment(){
        this.msg = this.msgEditText.getText().toString().trim();
        SupportUsActivity supportUsActivity = (SupportUsActivity) currentActivity;
        supportUsActivity.startPayment(this.selectedAmount, this.msg.trim());
        supportUsActivity.routing.goBack();
    }

}