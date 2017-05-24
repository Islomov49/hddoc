package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.QuestionEntity;
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

public class QuestionsFragment extends Fragment {
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
    QuestionAdapter questionAdapter;
    int windowWidth;
    int sides;
    HashMap<String,UserStatus> usersCacheList;

    private SharedPreferences sPref;
    private SharedPreferences forLikes;
    private SharedPreferences forSubscribers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        refreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.refreshLayout);
        rvQuestionsList = (RecyclerView) view.findViewById(R.id.questionsRecycler);
         usersCacheList = new HashMap<>();

        ProgressLayout headerView = new ProgressLayout(getContext());
        headerView.setColorSchemeColors(Color.parseColor("#0397da"));
        LoadingView loadingView = new LoadingView(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setAutoLoadMore(true);
        questionEntities = new ArrayList<>();
        sPref = getContext().getSharedPreferences("informat", MODE_PRIVATE);
        forLikes = getContext().getSharedPreferences("forLikes", MODE_PRIVATE);
        forSubscribers = getContext().getSharedPreferences("forSubscribers", MODE_PRIVATE);
        windowWidth = sPref.getInt(HomilaConstants.SAVED_WIDTH,0);
        sides = dpToPx(16,getContext());
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshLayout.setEnableLoadmore(false);

                if(NetworkUtils.isNetworkAvailable(getContext())){

                    rootRef.child("/Questions/").orderByChild("publishedDate").limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshots) {
                            questionEntities.clear();
                        for(DataSnapshot dataSnapshot:dataSnapshots.getChildren()) {
                            QuestionEntity questionEntity = new QuestionEntity();
                            try {

                                questionEntity.setFromSnapshot(dataSnapshot);
                            }catch (Exception o){
                                continue;
                            }


                            if(dataSnapshot.child("isblocked").getValue()!=null){
                                if(dataSnapshot.child("isblocked").getValue(Boolean.class))
                                continue;
                                else  questionEntities.add(0, questionEntity);
                            }
                             else  questionEntities.add(0, questionEntity);
                        }
                            refreshLayout.finishRefreshing();
                            refreshLayout.setEnableLoadmore(true);
                            questionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            refreshLayout.setEnableLoadmore(true);

                        }
                    });
                }else {
                    refreshLayout.finishRefreshing();
                    refreshLayout.setEnableLoadmore(true);

                    Toast.makeText(getContext(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                if(questionEntities.size()!=0){
                    indexer  = questionEntities.size();
                    refreshLayout.setEnableRefresh(false);
                rootRef.child("/Questions/").orderByChild("publishedDate").limitToLast(7).endAt(questionEntities.get(questionEntities.size()-1).getPublishedDateLong()-1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshots) {
                        if(dataSnapshots.getChildrenCount()==0){
                            refreshLayout.finishLoadmore();
                            refreshLayout.setEnableLoadmore(false);
                        }
                     for(DataSnapshot dataSnapshotik:dataSnapshots.getChildren()){
                        QuestionEntity questionEntity = new QuestionEntity();
                         try {

                             questionEntity.setFromSnapshot(dataSnapshotik);
                         }catch (Exception o){
                             continue;
                         }


                         if(dataSnapshotik.child("isblocked").getValue()!=null){
                             if(dataSnapshotik.child("isblocked").getValue(Boolean.class))
                                 continue;
                              else questionEntities.add(indexer, questionEntity);}
                             else  questionEntities.add(indexer, questionEntity);
                    }
                        refreshLayout.setEnableRefresh(true);
                        refreshLayout.finishLoadmore();
                            questionAdapter.notifyDataSetChanged();
                    for (DataSnapshot dataSnapshot:dataSnapshots.getChildren()){
                        rootRef.child("Questions/"+dataSnapshot.getKey()+"/state/views").runTransaction(new Transaction.Handler() {
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
                    }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        refreshLayout.setEnableRefresh(true);

                    }
                });
            }else {
                    refreshLayout.setEnableRefresh(true);
                    refreshLayout.finishLoadmore();
                }
            }
        });
        rvQuestionsList.setLayoutManager(new LinearManagerWithOutEx(getActivity()));
        questionAdapter = new QuestionAdapter();
        rvQuestionsList.setAdapter(questionAdapter);
        view.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.startRefresh();
            }
        });
        return view;
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
    public void onQuestionAdded(EventMessage eventMessage) {
        if(eventMessage.getForClass().equals("all")||eventMessage.getForClass().equals("QuestionsFragment")){
            if(eventMessage.getStatus().equals("added")){
               refreshLayout.startRefresh();
            }
            else if(eventMessage.getStatus().equals("changeitem")){
                QuestionEntity fromOutside = (QuestionEntity) eventMessage.getObjectForTransfer();
                for(int i= 0;i<questionEntities.size();i++){
                    if(questionEntities.get(i).getKeyQuestion().equals(fromOutside.getKeyQuestion())){
                        questionEntities.set(i,fromOutside);
                        questionAdapter.notifyItemChanged(i);
                    }
                }
            }
        }
    }
    public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder>{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        @Override
        public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.question_item, parent, false);
            return  new QuestionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final QuestionViewHolder holder, int position) {
            final QuestionEntity questionEntity = questionEntities.get(position);
            if(position==0)
                holder.infirstgone.setVisibility(View.GONE);
            holder.tvBodyQuestion.setLinkToAccountGuesta(questionEntity.getQuestionText(),getActivity());
            holder.tvDateQuestion.setText(simpleDateFormat.format(new Date(questionEntity.getPublishedDateLong())));
            holder.tvCountQiziqish.setText(String.valueOf(questionEntity.getStateQuestion().getSubsribers()));
            holder.tvCountKorilgan.setText(String.valueOf(questionEntity.getStateQuestion().getViews()));
            holder.tvCountAnswer.setText(String.valueOf(questionEntity.getStateQuestion().getAnswer()));


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
                                    try {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        holder.loading.setProgress((int)progress);
                                    }catch (Exception e){
                                    e.printStackTrace();
                                    }
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
        ProgressBar loading;
        FrameLayout togone;
        View infirstgone;
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
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            togone = (FrameLayout) itemView.findViewById(R.id.togone);
            infirstgone =  itemView.findViewById(R.id.infirstgone);





            tvNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountGuestFragment accountGuestFragment = new AccountGuestFragment();
                    Bundle bundle = new Bundle();
                    UserStatus userStatus = usersCacheList.get(questionEntities.get(getAdapterPosition()).getWriterUID());
                    if(userStatus==null) return;
                    bundle.putString(AccountGuestFragment.USER_NAME,userStatus.getNickName());
                    bundle.putString(AccountGuestFragment.LAST_AVATAR,userStatus.getAvatar());
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

                    openedQuestionFragment.setArguments(bundle);
                    ((HomilaDavri)getActivity()).getPaFragmentManager().displayFragment(openedQuestionFragment);
                }
            });
            ivSubsribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(forSubscribers.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                        HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                        nevEvent.put("/Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/date", ServerValue.TIMESTAMP);
                        nevEvent.put("/Subscribed/forServer/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid(), ServerValue.TIMESTAMP);
                        rootRef.updateChildren(nevEvent, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                EventBus.getDefault().post(new EventMessage(questionEntities.get(getAdapterPosition()),"subscribed","QuestionsViewPagerFragment"));
                            }
                        });
                        forSubscribers.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.history_questions);
                    }
                    else if(forSubscribers.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("/Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()).removeValue();
                        rootRef.child("/Subscribed/forServer/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                EventBus.getDefault().post(new EventMessage(questionEntities.get(getAdapterPosition()),"desubscribed","QuestionsViewPagerFragment"));

                            }
                        });

                        forSubscribers.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.to_historyicon);
                    }
                    else if(forSubscribers.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                        nevEvent.put("/Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/date", ServerValue.TIMESTAMP);
                        nevEvent.put("/Subscribed/forServer/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid(), ServerValue.TIMESTAMP);
                        rootRef.updateChildren(nevEvent, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                EventBus.getDefault().post(new EventMessage(questionEntities.get(getAdapterPosition()),"subscribed","QuestionsViewPagerFragment"));
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
                        rootRef.child("/Likes/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                        forLikes.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivHeart.setImageResource(R.drawable.heart);
                    }
                    else if(forLikes.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("/Likes/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).removeValue();
                        forLikes.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        ivHeart.setImageResource(R.drawable.emptyheart);
                    }
                    else if(forLikes.getInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        rootRef.child("/Likes/"+questionEntities.get(getAdapterPosition()).getKeyQuestion()+"/"+firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                        forLikes.edit().putInt(questionEntities.get(getAdapterPosition()).getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivHeart.setImageResource(R.drawable.heart);
                    }
                }
            });

        }
    }
    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
