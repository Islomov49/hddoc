package com.isoma.homiladavridoctor.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isoma.homiladavridoctor.Entity.AnketaEntity;
import com.isoma.homiladavridoctor.Entity.UserInfo;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.Bitmaps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class RegistratsiyaFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference genRefer = dataset.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getReference();

    DownloadImageTask imagetask;
        UploadTask uploadTask;

    StorageMetadata metadata;
    Bitmap newUserAvatar = null;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 18;
    private static final int MY_PERMISSIONS_READ_WRITE = 38;
    public static final int IMAGE_TANLANDI = 131;
    CircleImageView ivAccountImage;
    EditText etAccountName;
    TextView tvWeek;
    TextView tvEmail;
    TextView tvKirish;
    TextView tvLanguages;
    TextView tvSuratniOzgartirish;
    private long CREATE;
    private int HAFTA_SONI;
    private SharedPreferences sPref;
    int hafta =1;
    static final int REQUEST_IMAGE_CAPTURE = 112;
    boolean isWithoutSyntxError = true;
    private String language = "uz";
    public RegistratsiyaFragment() {

        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_reg, container, false);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);

        ivAccountImage = (CircleImageView) view.findViewById(R.id.ivAccountImage);
        etAccountName = (EditText) view.findViewById(R.id.tvAccountName);
        tvKirish = (TextView) view.findViewById(R.id.tvKirish);
        tvLanguages = (TextView) view.findViewById(R.id.tvLanguages);
        tvWeek = (TextView) view.findViewById(R.id.tvWeek);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvSuratniOzgartirish = (TextView) view.findViewById(R.id.tvSuratniOzgartirish);

        ((HomilaDavri)getActivity()).goneAllItems();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                if (user.getPhotoUrl() != null) {
                    imagetask = new DownloadImageTask(ivAccountImage);
                    imagetask.execute(user.getPhotoUrl().toString());
                }
            } catch (Exception o) {

            }
            etAccountName.setText(user.getDisplayName().replace(' ','_').toLowerCase());

        }
        CREATE=sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
        HAFTA_SONI=(int)((System.currentTimeMillis()-CREATE)/1000/60/60/24/7);
        if(CREATE==0){
            HAFTA_SONI=1;}
        if (HAFTA_SONI < 1) {
            HAFTA_SONI = 1;

        }
        tvWeek.setText(HAFTA_SONI+" "+getString(R.string.xaftada));
        hafta = HAFTA_SONI;
        if(!sPref.getString(HomilaConstants.EMAIL_USER,"").equals(""))
        tvEmail.setText(sPref.getString(HomilaConstants.EMAIL_USER,getString(R.string.app_name_main)));
        else ((HomilaDavri)getActivity()).getPaFragmentManager().displayMainWindow();

        tvSuratniOzgartirish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
            }
        });
        ivAccountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
            }
        });

        tvWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle(R.string.hafta_kuni);
                final ArrayList<String> strings = new ArrayList<String>();
                for(int i=1;i<=40;i++){
                    strings.add(Integer.toString(i)+" "+getString(R.string.xaftada));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,strings);
                builderSingle.setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hafta = which+1;
                        tvWeek.setText(hafta+" "+getString(R.string.xaftada));
                    }
                });
                AlertDialog alertDialog = builderSingle.create();
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int hieght = displayMetrics.heightPixels;
                alertDialog.show();
                alertDialog.getWindow().setLayout(9 * width / 10, (int) (6.8*hieght/10));
            }
        });
        tvLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle(R.string.qaysi_tilni_bilasiz);
                ArrayList<String> locations = new ArrayList<String>();
                locations.add(getString(R.string.ozbekiston));
                locations.add(getString(R.string.qozogiston_skoro));
                locations.add(getString(R.string.Rossiya_skoro));
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,locations);
                builderSingle.setNegativeButton(R.string.ortga, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            language = "uz";
                        }
                        else if(which == 1 || which == 2){
                            language = "uz";
                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.bu_davlat_hali_yo)
                                    .setPositiveButton(R.string.ozbekistonni_tanlash, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    }).setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }

                        tvLanguages.setText(getString(R.string.ozbekiston));

                    }
                });
                AlertDialog alertDialog = builderSingle.create();
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int hieght = displayMetrics.heightPixels;
                alertDialog.show();
                alertDialog.getWindow().setLayout(9 * width / 10, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
        etAccountName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Pattern pattern = Pattern.compile("[a-zA-Z0-9\\_\\-]{0,}");
                Matcher matcher = pattern.matcher(etAccountName.getText().toString());
                if (!matcher.matches()){
                    isWithoutSyntxError = false;
                    etAccountName.setError(getString(R.string.mumkun_bolmagan_belgi));
                    return;
                }
                else {
                    isWithoutSyntxError = true;
                    etAccountName.setError(null);
                }
            }
        });
        tvKirish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(newUserAvatar!=null){
                OutputStream outFile = null;
                File file = new File(getActivity().getFilesDir(), "userphoto.jpg");
                try {
                    outFile = new FileOutputStream(file);
                    newUserAvatar.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            if(!etAccountName.getText().toString().equals("")&&etAccountName.getText().length()>=4&&isWithoutSyntxError){
                final ProgressDialog mProgressDialog;
                mProgressDialog= new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.boglasnish));
                mProgressDialog.show();
                Query one=genRefer.child("users/").orderByChild("nickName").equalTo(etAccountName.getText().toString().toLowerCase());
                one.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue()!=null)
                           {
                               mProgressDialog.hide();
                               etAccountName.setError(getString(R.string.mavjud_ism));
                            }
                            else{



                                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                                TelephonyManager tm =  ( TelephonyManager ) getActivity().getSystemService ( Context . TELEPHONY_SERVICE );
                                final String countryCode = tm . getSimCountryIso ();
                                String myLongName=sPref.getString(HomilaConstants.SAVED_NAME,"");
                                String myage=Long.toString(sPref.getLong(HomilaConstants.SAVED_AGE, 100));
                                String myDoctorname=sPref.getString(HomilaConstants.SAVED_VISM, "");
                                String myDoctorNumber=sPref.getString(HomilaConstants.SAVED_NUMBER, "");
                                String myPoliklinika=sPref.getString(HomilaConstants.SAVED_HOSPITAL, "");
                                String myQongruh=sPref.getString(HomilaConstants.SAVED_BLOOD, "");
                                String myManzil=sPref.getString(HomilaConstants.SAVED_ADDRESS, "");;
                                String myhusband=sPref.getString(HomilaConstants.SAVED_ERI, "");;
                                String myhusbandNumber=sPref.getString(HomilaConstants.SAVED_ERI_NAME, "");
                                HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
                                dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
                                AnketaEntity anketaEntity = new AnketaEntity(myDoctorname,myDoctorNumber,myPoliklinika,myQongruh,myManzil,myhusband,myhusbandNumber);
                                UserInfo IAM=new UserInfo(language,etAccountName.getText().toString().toLowerCase(),countryCode,false,myLongName,myage,"P",hafta,dateLastChangedObj,anketaEntity);
                                IAM.setCountryCode("uz");
                                sPref.edit().putString(HomilaConstants.LANGUAGE,language).apply();



                                Map<String, Object> allInfoContener = new HashMap<String, Object>();
                                Map<String, Object> statusMAP = new HashMap<String, Object>();

                                statusMAP.put("avatar","");
                                statusMAP.put("nickName",etAccountName.getText().toString().toLowerCase());
                                statusMAP.put("lastseen", ServerValue.TIMESTAMP);
                                statusMAP.put("online",false);
                                statusMAP.put("week",hafta);

                                allInfoContener.put("/users/"+ uid,  IAM.toMap());
                                allInfoContener.put("/user-status/"+uid, statusMAP);
                                genRefer.updateChildren(allInfoContener, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                metadata = new StorageMetadata.Builder()
                                                        .setContentType("image/jpeg")
                                                        .build();
                                                final DatabaseReference photoPath = genRefer.push();



                                                Bitmap newBit = Bitmaps.createScaledBitmap(newUserAvatar, 320, 320, true);
                                                try {
                                                    savePhotoToCache(photoPath.getKey(), newBit);
                                                } catch (Exception o) {
                                                } finally {
                                                    Uri filek = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                                            "/Homila/cache/" + photoPath.getKey() + ".jpg"));

                                                    uploadTask = STORAGEREF.child("users/" + uid + "/" + photoPath.getKey()).putFile(filek, metadata);

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
                                                            System.out.println("Upload is onFailure");
                                                            mProgressDialog.hide();
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            mProgressDialog.hide();

                                                            Map<String, Object> PhotoUp = new HashMap<String, Object>();
                                                            PhotoUp.put("users/" + uid + "/avatar/" + photoPath.getKey(), ServerValue.TIMESTAMP);
                                                            PhotoUp.put("user-status/" + uid + "/avatar", photoPath.getKey());
                                                            genRefer.updateChildren(PhotoUp);
                                                            ((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                                                            ((HomilaDavri)getActivity()).getPaFragmentManager().displayFragment(new QuestionsViewPagerFragment());
                                                        }
                                                    });
                                                }
                                                sPref.edit().putString("extraname",etAccountName.getText().toString()).apply();
                                            }
                                        });

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            mProgressDialog.hide();
                            Toast.makeText(getContext(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                        }
                    });




            }
            else if(etAccountName.getText().toString().equals("")) {
                etAccountName.setError(getString(R.string.ismingizni_kiritinf));
            }
            else if(etAccountName.getText().toString().indexOf(' ')!=-1){
                etAccountName.setError(getString(R.string.mumkun_bolmagan_belgi));
                etAccountName.setText(etAccountName.getText().toString().replace(' ','_'));
            }
            else {
                etAccountName.setError(getString(R.string.ism_kichkina));
            }
            }
        });



        return view;
    }
        public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void savePhotoToCache(String name,Bitmap bmp) throws IOException {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Homila/cache";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, name + ".jpg");
        FileOutputStream fOut = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG,  85, fOut);
        fOut.flush();
        fOut.close();
    }


    private void getPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_TANLANDI);
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
                    getPhoto();
                }
                break;


        }
    }


    private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            final int REQUIRED_SIZE=350;
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_TANLANDI){
            if (resultCode == RESULT_OK) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(data.getData(),
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                File flAvatar = new File(picturePath);
                Bitmap b;

                b = decodeFile(flAvatar);
                Matrix m = new Matrix();
                m.postRotate(neededRotation(flAvatar));

                if (b.getWidth() >= b.getHeight()) {
                    newUserAvatar = Bitmap.createBitmap(b,b.getWidth() / 2 - b.getHeight() / 2,0,b.getHeight(),b.getHeight()
                    );

                } else {
                    newUserAvatar = Bitmap.createBitmap(b,0, b.getHeight() / 2 - b.getWidth() / 2,b.getWidth(),b.getWidth()
                    );
                }

                newUserAvatar = Bitmap.createBitmap(newUserAvatar,
                        0, 0, newUserAvatar.getWidth(), newUserAvatar.getHeight(),
                        m, true);
                ivAccountImage.setImageBitmap(newUserAvatar);

            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File flAvatar = new File(getContext().getExternalFilesDir(null), "temp.jpg");
            Bitmap b = decodeFile(flAvatar);
            Matrix m = new Matrix();
            m.postRotate(neededRotation(flAvatar));
            b = Bitmap.createBitmap(b,
                    0, 0, b.getWidth(), b.getHeight(),
                    m, true);

            if (b.getWidth() >= b.getHeight()) {
                newUserAvatar = Bitmap.createBitmap(b,b.getWidth() / 2 - b.getHeight() / 2,0,b.getHeight(),b.getHeight()
                );

            } else {
                newUserAvatar = Bitmap.createBitmap(b,0, b.getHeight() / 2 - b.getWidth() / 2,b.getWidth(),b.getWidth()
                );
            }

            ivAccountImage.setImageBitmap(newUserAvatar);
        }

    }
    public void changePhoto(){

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
                                getPhoto();
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
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File file = new File(getActivity().getFilesDir(), "userphoto.jpg");

            if (file.exists()) {
                newUserAvatar = decodeFile(file);
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
                bmImage.setImageBitmap(result);
                newUserAvatar = result;
                File file = new File(getActivity().getFilesDir(), "userphoto.jpg");
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
    @Override
    public void onDetach() {
        super.onDetach();

    }
    public static int neededRotation(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            }
            return 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
