package com.isoma.homiladavridoctor.intropage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;

import static android.content.Context.MODE_PRIVATE;


public class IntroFrameCommunity extends Fragment {

    private TextView tvTitle, tvInfo;
    private SharedPreferences sPref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_frame_cominity, container, false);
        tvInfo = (TextView) view.findViewById(R.id.IntroInfo);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        changeLanguage();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        changeLanguage();
    }


    public void changeLanguage() {
        sPref = getActivity().getSharedPreferences("informat", MODE_PRIVATE);
        String language = sPref.getString("language", getResources().getString(R.string.language_default));
        if (language.equals(getResources().getString(R.string.uz))) {
            tvInfo.setText(getResources().getString(R.string.suhbatlasing));
            tvTitle.setText(getResources().getString(R.string.homiladorlar_olami));
        } else if (language.equals(getResources().getString(R.string.ru))) {
            tvInfo.setText(getResources().getString(R.string.suhbatlasing_ru));
            tvTitle.setText(getResources().getString(R.string.homiladorlar_olami_ru));
        }
    }
}
