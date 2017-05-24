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
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.isoma.homiladavridoctor.Entity.nextstep.MaqolaEntity;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class InfoMaqolaFragment extends Fragment {
    private MaqolaEntity myStatya;
    private View frag;
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference GENERAL = dataset.getReference();
    public InfoMaqolaFragment() {
    }
    public InfoMaqolaFragment sharenformat(MaqolaEntity drugoy, View fragg) {
        myStatya=drugoy;
        frag=fragg;
         return this;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       }
    private CircleImageView cibAuthor;
    private TextView tvTheme;
    private TextView tvBody;
    private TextView tvAuthorName;
    private TextView tvWriter;
    private TextView tvLikes;
    private TextView tvComments;
    private ImageView ivMyPhoto;
    private ImageView tvHearts;
    private String myname;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed  ;
    private Context context;
    private RelativeLayout rvComments;

    PhotoViewAttacher attacher;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getReferenceFromUrl("gs://project-36196244065594115.appspot.com");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_nformat, container, false);

        context =getActivity();
        cibAuthor =(CircleImageView) V.findViewById(R.id.authphoto);
        tvTheme =(TextView) V.findViewById(R.id.themest);
        tvBody =(TextView) V.findViewById(R.id.bodyst);
        tvAuthorName =(TextView) V.findViewById(R.id.authname);
        tvWriter =(TextView) V.findViewById(R.id.creatat);
        tvLikes =(TextView) V.findViewById(R.id.hearts);
        tvComments =(TextView) V.findViewById(R.id.comments);
        ivMyPhoto =(ImageView) V.findViewById(R.id.photoStatiya);
        tvHearts =(ImageView) V.findViewById(R.id.emptyheart);
        rvComments =(RelativeLayout) V.findViewById(R.id.tcomment);

        sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);
        ed = sPref.edit();
        myname=sPref.getString(HomilaConstants.USER_NAME,"Anonymous");

        V.post(new Runnable() {
            @Override
            public void run() {
                frag.setVisibility(View.GONE);
            }
        });
        File extStoree = Environment.getExternalStorageDirectory();
        File myFilee = new File(extStoree.getAbsolutePath() + "/Homila/cache/mini/" + myStatya.getWriteBy()+ ".jpg");

        if (myFilee.exists()) {
            Picasso.with(context)
                    .load(myFilee)
                    .into(cibAuthor);
        }

        rvComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser authData = FirebaseAuth.getInstance().getCurrentUser();
                if (authData == null) {
                    try{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Сиз ҳам узунгизни қизиқтирган мақолаларни чоп етишингиз мумкун. Бунинг учун мақолангизни почта орқали юборишингиз ёки ушбу дастур орқали юборишингиз мумкун."+
                                "Дастур орқали юбориш учун илтимос руйхатдан утинг!")
                                .setPositiveButton("Руйхатдан ўтиш", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .add(R.id.frame, new RegistratsiyaFragment(), "REG").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .commit();}

                                }) .setNegativeButton("Ортга", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    }
                    catch(Exception o) {
                    }     }
            }
        });
       // Toast.makeText(getActivity(),myStatya.getWriteBy(),Toast.LENGTH_SHORT).show();


        tvAuthorName.setText(LinkerText.addClickablePart("@" + myStatya.getWriteBy() , new LinkerText.doSomethink() {
            @Override
            public void clickedA(String textView) {
                Toast.makeText(getActivity(),textView,Toast.LENGTH_SHORT).show();
            }
        }));
        tvAuthorName.setMovementMethod(LinkMovementMethod.getInstance());
        tvAuthorName.setHighlightColor(Color.TRANSPARENT);
        tvTheme.setText(myStatya.getTema());
        tvBody.setText(myStatya.getText());
        Date AAa = (new Date());
        AAa.setTime(myStatya.getDateLastChangedLong());
        tvWriter.setText((new SimpleDateFormat("dd.MM.yy HH:mm")).format(AAa));
        tvLikes.setText(Long.toString(myStatya.getLikeCount()));
        GENERAL.child("/Likes/Articles/"+myStatya.getPhotoId()+"/tvLikes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvLikes.setText(Long.toString((dataSnapshot.getValue(Long.class)==null)?0l:dataSnapshot.getValue(Long.class)));
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        if(!sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()),true)){
            tvHearts.setImageResource(R.drawable.heart);
        }



        tvHearts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()),true)){

                    tvHearts.setImageResource(R.drawable.heart);
                    GENERAL.child("/Likes/Articles/"+myStatya.getPhotoId()+"/tvLikes").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.setValue(1);
                            } else {
                                currentData.setValue((Long) currentData.getValue() + 1);
                            }
                            ed.putBoolean(Long.toString(myStatya.getDateLastChangedLong()),false);
                            ed.commit();
                            return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                            Log.d("Firebase",Boolean.toString(sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()),true)));
                            GENERAL.child("/Likes/Articles/"+myStatya.getPhotoId()+"/WhoLikeIt/"+myname).setValue(ServerValue.TIMESTAMP);
                        }
                    });
                }
                else{
                    tvHearts.setImageResource(R.drawable.emptyheart);
                    GENERAL.child("/Likes/Articles/"+myStatya.getPhotoId()+"/tvLikes").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.setValue(1);
                            } else {
                                currentData.setValue((Long) currentData.getValue() - 1);
                            }

                            ed.putBoolean(Long.toString(myStatya.getDateLastChangedLong()),true);
                            ed.commit();
                            return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                        }

                        @Override
                        public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                            Log.d("Firebase",Boolean.toString(sPref.getBoolean(Long.toString(myStatya.getDateLastChangedLong()),true)));
                            GENERAL.child("/Likes/Articles/"+myStatya.getPhotoId()+"/WhoLikeIt/"+myname).removeValue();


                        }
                    });
                }
            }
        });


        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/" + myStatya.getPhotoId()+ ".jpg");

        if (myFile.exists()) {
            Picasso.with(getActivity())
                    .load(myFile)
                    .into(ivMyPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher = new PhotoViewAttacher(ivMyPhoto);
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            try {
                File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/");
                if (!Aa.exists())
                    Aa.mkdirs();
                STORAGEREF.child("statiyalar/" + myStatya.getPhotoId()).getFile( new File(extStore.getAbsolutePath() + "/Homila/cache/" +myStatya.getPhotoId() + ".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Picasso.with(context)
                                .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/" + myStatya.getPhotoId() + ".jpg"))
                                .into(ivMyPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        attacher =new PhotoViewAttacher(ivMyPhoto);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }

                });

              /*  TransferObserver transferObservera = s3.download(
                        "homilaa",
                        "statiyalar/" + myStatya.getPhoto(),
                        new File(extStore.getAbsolutePath() + "/Homila/cache/" +myStatya.getPhoto() + ".jpg")
                );

                transferObservera.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d("awsS3adap", state + "");
                        if (state == TransferState.COMPLETED) {


                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        int percentage = (int) ((float) bytesCurrent / bytesTotal * 100);
                        Log.d("awsS3adap", percentage + "");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("awsS3adap", "error");
                    }
                });*/
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
            }

        }
       return V;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        frag.setVisibility(View.VISIBLE);
        frag=null;
    }

}
