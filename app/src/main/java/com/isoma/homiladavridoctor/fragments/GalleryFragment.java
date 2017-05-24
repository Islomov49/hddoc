package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.adapters.GalleryPageAdapter;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.GeneralConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    public GalleryFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

        long CREATE_VOQT = sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis());
        int HAFTA_SONI = (int) ((System.currentTimeMillis() - CREATE_VOQT) / 1000 / 60 / 60 / 24 / 7);
        LayoutInflater inflatere = LayoutInflater.from(getActivity());
        List<View> pages = new ArrayList<View>();
        for (int t = 0; t < 40; t++) {
            View page = inflater.inflate(R.layout.fragment_galery, null);
            TextView tvWeek = (TextView) page.findViewById(R.id.xaftapag);
            TextView tvAbout = (TextView) page.findViewById(R.id.textView);
            ImageView ivPhoto = (ImageView) page.findViewById(R.id.imagepa);
            Picasso.with(getActivity())
                    .load(GeneralConstants.Imagees[t])
                    .centerCrop().resize(475, 475)
                    .into(ivPhoto);
            tvWeek.setText(Integer.toString(t + 1) + " "+getString(R.string.hafta));
            tvAbout.setText(getString(R.string.ogirligi) + GeneralConstants.WEIGHT[t] +" "+ getString(R.string.gr) + "\n"+getString(R.string.olchami) + GeneralConstants.SIZES[t] + " "+getString(R.string.sm));
            pages.add(page);
        }
        GalleryPageAdapter pagerAdapter = new GalleryPageAdapter(pages);
        ViewPager viewPager = new ViewPager(getActivity());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(HAFTA_SONI - 1);
        return viewPager;
    }

    @Override
    public void onDestroy() {
        ((HomilaDavri) getActivity()).getSupportActionBar().show();
        getActivity().getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onDestroy();
    }
}