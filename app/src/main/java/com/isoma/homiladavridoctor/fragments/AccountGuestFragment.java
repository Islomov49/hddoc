package com.isoma.homiladavridoctor.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.LinkerTextView;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountGuestFragment extends Fragment {

    public final static String USER_NAME= "user_name";
    public static final String LAST_AVATAR="mylastavatar";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getInstance().getReference();
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference rootRef = dataset.getReference();
    SharedPreferences sPref;
    CircleImageView imAvatar;
    LinkerTextView tvNickName;
    TextView tvPeriod;
    TextView tvCountExp;
    TextView tvCountQuestions;
    TextView tvCountAnswers;
    TextView tvCountTrueAnswers;
    ImageView ivSendMessage;
    UserStatus userStatusGlobal;
    String userName;
    String userAvatar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guest_account, container, false);
        imAvatar= (CircleImageView) view.findViewById(R.id.imAvatar);
        tvNickName= (LinkerTextView) view.findViewById(R.id.tvNickName);
        tvPeriod= (TextView) view.findViewById(R.id.tvPeriod);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);

        tvCountExp= (TextView) view.findViewById(R.id.tvCountExp);
        tvCountQuestions= (TextView) view.findViewById(R.id.tvCountQuestions);
        tvCountAnswers= (TextView) view.findViewById(R.id.tvCountAnswers);
        tvCountTrueAnswers= (TextView) view.findViewById(R.id.tvCountTrueAnswers);
        ivSendMessage= (ImageView) view.findViewById(R.id.ivSendMessagee);
        ivSendMessage.setVisibility(View.GONE);
        if(getArguments()!=null){

            userName = getArguments().getString(USER_NAME);
            userAvatar = getArguments().getString(LAST_AVATAR,"");
        }
        if(userName.equals(sPref.getString(AccountFragment.LAST_NICKNAME,""))){
            EventBus.getDefault().post(new EventMessage(null,"toMyAccount","QuestionsViewPagerFragment"));
            ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
            return view;
        }
        if(!userAvatar.isEmpty()){
            final File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" + userAvatar + ".jpg");
            if (myFileForCache.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
                imAvatar.setImageBitmap(bitmap);
            }
        }
        tvNickName.setText("@"+userName);
        if(!userName.isEmpty()){


            rootRef.child("/user-status").orderByChild("nickName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshoti) {
                    if(dataSnapshoti.getValue()==null){
                        ((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                        Toast.makeText(getActivity(),getString(R.string.mavjud_bolmagan_foydalanuvchi),Toast.LENGTH_SHORT).show();
                    }
                    for (DataSnapshot dataSnapshot:dataSnapshoti.getChildren()) {
                        final UserStatus userStatus = new UserStatus();
                        userStatus.setFromSnapshot(dataSnapshot);
                        userStatusGlobal = userStatus;
                            final File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                            if (myFileForCache.exists()) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
                                imAvatar.setImageBitmap(bitmap);
                            } else {
                                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                                    File Aa = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/");
                                    if (!Aa.exists())
                                        Aa.mkdirs();
                                    STORAGEREF.child("users/" + userStatus.getUserUID() + "/" + userStatus.getAvatar()).getFile(myFileForCache).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Picasso.with(getActivity())
                                                    .load(myFileForCache)
                                                    .into(imAvatar);
                                        }
                                    });
                                }
                            }


                        tvCountExp.setText(checkForEmptyAndBack(userStatus.getAchivments().getExp() + ""));
                        tvCountAnswers.setText(checkForEmptyAndBack(userStatus.getAchivments().getAnswers() + ""));
                        tvCountTrueAnswers.setText(checkForEmptyAndBack(userStatus.getAchivments().getCorrectly() + ""));
                        tvCountQuestions.setText(checkForEmptyAndBack(userStatus.getAchivments().getQuestion() + ""));
                        tvNickName.setText("@" + userStatus.getNickName());
                        tvPeriod.setText(userStatus.getWeek()+" - "+ getActivity().getResources().getString(R.string.xaftada));
                        rootRef.child("users/"+userStatus.getUserUID()+"/whoIM").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null){
                                    if(!dataSnapshot.getValue(String.class).equals("P")){
                                        if(dataSnapshot.getValue(String.class).equals("D")){
                                            tvPeriod.setText(R.string.doctor);

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        ivSendMessage.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userStatusGlobal==null) return;
                ChatingFragment chatingFragment = new ChatingFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ChatingFragment.FROM_GUEST,true);
                bundle.putString(ChatingFragment.USER_STATUS,(new Gson()).toJson(userStatusGlobal));
                chatingFragment.setArguments(bundle);
                ((HomilaDavri) getActivity()).getPaFragmentManager().displayFragment(chatingFragment);
            }
        });

        return view;
    }

    private String checkForEmptyAndBack(String string){
        return (string.isEmpty()||string.equals("null"))? "0" :string;
     }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
