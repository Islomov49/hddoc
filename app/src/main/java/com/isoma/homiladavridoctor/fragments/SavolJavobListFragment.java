package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isoma.homiladavridoctor.Entity.AnswerQuestionEnity;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.isoma.homiladavridoctor.utils.PAFragmentManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.isoma.homiladavridoctor.utils.GeneralConstants.TYPE_OF_QUESTIONS;


public class SavolJavobListFragment extends Fragment {

    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    public static final String UID = "_id";
    public  String TABLE_NAME="sovoljavob" ;
    RecyclerView recyclerView;
    View viewSavoljavob;
    private PAFragmentManager paFragmentManager;
    ArrayList<AnswerQuestionEnity> answerQuestionEnities;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.savollari);
        paFragmentManager = ((HomilaDavri) getActivity()).getPaFragmentManager();
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed=sPref.edit();
        viewSavoljavob = inflater.inflate(R.layout.fragment_savoljavob, container, false);
        ButterKnife.bind(this, viewSavoljavob);
        recyclerView = (RecyclerView) viewSavoljavob.findViewById(R.id.rvAnswerQuesions);
        if(NetworkUtils.isNetworkAvailable(getContext())){
        //TODO VERSION CHECK FROM FIREBASE
        }
//        answerQuestionEnities = new ArrayList<>();
//        Cursor cursor = sdb.query(TABLE_NAME, new String[]{
//                        UID, HomilaConstants.QUESTIONS, HomilaConstants.ANSWER}, null,
//                null,
//                null,
//                null,
//                UID + " DESC"
//        );
//
//
//        while (cursor.moveToNext()) {
//            String savollar = cursor.getString(cursor
//                    .getColumnIndex(HomilaConstants.QUESTIONS));
//            String javoblar = cursor.getString(cursor
//                    .getColumnIndex(HomilaConstants.ANSWER));
//            String UID_N = cursor.getString(cursor
//                    .getColumnIndex(UID));
//             answerQuestionEnities.add(new AnswerQuestionEnity(javoblar,savollar,javoblar,UID_N));
//        }
//        cursor.close();

        return viewSavoljavob;
        }
    @Override
    public void onDetach() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);
        viewSavoljavob=null;
        super.onDetach();

    }

    class AdapterAnswerQuestions extends RecyclerView.Adapter<AdapterAnswerQuestions.MyViewHolder>{

        String[] answerQuestionEnities;
        AdapterAnswerQuestions(String[] answerQuestionEnities){
            this.answerQuestionEnities= answerQuestionEnities;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return  new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.tvAnswer.setText(answerQuestionEnities[position]);
            if (position <= 9) {
                holder.tvType.setText(TYPE_OF_QUESTIONS[0]);
            } else if (position >= 10 && position <= 16){
                holder.tvType.setText(TYPE_OF_QUESTIONS[1]);
            } else if (position == 17){
                holder.tvType.setText(TYPE_OF_QUESTIONS[2]);
            } else  if (position > 17){
                holder.tvType.setText(TYPE_OF_QUESTIONS[3]);
            }
            holder.myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed.putInt(HomilaConstants.UID_NUMBER,position);
                    ed.apply();
                    paFragmentManager.displayFragment(new InfoSavolJavobFragment());
                    //TODO SEND INFO WITH BUNDLE
                }
            });
        }

        @Override
        public int getItemCount() {
            return answerQuestionEnities.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvQuestion) TextView tvAnswer;
            @BindView(R.id.tvType) TextView tvType;
            View myView;
            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                myView = itemView;
            }
        }
    }
}