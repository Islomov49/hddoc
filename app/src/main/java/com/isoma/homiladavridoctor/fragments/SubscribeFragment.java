package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.QuestionEntity;
import com.isoma.homiladavridoctor.Entity.QuestionKeyWithEvent;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.LinearManagerWithOutEx;
import com.isoma.homiladavridoctor.utils.LinkerTextView;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.isoma.homiladavridoctor.utils.StatesLikesSubs;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class SubscribeFragment extends Fragment {
    RecyclerView rvQuestionsList;
    TwinklingRefreshLayout refreshLayout;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference STORAGEREF = storage.getReference();
    ArrayList<QuestionEntity> questionEntities;
    ArrayList<QuestionEntity> questionEntitiesTemp;
    FirebaseUser firebaseUser;
    int indexer = 0;
    QuestionsFragment.QuestionAdapter questionAdapter;
    int windowWidth;
    int sides;
    HashMap<String,UserStatus> usersCacheList;
    ArrayList<QuestionKeyWithEvent> myQuestionsKeys;
    ArrayList<QuestionKeyWithEvent> myQuestionsKeysTemp;
    private SharedPreferences sPref;
    private SharedPreferences forLikes;
    private SharedPreferences forSubscribers;

    long counter = 0;
    long geted = 0;
    final static long REFSHING_COUNT = 7;
    final static long LODING_COUNT = 5;
    int lastDownloadedPosition =0;
    int started =0;
    QuestionAdapter myQustionFragmentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        refreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.refreshLayout);
        rvQuestionsList = (RecyclerView) view.findViewById(R.id.questionsRecycler);
        ProgressLayout headerView = new ProgressLayout(getContext());
        headerView.setColorSchemeColors(Color.parseColor("#0397da"));
        LoadingView loadingView = new LoadingView(getContext());
        myQuestionsKeys = new ArrayList<>();
        myQuestionsKeysTemp = new ArrayList<>();
        usersCacheList = new HashMap<>();
        questionEntities = new ArrayList<>();
        questionEntitiesTemp = new ArrayList<>();
        sPref = getContext().getSharedPreferences("informat", MODE_PRIVATE);
        forLikes = getContext().getSharedPreferences("forLikes", MODE_PRIVATE);
        forSubscribers = getContext().getSharedPreferences("forSubscribers", MODE_PRIVATE);
        windowWidth = sPref.getInt(HomilaConstants.SAVED_WIDTH,0);
        sides = dpToPx(16,getContext());
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshLayout.setEnableLoadmore(false);
                if(NetworkUtils.isNetworkAvailable(getContext()))
                    rootRef.child("Subscribed/forUser/"+firebaseUser.getUid()).orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            questionEntitiesTemp.clear();
                            myQuestionsKeysTemp.clear();

                            counter = dataSnapshot.getChildrenCount();
                            if(counter>REFSHING_COUNT){
                                counter = REFSHING_COUNT;
                            }
                            if(geted!= 0 ){  return;  }
                            if(counter == 0){
                                refreshLayout.finishRefreshing();
                                questionEntities.clear();
                                myQustionFragmentAdapter.notifyDataSetChanged();
                            }
                            for(DataSnapshot childs:dataSnapshot.getChildren()){
                                long a = 0;
                                if(childs.child("newEvent").getValue()!=null)
                                    a= childs.child("newEvent").getValue(Long.class);
                                QuestionKeyWithEvent questionKeyWithEvent = new QuestionKeyWithEvent(childs.getKey(),a);
                                myQuestionsKeysTemp.add(0,questionKeyWithEvent);

                            }

                            for(int i = 0;i<myQuestionsKeysTemp.size()&&i<REFSHING_COUNT;i++){
                                lastDownloadedPosition = i;
                                rootRef.child("/Questions/"+myQuestionsKeysTemp.get(i).getQuestionKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        QuestionEntity questionEntity = new QuestionEntity();
                                        questionEntity.setFromSnapshot(dataSnapshot);


                                        if(dataSnapshot.child("isblocked").getValue()!=null){
                                            if(dataSnapshot.child("isblocked").getValue(Boolean.class)){
                                                for(int i =myQuestionsKeysTemp.size()-1;i>=0;i--){
                                                    if(questionEntity.getKeyQuestion().equals(myQuestionsKeysTemp.get(i).getQuestionKey()))
                                                        myQuestionsKeysTemp.remove(i);
                                                }
                                            }
                                            else  questionEntitiesTemp.add(questionEntity);

                                        }
                                        else  questionEntitiesTemp.add(questionEntity);
                                        geted ++;
                                        if(counter == geted){
                                            geted = 0;
                                            refreshLayout.finishRefreshing();
                                            refreshLayout.setEnableLoadmore(true);
                                            myQuestionsKeys = (ArrayList<QuestionKeyWithEvent>) myQuestionsKeysTemp.clone();
                                            myQuestionsKeysTemp.clear();
                                            questionEntities = (ArrayList<QuestionEntity>) questionEntitiesTemp.clone();
                                            questionEntitiesTemp.clear();
                                            myQustionFragmentAdapter.notifyDataSetChanged();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            refreshLayout.setEnableLoadmore(true);
                        }
                    });
                else {
                    refreshLayout.finishRefreshing();
                    refreshLayout.setEnableLoadmore(false);
                    Toast.makeText(getContext(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                refreshLayout.setEnableRefresh(false);
                if(!NetworkUtils.isNetworkAvailable(getContext())) {
                    refreshLayout.finishRefreshing();
                    Toast.makeText(getContext(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                    return;
                }

                if(myQuestionsKeys.size()!=0&&lastDownloadedPosition<myQuestionsKeys.size()-1){
                    started = lastDownloadedPosition;
                    counter = myQuestionsKeys.size() - lastDownloadedPosition-1;
                    if(counter>LODING_COUNT){
                        counter = LODING_COUNT;
                    }

                    for(int i = 0;i<counter;i++){
                        rootRef.child("/Questions/"+myQuestionsKeys.get(++lastDownloadedPosition).getQuestionKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                QuestionEntity questionEntity = new QuestionEntity();
                                questionEntity.setFromSnapshot(dataSnapshot);
                                if(dataSnapshot.child("isblocked").getValue()!=null){
                                    if(dataSnapshot.child("isblocked").getValue(Boolean.class)){
                                    }
                                    else  questionEntitiesTemp.add(questionEntity);

                                }
                                    else  questionEntities.add(questionEntity);
                                geted ++;
                                if(counter == geted){
                                    geted = 0;
                                    refreshLayout.finishLoadmore();
                                    refreshLayout.setEnableRefresh(true);

                                    myQustionFragmentAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                refreshLayout.setEnableRefresh(true);

                            }
                        });
                    }
                }
                else {
                    refreshLayout.setEnableRefresh(true);

                    refreshLayout.setEnableLoadmore(false);
                    refreshLayout.finishLoadmore();
                }

            }
        });
        rvQuestionsList.setLayoutManager(new LinearManagerWithOutEx(getActivity()));
        myQustionFragmentAdapter = new QuestionAdapter();
        rvQuestionsList.setAdapter(myQustionFragmentAdapter);
        view.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.startRefresh();
            }
        });
        return view;
    }
    public class QuestionAdapter extends RecyclerView.Adapter<SubscribeFragment.QuestionViewHolder>{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        @Override
        public SubscribeFragment.QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subscribe_item, parent, false);
            return  new SubscribeFragment.QuestionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final SubscribeFragment.QuestionViewHolder holder, int position) {
            final QuestionEntity questionEntity = questionEntities.get(position);
            if(position==0)
                holder.infirstgone.setVisibility(View.GONE);
            holder.tvBodyQuestion.setLinkToAccountGuesta(questionEntity.getQuestionText(),getActivity());
            holder.tvDateQuestion.setText(simpleDateFormat.format(new Date(questionEntity.getPublishedDateLong())));
            holder.tvCountQiziqish.setText(String.valueOf(questionEntity.getStateQuestion().getSubsribers()));
            holder.tvCountKorilgan.setText(String.valueOf(questionEntity.getStateQuestion().getViews()));
            holder.tvCountAnswer.setText(String.valueOf(questionEntity.getStateQuestion().getAnswer()));

            if(myQuestionsKeys.get(position).getNewEvent()!=0){
                holder.tvNewEvent.setText("+"+myQuestionsKeys.get(position).getNewEvent()+"\n"+getString(R.string.xabar));
            }
            else {
                holder.tvNewEvent.setVisibility(View.GONE);
            }

            if(forSubscribers.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                holder.ivSubsribe.setImageResource(R.drawable.to_historyicon);
                rootRef.child("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            forSubscribers.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        }
                        else {
                            forSubscribers.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                            holder.ivSubsribe.setImageResource(R.drawable.history_questions);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else if(forSubscribers.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                holder.ivSubsribe.setImageResource(R.drawable.history_questions);
            }
            else if(forSubscribers.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                holder.ivSubsribe.setImageResource(R.drawable.to_historyicon);
            }

            if(forLikes.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                holder.ivHeart.setImageResource(R.drawable.emptyheart);
                rootRef.child("Likes/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            forLikes.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        }
                        else {
                            forLikes.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                            holder.ivHeart.setImageResource(R.drawable.heart);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else if(forLikes.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                holder.ivHeart.setImageResource(R.drawable.heart);
            }
            else if(forLikes.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                holder.ivHeart.setImageResource(R.drawable.emptyheart);
            }

            if(usersCacheList.get(questionEntity.getWriterUID())!=null){
                UserStatus userStatus = usersCacheList.get(questionEntity.getWriterUID());
                holder.tvNick.setText("@"+userStatus.getNickName());
                holder.tvPeriod.setText(userStatus.getWeek()+" - "+getString(R.string.xaftada));

                File extStore = Environment.getExternalStorageDirectory();
                final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar()+ ".jpg");
                if (myFile.exists()) {
                    Picasso.with(getContext())
                            .load(myFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(holder.ivAvatar);
                }
                else {
                    if(NetworkUtils.isNetworkAvailable(getContext())) {
                        try {
                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                            if (!Aa.exists())
                                Aa.mkdirs();
                            final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                            STORAGEREF.child("users/" + questionEntity.getWriterUID()+"/"+userStatus.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(getContext())
                                            .load(file)
                                            .placeholder(R.drawable.avatar)
                                            .error(R.drawable.avatar)
                                            .into(holder.ivAvatar);
                                }
                            });

                        } catch (Exception ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

            }else {
                rootRef.child("/user-status/"+questionEntity.getWriterUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setFromSnapshot(dataSnapshot);
                        usersCacheList.put(questionEntity.getWriterUID(),userStatus);

                        holder.tvNick.setText("@"+userStatus.getNickName());
                        holder.tvPeriod.setText(userStatus.getWeek()+" - "+getString(R.string.xaftada));

                        File extStore = Environment.getExternalStorageDirectory();
                        final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar()+ ".jpg");
                        if (myFile.exists()) {
                            Picasso.with(getContext())
                                    .load(myFile)
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(holder.ivAvatar);
                        }
                        else {
                            if(NetworkUtils.isNetworkAvailable(getContext())) {
                                try {
                                    File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                                    if (!Aa.exists())
                                        Aa.mkdirs();
                                    final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                                    STORAGEREF.child("users/" + questionEntity.getWriterUID()+"/"+userStatus.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Picasso.with(getContext())
                                                    .load(file)
                                                    .placeholder(R.drawable.avatar)
                                                    .error(R.drawable.avatar)
                                                    .into(holder.ivAvatar);
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
            if(!questionEntity.getPhotoID().isEmpty()){
                holder.togone.setVisibility(View.VISIBLE);
                holder.loading.setVisibility(View.VISIBLE);
                File extStore = Environment.getExternalStorageDirectory();
                File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/" + questionEntity.getPhotoID()+ ".jpg");
                if (myFile.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(myFile.getAbsolutePath(), options);
                    holder.ivContentImage.setImageBitmap(bitmap);
                    holder.loading.setVisibility(View.GONE);
                    if (windowWidth!=0) {
                        holder.ivContentImage.setMaxHeight( windowWidth-sides );
                    }

                }
                else {
                    if(questionEntity.getThumbnail()!=null) {
                        if(!questionEntity.getThumbnail().isEmpty()) {
                            Bitmap bitmap_thunbal = CommonOperations.StringToBitMap(questionEntity.getThumbnail());
                            holder.ivContentImage.setImageBitmap(CommonOperations.fastblur(bitmap_thunbal, bitmap_thunbal.getWidth(), bitmap_thunbal.getHeight(), 3));

                        }
                    }
                    if(NetworkUtils.isNetworkAvailable(getContext())) {
                        try {
                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/");
                            if (!Aa.exists())
                                Aa.mkdirs();
                            STORAGEREF.child("QuestionsPhotos/" + questionEntity.getPhotoID()).getFile(new File(extStore.getAbsolutePath() + "/Homila/cache/" + questionEntity.getPhotoID() + ".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                    Bitmap bitmap = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/" + questionEntity.getPhotoID() + ".jpg").getAbsolutePath(), options);
                                    holder.ivContentImage.setImageBitmap(bitmap);
                                    holder.loading.setVisibility(View.GONE);
                                    if (windowWidth!=0) {
                                        holder.ivContentImage.setMaxHeight( windowWidth-sides );
                                    }


                                }
                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    holder.loading.setProgress((int)progress);
                                }
                            });


                        } catch (Exception ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }



            }
            else {
                holder.togone.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return questionEntities.size();
        }


    }
    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivContentImage;
        CircleImageView ivAvatar;
        LinkerTextView tvBodyQuestion;
        TextView tvDateQuestion;
        LinkerTextView tvNick;
        TextView tvPeriod;
        TextView tvCountQiziqish;
        TextView tvCountKorilgan;
        TextView tvCountAnswer;
        ImageView ivHeart;
        ImageView ivSubsribe;
        TextView tvOpen;
        TextView tvNewEvent;
        View infirstgone;
        ProgressBar loading;
        FrameLayout togone;
        public QuestionViewHolder(View itemView) {
            super(itemView);
            ivContentImage = (ImageView) itemView.findViewById(R.id.ivContentImage);
            ivAvatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
            tvBodyQuestion = (LinkerTextView) itemView.findViewById(R.id.tvBodyQuestion);
            tvDateQuestion = (TextView) itemView.findViewById(R.id.tvDateQuestion);
            tvNick = (LinkerTextView) itemView.findViewById(R.id.tvNick);
            tvPeriod = (TextView) itemView.findViewById(R.id.tvPeriodP);
            tvCountQiziqish = (TextView) itemView.findViewById(R.id.tvQiziqish);
            tvCountKorilgan = (TextView) itemView.findViewById(R.id.tvKorilgan);
            tvCountAnswer = (TextView) itemView.findViewById(R.id.tvCountAnswer);
            ivHeart = (ImageView) itemView.findViewById(R.id.ivHeart);
            ivSubsribe = (ImageView) itemView.findViewById(R.id.ivSubsribe);
            tvOpen = (TextView) itemView.findViewById(R.id.tvOpen);
            tvNewEvent = (TextView) itemView.findViewById(R.id.tvNewEvent);
            infirstgone =  itemView.findViewById(R.id.infirstgone);
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            togone = (FrameLayout) itemView.findViewById(R.id.togone);
            tvNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountGuestFragment accountGuestFragment = new AccountGuestFragment();
                    Bundle bundle = new Bundle();
                    UserStatus userStatus = usersCacheList.get(questionEntities.get(getAdapterPosition()).getWriterUID());
                    if(userStatus==null) return;
                    bundle.putString(AccountGuestFragment.USER_NAME,userStatus.getNickName());
                    accountGuestFragment.setArguments(bundle);
                    ((HomilaDavri) getActivity()).getPaFragmentManager().displayFragment(accountGuestFragment);

                }
            });

            tvOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OpenedQuestionFragment openedQuestionFragment = new OpenedQuestionFragment();
                    Bundle bundle = new Bundle();
                    QuestionEntity questionEntity = questionEntities.get(getAdapterPosition());
                    UserStatus userStatus = usersCacheList.get(questionEntities.get(getAdapterPosition()).getWriterUID());
                    if(userStatus==null) return;
                    bundle.putString(OpenedQuestionFragment.USER_STATUS,new Gson().toJson(userStatus));
                    bundle.putString(OpenedQuestionFragment.QUESTION_ENTITY,new Gson().toJson(questionEntity));
                    bundle.putBoolean(OpenedQuestionFragment.FROM_SUBSC,true);

                    openedQuestionFragment.setArguments(bundle);
                    ((HomilaDavri)getActivity()).getPaFragmentManager().displayFragment(openedQuestionFragment);
                }
            });
            ivSubsribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(forSubscribers.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                        HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                        nevEvent.put("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/date", ServerValue.TIMESTAMP);
                        nevEvent.put("Subscribed/forServer/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid(), ServerValue.TIMESTAMP);
                        rootRef.updateChildren(nevEvent, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                EventBus.getDefault().post(new EventMessage(null,"subscribed","QuestionsViewPagerFragment"));
                            }
                        });
                        forSubscribers.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.history_questions);
                    }
                    else if(forSubscribers.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()).removeValue();
                        rootRef.child("Subscribed/forServer/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).removeValue();

                        forSubscribers.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.to_historyicon);
                    }
                    else if(forSubscribers.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                        nevEvent.put("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/date", ServerValue.TIMESTAMP);
                        nevEvent.put("Subscribed/forServer/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid(), ServerValue.TIMESTAMP);
                        rootRef.updateChildren(nevEvent, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                EventBus.getDefault().post(new EventMessage(null,"subscribed","QuestionsViewPagerFragment"));
                            }
                        });
                        forSubscribers.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.history_questions);
                    }

                }
            });
            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(forLikes.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                        rootRef.child("Likes/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                        forLikes.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivHeart.setImageResource(R.drawable.heart);
                    }
                    else if(forLikes.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("Likes/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).removeValue();
                        forLikes.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        ivHeart.setImageResource(R.drawable.emptyheart);
                    }
                    else if(forLikes.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        rootRef.child("Likes/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                        forLikes.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivHeart.setImageResource(R.drawable.heart);
                    }
                }
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnimationSubscribe(EventMessage eventMessage) {
        if(eventMessage.getForClass().equals("QuestionsViewPagerFragment")||eventMessage.getForClass().equals("Subsc")){
            if(eventMessage.getStatus().equals("subscribed")) {
                refreshLayout.startRefresh();
//                QuestionEntity questionEntity = (QuestionEntity) eventMessage.getObjectForTransfer();
//                questionEntities.add(0,questionEntity);
//                myQuestionsKeys.add(0,questionEntity.getKeyQuestion());
//                myQuestionsEvents.add(0,0);
//                myQustionFragmentAdapter.notifyDataSetChanged();

            }else  if(eventMessage.getStatus().equals("desubscribed")) {
                refreshLayout.startRefresh();
//                QuestionEntity questionEntity = (QuestionEntity) eventMessage.getObjectForTransfer();
//                for (QuestionEntity questionEntity1:questionEntities){
//                    if(questionEntity.getKeyQuestion().equals(questionEntity1.getKeyQuestion())){
//                        questionEntities.remove(questionEntity1);
//                        myQuestionsEvents.remove(myQuestionsKeys.indexOf(questionEntity.getKeyQuestion()));
//                        myQuestionsKeys.remove(questionEntity.getKeyQuestion());
//                        break;
//                    }
//                }
//                myQustionFragmentAdapter.notifyDataSetChanged();
            } else if(eventMessage.getStatus().equals("refresh")){
                refreshLayout.startRefresh();
            }
            else if(eventMessage.getStatus().equals("readed")){
                QuestionEntity fromOutside = (QuestionEntity) eventMessage.getObjectForTransfer();
                for(int i= 0;i<myQuestionsKeys.size();i++){
                    if(myQuestionsKeys.get(i).getQuestionKey().equals(fromOutside.getKeyQuestion())){
                        QuestionKeyWithEvent questionKeyWithEvent = myQuestionsKeys.get(i);
                        questionKeyWithEvent.setNewEvent(0);
                        myQuestionsKeys.set(i,questionKeyWithEvent);
                        myQustionFragmentAdapter.notifyItemChanged(i);

                        break;
                    }
                }
            }
        }
    }
    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
