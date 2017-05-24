package com.isoma.homiladavridoctor.fragments;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.GeneralConstants;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class VirtualAnketaFragment extends AppCompatActivity {
    @BindView(R.id.Doctor) TextView tvDocNumber;
    @BindView(R.id.vracismi) TextView tvDocName;
    @BindView(R.id.poliklinika) TextView tvHospital;
    @BindView(R.id.qongruh) TextView tvBloodType;
    @BindView(R.id.manzil) TextView tvAddress;
    @BindView(R.id.eri) TextView tvHusband;
    @BindView(R.id.erinom) TextView tvHusbandName;
    @BindView(R.id.ned_usish) ImageView  ivGrowth;
    @BindView(R.id.utdi_hafta) TextView tvGone;
    @BindView(R.id.qol_hafta) TextView tvLeft;
    @BindView(R.id.ogirligi) TextView tvWeight;
    @BindView(R.id.razmer) TextView tvDimen;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private long time_self;
    private RelativeLayout scren;
    private AlarmManager am;
    int HAFTA_SONI_TEKSHIRISHGA;
    int HAFTA_SONI;
    private long CREATE_VOQT;;
    private String mPath;
    private SQLiteDatabase sdb;
    private TextView profi_name;
    private String uri_set_photo;
    private CircleImageView cibProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anketa);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sPref = getSharedPreferences("informat", MODE_PRIVATE);
        ed = sPref.edit();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        scren=(RelativeLayout)findViewById(R.id.screnRe);
        CREATE_VOQT = sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis());
        ObservableScrollView scrla = (ObservableScrollView) findViewById(R.id.savaas);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.attachToScrollView( scrla);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });
        HAFTA_SONI = (int) ((System.currentTimeMillis() - CREATE_VOQT) / 1000 / 60 / 60 / 24 / 7);
        cibProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        uri_set_photo = null;
        uri_set_photo = sPref.getString(HomilaConstants.URI_PHOTO, "");
        File file = new File(getFilesDir(), "userphoto.jpg");
        if(file.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            cibProfileImage.setImageBitmap(bitmap);}
        else {
            cibProfileImage.setImageResource(R.drawable.avatar);
        }

        if (HAFTA_SONI_TEKSHIRISHGA != HAFTA_SONI && HAFTA_SONI != 0) {
            ed.putInt(HomilaConstants.SAVED_WEEK, HAFTA_SONI);
            HAFTA_SONI_TEKSHIRISHGA = HAFTA_SONI;

        }
        if (HAFTA_SONI > 40) {
            HAFTA_SONI = 40;
            ed.putInt(HomilaConstants.SAVED_WEEK, HAFTA_SONI);
        }
        if (HAFTA_SONI < 1) {
            HAFTA_SONI = 1;
        }
    setINFO();

//        if(HAFTA_SONI<10){
//            tvWeek.setText(" "+Integer.toString(HAFTA_SONI));}
//        else  tvWeek.setText(Integer.toString(HAFTA_SONI));
        tvGone.setText(getString(R.string.utdi)+"\n"+Integer.toString(HAFTA_SONI)+" "+getString(R.string.haftaa));
        tvLeft.setText(getString(R.string.qoldi)+"\n"+Integer.toString(40-HAFTA_SONI)+" "+getString(R.string.haftaa));
        tvWeight.setText(getString(R.string.ogirlik)+"\n" + GeneralConstants.WEIGHT[HAFTA_SONI - 1] + " "+getString(R.string.grr));
        tvDimen.setText(getString(R.string.ulchami)+"\n" + GeneralConstants.SIZES[HAFTA_SONI - 1] + " "+getString(R.string.smm));
        ivGrowth.setImageBitmap(null);
        ivGrowth.setImageResource(GeneralConstants.Images[HAFTA_SONI - 1]);
        String temp;
        temp=sPref.getString(HomilaConstants.SAVED_VISM, "");
        if(temp.equals("")){
            findViewById(R.id.smi).setVisibility(View.GONE);
            tvDocName.setVisibility(View.GONE);
        }
        else {
            tvDocName.setText(temp);
        }

        temp=sPref.getString(HomilaConstants.SAVED_NUMBER, "");
        if(temp.equals("")){
            findViewById(R.id.tor).setVisibility(View.GONE);
            tvDocNumber.setVisibility(View.GONE);
        }
        else {
            tvDocNumber.setText(temp);
        }
        temp=sPref.getString(HomilaConstants.SAVED_HOSPITAL, "");
        if(temp.equals("")){
            findViewById(R.id.ika).setVisibility(View.GONE);
            tvHospital.setVisibility(View.GONE);
        }
        else {
            tvHospital.setText(temp+" оилавий поликлиника");
        }

        temp=sPref.getString(HomilaConstants.SAVED_BLOOD, "");
        if(temp.equals("")){
            findViewById(R.id.ruh).setVisibility(View.GONE);
            tvBloodType.setVisibility(View.GONE);
        }
        else {
            tvBloodType.setText(temp);
        }

        temp=sPref.getString(HomilaConstants.SAVED_ADDRESS, "");
        if(temp.equals("")){
            findViewById(R.id.zil).setVisibility(View.GONE);
            tvAddress.setVisibility(View.GONE);
        }
        else {
            tvAddress.setText(temp);
        }

        temp=sPref.getString(HomilaConstants.SAVED_ERI, "");
        if(temp.equals("")){
            findViewById(R.id.ri).setVisibility(View.GONE);
            tvHusband.setVisibility(View.GONE);
        }
        else {
            tvHusband.setText(temp);
        }

        temp=sPref.getString(HomilaConstants.SAVED_ERI_NAME, "");
        if(temp.equals("")){
            findViewById(R.id.enom).setVisibility(View.GONE);
            tvHusbandName.setVisibility(View.GONE);
        }
        else {
            tvHusbandName.setText(temp);
        }

        findViewById(R.id.yosi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Date aippe=new Date();
        aippe.setTime(System.currentTimeMillis());
        SimpleDateFormat formatterku=new SimpleDateFormat("dd.MM.yyyy" );
        ((TextView)findViewById(R.id.nowday)).setText(getString(R.string.anketa_toldirildi)+" "+formatterku.format(aippe));
        takeScreenshot();
    }

    public void onStart(){

        super.onStart();
    }
public void onResume(){


    super.onResume();
}
    private void takeScreenshot() {
        Date aippe=new Date();
        aippe.setTime(System.currentTimeMillis());
        SimpleDateFormat formatterku=new SimpleDateFormat("dd-MM-yyyy" );

        try {
            mPath = Environment.getExternalStorageDirectory().toString() + "/Pictures/" + "homila_davri_"+formatterku.format(aippe) + ".jpg";
            View v1 = findViewById(R.id.screnRe);
            Bitmap bitmap = Bitmap.createBitmap(v1.getWidth(), v1.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.setDensity(v1.getResources().getDisplayMetrics().densityDpi);
            Canvas canvas = new Canvas(bitmap);
            v1.draw(canvas);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            shareImage(imageFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
    private void shareImage(File imageFile) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        String imagePath = imageFile.getPath();
        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, getString(R.string.yuborish)));
    }


    public void setINFO(){
        try{
            ((TextView)findViewById(R.id.tugusi)).setText(Long.toString(sPref.getLong(HomilaConstants.SAVED_AGE, 18))+" "+getString(R.string.yoshda));
        }catch(Exception o){}
        try{ String tempa=sPref.getString(HomilaConstants.SAVED_NAME,getString(R.string.ismingizni_kiriting));
            if(tempa.equals("")){
                tempa=getString(R.string.ismingizni_kiriting);
            }
            ((TextView)findViewById(R.id.ismi)).setText(tempa);
        }catch(Exception o){}
        try{

        }catch(Exception o){        }
        try{
            CREATE_VOQT = sPref.getLong(HomilaConstants.SAVED_CREATE, System.currentTimeMillis());
            HAFTA_SONI = (int) ((System.currentTimeMillis() - CREATE_VOQT) / 1000 / 60 / 60 / 24 / 7);
            if (HAFTA_SONI < 1) {
                HAFTA_SONI = 1;
            }
            ((TextView)findViewById(R.id.homilasi)).setText(getString(R.string.homila_haftasi)+" "+Integer.toString(HAFTA_SONI));
        }catch(Exception o){}
        try{
            time_self=sPref.getLong(HomilaConstants.SAVED_CREATE, 0);
            time_self+=(long)280*24*60*60*1000;
            Date aipp=new Date();
            aipp.setTime(time_self);
            SimpleDateFormat formatterku=new SimpleDateFormat("dd.MM.yyyy" );
            ((TextView) findViewById(R.id.yosi)).setText(getString(R.string.tugushkuni)+" " +formatterku.format(aipp));
        }catch(Exception o){}

    }





    }

