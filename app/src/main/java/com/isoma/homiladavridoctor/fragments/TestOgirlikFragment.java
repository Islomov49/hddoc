package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.DateDialog;
import com.isoma.homiladavridoctor.utils.GeneralConstants;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TestOgirlikFragment extends Fragment {

    @BindView(R.id.datee) TextView tvMain;
    @BindView(R.id.ortiq) TextView tvMeasure;
    @BindView(R.id.oraliq) TextView tvDiffernce;
    @BindView(R.id.kunikuk)  EditText etDay;
    @BindView(R.id.boshlangicog) EditText etWeight;
    @BindView(R.id.boyuzunligi) EditText etHeight;
    @BindView(R.id.ogirliknow) EditText etCurrentWeight;
    @BindView(R.id.icona) ImageView ivCount;
    private float day, weight, height, currentWeight;
    private final  String OGIRLIGI="vazn";
    final  String BUYI="etHeight";
    private boolean counter =false;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private DateDialog dialog;
    private Calendar cal;
    private Date date;
    private float TVI;
    private float result;
    private int week;
    private long time_self;
    View weightView;
    public TestOgirlikFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        weightView =inflater.inflate(R.layout.fragment_ogirlik, container, false);
        ButterKnife.bind(this, weightView);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed = sPref.edit();
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "BebasNeueBook.ttf");
        tvMain.setTypeface(font);
        tvMeasure.setTypeface(font);
        tvDiffernce.setTypeface(font);
        tvMain.setTextSize(40);
        tvMeasure.setTextSize(25);
        tvDiffernce.setTextSize(22);
        time_self=sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
       date =new Date();
        date.setTime(time_self);
        cal = Calendar.getInstance();
        cal.setTime(date);
        etWeight.setText(sPref.getString(OGIRLIGI, ""));
        etHeight.setText(sPref.getString(BUYI, ""));


       etDay.setText(cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR));
        weightView.findViewById(R.id.hisoblasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hafta hisobi

                try {
                    Float.parseFloat(etWeight.getText().toString());
                } catch (Exception o) {
                    etWeight.setText("");
                    etWeight.setHintTextColor(Color.RED);
                    return;
                }
                try {
                    Float.parseFloat(etHeight.getText().toString());
                } catch (Exception o){
                    etWeight.setText("");
                    etWeight.setHintTextColor(Color.RED);
                    return;
                }
                try {
                    Float.parseFloat(etCurrentWeight.getText().toString());
                } catch (Exception o){
                    etCurrentWeight.setText("");
                    etCurrentWeight.setHintTextColor(Color.RED);
                    return;
                }

                if (etDay.getText().toString().equals("")) {
                    etDay.setHintTextColor(Color.RED);
                    return;
                } else {
                    if (counter) {
                        date = dialog.backDate();
                        cal.setTime(date);
                    }

                    week = (int) ((System.currentTimeMillis() - cal.getTimeInMillis()) / 1000 / 60 / 60 / 24 / 7);
                    if (week > 40 || week < 0) {
                        etDay.setText("");
                        etDay.setHintTextColor(Color.RED);
                        return;
                    }
                    if (week == 0) {
                        week = 1;
                    }
                }
                //birinchi og`irlik
                if (etWeight.getText().toString().equals("") || Float.parseFloat(etWeight.getText().toString()) < 30f
                        || Float.parseFloat(etWeight.getText().toString()) > 100f) {
                    etWeight.setText("");
                    etWeight.setHintTextColor(Color.RED);
                    return;
                } else {
                    ed.putString(OGIRLIGI, etWeight.getText().toString());
                    ed.apply();
                    //weight=new Float(etWeight.getText().toString());
                    weight = Float.parseFloat(etWeight.getText().toString());
                }
                //boy uzunligi
                if (etHeight.getText().toString().equals("") || Float.parseFloat(etHeight.getText().toString()) < 100f
                        || Float.parseFloat(etHeight.getText().toString()) > 220f) {
                    etHeight.setText("");
                    etHeight.setHintTextColor(Color.RED);
                    return;
                } else {
                    ed.putString(BUYI, etHeight.getText().toString());
                    ed.apply();
                    height = Float.parseFloat(etHeight.getText().toString());
                }
                //xozigi og`irligi
                if (etCurrentWeight.getText().toString().equals("") || Float.parseFloat(etCurrentWeight.getText().toString()) < 30f
                        || Float.parseFloat(etCurrentWeight.getText().toString()) > 100f) {
                    etCurrentWeight.setText("");
                    etCurrentWeight.setHintTextColor(Color.RED);
                    return;
                } else {
                    currentWeight = Float.parseFloat(etCurrentWeight.getText().toString());
                }
                TVI = ((height / 100) * (height / 100));
                TVI = weight / TVI;
                week++;
                if (TVI < 19.8f) {
                    result = weight + GeneralConstants.TVI_LOW[week / 2];
                } else if (TVI < 26.0f && TVI >= 19.8f) {
                    result = weight + GeneralConstants.TVI_NORMAL[week / 2];
                } else {
                    result = weight + GeneralConstants.TVI_HIGH[week / 2];
                }

                tvDiffernce.setText(getString(R.string.normal_vazn)+Float.toString((((int) ((result) * 10)) / 10f)) + " "+getString(R.string.kg));

                try {
                    if (currentWeight < (result - 0.3f) || currentWeight > (result + 0.3f)) {
                        if (currentWeight < result) {
                            tvMeasure.setText(getString(R.string.vazn_yetishmaslik)+" " + Float.toString((((int) ((result - currentWeight) * 10)) / 10f)) +" "+ getString(R.string.kg));
                            tvMain.setText(R.string.vazn_tuplayapsiz);
                            tvMain.setTextColor(Color.parseColor("#ffdace20"));
                            ivCount.setImageResource(R.drawable.scet_low);
                        } else {
                            tvMain.setText(R.string.ortiqcha_vazn_tuplayapsiz);
                            tvMain.setTextColor(Color.parseColor("#ffde2b3f"));
                            tvMeasure.setText(getString(R.string.ortiqcha_vazn)+"  " + Float.toString((((int) ((currentWeight - result) * 10)) / 10f))  +" "+ getString(R.string.kg));
                            ivCount.setImageResource(R.drawable.scet_high);
                        }

                    } else {
                        tvMain.setText(R.string.meyorida_vazn);
                        tvMeasure.setText(R.string.urtacha);
                        tvMain.setTextColor(Color.parseColor("#0c701f"));

                        ivCount.setImageResource(R.drawable.scet_medum);
                    }
                } catch (Exception o) {
                }
            }
        });
        return weightView;
    }

    @OnClick(R.id.kunikuk)
    public void onDateClick(){
        dialog = new DateDialog(etDay);
        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");

        counter = true;
    }

}
