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
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.DateDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class TestDateFragment extends Fragment {
    private View testDateView;
    @BindView(R.id.datee) TextView tvMain;
    @BindView(R.id.moth) TextView tvAddMonth;
    @BindView(R.id.yearr) TextView tvAddYear;
    private DateDialog dialog;
    @BindView(R.id.kuni) EditText etAdd;
    private Date PregnancyWeek;
    private String temp;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private long time_self;
    private Calendar calendar;

    public TestDateFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sPref = getActivity().getSharedPreferences("informat", MODE_PRIVATE);

        ed = sPref.edit();
        time_self = sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
        time_self += (long) 280 * 24 * 60 * 60 * 1000;
        Date aipp = new Date();
        aipp.setTime(time_self);
        calendar = Calendar.getInstance();
        calendar.setTime(aipp);

        testDateView = inflater.inflate(R.layout.fragment_testdate, container, false);
        ButterKnife.bind(this, testDateView);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "BebasNeueBook.ttf");
        tvMain.setTypeface(font);

        tvAddMonth.setTypeface(font);
        tvAddYear.setTypeface(font);
        tvMain.setTextSize(80);
        tvAddMonth.setTextSize(60);
        tvAddYear.setTextSize(60);
        settaDAte();
        etAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new DateDialog(v);
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");

            }
        });

        testDateView.findViewById(R.id.hisoblas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PregnancyWeek = dialog.backDate();
                    long hafta_mili = PregnancyWeek.getTime();
                    hafta_mili += (long) 280 * 24 * 60 * 60 * 1000;
                    Date aip = new Date();
                    aip.setTime(hafta_mili);

                    calendar.setTime(aip);
                    settaDAte();
                } catch (Exception o) {
                    etAdd.setText("");
                    etAdd.setHintTextColor(Color.RED);
                }
            }
        });
        return testDateView;
    }

    public void settaDAte() {

        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            temp = "0" + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        } else temp = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        tvMain.setText(temp);
        tvAddMonth.setText(MONTHS[calendar.get(Calendar.MONTH)]);
        tvAddYear.setText(Integer.toString(calendar.get(Calendar.YEAR)) + "-"+getString(R.string.yill));
    }
    public static final int MONTHS[] = {R.string.yanvar, R.string.fevral, R.string.mart, R.string.aprel, R.string.may, R.string.iyun, R.string.iyul
            , R.string.avgust, R.string.sentabr, R.string.oktabr, R.string.noyabr, R.string.dekabr};
    @Override
    public void onPause() {
        onDestroyView();
    }

    public void onDetach() {
        testDateView.setOnClickListener(null);
        testDateView = null; // now cleaning up!

        super.onDetach();
    }
}


