package com.isoma.homiladavridoctor.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.isoma.homiladavridoctor.Entity.UserInfo;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.googleUtils.SignInGoogleMoneyHold;
import com.isoma.homiladavridoctor.intropage.IntroIndicator;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.DateDialog;
import com.isoma.homiladavridoctor.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_FIRST;


public class IntroSecondAddInfoFragment extends Fragment {
    private EditText etWeek, etAge;
    private Date PregnancyWeek;
    private DateDialog dialog;
    private String chooseAge, cancel;
    private SharedPreferences sPref;
    private TextView tvIntroTitle;
    private TextView writeToUs;
    SignInGoogleMoneyHold reg;

    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference STORAGEREF = storage.getReference();
    public IntroSecondAddInfoFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View viewHierarchy = inflater.inflate(R.layout.fragment_intro_enter_data, container, false);
        etWeek = (EditText) viewHierarchy.findViewById(R.id.edit_hafta);
        etAge = (EditText) viewHierarchy.findViewById(R.id.edit_yosh);
        tvIntroTitle = (TextView) viewHierarchy.findViewById(R.id.tvIntroTitle);
        writeToUs = (TextView) viewHierarchy.findViewById(R.id.writeToUs);
        chooseAge = getResources().getString(R.string.choose_age);
        cancel = getResources().getString(R.string.cancel);
         sPref = getActivity().getSharedPreferences("informat", MODE_PRIVATE);

        changeLanguage();
        writeToUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGmail(getActivity(), new String[]{"untecidea@gmail.com"},
                        "REG",
                        "");            }
        });
        reg = new SignInGoogleMoneyHold(getActivity(), new SignInGoogleMoneyHold.UpdateSucsess() {
            @Override
            public void updateToSucsess() {
                etAge.setText(sPref.getString(HomilaConstants.EMAIL_USER, getString(R.string.app_name_main)));;
            }

            @Override
            public void updateToFailed() {

            }
        });

        etAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    reg.revokeAccess();
                    etAge.setText("");
                    reg.regitUser();
                }
                else reg.regitUser();
            }
        });
        return viewHierarchy;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public long getWeek() {
        if (dialog != null) {
            PregnancyWeek = dialog.backDate();
            long hafta_mili = PregnancyWeek.getTime();

            Date aip = new Date();
            aip.setTime(hafta_mili);
            return hafta_mili + 1;
        } else return 0;
    }

    public TextView getAge() {

        return etAge;
    }



    public void setNotTrueWeek() {
        etWeek.setText("");
        etWeek.setHintTextColor(Color.RED);
    }

    public void setNotTrueAge() {
        etAge.setText("");
        etAge.setHintTextColor(Color.RED);
    }

    @Override
    public void onStart() {
        super.onStart();
        changeLanguage();
    }
    public static void openGmail(Activity activity, String[] email, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = activity.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        activity.startActivity(emailIntent);
    }
    public void changeLanguage() {
        sPref = getActivity().getSharedPreferences("informat", MODE_PRIVATE);
        String language = sPref.getString("language", getResources().getString(R.string.language_default));
        if (language.equals(getResources().getString(R.string.uz))) {

            tvIntroTitle.setText(R.string.boshlaymizmi);
            etWeek.setHint(R.string.ohirgi_hayz_kuni);
            etAge.setHint(R.string.age);
        } else if (language.equals(getResources().getString(R.string.ru))) {

            tvIntroTitle.setText(R.string.boshlaymizmi_ru);
            etWeek.setHint(R.string.ohirgi_hayz_kuni_ru);
            etAge.setHint(R.string.age_ru);
        }
    }
    public void outSideActivityResult(Intent imageReturnedIntent){
        reg.regitRequstGet(imageReturnedIntent);

    }
    Runnable runable;
    Handler handler;
    boolean stop = false;
    public void checkForValidation(){
        if(NetworkUtils.isNetworkAvailable(getActivity())) {
            rootRef.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        UserInfo userinfo = new UserInfo();
                        userinfo.setFromSnapshot(dataSnapshot);
                        if (userinfo.getWhoIM().equals("D")) {
                            sPref.edit().putBoolean(SAVED_FIRST, false).apply();
                            Intent mainIntent = new Intent(getActivity(), HomilaDavri.class);
                            startActivity(mainIntent);
                            getActivity().finish();
                        } else {
                            request();
                        }
                    } else {

                        request();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                }
            });
        }
        else Toast.makeText(getActivity(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
    }

    public void request(){
        rootRef.child("Request/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/tel").setValue(etWeek.getText().toString());
        rootRef.child("Request/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        rootRef.child("Request/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/email").setValue(sPref.getString(HomilaConstants.EMAIL_USER,etAge.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final Dialog dialog = new Dialog(getActivity());
                final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_doc, null);
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
                        getActivity().finish();
                    }
                });
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                dialog.getWindow().setLayout(8 * width / 10, RelativeLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });
    }
}
