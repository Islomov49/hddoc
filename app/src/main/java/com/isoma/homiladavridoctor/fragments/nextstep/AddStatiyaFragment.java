package com.isoma.homiladavridoctor.fragments.nextstep;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.isoma.homiladavridoctor.Entity.nextstep.MaqolaEntity;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.adapters.PhotoFiltersListAdapter;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.FiltersCollectionByTojiev;
import com.isoma.homiladavridoctor.utils.LinearManagerWithOutEx;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.compressImage;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.getPath;
import static com.isoma.homiladavridoctor.utils.BitmapOperations.savePhotoToCache;


public class AddStatiyaFragment extends Fragment {
    private final int tanlangan_image = 11;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getInstance().getReference();
    UploadTask uploadTask;
    StorageMetadata metadata;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed  ;
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference GENERAL = dataset.getReference();
    private  final int MY_PERMISSIONS_REQUEST_CAMERA = 44;
    private static final int MY_PERMISSIONS_READ_WRITE = 35;
    public  final int IMAGE_TANLANDI = 434;
    static final int REQUEST_IMAGE_CAPTURE = 433;
    private ProgressDialog pd;
    private EditText tema,obwiy;
    private ImageView kartina;
    PhotoFiltersListAdapter photoFiltersListAdapter;
    RecyclerView photoFilters;
    public AddStatiyaFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_addstatiya, container, false);
        photoFilters = (RecyclerView)  view.findViewById(R.id.recyclerPhotoFilters);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        ed = sPref.edit();
        ed.putString("tempPhoto","");
        ed.commit();

        ObservableScrollView scrla = (ObservableScrollView) view.findViewById(R.id.savaas);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToScrollView( scrla);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tema.getText().toString().equals("")){
                    if (!obwiy.getText().toString().equals("")){
                        pd = new ProgressDialog(getActivity());

                        pd.setMessage("Илтимос кутиб туринг");
                        pd.show();
                        final DatabaseReference A1=GENERAL.push();
                        Log.d("awsS3","BEGIN!");

                        try {
                            savePhotoToCache(A1.getKey(), resultBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {

                            Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    "/Homila/cache/" + A1.getKey() + ".jpg"));
                            metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpeg")
                                    .build();
                            uploadTask = STORAGEREF.child("statiyabuck/" + A1.getKey()).putFile(file, metadata);
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
                                    HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
                                    dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    MaqolaEntity temp = new MaqolaEntity( obwiy.getText().toString(), tema.getText().toString(),  firebaseUser.getUid(), A1.getKey(), System.currentTimeMillis(),false,((double)resultBitmap.getWidth())/((double)resultBitmap.getHeight()), CommonOperations.BitMapToString(Bitmap.createScaledBitmap(resultBitmap,resultBitmap.getWidth()/15,resultBitmap.getHeight()/15,true)));
                                    GENERAL.child("unchecked/maqolalar/" + A1.getKey()).setValue(temp, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                            Toast.makeText(getActivity(),"SUCCESS",Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                            ((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                                        }
                                    });
                                }
                            });
                        }
                    }
                else {
                        obwiy.setError(getString(R.string.maqola_matnini_kiriting));
                }
                }
                else {
                    tema.setError(getString(R.string.maqola_mazmunini_kiriting));
                }
            }
        });

        tema=(EditText) view.findViewById(R.id.tema);
        obwiy=(EditText) view.findViewById(R.id.maintext);
        kartina=(ImageView) view.findViewById(R.id.kartinaa);
        kartina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
        return view;
    }
    String Urik;
    Bitmap resultBitmap;
    @Override
    public void onResume(){
        super.onResume();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == tanlangan_image&&resultCode == RESULT_OK){
            Uri imageUriK = data.getData();
            File file=new File(getPath(imageUriK, getActivity()));
            resultBitmap = compressImage(file.getAbsolutePath());
            //TODO ESLI NUJIN KVADRATNAYA KARTINKA TO OTKRIT TOAST
//            if (b.getWidth() >= b.getHeight()) {
//                resultBitmap = Bitmap.createBitmap(b,b.getWidth() / 2 - b.getHeight() / 2,0,b.getHeight(),b.getHeight()
//                );
//
//            } else {
//                resultBitmap = Bitmap.createBitmap(b,0, b.getHeight() / 2 - b.getWidth() / 2,b.getWidth(),b.getWidth()
//                );
//            }
            photoFiltersUpdate();
            clearBitmap = resultBitmap.copy(resultBitmap.getConfig(), true);
            kartina.setImageBitmap(resultBitmap);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File flAvatar = new File(getContext().getExternalFilesDir(null), "temp.jpg");
            Bitmap b = compressImage(flAvatar.getAbsolutePath());
            Matrix m = new Matrix();
            resultBitmap = Bitmap.createBitmap(b,
                    0, 0, b.getWidth(), b.getHeight(),
                    m, true);
            //TODO ESLI NUJIN KVADRATNAYA KARTINKA TO OTKRIT TOAST
//            if (b.getWidth() >= b.getHeight()) {
//                resultBitmap = Bitmap.createBitmap(b,b.getWidth() / 2 - b.getHeight() / 2,0,b.getHeight(),b.getHeight()
//                );
//
//            } else {
//                resultBitmap = Bitmap.createBitmap(b,0, b.getHeight() / 2 - b.getWidth() / 2,b.getWidth(),b.getWidth()
//                );
//            }
            photoFiltersUpdate();
            clearBitmap = resultBitmap.copy(resultBitmap.getConfig(), true);
            kartina.setImageBitmap(resultBitmap);
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
                        kartina.setImageBitmap( resultBitmap);
                        break;
                    case 1:
                        resultBitmap = FiltersCollectionByTojiev.getStarLitFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartina.setImageBitmap(resultBitmap);
                        break;
                    case 2:
                        resultBitmap = FiltersCollectionByTojiev.getLimeStutterFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartina.setImageBitmap(  resultBitmap);
                        break;
                    case 3:
                        resultBitmap = FiltersCollectionByTojiev.getNightWhisperFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartina.setImageBitmap( resultBitmap);
                        break;
                    case 4:
                        resultBitmap = FiltersCollectionByTojiev.getAweStruckVibeFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartina.setImageBitmap( resultBitmap);
                        break;
                    case 5:
                        resultBitmap = FiltersCollectionByTojiev.getBlueMessFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartina.setImageBitmap( resultBitmap);
                        break;
                    default:
                        resultBitmap = FiltersCollectionByTojiev.getStarLitFilter().processFilter(clearBitmap.copy(clearBitmap.getConfig(), true));
                        kartina.setImageBitmap( resultBitmap);
                        break;
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearManagerWithOutEx(getContext(), LinearLayoutManager.HORIZONTAL, false);
        photoFilters.setLayoutManager(layoutManager);
        photoFilters.setAdapter(photoFiltersListAdapter);
        photoFilters.setVisibility(View.VISIBLE);

    }
    @Override
    public void onDetach() {
        super.onDetach();
        ed.putString("tempPhoto","");
        ed.commit();
    }

    public static Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            final int REQUIRED_SIZE=10;
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }


}
