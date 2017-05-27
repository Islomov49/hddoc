package com.isoma.homiladavridoctor.fragments;

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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.Achivments;
import com.isoma.homiladavridoctor.Entity.ChatsEntity;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.Bitmaps;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getInstance().getReference();
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference rootRef = dataset.getReference();
    private SharedPreferences sPref;
    public static final String LAST_AVATAR="mylastavatar";
    public static final String LAST_NICKNAME="last_nick";
    public static final String LAST_PERIOD="last_period";
    CircleImageView imAvatar;
    TextView tvNickName;
    TextView tvCountExp;
    TextView tvCountQuestions;
    TextView tvCountAnswers;
    TextView tvCountTrueAnswers;
    FirebaseUser firebaseUser;
    RecyclerView rvFriends;
    ArrayList<ChatsEntity> chatsEntities;
    AdapterForFriends adapterForFriends;
    ChildEventListener childEventListener;
    ValueEventListener achivmentListner;
    HashMap<String,UserStatus> usersCacheList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        imAvatar= (CircleImageView) view.findViewById(R.id.imAvatar);
        tvNickName= (TextView) view.findViewById(R.id.tvNickName);
        tvCountExp= (TextView) view.findViewById(R.id.tvCountExp);
        tvCountQuestions= (TextView) view.findViewById(R.id.tvCountQuestions);
        tvCountAnswers= (TextView) view.findViewById(R.id.tvCountAnswers);
        tvCountTrueAnswers= (TextView) view.findViewById(R.id.tvCountTrueAnswers);
        rvFriends = (RecyclerView) view.findViewById(R.id.rvFriends);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        chatsEntities = new ArrayList<>();
        usersCacheList = new HashMap<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        int height = rvFriends.getHeight();
//        int count = height / dpToPx(80);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        rvFriends.setLayoutManager(gridLayoutManager);
        adapterForFriends = new AdapterForFriends();
        rvFriends.setAdapter(adapterForFriends);

        if(firebaseUser!=null){
            imAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePhoto();
                }
            });

            File extStore = Environment.getExternalStorageDirectory();
            final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + sPref.getString(LAST_AVATAR,"")+ ".jpg");
            if (myFile.exists()) {
                final File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" +sPref.getString(LAST_AVATAR,"")+ ".jpg");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
                imAvatar.setImageBitmap(bitmap);
            }
            else {
                if(NetworkUtils.isNetworkAvailable(getContext())) {
                    try {
                        File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                        if (!Aa.exists())
                            Aa.mkdirs();
                        final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + sPref.getString(LAST_AVATAR,"") + ".jpg");
                        STORAGEREF.child("users/" + firebaseUser.getUid()+"/"+sPref.getString(LAST_AVATAR,"")).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Picasso.with(getContext())
                                        .load(file)
                                        .placeholder(R.drawable.avatar)
                                        .error(R.drawable.avatar)
                                        .into(imAvatar);
                            }
                        });

                    } catch (Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }


            tvNickName.setText("@" + sPref.getString(LAST_NICKNAME,""));
            rootRef.child("/user-status/"+firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshoti) {
                    final UserStatus userStatus = new UserStatus();
                    userStatus.setFromSnapshot(dataSnapshoti);
                    if(!userStatus.getAvatar().equals(sPref.getString(LAST_AVATAR,""))){
                        final File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" +userStatus.getAvatar()+ ".jpg");
                        if (myFileForCache.exists()) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
                            imAvatar.setImageBitmap(bitmap);
                            sPref.edit().putString(LAST_AVATAR,userStatus.getAvatar()).apply();
                        }
                        else {
                            if(NetworkUtils.isNetworkAvailable(getActivity())) {
                                File Aa=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/");
                                if(!Aa.exists())
                                    Aa.mkdirs();
                                final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");

                                STORAGEREF.child("users/"+ firebaseUser.getUid()+"/"+userStatus.getAvatar()).getFile( file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        sPref.edit().putString(LAST_AVATAR,userStatus.getAvatar()).apply();
                                        Picasso.with(getActivity())
                                                .load(file)
                                                .into(imAvatar);
                                    }
                                });
                            }
                        }
                    }

                    sPref.edit().putString(LAST_NICKNAME,userStatus.getNickName()).apply();
                    tvNickName.setText("@" + userStatus.getNickName());
                    rootRef.child("/user-status/"+firebaseUser.getUid()+"/week").setValue(sPref.getInt(HomilaConstants.SAVED_WEEK, 41));
                    tvCountExp.setText(CommonOperations.checkForEmptyAndBack(userStatus.getAchivments().getExp()+""));
                    tvCountAnswers.setText(CommonOperations.checkForEmptyAndBack(userStatus.getAchivments().getAnswers()+""));
                    tvCountTrueAnswers.setText(CommonOperations.checkForEmptyAndBack(userStatus.getAchivments().getCorrectly()+""));
                    tvCountQuestions.setText(CommonOperations.checkForEmptyAndBack(userStatus.getAchivments().getQuestion()+""));
                     achivmentListner = rootRef.child("/user-status/" + firebaseUser.getUid() + "/achivments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Achivments userAchivments = new Achivments();
                            userAchivments.setFromSnapshot(dataSnapshot);
                            tvCountExp.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getExp() + ""));
                            tvCountAnswers.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getAnswers() + ""));
                            tvCountTrueAnswers.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getCorrectly() + ""));
                            tvCountQuestions.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getQuestion() + ""));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        childEventListener = null;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public class AdapterForFriends extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friends_item, parent, false);
            return  new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
           final ChatsEntity chatsEntity  = chatsEntities.get(position);
            if(usersCacheList.get(chatsEntity.getKeyCompanion())!=null){
                UserStatus userStatus = usersCacheList.get(chatsEntity.getKeyCompanion());
                holder.tvFriendNick.setText("@"+userStatus.getNickName());
                if(chatsEntity.getNewMessage()>0) {
                    holder.ivNewMessage.setVisibility(View.VISIBLE);
                    holder.ivNewMessage.setText(String.valueOf(chatsEntity.getNewMessage()));
                }
                else holder.ivNewMessage.setVisibility(View.GONE);

                File extStore = Environment.getExternalStorageDirectory();
                final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar()+ ".jpg");
                if (myFile.exists()) {
                    Picasso.with(getContext())
                            .load(myFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(holder.ivAccountAvatar);
                }
                else {
                    if(NetworkUtils.isNetworkAvailable(getContext())) {
                        try {
                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                            if (!Aa.exists())
                                Aa.mkdirs();
                            final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                            STORAGEREF.child("users/" + chatsEntity.getKeyCompanion()+"/"+userStatus.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(getContext())
                                            .load(file)
                                            .placeholder(R.drawable.avatar)
                                            .error(R.drawable.avatar)
                                            .into(holder.ivAccountAvatar);
                                }
                            });

                        } catch (Exception ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

            }else {
                rootRef.child("/user-status/"+chatsEntity.getKeyCompanion()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setFromSnapshot(dataSnapshot);
                        usersCacheList.put(chatsEntity.getKeyCompanion(),userStatus);

                        holder.tvFriendNick.setText("@"+userStatus.getNickName());
                        if(chatsEntity.getNewMessage()>0) {
                            holder.ivNewMessage.setVisibility(View.VISIBLE);
                            holder.ivNewMessage.setText(String.valueOf(chatsEntity.getNewMessage()));
                        }
                        else holder.ivNewMessage.setVisibility(View.GONE);

                        File extStore = Environment.getExternalStorageDirectory();
                        final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar()+ ".jpg");
                        if (myFile.exists()) {
                            Picasso.with(getContext())
                                    .load(myFile)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(holder.ivAccountAvatar);
                        }
                        else {
                            if(NetworkUtils.isNetworkAvailable(getContext())) {
                                try {
                                    File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                                    if (!Aa.exists())
                                        Aa.mkdirs();
                                    final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                                    STORAGEREF.child("users/" + chatsEntity.getKeyCompanion()+"/"+userStatus.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Picasso.with(getContext())
                                                    .load(file)
                                                    .placeholder(R.drawable.avatar)
                                                    .error(R.drawable.avatar)
                                                    .into(holder.ivAccountAvatar);
                                        }
                                    });

                                } catch (Exception ex) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return chatsEntities.size();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView ivAccountAvatar;
        TextView ivNewMessage;
        TextView tvFriendNick;
        public ViewHolder(View itemView) {
            super(itemView);
            ivAccountAvatar = (CircleImageView) itemView.findViewById(R.id.ivAccountAvatar);
            ivNewMessage = (TextView) itemView.findViewById(R.id.ivNewMessage);
            tvFriendNick = (TextView) itemView.findViewById(R.id.tvFriendNick);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatingFragment chatingFragment = new ChatingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ChatingFragment.CHATS_ENTITY,new Gson().toJson(chatsEntities.get(getAdapterPosition())));
                    chatingFragment.setArguments(bundle);
                    ((HomilaDavri) getActivity()).getPaFragmentManager().displayFragment(chatingFragment);
                }
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if(achivmentListner==null)
            achivmentListner = rootRef.child("/user-status/" + firebaseUser.getUid() + "/achivments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Achivments userAchivments = new Achivments();
                    userAchivments.setFromSnapshot(dataSnapshot);
                    tvCountExp.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getExp() + ""));
                    tvCountAnswers.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getAnswers() + ""));
                    tvCountTrueAnswers.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getCorrectly() + ""));
                    tvCountQuestions.setText(CommonOperations.checkForEmptyAndBack(userAchivments.getQuestion() + ""));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        if(firebaseUser!=null)
          setListnerChats();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        achivmentListner = null;
        disableListnerChats();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAdded(EventMessage eventMessage) {
        if(eventMessage.getForClass().equals("all")||eventMessage.getForClass().equals("AccountFragment")){
            if(eventMessage.getStatus().equals("setListnerChats")) {
                setListnerChats();
            }
            else if(eventMessage.getStatus().equals("disableListnerChats"))  {
                disableListnerChats();
            }
        }
    }

    ChildEventListener chatsListner;
    public void setListnerChats(){
        chatsEntities.clear();
        chatsListner = null;
        chatsListner = rootRef.child("Chats/" + firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatsEntity chatsEntity = new ChatsEntity();
                chatsEntity.setFromSnapshot(dataSnapshot);
                chatsEntities.add(chatsEntity);
                Collections.sort(chatsEntities, new Comparator<ChatsEntity>() {
                    public int compare(ChatsEntity o1, ChatsEntity o2) {
                        return ((Long) o1.getDateLong()).compareTo(((Long) o2.getDateLong()));
                    }
                });
                adapterForFriends.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatsEntity chatsEntity = new ChatsEntity();
                chatsEntity.setFromSnapshot(dataSnapshot);
                for (int i = 0; i < chatsEntities.size(); i++) {
                    if (chatsEntities.get(i).getRoomId().equals(chatsEntity.getRoomId())) {
                        chatsEntities.set(i, chatsEntity);
                    }
                }
                Collections.sort(chatsEntities, new Comparator<ChatsEntity>() {
                    public int compare(ChatsEntity o1, ChatsEntity o2) {
                        return ((Long) o1.getDateLong()).compareTo(((Long) o2.getDateLong()));
                    }
                });
                adapterForFriends.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ChatsEntity chatsEntity = new ChatsEntity();
                chatsEntity.setFromSnapshot(dataSnapshot);
                for (int i = 0; i < chatsEntities.size(); i++) {
                    if (chatsEntities.get(i).getRoomId().equals(chatsEntity.getRoomId())) {
                        chatsEntities.remove(i);
                    }
                }
                Collections.sort(chatsEntities, new Comparator<ChatsEntity>() {
                    public int compare(ChatsEntity o1, ChatsEntity o2) {
                        return ((Long) o1.getDateLong()).compareTo(((Long) o2.getDateLong()));
                    }
                });
                adapterForFriends.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void disableListnerChats(){
        chatsListner = null;
    }



    private static final int MY_PERMISSIONS_READ_WRITE = 343;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 106;
    static final int REQUEST_IMAGE_CAPTURE = 765;
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
    public static final int IMAGE_TANLANDI = 330;
    Bitmap newUserAvatar = null;

    private void getPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_TANLANDI);
    }
    UploadTask uploadTask;

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
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();

                    final DatabaseReference photoPath = rootRef.push();
                    Bitmap newBit = Bitmaps.createScaledBitmap(newUserAvatar, 320, 320, true);
                    try {
                        savePhotoToCache(photoPath.getKey(), newBit);
                    } catch (Exception o) {
                    } finally {
                        Uri filek = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/Homila/cache/" + photoPath.getKey() + ".jpg"));

                        uploadTask = STORAGEREF.child("users/" + firebaseUser.getUid() + "/" + photoPath.getKey()).putFile(filek, metadata);

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
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Map<String, Object> PhotoUp = new HashMap<String, Object>();
                                PhotoUp.put("users/" + firebaseUser.getUid() + "/avatar/" + photoPath.getKey(), ServerValue.TIMESTAMP);
                                PhotoUp.put("user-status/" + firebaseUser.getUid() + "/avatar", photoPath.getKey());
                                rootRef.updateChildren(PhotoUp);
                                sPref.edit().putString(LAST_AVATAR,photoPath.getKey()).apply();

                            }
                        });
                    }
                    imAvatar.setImageBitmap(newUserAvatar);

                }


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

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();

                final DatabaseReference photoPath = rootRef.push();
                Bitmap newBit = Bitmaps.createScaledBitmap(newUserAvatar, 320, 320, true);
                try {
                    savePhotoToCache(photoPath.getKey(), newBit);
                } catch (Exception o) {
                } finally {
                    Uri filek = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Homila/cache/" + photoPath.getKey() + ".jpg"));

                    uploadTask = STORAGEREF.child("users/" + firebaseUser.getUid() + "/" + photoPath.getKey()).putFile(filek, metadata);

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
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Map<String, Object> PhotoUp = new HashMap<String, Object>();
                            PhotoUp.put("users/" + firebaseUser.getUid() + "/avatar/" + photoPath.getKey(), ServerValue.TIMESTAMP);
                            PhotoUp.put("user-status/" + firebaseUser.getUid() + "/avatar", photoPath.getKey());
                            rootRef.updateChildren(PhotoUp);
                            sPref.edit().putString(LAST_AVATAR,photoPath.getKey()).apply();

                        }
                    });
                }
                imAvatar.setImageBitmap(newUserAvatar);

            }



        }

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
