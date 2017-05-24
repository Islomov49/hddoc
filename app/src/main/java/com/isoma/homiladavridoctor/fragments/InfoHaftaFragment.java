package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.GeneralConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_CREATE;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.WEEKS_INFO;

public class InfoHaftaFragment extends Fragment {
    @BindView(R.id.haftanomeri) ImageView ivWeekNumber;
    @BindView(R.id.ulcham) TextView tvDimen;
    @BindView(R.id.qoldi) TextView tvWeight;
    @BindView(R.id.haftan) TextView tvWeekOf;
    @BindView(R.id.umumiy1) TextView tvCommonInfo;
    @BindView(R.id.umumiy2) TextView tvInfo;
    @BindView(R.id.umumiy3) TextView tvGrowthInfo;
    private SharedPreferences sPref;
    long creation_time;
    int weeks;
    private Context context;
    public String TABLE_NAME = "usishi";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((HomilaDavri) getActivity()).setHomeButton();
        ((HomilaDavri) getActivity()).goneAll();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.bhftda);
        View view = inflater.inflate(R.layout.fragment_toliq_hafta, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);
        creation_time = sPref.getLong(SAVED_CREATE, System.currentTimeMillis());
        weeks = (int) ((System.currentTimeMillis() - creation_time) / 1000 / 60 / 60 / 24 / 7);
        if (weeks > 40) {
            weeks = 40;

        }
        if (weeks < 1) {
            weeks = 1;
        }
        ivWeekNumber.setImageResource(GeneralConstants.Imagees[weeks - 1]);
        tvWeekOf.setText(Integer.toString(weeks) + " " + getString(R.string.hafta));
        tvDimen.setText(getString(R.string.olchami) + GeneralConstants.SIZES[weeks - 1] + " " + getString(R.string.sm));
        tvWeight.setText(getString(R.string.ogirligi) + GeneralConstants.WEIGHT[weeks - 1] + " " + getString(R.string.gr));


        String info[] = getResources().getStringArray(WEEKS_INFO[weeks - 1]);
        if (weeks == 1) {
            tvCommonInfo.setText(info[0]);
            tvInfo.setText(info[1]);

        } else if (weeks == 40) {
            tvCommonInfo.setText(info[0]);
        } else {
            tvCommonInfo.setText(info[0]);
            tvInfo.setText(info[1]);
            tvGrowthInfo.setText(info[2]);
        }

        return view;
    }

    @Override
    public void onDetach() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);
        super.onDetach();
    }
}
