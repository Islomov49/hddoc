package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

import static com.isoma.homiladavridoctor.utils.GeneralConstants.ZODIACS;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.ZODIAC_BABY;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.ZODIAC_DATE;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.ZODIAC_INFO;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.ZODIAC_PEOPLE;


public class TestMunajimFragment extends Fragment {
    @BindView(R.id.kunni) EditText etDate;
    @BindView(R.id.burj) TextView tvZodiac;
    @BindView(R.id.oraliq) TextView tvBetween;
    @BindView(R.id.umumburj) TextView tvCommonInfo;
    @BindView(R.id.bolaburj) TextView tvInfo;
    @BindView(R.id.avtoretetla) TextView tvGreatPeople;
    @BindView(R.id.radioGroup) RadioGroup radioGroupChoose;
    @BindView(R.id.imageView12) ImageView ivIcon;
    private DateDialog dialog;
    private Date zodiacDate;
    private Calendar calendar;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private long time_self;
    public String TABLE_NAME = "burjlar";
    private int count;
    private View zodiacView;


    public TestMunajimFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        zodiacView = inflater.inflate(R.layout.fragment_zadiak, container, false);
        ButterKnife.bind(this, zodiacView);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed = sPref.edit();
        time_self = sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
        time_self += (long) 280 * 24 * 60 * 60 * 1000;
        final Date aipp = new Date();
        aipp.setTime(time_self);
        calendar = Calendar.getInstance();
        calendar.setTime(aipp);
        setZodiac();
        zodiacView.findViewById(R.id.hisoblas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (radioGroupChoose.getCheckedRadioButtonId()) {
                    case R.id.tugul:
                        try {
                            calendar = Calendar.getInstance();
                            calendar.setTime(dialog.backDate());
                            setZodiac();
                        } catch (Exception o) {
                            etDate.setText("");
                            etDate.setHintTextColor(Color.RED);
                        }
                        break;
                    case R.id.dunke:
                        try {
                            calendar = Calendar.getInstance();
                            aipp.setTime(dialog.backDate().getTime() + (long) 280 * 24 * 60 * 60 * 1000);
                            calendar.setTime(aipp);
                            setZodiac();
                        } catch (Exception o) {
                            etDate.setText("");
                            etDate.setHintTextColor(Color.RED);
                        }

                        break;
                    default:
                        return;
                }


            }
        });
        return zodiacView;
    }

    @OnClick(R.id.kunni)
    public void onEditDateClick() {
        dialog = null;
        dialog = new DateDialog(etDate);
        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");
    }

    void setZodiac() {
        count = calendar.get(Calendar.DAY_OF_YEAR);
        int t = 0;

        if (356 <= count) {
            return;
        }
        while (count > 0) {
            if (count <= GeneralConstants.COUNT_OF_DAYS[t]) {
                tvCommonInfo.setText(getString(ZODIAC_INFO[t]));
                tvInfo.setText(getString(ZODIAC_BABY[t]));
                tvGreatPeople.setText(getString(ZODIAC_PEOPLE[t]));
                tvZodiac.setText(getString(ZODIACS[t]));
                tvBetween.setText(getString(ZODIAC_DATE[t]));
                ivIcon.setImageResource(GeneralConstants.ZODIACS_IMAGE[t]);
                if (t % 4 == 0) {
                    tvZodiac.setTextColor(Color.parseColor("#f69a33"));
                } else if (t % 4 == 1) {
                    tvZodiac.setTextColor(Color.parseColor("#6baa40"));
                } else if (t % 4 == 2) {
                    tvZodiac.setTextColor(Color.parseColor("#036491"));
                } else tvZodiac.setTextColor(Color.parseColor("#ec342f"));
                return;
            } else t++;
        }


    }
}
