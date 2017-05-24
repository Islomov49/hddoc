package com.isoma.homiladavridoctor.fragments.nextstep;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.isoma.homiladavridoctor.Entity.nextstep.Forcoments;
import com.isoma.homiladavridoctor.Entity.nextstep.Lentadeteli;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.fragments.RegistratsiyaFragment;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.LinkerText;
import com.isoma.homiladavridoctor.utils.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class InfoLentaFragment extends Fragment {
    private Lentadeteli myStatya;
    private View frag;
    boolean forcome = false;
    private CircleImageView cibAuthor;
    private PhotoViewAttacher attacher;
    private TextView tvBody;
    private TextView tvAuthorName;
    private TextView tvWriter;
    private TextView tvLikes;
    private TextView tvComments;
    private ImageView tvMyPhoto;
    private ImageView tvHearts;
    private String myname;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private Context context;
    private RelativeLayout rvComments;
    private ScrollView svHorizontal;
    private EditText etComments;
    private ImageView ivSender;
    private ImageView ivAddAnother;
    private Long headCreateAt;
    private Long lastCreateAt;
    private LinearLayout llComment;
    private ChildEventListener eventListener;
    private boolean isSend = false;
    private List<DataSnapshot> coments;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getReferenceFromUrl("gs://project-36196244065594115.appspot.com");
    FirebaseDatabase dataset = FirebaseDatabase.getInstance();
    DatabaseReference GENERAL = dataset.getReference();
    DatabaseReference fordel;

    public InfoLentaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public InfoLentaFragment sharenformat(Lentadeteli drugoy, View fragg, boolean isItForCom, List<DataSnapshot> listcoment) {
        myStatya = drugoy;
        frag = fragg;
        coments = listcoment;
        forcome = isItForCom;
        Log.d("commentss", coments.size() + "");
        Log.d("isbardak", "OnInstalize: " + coments.size());
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_nformat_lenta, container, false);
        Log.d("isbardak", "OnCreate: " + coments.size());
        context = getActivity();
        cibAuthor = (CircleImageView) V.findViewById(R.id.authphoto);
        tvBody = (TextView) V.findViewById(R.id.bodyst);
        tvAuthorName = (TextView) V.findViewById(R.id.authname);
        tvWriter = (TextView) V.findViewById(R.id.creatat);
        llComment = (LinearLayout) V.findViewById(R.id.comentike);
        tvLikes = (TextView) V.findViewById(R.id.hearts);
        tvComments = (TextView) V.findViewById(R.id.comments);
        tvMyPhoto = (ImageView) V.findViewById(R.id.photoStatiya);
        tvHearts = (ImageView) V.findViewById(R.id.emptyheart);
        ivAddAnother = (ImageView) V.findViewById(R.id.addothercoments);
        rvComments = (RelativeLayout) V.findViewById(R.id.tcomment);
        etComments = (EditText) V.findViewById(R.id.commentmes);
        ivSender = (ImageView) V.findViewById(R.id.imageView21);
        sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);
        ed = sPref.edit();
        svHorizontal = (ScrollView) V.findViewById(R.id.horizsc);
        myname = sPref.getString(HomilaConstants.USER_NAME, "Anonymous");
        headCreateAt = 0l;
        lastCreateAt = 0l;
        V.post(new Runnable() {
            @Override
            public void run() {
                frag.setVisibility(View.GONE);
                if (forcome)
                    svHorizontal.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        File extStoree = Environment.getExternalStorageDirectory();
        File myFilee = new File(extStoree.getAbsolutePath() + "/Homila/cache/mini/" + myStatya.getWriteBy() + ".jpg");
        if (myFilee.exists()) {
            Picasso.with(context)
                    .load(myFilee)
                    .into(cibAuthor);
        }
        etComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etComments.getText().toString().equals("") && !isSend) {
                    ivSender.setImageResource(R.drawable.sendddd_full);
                    svHorizontal.fullScroll(View.FOCUS_DOWN);
                    isSend = true;
                } else if (etComments.getText().toString().equals("")) {
                    ivSender.setImageResource(R.drawable.sendddd);
                    isSend = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (coments.size() != 0) {
            lastCreateAt = coments.get(0).child("createAt").getValue(Long.class);
            for (DataSnapshot comik : coments) {
                headCreateAt = comik.child("createAt").getValue(Long.class);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView tv = new TextView(context);
                lparams.setMargins(16, 4, 16, 0);
                tv.setLayoutParams(lparams);
                tv.setTextColor(Color.parseColor("#89000000"));
                tv.setTextSize(14);
                String mText = "@" + comik.child("whoIm").getValue(String.class) + " " + comik.child("glavText").getValue(String.class);
                tv.setText(LinkerText.addClickablePart(getSafeSubstring(mText, 600), new LinkerText.doSomethink() {
                    @Override
                    public void clickedA(String textView) {
                        Toast.makeText(context, textView, Toast.LENGTH_SHORT).show();
                    }
                }));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setHighlightColor(Color.TRANSPARENT);
                llComment.addView(tv);
            }
            Log.d("coments", "headCreateAt: " + headCreateAt);
            Log.d("coments", "lastCreateAt: " + lastCreateAt);
        }
        fordel = GENERAL.child("messageRooms/Comments/" + myStatya.getKEY());
        eventListener = GENERAL.child("messageRooms/Comments/" + myStatya.getKEY()).orderByChild("createAt").startAt(headCreateAt + 1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("isbardak", "onChildAdded: " + coments.size() + "  s");
                coments.add(dataSnapshot);
                refreshComents();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ivAddAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GENERAL.child("messageRooms/Comments/" + myStatya.getKEY()).orderByChild("createAt").endAt(lastCreateAt - 1).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int t = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            coments.add(t++, data);
                        }
                        refreshComents();
                        if (t < 10) {
                            ivAddAnother.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

            }
        });
        V.findViewById(R.id.imageView21).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSend) {
                    if (forcome)
                        svHorizontal.fullScroll(ScrollView.FOCUS_DOWN);
                    Forcoments newMessage = new Forcoments(etComments.getText().toString(), myname);
                    DatabaseReference forPush = GENERAL.push();
                    etComments.setText("");
                    GENERAL.child("messageRooms/Comments/" + myStatya.getKEY() + "/" + forPush.getKey()).setValue(newMessage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                            Toast.makeText(getActivity(), "Xabar yuborildi", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        rvComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser authData = FirebaseAuth.getInstance().getCurrentUser();
                if (authData == null) {
                    try {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Сиз ҳам узунгизни қизиқтирган мақолаларни чоп етишингиз мумкун. Бунинг учун мақолангизни почта орқали юборишингиз ёки ушбу дастур орқали юборишингиз мумкун." +
                                "Дастур орқали юбориш учун илтимос руйхатдан утинг!")
                                .setPositiveButton("Руйхатдан ўтиш", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .add(R.id.frame, new RegistratsiyaFragment(), "REG").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();
                                    }

                                }).setNegativeButton("Ортга", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    } catch (Exception o) {
                    }
                }
            }
        });
        // Toast.makeText(getActivity(),myStatya.getWriteBy(),Toast.LENGTH_SHORT).show();


        tvAuthorName.setText(LinkerText.addClickablePart("@" + myStatya.getWriteBy(), new LinkerText.doSomethink() {
            @Override
            public void clickedA(String textView) {
                Toast.makeText(getActivity(), textView, Toast.LENGTH_SHORT).show();
            }
        }));
        tvAuthorName.setMovementMethod(LinkMovementMethod.getInstance());
        tvAuthorName.setHighlightColor(Color.TRANSPARENT);
        tvBody.setText(myStatya.getText());
        Date AAa = (new Date());
        AAa.setTime(myStatya.getDateLastChangedLong());
        tvWriter.setText((new SimpleDateFormat("dd.MM.yy HH:mm")).format(AAa));
        tvLikes.setText(Long.toString(myStatya.getLikes()));
        GENERAL.child("/Likes/Articles/" + myStatya.getKEY() + "/tvLikes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvLikes.setText(Long.toString((dataSnapshot.getValue(Long.class) == null) ? 0l : dataSnapshot.getValue(Long.class)));
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        if (!sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()), true)) {
            tvHearts.setImageResource(R.drawable.heart);
        }
        tvHearts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()), true)) {

                    tvHearts.setImageResource(R.drawable.heart);
                    GENERAL.child("/Likes/Articles/" + myStatya.getKEY() + "/tvLikes").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.setValue(1);
                            } else {
                                currentData.setValue((Long) currentData.getValue() + 1);
                            }
                            ed.putBoolean(Long.toString(myStatya.getDateLastChangedLong()), false);
                            ed.commit();
                            return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                            Log.d("Firebase", Boolean.toString(sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()), true)));
                            GENERAL.child("/Likes/Articles/" + myStatya.getKEY() + "/WhoLikeIt/" + myname).setValue(ServerValue.TIMESTAMP);
                        }
                    });
                } else {
                    tvHearts.setImageResource(R.drawable.emptyheart);
                    GENERAL.child("/Likes/Articles/" + myStatya.getKEY() + "/tvLikes").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.setValue(1);
                            } else {
                                currentData.setValue((Long) currentData.getValue() - 1);
                            }

                            ed.putBoolean(Long.toString(myStatya.getDateLastChangedLong()), true);
                            ed.commit();
                            return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                            Log.d("Firebase", Boolean.toString(sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()), true)));
                            GENERAL.child("/Likes/Articles/" + myStatya.getKEY() + "/WhoLikeIt/" + myname).removeValue();


                        }
                    });
                }
            }
        });
        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/" + myStatya.getPhoto() + ".jpg");
        if (myFile.exists()) {
            Picasso.with(getActivity())
                    .load(myFile)
                    .into(tvMyPhoto);
            attacher = new PhotoViewAttacher(tvMyPhoto);
            if (forcome)
                svHorizontal.fullScroll(ScrollView.FOCUS_DOWN);
        } else {
            try {
                File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/");
                if (!Aa.exists())
                    Aa.mkdirs();
                STORAGEREF.child("statiyalar/" + myStatya.getPhoto()).getFile(new File(extStore.getAbsolutePath() + "/Homila/cache/" + myStatya.getPhoto() + ".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Picasso.with(context)
                                .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/" + myStatya.getPhoto() + ".jpg"))
                                .into(tvMyPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        attacher = new PhotoViewAttacher(tvMyPhoto);
                                        if (forcome)
                                            svHorizontal.fullScroll(ScrollView.FOCUS_DOWN);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }

                });
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
            }
        }
        return V;
    }

    private void refreshComents() {
        Log.d("someone", "refrew " + coments.size());
        Log.d("isbardak", "Refreshed: " + coments.size());
        if (coments.size() == 0) {
            return;
        }
        llComment.removeAllViews();
        lastCreateAt = coments.get(0).child("createAt").getValue(Long.class);
        for (DataSnapshot comik : coments) {
            headCreateAt = comik.child("createAt").getValue(Long.class);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final TextView tv = new TextView(context);
            lparams.setMargins(16, 4, 16, 0);
            tv.setLayoutParams(lparams);
            tv.setTextColor(Color.parseColor("#89000000"));
            tv.setTextSize(14);
            String mText = "@" + comik.child("whoIm").getValue(String.class) + " " + comik.child("glavText").getValue(String.class);
            tv.setText(LinkerText.addClickablePart(getSafeSubstring(mText, 600), new LinkerText.doSomethink() {
                @Override
                public void clickedA(String textView) {
                    Toast.makeText(context, textView, Toast.LENGTH_SHORT).show();
                }
            }));
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setHighlightColor(Color.TRANSPARENT);
            llComment.addView(tv);
        }

    }

    public String getSafeSubstring(String s, int maxLength) {
        if (!TextUtils.isEmpty(s)) {
            if (s.length() >= maxLength) {
                int t;
                int siz = s.length();
                for (t = maxLength; t < siz; t++) {
                    if (s.charAt(t) == ' ' || s.charAt(t) == '.' || s.charAt(t) == '@') {
                        break;
                    }
                }
                if (t == siz - 1) {
                    return s;
                }
                return s.substring(0, t) + " ...";
            }
        }
        return s;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fordel.removeEventListener(eventListener);
        frag.setVisibility(View.VISIBLE);
        frag = null;
        Log.d("isDestroyed", "yeah");
    }

}
