package com.isoma.homiladavridoctor.intropage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.UserInfo;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.fragments.IntroSecondAddInfoFragment;
import com.isoma.homiladavridoctor.googleUtils.SignInGoogleMoneyHold;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_AGE;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_CREATE;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_FIRST;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_WEEK;

public class IntroIndicator extends AppCompatActivity {
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.ivToNextButton) ImageView ivToNextButton;
    MediaPlayer mp;
    PagerAdapter pagerAdapter;
    List<Fragment> fragments;
    IntroSecondAddInfoFragment infoFragment;
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private long week;
    private TextView age;
    private long time;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference STORAGEREF = storage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro_indicator);
        ButterKnife.bind(this);
        sPref = getSharedPreferences("informat", MODE_PRIVATE);
        editor = sPref.edit();
        week = 0;
        infoFragment = new IntroSecondAddInfoFragment();
        initFrags();
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        PageIndicator mIndicator = (PageCircleIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
        editor.putString("language", getResources().getString(R.string.uz)).apply();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            rootRef.child("Doctors/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null)
                    if (dataSnapshot.child("isDoctor").getValue(Boolean.class)) {

                        sPref.edit().putBoolean(SAVED_FIRST, false).apply();
                        Intent mainIntent = new Intent(IntroIndicator.this, HomilaDavri.class);
                        startActivity(mainIntent);
                        finish();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(IntroIndicator.this,R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();

                }
            });

        pager.setPageTransformer(true, new ZoomOutTranformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mp = MediaPlayer.create(this, R.raw.revhiti);
        mp.setVolume(0.1f, 0.1f);
        ivToNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pager.getCurrentItem() != 3) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    mp.start();

                } else {

                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        infoFragment.setNotTrueWeek();
                        return;
                    }
                    age =infoFragment.getAge();


                    if (!age.getText().toString().equals("")) {
                        if (age.getText().toString().length()>=9) {
                            infoFragment.checkForValidation();
                        } else {
                            Toast.makeText(getApplicationContext(), getText(R.string.not_yosh_true).toString(), Toast.LENGTH_SHORT).show();
                            infoFragment.setNotTrueAge();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getText(R.string.not_hafta_true).toString(), Toast.LENGTH_SHORT).show();
                        infoFragment.setNotTrueWeek();
                    }
                }
            }
        });
    }

    private void initFrags() {
        fragments = new ArrayList<>();
        fragments.add(new LanguageSelector());
        fragments.add(new IntroFrame());
        fragments.add(new IntroFrameCommunity());
        fragments.add(infoFragment);
    }

    private void init() {
        ((LanguageSelector) fragments.get(0)).changeLanguage();
        ((IntroFrame) fragments.get(1)).changeLanguage();
        ((IntroFrameCommunity) fragments.get(2)).changeLanguage();
        ((IntroSecondAddInfoFragment) fragments.get(3)).changeLanguage();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeLang(EventMessage message) {
        if (message.getForClass().equals("Lang")) {
            init();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {

            case   SignInGoogleMoneyHold.RC_SIGN_IN:
                infoFragment.outSideActivityResult(imageReturnedIntent);
                break;}
    }}
