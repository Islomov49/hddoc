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


public class IntroFrame extends Fragment {

    private TextView tvTitleSecond, tvText;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_frame, container, false);
        tvText = (TextView) view.findViewById(R.id.tvTextSecond);
        tvTitleSecond = (TextView) view.findViewById(R.id.tvTitleSecond);
        changeLanguage();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        changeLanguage();
    }


    public void changeLanguage() {
        preferences = getActivity().getSharedPreferences("informat", MODE_PRIVATE);
        String language = preferences.getString("language", getResources().getString(R.string.language_default));
        if (language.equals(getResources().getString(R.string.uz))) {
            tvTitleSecond.setText(getResources().getString(R.string.weekly_info));
            tvText.setText(getString(R.string.intro_sec));
        } else if (language.equals(getResources().getString(R.string.ru))) {
            tvTitleSecond.setText(getString(R.string.weekly_info_ru));
            tvText.setText(getString(R.string.intro_sec_ru));
        }
    }
}
