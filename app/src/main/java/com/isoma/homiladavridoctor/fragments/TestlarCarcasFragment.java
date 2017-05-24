package com.isoma.homiladavridoctor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.PAFragmentManager;


public class TestlarCarcasFragment extends Fragment {
    private View testlar_view;
    private PAFragmentManager paFragmentManager;
   public TestlarCarcasFragment(){

   }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        testlar_view=inflater.inflate(R.layout.fragment_testlar,container,false);
        ((HomilaDavri)getActivity()).hideKeyboard();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.testlar);
        paFragmentManager = ((HomilaDavri) getActivity()).getPaFragmentManager();
        testlar_view.findViewById(R.id.kuninibil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new TestDateFragment());

            }
        });
        testlar_view.findViewById(R.id.jinsi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new TestJinsChinaFragment());

            }
        });
        testlar_view.findViewById(R.id.ogirtest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new TestOgirlikFragment());

            }
        });
        testlar_view.findViewById(R.id.belgisi).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new JinsAniqlashBelgilarFragment());

            }
        });
        testlar_view.findViewById(R.id.zadiaka).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new TestMunajimFragment());

            }
        });
        return testlar_view;
    }
    @Override
    public void onDetach() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);
        testlar_view = null;
        ((HomilaDavri)getActivity()).hideKeyboard();

        super.onDetach();
         // now cleaning up!
    }


}
