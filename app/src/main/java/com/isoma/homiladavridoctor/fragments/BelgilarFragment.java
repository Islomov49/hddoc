package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.isoma.homiladavridoctor.utils.GeneralConstants.DANGEROUS;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.DOUBTFUL;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.HARMLESS;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.PERIOD;

public class BelgilarFragment extends Fragment {

    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private long create;
    private int weeks;

    @BindView(R.id.xovotirsz) TextView harmless;
    @BindView(R.id.shubxali) TextView doubtful;
    @BindView(R.id.xafliku) TextView dangerous;
    @BindView(R.id.tvHardoim) TextView tvHardoim;
    @BindView(R.id.zagalovka) TextView title;
    @BindView(R.id.llHavfsizBelgilar) LinearLayout llHavfsizBelgilar;
    @BindView(R.id.llShubhaliBelgilar) LinearLayout llShubhaliBelgilar;
    @BindView(R.id.llXavfliBelgilar) LinearLayout llXavfliBelgilar;
    @BindView(R.id.llXarQandayVaqtda) LinearLayout llXarQandayVaqtda;

    @BindView(R.id.llHavfsizBelgilarExpand) ImageView llHavfsizBelgilarExpand;
    @BindView(R.id.llShubhaliBelgilarExpand) ImageView llShubhaliBelgilarExpand;
    @BindView(R.id.llXavfliBelgilarExpand) ImageView llXavfliBelgilarExpand;
    @BindView(R.id.llXarQandayVaqtdaExpand) ImageView llXarQandayVaqtdaExpand;

    public BelgilarFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.belgilar);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed = sPref.edit();
        create = sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
        weeks = (int) ((System.currentTimeMillis() - create) / 1000 / 60 / 60 / 24 / 7);
        if (create == 0) {
            weeks = 1;
        }
        if (weeks < 1) {
            weeks = 1;
        }
        View viewtep = inflater.inflate(R.layout.fragment_bilgilar, container, false);
        ButterKnife.bind(this, viewtep);

        harmless.setVisibility(View.VISIBLE);
        doubtful.setVisibility(View.GONE);
        dangerous.setVisibility(View.GONE);
        tvHardoim.setVisibility(View.GONE);

        llHavfsizBelgilarExpand.setImageResource(R.drawable.expandtop);
        llShubhaliBelgilarExpand.setImageResource(R.drawable.expandbottom);
        llXavfliBelgilarExpand.setImageResource(R.drawable.expandbottom);
        llXarQandayVaqtdaExpand.setImageResource(R.drawable.expandbottom);

        title = (TextView) viewtep.findViewById(R.id.zagalovka);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "BebasNeueBook.ttf");
        title.setTypeface(font);
        ((TextView)viewtep.findViewById(R.id.tvHaftalarUchun)).setTypeface(font);
        title.setText(PERIOD[(weeks - 1) / 13]);
        harmless.setText(HARMLESS[(weeks - 1) / 13]);
        doubtful.setText(DOUBTFUL[(weeks - 1) / 13]);
        dangerous.setText(DANGEROUS[(weeks - 1) / 13]);

        return viewtep;
    }
    @OnClick(R.id.llHavfsizBelgilar)
    public void onHavfsizClick(){
        if(harmless.getVisibility() != View.VISIBLE){
            harmless.setVisibility(View.VISIBLE);
            llHavfsizBelgilarExpand.setImageResource(R.drawable.expandtop);
        }
        else {
            harmless.setVisibility(View.GONE);
            llHavfsizBelgilarExpand.setImageResource(R.drawable.expandbottom);
        }
    }
    @OnClick(R.id.llShubhaliBelgilar)
    public void onShubhaliClick(){
        if(doubtful.getVisibility() != View.VISIBLE){
            doubtful.setVisibility(View.VISIBLE);
            llShubhaliBelgilarExpand.setImageResource(R.drawable.expandtop);
        }
        else {
            doubtful.setVisibility(View.GONE);
            llShubhaliBelgilarExpand.setImageResource(R.drawable.expandbottom);
        }
    }
    @OnClick(R.id.llXavfliBelgilar)
    public void onXavfliClick(){
        if(dangerous.getVisibility() != View.VISIBLE){
            dangerous.setVisibility(View.VISIBLE);
            llXavfliBelgilarExpand.setImageResource(R.drawable.expandtop);
        }
        else {
            dangerous.setVisibility(View.GONE);
            llXavfliBelgilarExpand.setImageResource(R.drawable.expandbottom);
        }
    }
    @OnClick(R.id.llXarQandayVaqtda)
    public void onHarQandayVaqtdaClick(){
        if(tvHardoim.getVisibility() != View.VISIBLE){
            tvHardoim.setVisibility(View.VISIBLE);
            llXarQandayVaqtdaExpand.setImageResource(R.drawable.expandtop);
        }
        else {
            tvHardoim.setVisibility(View.GONE);
            llXarQandayVaqtdaExpand.setImageResource(R.drawable.expandbottom);
        }
    }

    @Override
    public void onDetach() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);

        super.onDetach();

    }
}
