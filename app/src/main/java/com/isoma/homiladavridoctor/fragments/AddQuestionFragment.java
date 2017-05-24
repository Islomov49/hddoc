package com.isoma.homiladavridoctor.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.QuestionEntity;
import com.isoma.homiladavridoctor.Entity.StateQuestion;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.adapters.PhotoFiltersListAdapter;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.FiltersCollectionByTojiev;
import com.isoma.homiladavridoctor.utils.LinearManagerWithOutEx;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.compressImage;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.getPath;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.savePhotoToCache;

public class AddQuestionFragment extends Fragment {
    private EditText yourEditText;
    private ImageView photo;
    private ImageView kartinka;
    private SharedPreferences sPref;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getInstance().getReference();
    UploadTask uploadTask;
    StorageMetadata metadata;
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference GENERAL = dataset.getReference();
    private  final int MY_PERMISSIONS_REQUEST_CAMERA = 44;
    private static final int MY_PERMISSIONS_READ_WRITE = 35;
    public  final int IMAGE_TANLANDI = 434;
    static final int REQUEST_IMAGE_CAPTURE = 433;
    private final int tanlangan_image = 11;
    RecyclerView photoFilters;
    TextView tvAddQuestion;
    String condition ;
    RadioGroup rgType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_question, container, false);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ((HomilaDavri)getActivity()).getSupportActionBar().setTitle(R.string.savol_berish);
        yourEditText= (EditText) view.findViewById(R.id.etSavolBody);
        tvAddQuestion= (TextView) view.findViewById(R.id.tvAddQuestion);
        photo = (ImageView) view.findViewById(R.id.addPhoto);
        rgType = (RadioGroup) view.findViewById(R.id.rgType);
        kartinka = (ImageView) view.findViewById(R.id.kartinaa);
        photoFilters = (RecyclerView)  view.findViewById(R.id.recyclerPhotoFilters);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);
        yourEditText.requestFocus();

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.choesetypeing))
                        .setItems(R.array.adding_ticket_type, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (ContextCompat.checkSelfPermission(getContext(),
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            ActivityCompat.requestPermissions((HomilaDavri) getContext(),
                                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    MY_PERMISSIONS_READ_WRITE);
                                        } else {
                                            ActivityCompat.requestPermissions((HomilaDavri) getContext(),
                                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    MY_PERMISSIONS_READ_WRITE);
                                        }
                                    } else {
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, tanlangan_image);
                                    }
                                } else if (which == 1) {
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                        if (ContextCompat.checkSelfPermission(getContext(),
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                                ActivityCompat.requestPermissions((HomilaDavri) getContext(),
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        MY_PERMISSIONS_REQUEST_CAMERA);
                                            } else {
                                                ActivityCompat.requestPermissions((HomilaDavri) getContext(),
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        MY_PERMISSIONS_REQUEST_CAMERA);
                                            }
                                        } else {
                                            File f = new File(getContext().getExternalFilesDir(null), "temp.jpg");
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                        }
                                    }
                                }
                            }
                        });
                builder.create().show();

            }
        });
        tvAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(yourEditText.getText().toString().isEmpty()){
                   yourEditText.setError(getString(R.string.savolingizni_kiriting));
                    return;
                }

                if(R.id.hammaga == rgType.getCheckedRadioButtonId()){
                    condition = "0>";
                }
                else if(R.id.mandankottalaga == rgType.getCheckedRadioButtonId()){
                    condition = sPref.getInt(HomilaConstants.SAVED_WEEK,-1)+">";
                }
                //TODO CHOISED COUNTRY
                final String countryCode = "uz";
                final DatabaseReference pushId=GENERAL.push();
                showProgressDialog(getString(R.string.yuklanmoqda));
                if (resultBitmap == null) {
                    final QuestionEntity questionEntity = new QuestionEntity(condition,countryCode,sPref.getString(HomilaConstants.LANGUAGE,sPref.getString("language","uz")),"",yourEditText.getText().toString(),new StateQuestion(), FirebaseAuth.getInstance().getCurrentUser().getUid(),"");
                    DatabaseReference push = GENERAL.child("Questions/").push();
                    HashMap<String,Object> pushObjects = new HashMap<String, Object>();
                    pushObjects.put("/Questions/"+push.getKey(),questionEntity.toMap());
                    HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                    nevEvent.put("date", ServerValue.TIMESTAMP);
                    pushObjects.put("/UserQuestionList/"+questionEntity.getWriterUID()+"/"+push.getKey(),nevEvent);

                    GENERAL.child("user-status/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/achivments/exp").runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            long p =0;
                            if(mutableData.getValue()!=null)
                                p = mutableData.getValue(Integer.class);
                            mutableData.setValue(p+3);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {
                        }
                    });
                    GENERAL.child("user-status/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/achivments/question").runTransaction(new Transaction.Handler() {
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
                    GENERAL.updateChildren(pushObjects, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            hideProgressDialog();
                            EventBus.getDefault().post(new EventMessage(questionEntity,"added","all"));
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm == null) return;
                            imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);
                            ((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                        }
                    });
                }
                else {
                    try {
                        savePhotoToCache(pushId.getKey(), resultBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/Homila/cache/" + pushId.getKey() + ".jpg"));
                        metadata = new StorageMetadata.Builder()
                                .setContentType("image/jpeg")
                                .build();
                        uploadTask = STORAGEREF.child("QuestionsPhotos/" + pushId.getKey()).putFile(file, metadata);
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    Log.d("awsS3", progress + "");
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
                                final QuestionEntity questionEntity = new QuestionEntity(condition,countryCode,sPref.getString(HomilaConstants.LANGUAGE,sPref.getString(HomilaConstants.LANGUAGE,"uz")),pushId.getKey(),yourEditText.getText().toString(),new StateQuestion(), FirebaseAuth.getInstance().getCurrentUser().getUid(), CommonOperations.BitMapToString(Bitmap.createScaledBitmap(resultBitmap,resultBitmap.getWidth()/15,resultBitmap.getHeight()/15,true)));

                                DatabaseReference push = GENERAL.child("Questions/").push();

                                HashMap<String,Object> pushObjects = new HashMap<String, Object>();
                                pushObjects.put("Questions/"+push.getKey(),questionEntity.toMap());
                                   HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                                   nevEvent.put("date", ServerValue.TIMESTAMP);
                                pushObjects.put("UserQuestionList/"+questionEntity.getWriterUID()+"/"+push.getKey(),nevEvent);


                                GENERAL.child("user-status/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/achivments/exp").runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        long p =0;
                                        if(mutableData.getValue()!=null)
                                            p = mutableData.getValue(Integer.class);
                                        mutableData.setValue(p+3);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                    }
                                });
                                GENERAL.child("user-status/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/achivments/question").runTransaction(new Transaction.Handler() {
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

                                GENERAL.updateChildren(pushObjects, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        hideProgressDialog();
                                        EventBus.getDefault().post(new EventMessage(questionEntity,"added","all"));
                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm == null) return;
                                        imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);
                                        ((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                                    }
                                });
                            }
                        });
                    }
                }

            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        File f = new File(getContext().getExternalFilesDir(null), "temp.jpg");
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                    return;
                }
                break;
            case MY_PERMISSIONS_READ_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, tanlangan_image);
                }
                break;


        }
    }
    Bitmap resultBitmap;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == tanlangan_image&&resultCode == RESULT_OK){
            Uri imageUriK = data.getData();

            File file=new File(getPath(imageUriK, getActivity()));
            Bitmap b = compressImage(file.getAbsolutePath());
            //TODO ESLI NUJIN KVADRATNAYA KARTINKA TO OTKRIT TOAST
            if (b.getWidth() >= b.getHeight()) {
                resultBitmap = Bitmap.createBitmap(b,b.getWidth() / 2 - b.getHeight() / 2,0,b.getHeight(),b.getHeight()
                );

            } else {
                resultBitmap = Bitmap.createBitmap(b,0, b.getHeight() / 2 - b.getWidth() / 2,b.getWidth(),b.getWidth()
                );
            }
            photoFiltersUpdate();
            clearBitmap = resultBitmap.copy(resultBitmap.getConfig(), true);
            kartinka.setImageBitmap(resultBitmap);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File flAvatar = new File(getContext().getExternalFilesDir(null), "temp.jpg");
            Bitmap b = compressImage(flAvatar.getAbsolutePath());
            Matrix m = new Matrix();
            resultBitmap = Bitmap.createBitmap(b,
                    0, 0, b.getWidth(), b.getHeight(),
                    m, true);
            //TODO ESLI NUJIN KVADRATNAYA KARTINKA TO OTKRIT TOAST
            if (b.getWidth() >= b.getHeight()) {
                resultBitmap = Bitmap.createBitmap(b,b.getWidth() / 2 - b.getHeight() / 2,0,b.getHeight(),b.getHeight()
                );

            } else {
                resultBitmap = Bitmap.createBitmap(b,0, b.getHeight() / 2 - b.getWidth() / 2,b.getWidth(),b.getWidth()
                );
            }
            photoFiltersUpdate();
            clearBitmap = resultBitmap.copy(resultBitmap.getConfig(), true);
            kartinka.setImageBitmap(resultBitmap);
        }

    }


    Bitmap clearBitmap;
    public void photoFiltersUpdate(){
        PhotoFiltersListAdapter photoFiltersListAdapter = new PhotoFiltersListAdapter(getContext(), Bitmap.createScaledBitmap(resultBitmap, 100, 100, true), new PhotoFiltersListAdapter.AddPhotoEffects() {
            @Override
            public void effectSelected(int positionEffect) {
                switch (positionEffect){
                    case 0:
                        resultBitmap = clearBitmap.copy(clearBitmap.getConfig(), true);
                        kartinka.setImageBitmap( resultBitmap);
                        break;
                    case 1:
                        resultBitmap = FiltersCollectionByTojiev.getStarLitFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartinka.setImageBitmap(resultBitmap);
                        break;
                    case 2:
                        resultBitmap = FiltersCollectionByTojiev.getLimeStutterFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartinka.setImageBitmap(  resultBitmap);
                        break;
                    case 3:
                        resultBitmap = FiltersCollectionByTojiev.getNightWhisperFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartinka.setImageBitmap( resultBitmap);
                        break;
                    case 4:
                        resultBitmap = FiltersCollectionByTojiev.getAweStruckVibeFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartinka.setImageBitmap( resultBitmap);
                        break;
                    case 5:
                        resultBitmap = FiltersCollectionByTojiev.getBlueMessFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartinka.setImageBitmap( resultBitmap);
                        break;
                    default:
                        resultBitmap = FiltersCollectionByTojiev.getStarLitFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartinka.setImageBitmap( resultBitmap);
                        break;
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearManagerWithOutEx(getContext(), LinearLayoutManager.HORIZONTAL, false);

        photoFilters.setLayoutManager(layoutManager);
        photoFilters.setAdapter(photoFiltersListAdapter);
        photoFilters.setVisibility(View.VISIBLE);

    }



    private ProgressDialog mProgressDialog;
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
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
    public void onDetach() {
        super.onDetach();
    }
}
