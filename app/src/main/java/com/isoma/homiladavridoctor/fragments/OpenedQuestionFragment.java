package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.isoma.homiladavridoctor.Entity.Answer;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.QuestionEntity;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.LaodingViewBlue;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.isoma.homiladavridoctor.R.id.ivHeart;

public class OpenedQuestionFragment extends Fragment {
    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference STORAGEREF = storage.getReference();
    public static final String QUESTION_ENTITY = "question_entity";
    public static final String USER_STATUS = "user_status";
    public static final String FROM_SUBSC = "FROM_SUBSC";
    EditText etAnswer;
    ImageView ivSendAnswer;
    FrameLayout frameSendOrLoading;
    FirebaseUser firebaseUser;
    UserStatus userStatus;
    QuestionEntity questionEntity;
    private SharedPreferences sPref;
    private SharedPreferences forLikes;
    private SharedPreferences forSubscribers;
    private SharedPreferences forQoshilaman;
    ArrayList<Object> questionAnswers;
    ArrayList<Object> questionAnswersTemp;
    LaodingViewBlue loadingViewBlue;
    AdapterForOpenedQuestion adapterForOpenedQue;
    HashMap<String,UserStatus> usersCacheList;
    boolean firstPartDownloaded = false;
    boolean fromSubscribe = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opened_question, container, false);
        refreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.refreshLayoutQuest);
        recyclerView = (RecyclerView) view.findViewById(R.id.questionOpenedRecyclerview);
        etAnswer = (EditText) view.findViewById(R.id.etAnswer);
        ivSendAnswer = (ImageView) view.findViewById(R.id.ivSendAnswer);
        frameSendOrLoading = (FrameLayout) view.findViewById(R.id.frameSendOrLoading);
        firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();
        usersCacheList = new HashMap<>();
        if(getArguments()!=null){
            userStatus = new Gson().fromJson(getArguments().getString(USER_STATUS),UserStatus.class);
            questionEntity = new Gson().fromJson(getArguments().getString(QUESTION_ENTITY),QuestionEntity.class);
            fromSubscribe =getArguments().getBoolean(FROM_SUBSC,false);


        }
        else {
            ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
        }
        questionAnswers = new ArrayList<>();
        questionAnswersTemp = new ArrayList<>();
        questionAnswers.add(null);
        ProgressLayout headerView = new ProgressLayout(getContext());
        headerView.setColorSchemeColors(Color.parseColor("#0397da"));
        LoadingView loadingView = new LoadingView(getContext());
        sPref = getContext().getSharedPreferences("informat", MODE_PRIVATE);
        forLikes = getContext().getSharedPreferences("forLikes", MODE_PRIVATE);
        forSubscribers = getContext().getSharedPreferences("forSubscribers", MODE_PRIVATE);
        forQoshilaman = getContext().getSharedPreferences("forQoshilaman", MODE_PRIVATE);
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setHeaderView(headerView);
        refreshLayout.setEnableLoadmore(false);

        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            if(!etAnswer.getText().toString().equals("")){
                recyclerView.scrollToPosition(questionAnswers.size()-1);
            }
            }
        });
        ivSendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etAnswer.getText().toString().isEmpty()){
                    if(loadingViewBlue == null) {
                        loadingViewBlue = new LaodingViewBlue(getContext());
                        loadingViewBlue.startAnim(dpToPx(40, getActivity()), dpToPx(40, getActivity()));
                        frameSendOrLoading.addView(loadingViewBlue);
                    }
                    loadingViewBlue.setVisibility(View.VISIBLE);
                    ivSendAnswer.setVisibility(View.GONE);
                    final Answer answer = new Answer(etAnswer.getText().toString(),0,firebaseUser.getUid(),questionEntity.getWriterUID());
                    answer.setPublishedDate(System.currentTimeMillis());
                    answer.setPriority(System.currentTimeMillis());
                    DatabaseReference pushValue = rootRef.push();
                    answer.setKeyAnswer(pushValue.getKey());
                    rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+pushValue.getKey()).setValue(answer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ivSendAnswer.setVisibility(View.VISIBLE);
                            loadingViewBlue.setVisibility(View.GONE);
                            etAnswer.setText("");
                            questionAnswers.add(answer);
                            adapterForOpenedQue.notifyItemInserted(questionAnswers.size()-1);
                            recyclerView.scrollToPosition(questionAnswers.size()-1);

                        }
                    });


                }else {
                    etAnswer.setError(getString(R.string.javob_emp));
                }
            }
        });

        if(NetworkUtils.isNetworkAvailable(getContext())){
            rootRef.child("Answers/"+questionEntity.getKeyQuestion()).orderByChild("stateTrusnes").equalTo(StatesLikesSubs.LIKED).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshots) {
                    questionAnswersTemp.clear();
                    for(DataSnapshot dataSnapshot:dataSnapshots.getChildren()) {
                        Answer answer = new Answer();
                        answer.setFromSnapshot(dataSnapshot);
                        questionAnswersTemp.add(0, answer);
                    }

                    rootRef.child("Answers/"+questionEntity.getKeyQuestion()).orderByChild("priority").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshots) {

                            for(DataSnapshot dataSnapshot:dataSnapshots.getChildren()) {
                                Answer answer = new Answer();
                                answer.setFromSnapshot(dataSnapshot);
                                boolean isItMain = false;
                                for(Object ans: questionAnswersTemp){
                                    Answer answ = (Answer) ans;
                                    if(answ.getKeyAnswer().equals(answer.getKeyAnswer())){
                                        isItMain = true;
                                    }
                                }
                                if(!isItMain) questionAnswersTemp.add(answer);
                            }
                            refreshLayout.finishRefreshing();
                            if(dataSnapshots.getChildrenCount()==0 || dataSnapshots.getChildrenCount()<10 ){
                                refreshLayout.setEnableLoadmore(false);
                            }else {
                                refreshLayout.setEnableLoadmore(true);
                            }
                            firstPartDownloaded = true;
                            questionAnswersTemp.add(0,null);
                            questionAnswers = (ArrayList<Object>) questionAnswersTemp.clone();
                            questionAnswersTemp.clear();
                            adapterForOpenedQue.notifyDataSetChanged();

                            if (fromSubscribe){
                                rootRef.child("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()+"/newEvent").setValue(0l).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        EventBus.getDefault().post(new EventMessage(questionEntity,"readed","Subsc"));
                                    }
                                });
                            }
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

        }else {
            refreshLayout.finishRefreshing();
            Toast.makeText(getContext(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
        }

        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshLayout.setEnableLoadmore(false);
                if(NetworkUtils.isNetworkAvailable(getContext())){
                    rootRef.child("/Answers/"+questionEntity.getKeyQuestion()).orderByChild("stateTrusnes").equalTo(StatesLikesSubs.LIKED).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshots) {
                            questionAnswersTemp.clear();
                            for(DataSnapshot dataSnapshot:dataSnapshots.getChildren()) {
                                Answer answer = new Answer();
                                answer.setFromSnapshot(dataSnapshot);
                                questionAnswersTemp.add(0, answer);
                            }

                            rootRef.child("/Answers/"+questionEntity.getKeyQuestion()).orderByChild("priority").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshots) {

                                    for(DataSnapshot dataSnapshot:dataSnapshots.getChildren()) {
                                        Answer answer = new Answer();
                                        answer.setFromSnapshot(dataSnapshot);
                                        boolean isItMain = false;
                                        for(Object ans: questionAnswersTemp){
                                            Answer answ = (Answer) ans;
                                            if(answ.getKeyAnswer().equals(answer.getKeyAnswer())){
                                                isItMain = true;
                                            }
                                        }
                                        if(!isItMain) questionAnswersTemp.add(answer);
                                    }
                                    refreshLayout.finishRefreshing();
                                    if(dataSnapshots.getChildrenCount()==0 || dataSnapshots.getChildrenCount()<10 ){
                                        refreshLayout.setEnableLoadmore(false);
                                    }else {
                                        refreshLayout.setEnableLoadmore(true);
                                    }

                                    questionAnswersTemp.add(0,null);
                                    questionAnswers = (ArrayList<Object>) questionAnswersTemp.clone();
                                    questionAnswersTemp.clear();
                                    adapterForOpenedQue.notifyDataSetChanged();

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

                }else {
                    refreshLayout.finishRefreshing();
                    Toast.makeText(getContext(),R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                    if(firstPartDownloaded){
                        refreshLayout.setEnableRefresh(false);

                        if(questionAnswers.size()!=0 ||questionAnswers.size()!=1){
                            Answer answer = (Answer) questionAnswers.get(questionAnswers.size() - 1);
                            rootRef.child("/Answers/"+questionEntity.getKeyQuestion()).orderByChild("priority").limitToLast(10).startAt(answer.getPriorityDateLong()+1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshots) {
                                    if(dataSnapshots.getChildrenCount()<10){
                                        refreshLayout.finishLoadmore();
                                        refreshLayout.setEnableLoadmore(false);
                                    }
                                    for(DataSnapshot dataSnapshot:dataSnapshots.getChildren()){
                                        Answer answer1 = new Answer();
                                        answer1.setFromSnapshot(dataSnapshot);
                                        boolean isItMain = false;
                                        for(Object ans: questionAnswers){
                                            Answer answ = (Answer) ans;
                                            if(answ==null) continue;
                                            if(answ.getKeyAnswer().equals(answer1.getKeyAnswer())){
                                                isItMain = true;
                                            }
                                        }
                                        if(!isItMain) questionAnswers.add(answer1);
                                    }

                                    refreshLayout.setEnableRefresh(true);
                                    refreshLayout.finishLoadmore();
                                    adapterForOpenedQue.notifyDataSetChanged();

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

            }
        });
        adapterForOpenedQue = new AdapterForOpenedQuestion();
        recyclerView.setLayoutManager(new LinearManagerWithOutEx(getContext()));
        recyclerView.setAdapter(adapterForOpenedQue);

        return view;
    }

    public class AdapterForOpenedQuestion extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;
            RecyclerView.ViewHolder holder = null;
            if(viewType==0){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.opened_question_hearder_item, parent, false);
                holder = new ViewHolderHeader(v);
            }
            else {

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_item, parent, false);
                holder = new ViewHolder(v);
            }
            return  holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder decideHolder, int position) {
            if(decideHolder instanceof ViewHolderHeader){
                final ViewHolderHeader holder = (ViewHolderHeader) decideHolder;
                if(userStatus!=null){
                    if(!userStatus.getAvatar().isEmpty()){
                        final File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                        if (myFileForCache.exists()) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
                            holder.ivAvatar.setImageBitmap(bitmap);
                        }
                    }
                    holder.tvNick.setText("@"+userStatus.getNickName());
                    holder.tvPeriodP.setText(userStatus.getWeek()+" - "+ getActivity().getResources().getString(R.string.xaftada));

                }else {
                           rootRef.child("user-status/"+questionEntity.getWriterUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   userStatus.setFromSnapshot(dataSnapshot);
                                   if(userStatus == null) ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                                   if(!userStatus.getAvatar().isEmpty()){
                                       final File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                                       if (myFileForCache.exists()) {
                                           BitmapFactory.Options options = new BitmapFactory.Options();
                                           options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                           Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
                                           holder.ivAvatar.setImageBitmap(bitmap);
                                       }
                                   }
                                   holder.tvNick.setText("@"+userStatus.getNickName());
                                   holder.tvPeriodP.setText(userStatus.getWeek()+" - "+ getActivity().getResources().getString(R.string.xaftada));

                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                }
                holder.tvBodyQuestion.setLinkToAccountGuesta(questionEntity.getQuestionText(),getActivity());
                holder.tvDateQuestion.setText(simpleDateFormat.format(new Date(questionEntity.getPublishedDateLong())));
                if(!firstPartDownloaded){
                    LaodingViewBlue loadBlue = new LaodingViewBlue(getContext());
                    loadBlue.startAnim(dpToPx(40, getActivity()), dpToPx(40, getActivity()));
                    holder.loadAnswers.addView(loadBlue);
                }
                else holder.loadAnswers.setVisibility(View.GONE);



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
            else {
                final ViewHolder holderAnswer = (ViewHolder) decideHolder;
                final Answer answer = (Answer) questionAnswers.get(position);
                holderAnswer.tvDateAnswer.setText(simpleDateFormat.format(new Date(answer.getPublishedDateLong())));
                holderAnswer.tvQoshiladiAns.setText(String.valueOf((answer.getLikes()<0)?0:answer.getLikes()));

                if(answer.getStateTrusnes()==StatesLikesSubs.TRUSTED){
                    holderAnswer.tvBodyAnswer.setBackgroundResource(R.drawable.green_stroke);
                    holderAnswer.tvBodyAnswer.setTypeface(null, Typeface.BOLD);
                }
                else if(answer.getStateTrusnes()==StatesLikesSubs.FAKE){
                    holderAnswer.tvBodyAnswer.setBackgroundResource(R.drawable.red_stroke);
                    holderAnswer.tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                    holderAnswer.tvBodyAnswer.setTextColor(Color.parseColor("#d3d3d3"));
                }
                else {
                    holderAnswer.tvBodyAnswer.setBackgroundResource(R.drawable.emp);
                    holderAnswer.tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                }

                holderAnswer.tvBodyAnswer.setLinkToAccountGuesta(answer.getBodyAnswer(),getActivity());

                if(usersCacheList.get(answer.getWriterUID())!=null){
                    UserStatus userStatus = usersCacheList.get(answer.getWriterUID());
                    holderAnswer.tvNickAns.setText("@"+userStatus.getNickName());
                    holderAnswer.tvPeriodAns.setText(userStatus.getWeek()+" - "+ getActivity().getResources().getString(R.string.xaftada));
                    holderAnswer.tvBilimdonligiAns.setText(String.valueOf(userStatus.getAchivments().getExp()));

                    File extStore = Environment.getExternalStorageDirectory();
                    final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar()+ ".jpg");
                    if (myFile.exists()) {
                        Picasso.with(getContext())
                                .load(myFile)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(holderAnswer.ivAuthorAns);
                    }
                    else {
                        if(NetworkUtils.isNetworkAvailable(getContext())) {
                            try {
                                File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                                if (!Aa.exists())
                                    Aa.mkdirs();
                                final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                                STORAGEREF.child("users/" + answer.getWriterUID()+"/"+userStatus.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Picasso.with(getContext())
                                                .load(file)
                                                .placeholder(R.drawable.avatar)
                                                .error(R.drawable.avatar)
                                                .into(holderAnswer.ivAuthorAns);
                                    }
                                });

                            } catch (Exception ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                }else {
                    rootRef.child("/user-status/"+answer.getWriterUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserStatus userStatus = new UserStatus();
                            userStatus.setFromSnapshot(dataSnapshot);
                            usersCacheList.put(answer.getWriterUID(),userStatus);

                            holderAnswer.tvNickAns.setText("@"+userStatus.getNickName());
                            holderAnswer.tvPeriodAns.setText(userStatus.getWeek()+" - "+ getActivity().getResources().getString(R.string.xaftada));
                            holderAnswer.tvBilimdonligiAns.setText(String.valueOf(userStatus.getAchivments().getExp()));
                            File extStore = Environment.getExternalStorageDirectory();
                            final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar()+ ".jpg");
                            if (myFile.exists()) {
                                Picasso.with(getContext())
                                        .load(myFile)
                                        .placeholder(R.drawable.avatar)
                                        .error(R.drawable.avatar)
                                        .into(holderAnswer.ivAuthorAns);
                            }
                            else {
                                if(NetworkUtils.isNetworkAvailable(getContext())) {
                                    try {
                                        File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                                        if (!Aa.exists())
                                            Aa.mkdirs();
                                        final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + userStatus.getAvatar() + ".jpg");
                                        STORAGEREF.child("users/" + answer.getWriterUID()+"/"+userStatus.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Picasso.with(getContext())
                                                        .load(file)
                                                        .placeholder(R.drawable.avatar)
                                                        .error(R.drawable.avatar)
                                                        .into(holderAnswer.ivAuthorAns);
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

                if(forQoshilaman.getInt(answer.getKeyAnswer(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED || forQoshilaman.getInt(answer.getKeyAnswer(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                    holderAnswer.ivKlassAns.setImageResource(R.drawable.klass);

                }
                else if(forQoshilaman.getInt(answer.getKeyAnswer(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                    holderAnswer.ivKlassAns.setImageResource(R.drawable.klassfill);
                }




            }
        }

        @Override
        public int getItemCount() {
            return questionAnswers.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (position==0)?0:1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinkerTextView tvBodyAnswer;
        TextView tvDateAnswer;
        CircleImageView ivAuthorAns;
        TextView tvNickAns;
        TextView tvPeriodAns;
        TextView tvBilimdonligiAns;
        TextView tvQoshiladiAns;
        ImageView ivKlassAns;
        public ViewHolder(View itemView) {
            super(itemView);
            tvBodyAnswer = (LinkerTextView) itemView.findViewById(R.id.tvBodyAnswer);
            tvDateAnswer = (TextView) itemView.findViewById(R.id.tvDateAnswer);
            tvNickAns = (TextView) itemView.findViewById(R.id.tvNickAns);
            tvPeriodAns = (TextView) itemView.findViewById(R.id.tvPeriodAns);
            tvBilimdonligiAns = (TextView) itemView.findViewById(R.id.tvBilimdonligiAns);
            tvQoshiladiAns = (TextView) itemView.findViewById(R.id.tvQoshiladiAns);
            ivAuthorAns = (CircleImageView) itemView.findViewById(R.id.ivAuthorAns);
            ivKlassAns = (ImageView) itemView.findViewById(R.id.ivKlassAns);

            tvNickAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountGuestFragment accountGuestFragment = new AccountGuestFragment();
                    Bundle bundle = new Bundle();
                    Answer answer = (Answer) questionAnswers.get(getAdapterPosition());
                    UserStatus userStatus = usersCacheList.get(answer.getWriterUID());
                    if(userStatus==null) return;
                    bundle.putString(AccountGuestFragment.USER_NAME,userStatus.getNickName());
                    bundle.putString(AccountGuestFragment.LAST_AVATAR,userStatus.getAvatar());
                    accountGuestFragment.setArguments(bundle);
                    ((HomilaDavri) getActivity()).getPaFragmentManager().displayFragment(accountGuestFragment);
                }
            });
            ivKlassAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Answer answeri = (Answer) questionAnswers.get(getAdapterPosition());
                    if(forQoshilaman.getInt(answeri.getKeyAnswer(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED ||forQoshilaman.getInt(answeri.getKeyAnswer(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/likes").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = 0;
                                if(mutableData.getValue()!=null)
                                p = mutableData.getValue(Long.class);
                                mutableData.setValue(++p);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPublishedDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p-30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                        forQoshilaman.edit().putInt(answeri.getKeyAnswer(),StatesLikesSubs.LIKED).apply();
                        answeri.setLikes(answeri.getLikes()+1);
                        tvQoshiladiAns.setText(answeri.getLikes()+"");
                        questionAnswers.set(getAdapterPosition(),answeri);
                        ivKlassAns.setImageResource(R.drawable.klassfill);
                    }
                    else if(forQoshilaman.getInt(answeri.getKeyAnswer(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/likes").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = mutableData.getValue(Long.class);
                                mutableData.setValue(--p);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPublishedDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p+30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                        forQoshilaman.edit().putInt(answeri.getKeyAnswer(),StatesLikesSubs.NOT_LIKED).apply();
                        answeri.setLikes(answeri.getLikes()-1);
                        questionAnswers.set(getAdapterPosition(),answeri);
                        tvQoshiladiAns.setText(answeri.getLikes()+"");
                        ivKlassAns.setImageResource(R.drawable.klass);
                    }

                }
            });
        }
    }
    public class ViewHolderHeader extends RecyclerView.ViewHolder{
        CircleImageView ivAvatar;
        LinkerTextView tvBodyQuestion;
        TextView tvDateQuestion;
        TextView tvNick;
        TextView tvPeriodP;
        ImageView ivSubsribe;
        ImageView ivContentImage;
        ImageView ivHeart;
        FrameLayout togone;
        ProgressBar loading;
        FrameLayout loadAnswers;
        public ViewHolderHeader(View itemView) {
            super(itemView);
            ivAvatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
            tvBodyQuestion = (LinkerTextView) itemView.findViewById(R.id.tvBodyQuestion);
            tvDateQuestion = (TextView) itemView.findViewById(R.id.tvDateQuestion);
            tvNick = (TextView) itemView.findViewById(R.id.tvNick);
            tvPeriodP = (TextView) itemView.findViewById(R.id.tvPeriodP);
            ivSubsribe = (ImageView) itemView.findViewById(R.id.ivSubsribe);
            ivContentImage = (ImageView) itemView.findViewById(R.id.ivContentImage);
            ivHeart = (ImageView) itemView.findViewById(R.id.ivHeart);
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            togone = (FrameLayout) itemView.findViewById(R.id.togone);
            loadAnswers = (FrameLayout) itemView.findViewById(R.id.loadAnswers);



            ivSubsribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(forSubscribers.getInt(questionEntity.getKeyQuestion(), StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                        HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                        nevEvent.put("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()+"/date", ServerValue.TIMESTAMP);
                        nevEvent.put("Subscribed/forServer/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid(), ServerValue.TIMESTAMP);
                        rootRef.updateChildren(nevEvent, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                EventBus.getDefault().post(new EventMessage(questionEntity,"subscribed","QuestionsViewPagerFragment"));
                            }
                        });
                        forSubscribers.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.history_questions);
                    }
                    else if(forSubscribers.getInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()).removeValue();
                        rootRef.child("Subscribed/forServer/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                EventBus.getDefault().post(new EventMessage(questionEntity,"desubscribed","QuestionsViewPagerFragment"));

                            }
                        });

                        forSubscribers.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.to_historyicon);
                    }
                    else if(forSubscribers.getInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        HashMap<String,Object> nevEvent = new HashMap<String, Object>();
                        nevEvent.put("Subscribed/forUser/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()+"/date", ServerValue.TIMESTAMP);
                        nevEvent.put("Subscribed/forServer/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid(), ServerValue.TIMESTAMP);
                        rootRef.updateChildren(nevEvent, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                EventBus.getDefault().post(new EventMessage(questionEntity,"subscribed","QuestionsViewPagerFragment"));
                            }
                        });
                        forSubscribers.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivSubsribe.setImageResource(R.drawable.history_questions);
                    }

                }
            });
            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(forLikes.getInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_DOWNLOADED){
                        rootRef.child("Likes/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                        forLikes.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivHeart.setImageResource(R.drawable.heart);
                    }
                    else if(forLikes.getInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.LIKED){
                        rootRef.child("Likes/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid()).removeValue();
                        forLikes.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_LIKED).apply();
                        ivHeart.setImageResource(R.drawable.emptyheart);
                    }
                    else if(forLikes.getInt(questionEntity.getKeyQuestion(),StatesLikesSubs.NOT_DOWNLOADED)==StatesLikesSubs.NOT_LIKED){
                        rootRef.child("Likes/"+questionEntity.getKeyQuestion()+"/"+firebaseUser.getUid()).setValue(ServerValue.TIMESTAMP);
                        forLikes.edit().putInt(questionEntity.getKeyQuestion(),StatesLikesSubs.LIKED).apply();
                        ivHeart.setImageResource(R.drawable.heart);
                    }
                }
            });
            tvNick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountGuestFragment accountGuestFragment = new AccountGuestFragment();
                    Bundle bundle = new Bundle();
                    if(userStatus==null) return;
                    bundle.putString(AccountGuestFragment.USER_NAME,userStatus.getNickName());
                    bundle.putString(AccountGuestFragment.LAST_AVATAR,userStatus.getAvatar());
                    accountGuestFragment.setArguments(bundle);
                    ((HomilaDavri) getActivity()).getPaFragmentManager().displayFragment(accountGuestFragment);

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
