package com.isoma.homiladavridoctor.intropage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.ChooseLangDialog;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.MODE_PRIVATE;

public class LanguageSelector extends Fragment {
    private TextView tvLanguage, tvTitle, tvText;
    String[] languages, languagesValues;
    String chooseLang, cancel;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LinearLayout llLang;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_choose_fragment, container, false);
        languagesValues = new String[]{getResources().getString(R.string.uz), getResources().getString(R.string.ru)};
        languages = new String[]{getResources().getString(R.string.ozbek_til), getResources().getString(R.string.rus_til)};
        tvLanguage = (TextView) view.findViewById(R.id.tvLang);
        tvTitle = (TextView) view.findViewById(R.id.tvTitleFirst);
        tvText = (TextView) view.findViewById(R.id.tvIntroText);
        llLang = (LinearLayout) view.findViewById(R.id.llLang);
        if(getActivity().getSharedPreferences("informat", MODE_PRIVATE).getString("language", getResources().getString(R.string.uz)).equals(getString(R.string.uz)))
        tvLanguage.setText(languages[0]);
        else
        tvLanguage.setText(languages[1]);

        preferences = getActivity().getSharedPreferences("informat", MODE_PRIVATE);
        editor = preferences.edit();

        chooseLang = getResources().getString(R.string.choose_lang);
        cancel = getResources().getString(R.string.cancel);
        changeLanguage();
        llLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ChooseLangDialog langDialog = new ChooseLangDialog(getContext());
                langDialog.setAdapter(languages);
                langDialog.setTitle(chooseLang);
                langDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tvLanguage.setText(languages[i]);
                        editor.putString("language", languagesValues[i]).apply();
                        EventBus.getDefault().post(new EventMessage(null,"change","Lang"));
                        langDialog.dismiss();
                    }
                });
                langDialog.show();
            }
        });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        changeLanguage();
    }

    public void changeLanguage() {
        preferences = getActivity().getSharedPreferences("informat", MODE_PRIVATE);
        String language = preferences.getString("language", getResources().getString(R.string.uz));
        if (language.equals(getResources().getString(R.string.uz))) {
            chooseLang = getResources().getString(R.string.choose_lang);
            cancel = getResources().getString(R.string.cancel);
            tvTitle.setText(getResources().getString(R.string.asnova));
            tvText.setText(getString(R.string.intro_first));
        } else if (language.equals(getResources().getString(R.string.ru))) {
            chooseLang = getResources().getString(R.string.choose_lang_ru);
            cancel = getResources().getString(R.string.cancel_ru);
            tvTitle.setText(getString(R.string.asnova_ru));
            tvText.setText(getString(R.string.intro_first_ru));
        }
    }
}
