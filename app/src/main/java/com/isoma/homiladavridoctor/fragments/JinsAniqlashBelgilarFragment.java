package com.isoma.homiladavridoctor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JinsAniqlashBelgilarFragment extends Fragment {
    @BindView(R.id.datee) TextView tvUp;
    @BindView(R.id.yozu) TextView tvDown;
    public JinsAniqlashBelgilarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View belgi_view = inflater.inflate(R.layout.fragment_jinsbelgi, container, false);
        ButterKnife.bind(this, belgi_view);
        tvUp.setTextSize(18);
        tvDown.setTextSize(18);
        return belgi_view;
    }

}
