package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.isoma.homiladavridoctor.systemic.HomilaConstants.UID_NUMBER;

public class InfoSavolJavobFragment extends Fragment {
    public  String TABLE_NAME="sovoljavob" ;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    @BindView(R.id.sovoli) TextView tvQuestion;
    @BindView(R.id.jovobi) TextView tvAnswer;
    private int UID;
    public InfoSavolJavobFragment() {
   }
       public View onCreateView(LayoutInflater inflater, ViewGroup container,
               Bundle savedInstanceState) {
           View view = inflater.inflate(R.layout.fragment_viewjovob, container, false);
           ButterKnife.bind(this, view);
           sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
           ed=sPref.edit();
           UID =sPref.getInt(UID_NUMBER,3);
           int pos =  sPref.getInt(UID_NUMBER, 0);

           return view;
       }

}
