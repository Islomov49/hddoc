package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.isoma.homiladavridoctor.utils.GeneralConstants.COUNT_OF_WEEKS;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.FOOD;

public class OvqotlanishFragment extends Fragment {
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private View viewtep;
    public  String TABLE_NAME="ovqtot" ;
    private long CREATE;
    private int HAFTA_SONI;
    @BindView(R.id.mainnovaqt) TextView main_text;
    @BindView(R.id.zagalovka) TextView zagalovka;
    @BindView(R.id.tvHaftalarUchun) TextView tvHaftalarUchun;
    public OvqotlanishFragment(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewtep = inflater.inflate(R.layout.frag_ovq_new, container, false);
        ButterKnife.bind(this, viewtep);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "BebasNeueBook.ttf");
        zagalovka.setTypeface(font);
        tvHaftalarUchun.setTypeface(font);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.ovqat);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed=sPref.edit();

       CREATE=sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
        HAFTA_SONI=(int)((System.currentTimeMillis()-CREATE)/1000/60/60/24/7);
        if(CREATE==0){
            HAFTA_SONI=1;}
        if (HAFTA_SONI < 1) {
            HAFTA_SONI = 1;
        }
        for (int i = 0; i < COUNT_OF_WEEKS.length; i++) {
            if (HAFTA_SONI <= COUNT_OF_WEEKS[i])
            {
                main_text.setText(getString(FOOD[i]));
                break;
            }
        }


        zagalovka.setText(Integer.toString(HAFTA_SONI));
        return viewtep;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewtep = null;
    }
    @Override
    public void onDetach() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);
        super.onDetach();
    }
}
