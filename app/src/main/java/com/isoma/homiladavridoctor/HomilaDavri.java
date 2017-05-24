package com.isoma.homiladavridoctor;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.isoma.homiladavridoctor.broadcastservice.NotifBroadcastOlderJellyBean;
import com.isoma.homiladavridoctor.broadcastservice.NotifBroatcastJellyBean;
import com.isoma.homiladavridoctor.fragments.BelgilarFragment;
import com.isoma.homiladavridoctor.fragments.GalleryFragment;
import com.isoma.homiladavridoctor.fragments.InfoHaftaFragment;
import com.isoma.homiladavridoctor.fragments.OvqotlanishFragment;
import com.isoma.homiladavridoctor.fragments.QuestionsViewPagerFragment;
import com.isoma.homiladavridoctor.fragments.RegistratsiyaFragment;
import com.isoma.homiladavridoctor.fragments.SettingsFragment;
import com.isoma.homiladavridoctor.fragments.SupportFragment;
import com.isoma.homiladavridoctor.fragments.TestlarCarcasFragment;
import com.isoma.homiladavridoctor.fragments.VirtualAnketaFragment;
import com.isoma.homiladavridoctor.googleUtils.SignInGoogleMoneyHold;
import com.isoma.homiladavridoctor.intropage.IntroIndicator;
import com.isoma.homiladavridoctor.systemic.AppCompatActivityParent;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.GeneralConstants;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.isoma.homiladavridoctor.utils.PAFragmentManager;
import com.isoma.homiladavridoctor.widget.WidgetKeys;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_NAME;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.WEEKS_INFO;


public class HomilaDavri extends AppCompatActivityParent {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    private Toolbar toolbar;
    private View forgone;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private TextView tvProfileName;
    private TextView tvEmail;
    private String URI_SET_PHOTO;
    private CircleImageView cibProfilePhoto;
    private AlarmManager alarmManager;
    private String cancel;
    private String[] phoneNumbers = {"103", "102"};
    int weeks_for_check;
    ActionBarDrawerToggle actionBarDrawerToggle;
    public SignInGoogleMoneyHold reg;
    boolean downloadnycCanRest = true;
    DownloadImageTask imagetask;
    int weeks;
    FirebaseDatabase GENERAL1 = FirebaseDatabase.getInstance();
    DatabaseReference GENERAL = GENERAL1.getReference();
    @BindView(R.id.green_frame)
    ImageView ivGreenPadFrame;
    @BindView(R.id.grey_frame) ImageView ivGreyPadFrame;
    private long creation_time;
    @BindView(R.id.juchok) TextView tvCurrentWeek;
    @BindView(R.id.utdi_hafta) TextView tvGone;
    @BindView(R.id.qol_hafta) TextView tvLeft;
    @BindView(R.id.ogirligi) TextView tvWeight;
    @BindView(R.id.razmer) TextView tvDimen;
    @BindView(R.id.xafta) TextView tvCurrentPeriod;
    @BindView(R.id.umum1) TextView tvCommon;
    @BindView(R.id.umum23) TextView tvArticles;
    private LinearLayout.LayoutParams leftParams, rightParams;
    @BindView(R.id.ned_usish) ImageView ivBabyGrowth;
    public String TABLE_NAME = "usishi";
    private final int chosen_image = 13;
    private ImageView fabbutton;
    private String IS_IT_CHECKED_USER = "dfsdfwwweq";
    private boolean toQuestions = false;
    private TextView indicatorNewAnswer;
    private TextView indicatorNewTest;
    private static final int MY_PERMISSIONS_READ_WRITE = 38;
    public static String PAGE = "page";
    ChildEventListener eventListener;
    Uri imageUri;
    PAFragmentManager paFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forgone = findViewById(R.id.forgone);
        sPref = getSharedPreferences("informat", MODE_PRIVATE);
        ed = sPref.edit();
        String language = sPref.getString("language", getResources().getString(R.string.language_default));
        if (language.matches(getResources().getString(R.string.language_default)))
            setLocale(Locale.getDefault().getLanguage());
        else
            setLocale(language);
        weeks_for_check = sPref.getInt(HomilaConstants.SAVED_WEEK, 1);
        if (sPref.getBoolean(HomilaConstants.SAVED_FIRST, true)) {
            File new_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/");
            File new_file_cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini");
            if (!new_file.exists()) {
                new_file.mkdirs();
                new_file_cache.mkdirs();

            }
            File Nn = new File(new_file, ".nomedia");
            File Nnm = new File(new_file_cache, ".nomedia");
            try {
                Nn.createNewFile();
                Nnm.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            try {
                Intent intro = new Intent(this, IntroIndicator.class);
                startActivity(intro);
            } catch (Exception o) {
            }
            finish();
        }
        setContentView(R.layout.main_modern_greater);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.app_name_main);

        creation_time = sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis());
        weeks = (int) ((System.currentTimeMillis() - creation_time) / 1000 / 60 / 60 / 24 / 7);
        URI_SET_PHOTO = null;
        URI_SET_PHOTO = sPref.getString(HomilaConstants.URI_PHOTO, "topilmadi");
        restartNotify();
        if (weeks_for_check != weeks && weeks != 0) {
            ed.putInt(HomilaConstants.SAVED_WEEK, weeks);
            ed.apply();
            weeks_for_check = weeks;
        }
        if (weeks > 40) {
            weeks = 40;
            ed.putInt(HomilaConstants.SAVED_WEEK, weeks);
            ed.apply();
        }
        if (weeks < 1) {
            weeks = 1;
            ed.putInt(HomilaConstants.SAVED_WEEK, weeks);
            //  ed.putLong(SAVED_CREATE,System.currentTimeMillis()-1*7*24*60*60*1000);
            ed.apply();
        }
        setposition();
        settextall();

        findViewById(R.id.gochat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(HomilaDavri.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getPermision();
                }else {
                    FirebaseUser authData = FirebaseAuth.getInstance().getCurrentUser();
                    if (authData == null) {
                        try {
                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HomilaDavri.this);
                            builder.setMessage(R.string.ruyxatdan_otish)
                                    .setPositiveButton(R.string.ruyxa, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            toQuestions = true;
                                            reg.regitUser();


                                        }
                                    }).setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        } catch (Exception o) {
                        }
                    } else {
                        if (sPref.getBoolean(IS_IT_CHECKED_USER, false)) {
                            QuestionsViewPagerFragment questionsViewPagerFragment = new QuestionsViewPagerFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("FROM_MAIN",true);
                            questionsViewPagerFragment.setArguments(bundle);
                            paFragmentManager.displayFragment(questionsViewPagerFragment);
                        } else {
                            showProgressDialog(getString(R.string.boglanis));
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            GENERAL.child("/users/" + firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    hideProgressDialog();
                                    if (dataSnapshot.getValue() == null) {
                                        paFragmentManager.displayFragment(new RegistratsiyaFragment());
                                    } else {
                                        QuestionsViewPagerFragment questionsViewPagerFragment = new QuestionsViewPagerFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("FROM_MAIN",true);
                                        questionsViewPagerFragment.setArguments(bundle);
                                        paFragmentManager.displayFragment(questionsViewPagerFragment);
                                        sPref.edit().putBoolean(IS_IT_CHECKED_USER, true).apply();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    hideProgressDialog();
                                }
                            });
                        }
                    }
                }
            }
        });
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        tvProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        tvEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        paFragmentManager  = new PAFragmentManager(this);
        String intent = getIntent().getStringExtra(WidgetKeys.KEY_TO_INTENT);
        if (intent != null){
            if (intent.matches(WidgetKeys.KEY_NEWSFEED)){
                paFragmentManager.displayFragment(new TestlarCarcasFragment());
            }
            if (intent.matches(WidgetKeys.KEY_ARTICLE)){
                final QuestionsViewPagerFragment viewPagerFragment = new QuestionsViewPagerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(PAGE, 3);
                viewPagerFragment.setArguments(bundle);
                if (sPref.getBoolean(IS_IT_CHECKED_USER, false)) {
                    paFragmentManager.displayFragment(viewPagerFragment);
                } else {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {
                        showProgressDialog(getString(R.string.boglanis));
                        GENERAL.child("/users/" + firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                hideProgressDialog();
                                if (dataSnapshot.getValue() == null) {
                                    paFragmentManager.displayFragment(new RegistratsiyaFragment());
                                } else {
                                    paFragmentManager.displayFragment(viewPagerFragment);
                                    sPref.edit().putBoolean(IS_IT_CHECKED_USER, true).apply();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                hideProgressDialog();
                            }
                        }); } else hideProgressDialog();
                }
            }
            if (intent.matches(WidgetKeys.KEY_INFO)){
                paFragmentManager.displayFragment(new InfoHaftaFragment());
            }
        }
        cibProfilePhoto = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        fabbutton = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.floatbut);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fabbutton.setVisibility(View.GONE);
        } else{
            fabbutton.setImageResource(R.drawable.ic_action_camera);
        }
        sPref.edit().putInt("startCheck",sPref.getInt("startCheck",0)+1).apply();
        if(sPref.getInt("startCheck",0)%5==0 && NetworkUtils.isNetworkAvailable(this) && !sPref.getBoolean("isMarked",false) ){
            openDialogMark();

        }
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    if(sPref.getBoolean(IS_IT_CHECKED_USER,false)){
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        GENERAL.child("user-status/"+firebaseUser.getUid()+"/online").setValue(true);
                    }

                } else {
                    DatabaseReference.goOnline();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });

        fabbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, chosen_image);
                } else{
                    toQuestions = false;
                    reg.regitUser();

                }



            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        tvProfileName.setText(sPref.getString(SAVED_NAME, getResources().getString(R.string.put_name)));
        if (user != null) {
            tvEmail.setText(sPref.getString(HomilaConstants.EMAIL_USER,getString(R.string.app_name_main)));
            try {
                if (user.getPhotoUrl() != null) {
                    imagetask = new DownloadImageTask(cibProfilePhoto);
                    imagetask.execute(user.getPhotoUrl().toString());
                    imageUri = user.getPhotoUrl();
                }
            } catch (Exception o) {

            }
        }

        reg = new SignInGoogleMoneyHold(this, new SignInGoogleMoneyHold.UpdateSucsess() {
            @Override
            public void updateToSucsess() {
                succesLogin();
            }

            @Override
            public void updateToFailed() {
                failedLogin();
            }
        });



        cibProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri!=null) {

                } else {
                    FirebaseUser authData = FirebaseAuth.getInstance().getCurrentUser();
                    if (authData == null) {
                        toQuestions = false;
                        reg.regitUser();
                    }
                }
            }
        });
        findViewById(R.id.gotest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new TestlarCarcasFragment());
            }
        });
        findViewById(R.id.maqola).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(HomilaDavri.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getPermision();
                }else {
                    FirebaseUser authData = FirebaseAuth.getInstance().getCurrentUser();
                    if (authData == null) {
                        try {
                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HomilaDavri.this);
                            builder.setMessage(R.string.ruyxatdan_otish)
                                    .setPositiveButton(R.string.ruyxa, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            toQuestions = true;
                                            reg.regitUser();


                                        }
                                    }).setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        } catch (Exception o) {
                        }
                    } else {
                        if (sPref.getBoolean(IS_IT_CHECKED_USER, false)) {
                            paFragmentManager.displayFragment(new QuestionsViewPagerFragment());
                        } else {
                            showProgressDialog(getString(R.string.boglanis));
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            GENERAL.child("/users/" + firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    hideProgressDialog();
                                    if (dataSnapshot.getValue() == null) {
                                        paFragmentManager.displayFragment(new RegistratsiyaFragment());
                                    } else {
                                        paFragmentManager.displayFragment(new QuestionsViewPagerFragment());
                                        sPref.edit().putBoolean(IS_IT_CHECKED_USER, true).apply();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    hideProgressDialog();
                                }
                            });
                        }
                    }
                }
            }
        });




        findViewById(R.id.toliqhaftagaa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paFragmentManager.displayFragment(new InfoHaftaFragment());

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {

                drawerLayout.closeDrawers();
                drawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (menuItem.getItemId()) {


                            case R.id.testlar:
                                paFragmentManager.clearAllFragments();
                                paFragmentManager.displayFragment(new TestlarCarcasFragment());
                                break;




                            case R.id.talabtaklif:
                                paFragmentManager.clearAllFragments();
                                paFragmentManager.displayFragment(new SupportFragment());
                                break;

                            case R.id.galer:
                                paFragmentManager.clearAllFragments();
                                paFragmentManager.displayFragment(new GalleryFragment());

                                break;


                            case R.id.belgilar:
                                paFragmentManager.clearAllFragments();
                                paFragmentManager.displayFragment(new BelgilarFragment());


                                break;

                            case R.id.ovqotlan:
                                paFragmentManager.clearAllFragments();
                                paFragmentManager.displayFragment(new OvqotlanishFragment());


                                break;


                            case R.id.sozlamalar:
                                paFragmentManager.clearAllFragments();
                                paFragmentManager.displayFragment(new SettingsFragment());
                                break;

                            default:
                                paFragmentManager.clearAllFragments();
                                Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                                break;


                        }
                    }
                },170);
                return true;
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {



            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                tvProfileName.setText(sPref.getString(SAVED_NAME, getResources().getString(R.string.put_name)));

                super.onDrawerOpened(drawerView);
            }
        };
        setNavigationButton();
    }

    public void setNavigationButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.setToolbarNavigationClickListener(null);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }
    public void setHomeButton() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = HomilaDavri.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                paFragmentManager.remoteBackPress();
            }
        });

    }
    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
    public PAFragmentManager getPaFragmentManager(){
        if(paFragmentManager==null) paFragmentManager = new PAFragmentManager(HomilaDavri.this);
        return paFragmentManager;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_READ_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findViewById(R.id.maqola).callOnClick(); }
                break;


        }

        List<Fragment> fragments = paFragmentManager.getFragmentManager().getFragments();
        if (fragments != null) {
            for (android.support.v4.app.Fragment fragment : fragments) {
                if(fragment!=null)
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {

            case   SignInGoogleMoneyHold.RC_SIGN_IN:
                reg.regitRequstGet(imageReturnedIntent);

                break;
            case chosen_image:
                if (resultCode == RESULT_OK) {

                    try {
                        final Uri imageUri = imageReturnedIntent.getData();

                        ed.putString(HomilaConstants.URI_PHOTO, imageUri.toString());
                        ed.apply();
                        cibProfilePhoto.setImageBitmap(null);
                        Picasso.with(this)
                                .load(imageUri)
                                .error(R.drawable.avatar)
                                .resize(200, 200)
                                .centerCrop()
                                .into(cibProfilePhoto);
                        URI_SET_PHOTO = imageUri.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                ed.putString("tempPhoto", "");
                ed.commit();
        }
    }


    @Override
    public void onBackPressed() {
        if  (drawerLayout.isDrawerOpen(navigationView))
            drawerLayout.closeDrawers();
        else if (paFragmentManager.getFragmentManager().findFragmentById(R.id.frame) != null){
            paFragmentManager.remoteBackPress();
            drawerLayout.closeDrawers();
        }
        else{
            super.onBackPressed();
        }


    }

    private void settextall() {
        try {
            tvCurrentWeek.setText(Integer.toString(weeks) + " "+getString(R.string.haftada));
            tvGone.setText(getString(R.string.utdi)+"\n"+Integer.toString(weeks)+" "+getString(R.string.haftaa));
            tvLeft.setText(getString(R.string.qoldi)+"\n"+Integer.toString(40-weeks)+" "+getString(R.string.haftaa));
            tvWeight.setText(getString(R.string.ogirlik)+"\n" + GeneralConstants.WEIGHT[weeks - 1] + " "+getString(R.string.grr));
            tvDimen.setText(getString(R.string.ulchami)+"\n" + GeneralConstants.SIZES[weeks - 1] + " "+getString(R.string.smm));


            tvCurrentPeriod.setText(Integer.toString(((int) ((float) weeks / (float) 4.3) + 1)) + " "+getString(R.string.oyy));

            String info[] = getResources().getStringArray(WEEKS_INFO[weeks-1]);

            tvCommon.setText(info[0]);
        } catch (Exception o) {
        }

    }

    private void setposition() {
        int left, right;
        left = weeks;
        right = 40 - weeks;
        leftParams = (LinearLayout.LayoutParams) ivGreenPadFrame.getLayoutParams();
        leftParams.weight = left;
        rightParams = (LinearLayout.LayoutParams) ivGreyPadFrame.getLayoutParams();
        rightParams.weight = right;
        ivBabyGrowth.setImageBitmap(null);
        ivBabyGrowth.setImageResource(GeneralConstants.Images[weeks - 1]);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        // visbview();
        creation_time = sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis());
        weeks = (int) ((System.currentTimeMillis() - sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis())) / 1000 / 60 / 60 / 24 / 7);
        if (weeks_for_check != weeks && weeks != 0) {
            ed.putInt(HomilaConstants.SAVED_WEEK, weeks);
            ed.apply();
            weeks_for_check = weeks;
            restartNotify();
            setposition();
            settextall();
        }
    }

    private void restartNotify() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent = new Intent(this, NotifBroatcastJellyBean.class);
        } else {
            intent = new Intent(this, NotifBroadcastOlderJellyBean.class);
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        long DingDing = creation_time + (long) (weeks + 1) * (long) 7 * (long) 24 * (long) 60 * (long) 60 * (long) 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, DingDing, pendingIntent);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStart() {
        super.onStart();
        if (sPref.getInt(HomilaConstants.SAVED_WIDTH, 0) == 0) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            ed.putInt(HomilaConstants.SAVED_WIDTH, width);
            ed.putInt(HomilaConstants.SAVED_HEIGHT, height);
            ed.commit();
        }

        if(sPref.getBoolean(IS_IT_CHECKED_USER,false)){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser!=null)
                GENERAL.child("user-status/"+firebaseUser.getUid()+"/online").onDisconnect().setValue(false);
        }
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String token = FirebaseInstanceId.getInstance().getToken();
            GENERAL.child("user-status/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/fcm/" + token).setValue(true);
        }
    }

    Menu A1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        A1 = menu;

        return true;
    }
    public void failedLogin(){
        tvProfileName.setText(R.string.try_later);
        tvEmail.setText(R.string.kiyin_boglanin);

    }
    public void succesLogin(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            imagetask = new DownloadImageTask(cibProfilePhoto);
            tvProfileName.setText(user.getDisplayName());
            sPref.edit().putString(SAVED_NAME, user.getDisplayName()).apply();
            tvEmail.setText(sPref.getString(HomilaConstants.EMAIL_USER, getString(R.string.app_name_main)));
            fabbutton.setVisibility(View.GONE);
            if (user.getPhotoUrl() != null) {
                try {
                    imagetask.execute(user.getPhotoUrl().toString());

                } catch (Exception o) {
                }
                imageUri = user.getPhotoUrl();
            }
            if(toQuestions){
                showProgressDialog(getString(R.string.boglanis));
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                GENERAL.child("/users/"+firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        hideProgressDialog();
                        if(dataSnapshot.getValue() == null){
                            paFragmentManager.displayFragment(new RegistratsiyaFragment());
                        }
                        else {
                            paFragmentManager.displayFragment(new QuestionsViewPagerFragment());
                            sPref.edit().putBoolean(IS_IT_CHECKED_USER, true).apply();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                paFragmentManager.remoteBackPress();
                return true;

//            case R.id.addd:
//                FirebaseUser authData = FirebaseAuth.getInstance().getCurrentUser();
//                if (authData == null) {
//                    try {
//                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
//                        builder.setMessage("Сиз ҳам узунгизни қизиқтирган мақолаларни чоп етишингиз мумкун. Бунинг учун мақолангизни почта орқали юборишингиз ёки ушбу дастур орқали юборишингиз мумкун." +
//                                "Дастур орқали юбориш учун илтимос руйхатдан утинг!")
//                                .setPositiveButton("Руйхатдан ўтиш", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        paFragmentManager.displayFragment(new RegistratsiyaFragment());
//
//                                    }
//                                }).setNegativeButton("Ортга", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.create().show();
//                    } catch (Exception o) {
//                    }
//                } else {
//                    android.support.v4.app.Fragment fragment = paFragmentManager.getFragmentManager().findFragmentById(R.id.frame);
//                    if (fragment instanceof StatiyalarFragment) {
//                        paFragmentManager.displayFragment(new AddStatiyaFragment());
//
//                    } else if (fragment instanceof LentaFragment) {
//                        paFragmentManager.displayFragment(new AddLentaFragment());
//
//                    }
//                }
//                break;
            case R.id.shareanketa:

                String temp2 = sPref.getString(SAVED_NAME, "");
                if (!temp2.equals("")) {
                    Intent share = new Intent(HomilaDavri.this, VirtualAnketaFragment.class);
                    startActivity(share);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.obyasneniya_anketa)
                            .setPositiveButton(R.string.sozlamalarga, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    paFragmentManager.displayFragment(new SettingsFragment());
                                }
                            }).setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });


                    builder.create().show();
                }
                break;
            case R.id.calldoc:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.hozircha_nedostupniy_call)
                        .setPositiveButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });


                builder.create().show();
//
//                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
//                builderSingle.setTitle(R.string.telefon_raqamlar);
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phoneNumbers);
//                builderSingle.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int i) {
//                        dialog.dismiss();
//                    }
//                });
//
//                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int i) {
//                        String temp = phoneNumbers[i];
//                        Intent intentCall = new Intent();
//                        intentCall.setAction(Intent.ACTION_DIAL);
//                        intentCall.setData(Uri.parse("tel:" + temp));
//                        startActivity(intentCall);
//                    }
//                });
//                AlertDialog alertDialog = builderSingle.create();
//                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//                int width = displayMetrics.widthPixels;
//                int hieght = displayMetrics.heightPixels;
//                alertDialog.show();
//                alertDialog.getWindow().setLayout(9 * width / 10, (int) (4.0 * hieght / 10));
//                break;

        }

        return super.onOptionsItemSelected(item);
    }

    int positin = 0;
    public void updateInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(!sPref.getString("extraname","").equals(""))
                tvProfileName.setText(user.getDisplayName());
            else tvProfileName.setText(sPref.getString("extraname",""));
            tvEmail.setText(sPref.getString(HomilaConstants.EMAIL_USER,getString(R.string.app_name)));
            try {
                if (user.getPhotoUrl() != null) {
                    imagetask = new DownloadImageTask(cibProfilePhoto);
                    imagetask.execute(user.getPhotoUrl().toString());
                    imageUri = user.getPhotoUrl();
                }
            } catch (Exception o) {

            }
        }
    }
    public void menuchangeback() {
        if(A1!=null){
            A1.findItem(R.id.calldoc).setVisible(true);
            A1.findItem(R.id.shareanketa).setVisible(true);
        }
        return;
    }

    public void goneAllItems(){
        if(A1!=null){
            A1.findItem(R.id.calldoc).setVisible(false);
            A1.findItem(R.id.shareanketa).setVisible(false);}
    }


    public void goneAll() {
        if(A1!=null){
            A1.findItem(R.id.calldoc).setVisible(false);
            A1.findItem(R.id.shareanketa).setVisible(false);}
        return;
    }
    public void visbview() {
        forgone.setVisibility(View.VISIBLE);
        viewgone = false;
        return;
    }

    boolean viewgone = false;

    public void goneview() {
        if(A1!=null) {
            forgone.setVisibility(View.GONE);
            viewgone = true;
        }
        return;
    }

    boolean onlineornot = false;
    public DownloadImageTask getDownloadTasker(){
        return imagetask;
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File file = new File(getFilesDir(), "userphoto.jpg");
            if (file.exists()) {
                bmImage.setImageURI(Uri.parse(file.getAbsolutePath()));
                this.cancel(true);
            }

        }

        protected Bitmap doInBackground(String... urls) {
            if (isCancelled()) return null;
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            for (; true; ) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if (isCancelled()) break;
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                downloadnycCanRest = false;
                bmImage.setImageBitmap(result);
                File file = new File(getFilesDir(), "userphoto.jpg");
                FileOutputStream out = null;

                try {
                    out = new FileOutputStream(file);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private ProgressDialog mProgressDialog;
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(sPref.getBoolean(IS_IT_CHECKED_USER,false)){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            GENERAL.child("user-status/"+firebaseUser.getUid()+"/online").setValue(false);
            GENERAL.child("user-status/"+firebaseUser.getUid()+"/lastseen").setValue(ServerValue.TIMESTAMP);
            DatabaseReference.goOffline();
        }
        onStopSuniy();
    }

    public void onStopSuniy(){
        try {
            if (imagetask != null)
                imagetask.cancel(true);
            if (imagetask != null) {
                imagetask.cancel(true);
                imagetask = null;
            }
        } catch (Exception o) {
            o.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if(sPref.getBoolean(IS_IT_CHECKED_USER,false)){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            GENERAL.child("user-status/"+firebaseUser.getUid()+"/online").setValue(false);
            GENERAL.child("user-status/"+firebaseUser.getUid()+"/lastseen").setValue(ServerValue.TIMESTAMP);
            DatabaseReference.goOffline();
        }
    }
    private void getPermision(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions( this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_WRITE);
            } else {
                ActivityCompat.requestPermissions( this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_WRITE);
            }
        }
    }
    int starscount = 0;
    public void openDialogMark(){
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_mark, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        View v = dialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        final ImageView ivOneStar = (ImageView) dialogView.findViewById(R.id.ivOneStar);
        final ImageView ivTwoStar = (ImageView) dialogView.findViewById(R.id.ivTwoStar);
        final ImageView ivThreeStar = (ImageView) dialogView.findViewById(R.id.ivThreeStar);
        final ImageView ivFourStar = (ImageView) dialogView.findViewById(R.id.ivFourStar);
        final ImageView ivFiveStar = (ImageView) dialogView.findViewById(R.id.ivFiveStar);
        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        final TextView discriptionText = (TextView) dialogView.findViewById(R.id.discriptionText);

        ivOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivOneStar.setImageResource(R.drawable.fill_start);
                ivTwoStar.setImageResource(R.drawable.emp_start);
                ivThreeStar.setImageResource(R.drawable.emp_start);
                ivFourStar.setImageResource(R.drawable.emp_start);
                ivFiveStar.setImageResource(R.drawable.emp_start);
                starscount = 1;
            }
        });
        ivTwoStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivOneStar.setImageResource(R.drawable.fill_start);
                ivTwoStar.setImageResource(R.drawable.fill_start);
                ivThreeStar.setImageResource(R.drawable.emp_start);
                ivFourStar.setImageResource(R.drawable.emp_start);
                ivFiveStar.setImageResource(R.drawable.emp_start);
                starscount = 2;
            }
        });
        ivThreeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivOneStar.setImageResource(R.drawable.fill_start);
                ivTwoStar.setImageResource(R.drawable.fill_start);
                ivThreeStar.setImageResource(R.drawable.fill_start);
                ivFourStar.setImageResource(R.drawable.emp_start);
                ivFiveStar.setImageResource(R.drawable.emp_start);
                starscount = 3;
            }
        });
        ivFourStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivOneStar.setImageResource(R.drawable.fill_start);
                ivTwoStar.setImageResource(R.drawable.fill_start);
                ivThreeStar.setImageResource(R.drawable.fill_start);
                ivFourStar.setImageResource(R.drawable.fill_start);
                ivFiveStar.setImageResource(R.drawable.emp_start);
                starscount = 4;
            }
        });
        ivFiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivOneStar.setImageResource(R.drawable.fill_start);
                ivTwoStar.setImageResource(R.drawable.fill_start);
                ivThreeStar.setImageResource(R.drawable.fill_start);
                ivFourStar.setImageResource(R.drawable.fill_start);
                ivFiveStar.setImageResource(R.drawable.fill_start);
                starscount = 5;
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(starscount ==0){
                    discriptionText.setText(R.string.befarq_bolmang);
                    discriptionText.setTextColor(Color.parseColor("#414141"));
                } else {
                    if(starscount<4){
                        openDialogWhy();

                    }
                    else {
                        openDialogThanks();

                    }
                    sPref.edit().putBoolean("isMarked",true).apply();
                    GENERAL.child("Other/localstars/"+String.valueOf(starscount)).runTransaction(new Transaction.Handler() {
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

                    dialog.dismiss();

                }
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(8 * width / 10, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    Runnable runable;
    Handler handler;
    boolean stop = false;
    public void openDialogThanks(){
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_thanks, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        View v = dialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        final ImageView ivHeart = (ImageView) dialogView.findViewById(R.id.ivHeart);
        handler = new Handler();
        runable = new Runnable() {
            @Override
            public void run() {

                ivHeart.animate().scaleX(1.015f).scaleY(1.035f).setDuration(700).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ivHeart.animate().scaleX(0.985f).scaleY(0.965f).setDuration(700).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(!stop)
                                    handler.post(runable);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        };
        handler.post(runable);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop =true;
                Intent rate_app_web = new Intent(Intent.ACTION_VIEW);
                rate_app_web.setData(Uri.parse(getResources().getString(R.string.rate_app_web)));
                startActivity(rate_app_web);
                dialog.dismiss();
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(8 * width / 10, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    boolean isItFirst = true;
    public void openDialogWhy(){
        final Dialog dialog = new Dialog(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_why, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        View v = dialog.getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        final TextView tvOk = (TextView) dialogView.findViewById(R.id.tvOk);
        final ImageView ivWhy = (ImageView) dialogView.findViewById(R.id.ivWhy);
        final TextView tvOrtga = (TextView) dialogView.findViewById(R.id.tvOrtga);
        handler = new Handler();
        runable = new Runnable() {
            @Override
            public void run() {
                if(isItFirst) {
                    ivWhy.setImageResource(R.drawable.whyfirs);
                    isItFirst = false;
                }
                else { ivWhy.setImageResource(R.drawable.whysec);
                    isItFirst = true; }
                handler.postDelayed(runable,1000);
            }
        };
        handler.postDelayed(runable,1000);
        tvOrtga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMark();
                dialog.dismiss();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop =true;
                paFragmentManager.displayFragment(new SupportFragment());
                dialog.dismiss();
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(8 * width / 10, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


}
