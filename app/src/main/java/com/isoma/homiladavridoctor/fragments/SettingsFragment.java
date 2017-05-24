package com.isoma.homiladavridoctor.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends Fragment {
    private SharedPreferences sPref, sPref2;
    private SharedPreferences.Editor editor, editor1;
    private long creation_time,time_self;
    private int weeks;
    @BindView(R.id.ismfamila) EditText etName;
    @BindView(R.id.yosingiz) EditText etAge;
    @BindView(R.id.Doctor) EditText etDocNumber;
    @BindView(R.id.vracismi) EditText etDocName;
    @BindView(R.id.poliklinika) EditText etHospital;
    @BindView(R.id.qongruh) EditText etBloodType;
    @BindView(R.id.manzil) EditText etAddress;
    @BindView(R.id.eri) EditText etHusband;
    @BindView(R.id.erinom) EditText etHusbandNum;
    private String uri_set_photo;
    private CircleImageView cibProfile;
    private View settingsView;
    public SettingsFragment() {
    }
    public void setINFO(){
        try{
            ((TextView) settingsView.findViewById(R.id.yosi)).setText(Long.toString(sPref.getLong(HomilaConstants.SAVED_AGE, 18))+ getString(R.string.yoshda));
        }catch(Exception o){}
        try{ String tempa = sPref.getString(HomilaConstants.SAVED_NAME, getString(R.string.put_name));
            ((TextView) settingsView.findViewById(R.id.ismi)).setText(tempa);
        }catch(Exception o){}
        try{
            String hom_tip;
            if(sPref.getBoolean(HomilaConstants.SAVED_TYPE,false)){ hom_tip=getString(R.string.birinchi_homila); }
            else {  hom_tip=getString(R.string.birinchi_homila_emas); }
                    ((TextView) settingsView.findViewById(R.id.homilasi)).setText(hom_tip);
                    hom_tip=null;
        }catch(Exception o){        }
        try{
            creation_time = sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis());
            weeks = (int) ((System.currentTimeMillis() - creation_time) / 1000 / 60 / 60 / 24 / 7);
            if (weeks < 1) {
                weeks = 1;
                      }
            ((TextView) settingsView.findViewById(R.id.xaftasi)).setText(getString(R.string.homila_haftada)+Integer.toString(weeks));
        }catch(Exception o){}
        try{
            time_self=sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
            time_self+=(long)280*24*60*60*1000;
            Date aipp=new Date();
            aipp.setTime(time_self);
            SimpleDateFormat formatterku=new SimpleDateFormat("dd.MM.yyyy" );
            ((TextView) settingsView.findViewById(R.id.tugusi)).setText(getString(R.string.tugushkuni) +formatterku.format(aipp));
        }catch(Exception o){}

    }
    public void update_edits(){
        etName.setText(sPref.getString(HomilaConstants.SAVED_NAME, ""));
        etAge.setText(Long.toString(sPref.getLong(HomilaConstants.SAVED_AGE, 20)));
        etDocName.setText(sPref.getString(HomilaConstants.SAVED_VISM, ""));
        etDocNumber.setText(sPref.getString(HomilaConstants.SAVED_NUMBER, ""));
        etHospital.setText(sPref.getString(HomilaConstants.SAVED_HOSPITAL,""));
        etBloodType.setText(sPref.getString(HomilaConstants.SAVED_BLOOD,""));
        etAddress.setText(sPref.getString(HomilaConstants.SAVED_ADDRESS, ""));
        etHusband.setText(sPref.getString(HomilaConstants.SAVED_ERI, ""));
        etHusbandNum.setText(sPref.getString(HomilaConstants.SAVED_ERI_NAME, ""));
        File file = new File(getActivity().getFilesDir(), "userphoto.jpg");
        if(file.exists()){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        cibProfile.setImageBitmap(bitmap);}
        else {
            cibProfile.setImageResource(R.drawable.avatar);
        }

    }
    public void get_write(){
        try {
            editor.putString(HomilaConstants.SAVED_NAME, etName.getText().toString());
        } catch (Exception o){
            etName.setText("");
            etName.setHintTextColor(Color.RED);
        }
        if (!etAge.getText().toString().equals("")){

            try {
            editor.putLong(HomilaConstants.SAVED_AGE, Long.parseLong(etAge.getText().toString()));
        } catch (Exception o){
            etAge.setText("");
            etAge.setHintTextColor(Color.RED);
        }}
        try {
            editor.putString(HomilaConstants.SAVED_NUMBER, etDocNumber.getText().toString());
        } catch (Exception o){
            etDocNumber.setText("");
            etDocNumber.setHintTextColor(Color.RED);
        }
        try {
            editor.putString(HomilaConstants.SAVED_VISM, etDocName.getText().toString());
        } catch (Exception o){
            etDocName.setText("");
            etDocName.setHintTextColor(Color.RED);
        }
        try {
            editor.putString(HomilaConstants.SAVED_VISM, etDocName.getText().toString());
        } catch (Exception o){
            etDocName.setText("");
            etDocName.setHintTextColor(Color.RED);
        }
        if (!etHospital.getText().toString().equals("")) {

            try {
                editor.putString(HomilaConstants.SAVED_HOSPITAL, etHospital.getText().toString());
        } catch (Exception o){
            etHospital.setText("");
            etHospital.setHintTextColor(Color.RED);
        } }
        try {
            editor.putString(HomilaConstants.SAVED_BLOOD, etBloodType.getText().toString());
        } catch (Exception o){
            etBloodType.setText("");
            etBloodType.setHintTextColor(Color.RED);
        }
        try {
            editor.putString(HomilaConstants.SAVED_ADDRESS, etAddress.getText().toString());
        } catch (Exception o){
            etAddress.setText("");
            etAddress.setHintTextColor(Color.RED);
        }
        try {
            editor.putString(HomilaConstants.SAVED_ERI, etHusband.getText().toString());
        } catch (Exception o){
            etHusband.setText("");
            etHusband.setHintTextColor(Color.RED);
        }try {
            editor.putString(HomilaConstants.SAVED_ERI_NAME, etHusbandNum.getText().toString());
        } catch (Exception o){
            etHusbandNum.setText("");
            etHusbandNum.setHintTextColor(Color.RED);
        }

            editor.apply();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.sozlamalar);
        settingsView =inflater.inflate(R.layout.fragment_sozlamalar, container, false);
        ButterKnife.bind(this, settingsView);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        editor = sPref.edit();
        uri_set_photo = null;
         cibProfile =(CircleImageView) settingsView.findViewById(R.id.profile_image);
        cibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhoto();
            }
        });
        sPref2 = getActivity().getSharedPreferences("useric", getActivity().MODE_PRIVATE);
        editor1 = sPref2.edit();
        uri_set_photo = sPref.getString(HomilaConstants.URI_PHOTO, "topilmadi");

        setINFO();
        update_edits();

        settingsView.findViewById(R.id.eslabbb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_write();
                setINFO();
                ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();



            }
        });
        return settingsView;
    }
    @Override
    public void onDetach() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);
        ((HomilaDavri)getActivity()).hideKeyboard();
        super.onDetach();

    }

    private static final int MY_PERMISSIONS_READ_WRITE = 33;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 19;
    static final int REQUEST_IMAGE_CAPTURE = 113;

    public void changePhoto(){

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
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
    public static final int IMAGE_TANLANDI = 135;
    Bitmap newUserAvatar = null;

    private void getPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_TANLANDI);
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

                cibProfile.setImageBitmap(newUserAvatar);

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

            cibProfile.setImageBitmap(newUserAvatar);
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
