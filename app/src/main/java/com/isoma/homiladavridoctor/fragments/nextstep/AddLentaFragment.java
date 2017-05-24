package com.isoma.homiladavridoctor.fragments.nextstep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isoma.homiladavridoctor.Entity.nextstep.Lentadeteli;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.isoma.homiladavridoctor.utils.BitmapOperations.compressImage;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.getPath;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.savePhotoToCache;

public class AddLentaFragment extends Fragment {
    private final int tanlangan_image = 11;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference GENERAL = dataset.getReference();
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed  ;
    private EditText obwiy;
    private ImageView kartina;
    private ProgressDialog pd;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getReferenceFromUrl("gs://project-36196244065594115.appspot.com");
    UploadTask uploadTask;
    StorageMetadata metadata;
    public AddLentaFragment shareAddstatiya() {
        return this;
    }
    public AddLentaFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_addlenta, container, false);
        metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed = sPref.edit();
        ed.putString("tempPhoto","");
        ed.commit();
        ObservableScrollView scrla = (ObservableScrollView) V.findViewById(R.id.savaas);
        final FloatingActionButton fab = (FloatingActionButton) V.findViewById(R.id.fab);
        fab.attachToScrollView( scrla);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!obwiy.getText().toString().equals("")){
                        pd = new ProgressDialog(getActivity());
                        pd.setMessage("Илтимос кутиб туринг");
                        pd.show();
                        final DatabaseReference A1=GENERAL.push();
                        Log.d("awsS3","BEGIN!");
                        try {
                            savePhotoToCache(A1.getKey(), bibi);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    "/Homila/cache/"+A1.getKey()+".jpg"));
                            uploadTask=STORAGEREF.child("lentabuck/"+A1.getKey()).putFile(file,metadata);
                            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    Log.d("awsS3",progress +"");
                                }
                            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                    System.out.println("Upload is paused");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
                                    dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
                                    Lentadeteli temp=new Lentadeteli(A1.getKey(),obwiy.getText().toString(), 0l,sPref.getString(HomilaConstants.USER_NAME,"ERROR SYS"),A1.getKey(), System.currentTimeMillis());
                                    GENERAL.child("listoflenta/"+A1.getKey()).setValue(temp, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                            Log.d("awsS3","All of good!");
                                            pd.dismiss();
                                            Fragment temp03 = getActivity().getSupportFragmentManager().
                                                    findFragmentByTag("addstat");
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction().remove(temp03).commit();
                                        }
                                    });
                                }
                            });
                        }
                    }
            }
        });
        V.post(new Runnable() {
            @Override
            public void run() {
            }
        });

        obwiy=(EditText) V.findViewById(R.id.maintext);
        kartina=(ImageView) V.findViewById(R.id.kartinaa);
        kartina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                getActivity().startActivityForResult(photoPickerIntent, tanlangan_image);
            }
        });
        return V;
    }
    String Urik;
    Bitmap bibi;
    @Override
    public void onResume(){
        super.onResume();
        Urik=sPref.getString("tempPhoto", "");
        if(!Urik.equals("")) {
            File file=new File(getPath(Uri.parse(Urik),getActivity()));
            Bitmap b = compressImage(file.toString());
            bibi=b;
           kartina.setImageBitmap(b);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        ed.putString("tempPhoto","");
        ed.commit();
    }



}
