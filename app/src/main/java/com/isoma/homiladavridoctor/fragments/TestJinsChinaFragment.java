package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.GeneralConstants;
import com.isoma.homiladavridoctor.utils.NetworkUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TestJinsChinaFragment extends Fragment {
    private SeekBar seekBar, seekBar1;
    @BindView(R.id.ttt) TextView tvAge;
    @BindView(R.id.oyi) TextView tvMonth;
    @BindView(R.id.datee) TextView tvDate;
    @BindView(R.id.moth) TextView tvMonths;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private String sex, boy, girl;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private int age;
    private int teke_age;
    private Calendar calendar;
    private static final String SHE_VEIW_FIRST_RESULT = "first_view_res_sex";
    private LinearLayout frameMark;
    @BindView(R.id.iconaaa) ImageView ivIcon;
    @BindView(R.id.hisoblasiin) TextView tvCalculate;
    @BindView(R.id.llLike) LinearLayout llLikeLayout;
    TextView likeee, dislike;
    ImageView likeeeImage,dislikeImage;
    LinearLayout dislikeLayout,likeLayout;
    int likesCount = 0;
    int dislikesCount = 0;

    public static final int MONTHS[] = {R.string.yanvar, R.string.fevral, R.string.mart, R.string.aprel, R.string.may, R.string.iyun, R.string.iyul
            , R.string.avgust, R.string.sentabr, R.string.oktabr, R.string.noyabr, R.string.dekabr};

    public TestJinsChinaFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View jinsView = inflater.inflate(R.layout.fragment_testjins, container, false);
        ButterKnife.bind(this, jinsView);
        likeee = (TextView) jinsView.findViewById(R.id.likeee);
        dislike = (TextView) jinsView.findViewById(R.id.dislike);
        dislikeImage = (ImageView) jinsView.findViewById(R.id.dislikeImage);
        likeeeImage = (ImageView) jinsView.findViewById(R.id.likeeeImage);
        dislikeLayout = (LinearLayout) jinsView.findViewById(R.id.dislikeLayout);
        likeLayout = (LinearLayout) jinsView.findViewById(R.id.likeLayout);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed = sPref.edit();
        rootRef.child("Other/TestJins/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               likesCount = (dataSnapshot.child("likes").getValue()==null)?0:dataSnapshot.child("likes").getValue(Integer.class);
                dislikesCount = (dataSnapshot.child("dislike").getValue()==null)?0:dataSnapshot.child("dislike").getValue(Integer.class);
                likeee.setText(likesCount+"");
                dislike.setText(dislikesCount+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(sPref.getInt("testLiked",0)==0){

        }
        else if(sPref.getInt("testLiked",0)==1){
            likeee.setTextColor(Color.parseColor("#19b915"));
            likeeeImage.setColorFilter(Color.parseColor("#19b915"));
            likeLayout.setBackgroundResource(R.drawable.diaolog_buttons_green_stroke);

        }
        else if(sPref.getInt("testLiked",0)==2){
            dislike.setTextColor(Color.parseColor("#ff3505"));
            dislikeImage.setColorFilter(Color.parseColor("#ff3505"));
            dislikeLayout.setBackgroundResource(R.drawable.diaolog_buttons_red);

        }
        dislikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sPref.getInt("testLiked",0)==1) return;

                dislike.setTextColor(Color.parseColor("#acacac"));
                dislikeImage.setColorFilter(null);
                dislikeLayout.setBackgroundResource(R.drawable.rounded_rect);

                likeee.setTextColor(Color.parseColor("#19b915"));
                likeeeImage.setColorFilter(Color.parseColor("#19b915"));
                likeLayout.setBackgroundResource(R.drawable.diaolog_buttons_green_stroke);

                if(sPref.getInt("testLiked",0)==0){

                            rootRef.child("Other/TestJins/likes").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    long p =0;
                                    if(mutableData.getValue()!=null)
                                        p = mutableData.getValue(Integer.class);
                                    mutableData.setValue(++p);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                }
                            });

                    likeee.setText(String.valueOf(++likesCount));
                }
                else if(sPref.getInt("testLiked",0)==2){

                            rootRef.child("Other/TestJins/likes").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    long p =0;
                                    if(mutableData.getValue()!=null)
                                        p = mutableData.getValue(Integer.class);
                                    mutableData.setValue(p+1);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                }
                            });


                            rootRef.child("Other/TestJins/dislike").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    long p =0;
                                    if(mutableData.getValue()!=null)
                                        p = mutableData.getValue(Integer.class);
                                    mutableData.setValue(p-1);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                }
                            });

                    likeee.setText(String.valueOf(++likesCount));
                    dislike.setText(String.valueOf(--dislikesCount));
                }
                sPref.edit().putInt("testLiked",1).apply();
            }
        });

        dislikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sPref.getInt("testLiked",0)==2) return;

                likeee.setTextColor(Color.parseColor("#acacac"));
                likeeeImage.setColorFilter(null);
                likeLayout.setBackgroundResource(R.drawable.rounded_rect);

                dislike.setTextColor(Color.parseColor("#ff3505"));
                dislikeImage.setColorFilter(Color.parseColor("#ff3505"));
                dislikeLayout.setBackgroundResource(R.drawable.diaolog_buttons_red);

                if(sPref.getInt("testLiked",0)==0){

                            rootRef.child("Other/TestJins/dislike").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    long p =0;
                                    if(mutableData.getValue()!=null)
                                        p = mutableData.getValue(Integer.class);
                                    mutableData.setValue(p+1);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                }
                            });
                    likeee.setText(String.valueOf(++dislikesCount));

                }
                else if(sPref.getInt("testLiked",0)==1){


                            rootRef.child("Other/TestJins/dislike").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    long p =0;
                                    if(mutableData.getValue()!=null)
                                        p = mutableData.getValue(Integer.class);
                                    mutableData.setValue(p+1);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                }
                            });

                            rootRef.child("Other/TestJins/likes").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    long p =0;
                                    if(mutableData.getValue()!=null)
                                        p = mutableData.getValue(Integer.class);
                                    mutableData.setValue(p-1);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b,
                                                       DataSnapshot dataSnapshot) {
                                }
                            });
                    dislike.setText(String.valueOf(++dislikesCount));
                    likeee.setText(String.valueOf(--likesCount));
                }
                sPref.edit().putInt("testLiked",2).apply();

            }
        });

        teke_age = (int) (sPref.getLong(HomilaConstants.SAVED_AGE, 18));
        boy = getString(R.string.boy);
        girl = getString(R.string.girl);
        Date aipp = new Date();
        aipp.setTime(sPref.getLong(HomilaConstants.SAVED_CREATE, 0));
        calendar = Calendar.getInstance();
        calendar.setTime(aipp);
//        frameMark = (LinearLayout) jinsView.findViewById(R.id.frameMark);
        seekBar = (SeekBar) jinsView.findViewById(R.id.seekyosh);
        seekBar1 = (SeekBar) jinsView.findViewById(R.id.seekoy);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "BebasNeueBook.ttf");
        tvDate.setTypeface(font);
        tvDate.setTextSize(80);
        tvMonths.setTypeface(font);
        tvMonths.setTextSize(20);

        if (GeneralConstants.DAYS[teke_age - 18][calendar.get(Calendar.MONTH)]) {
            sex = boy;
            tvDate.setTextColor(Color.parseColor("#1f8397"));
            ivIcon.setImageResource(R.drawable.male);
            tvCalculate.setBackgroundColor(Color.parseColor("#1f8397"));
            tvDate.setText(sex);
        } else {
            sex = girl;
            tvDate.setTextColor(Color.parseColor("#e3393d"));
            ivIcon.setImageResource(R.drawable.female);
            tvCalculate.setBackgroundColor(Color.parseColor("#c84473"));
            tvDate.setText(sex);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                age = progress;
                tvAge.setText(Integer.toString(progress + 18) + "-"+getString(R.string.yosh));
                if (GeneralConstants.DAYS[TestJinsChinaFragment.this.seekBar.getProgress()][seekBar1.getProgress()]) {
                    sex = boy;
                    tvDate.setTextColor(Color.parseColor("#1f8397"));
                    ivIcon.setImageResource(R.drawable.male);
                    tvCalculate.setBackgroundColor(Color.parseColor("#1f8397"));
                    ed.putBoolean(SHE_VEIW_FIRST_RESULT,true).apply();
                } else {
                    sex = girl;
                    tvDate.setTextColor(Color.parseColor("#e3393d"));
                    ivIcon.setImageResource(R.drawable.female);
                    tvCalculate.setBackgroundColor(Color.parseColor("#c84473"));
                    ed.putBoolean(SHE_VEIW_FIRST_RESULT,true).apply();

                }
                tvDate.setText(sex);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(NetworkUtils.isNetworkAvailable(getContext()))
                llLikeLayout.setVisibility(View.VISIBLE);

            }
        });
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                age = progress;
                tvMonth.setText(MONTHS[progress]);
                if (GeneralConstants.DAYS[TestJinsChinaFragment.this.seekBar.getProgress()][seekBar1.getProgress()]) {
                    sex = boy;
                    tvDate.setTextColor(Color.parseColor("#1f8397"));
                    ivIcon.setImageResource(R.drawable.male);
                    tvCalculate.setBackgroundColor(Color.parseColor("#1f8397"));
                    ed.putBoolean(SHE_VEIW_FIRST_RESULT,true).apply();

                } else {
                    sex = girl;
                    tvDate.setTextColor(Color.parseColor("#e3393d"));
                    ivIcon.setImageResource(R.drawable.female);
                    tvCalculate.setBackgroundColor(Color.parseColor("#c84473"));
                    ed.putBoolean(SHE_VEIW_FIRST_RESULT,true).apply();

                }
                tvDate.setText(sex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(NetworkUtils.isNetworkAvailable(getContext()))
                llLikeLayout.setVisibility(View.VISIBLE);

            }
        });
        jinsView.findViewById(R.id.hisoblasiin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GeneralConstants.DAYS[seekBar.getProgress()][seekBar1.getProgress()]) {
                    sex = boy;
                    tvDate.setTextColor(Color.parseColor("#1f8397"));
                    ivIcon.setImageResource(R.drawable.male);
                    tvCalculate.setBackgroundColor(Color.parseColor("#1f8397"));
                } else {
                    sex = girl;
                    tvDate.setTextColor(Color.parseColor("#e3393d"));
                    ivIcon.setImageResource(R.drawable.female);
                    tvCalculate.setBackgroundColor(Color.parseColor("#c84473"));
                }
                tvDate.setText(sex);
            }
        });
        return jinsView;
    }

}
