package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.Answer;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.QuestionEntity;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
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

public class OpenedMyQuestionFragment extends Fragment {
    TwinklingRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    UserStatus userStatus;
    QuestionEntity questionEntity;
    public static final String QUESTION_ENTITY = "question_entity";
    ArrayList<Object> questionAnswers;
    ArrayList<Object> questionAnswersTemp;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference STORAGEREF = storage.getReference();
    boolean firstPartDownloaded = false;
    HashMap<String,UserStatus> usersCacheList;
    EditText etAnswer;
    LaodingViewBlue loadingViewBlue;
    FrameLayout frameSendOrLoading;

    ImageView ivSendAnswer;
    AdapterForOpenedQuestion adapterForOpenedQuestion;
    boolean itItsSynced = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_opened_question, container, false);
        refreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.refreshLayoutQuest);
        etAnswer = (EditText) view.findViewById(R.id.etAnswer);
        ivSendAnswer = (ImageView) view.findViewById(R.id.ivSendAnswer);
        recyclerView = (RecyclerView) view.findViewById(R.id.questionOpenedRecyclerview);
        frameSendOrLoading = (FrameLayout) view.findViewById(R.id.frameSendOrLoading);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        if(getArguments()!=null){
            questionEntity = new Gson().fromJson(getArguments().getString(QUESTION_ENTITY),QuestionEntity.class);
        }
        else {
            ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
            return view;
        }
        usersCacheList = new HashMap<>();
        questionAnswers = new ArrayList<>();
        questionAnswersTemp = new ArrayList<>();
        questionAnswers.add(null);


        ProgressLayout headerView = new ProgressLayout(getContext());
        headerView.setColorSchemeColors(Color.parseColor("#0397da"));
        LoadingView loadingView = new LoadingView(getContext());

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
                            adapterForOpenedQuestion.notifyItemInserted(questionAnswers.size()-1);
                            recyclerView.scrollToPosition(questionAnswers.size()-1);

                        }
                    });


                }else {
                    etAnswer.setError(getString(R.string.javob_emp));
                }
            }
        });



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
                            firstPartDownloaded = true;
                            questionAnswersTemp.add(0,null);
                            questionAnswers = (ArrayList<Object>) questionAnswersTemp.clone();
                            questionAnswersTemp.clear();
                            adapterForOpenedQuestion.notifyDataSetChanged();
                            rootRef.child("UserQuestionList/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()+"/newEvent").setValue(0l).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    EventBus.getDefault().post(new EventMessage(questionEntity,"readed","all"));

                                }
                            });
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

                                    adapterForOpenedQuestion.notifyDataSetChanged();

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
                    if(questionAnswers.size()!=0 ||questionAnswers.size()!=1){
                        Answer answer = (Answer) questionAnswers.get(questionAnswers.size() - 1);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()).orderByChild("priority").limitToLast(10).startAt(answer.getPriorityDateLong()+1).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                refreshLayout.finishLoadmore();
                                adapterForOpenedQuestion.notifyDataSetChanged();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        refreshLayout.finishLoadmore();
                    }
                }
            }
        });
         adapterForOpenedQuestion = new AdapterForOpenedQuestion();
        recyclerView.setLayoutManager(new LinearManagerWithOutEx(getContext()));
        recyclerView.setAdapter(adapterForOpenedQuestion);
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
                        .inflate(R.layout.opened_my_question_hearder_item, parent, false);
                holder = new ViewHolderHeader(v);
            }
            else {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_to_me_item, parent, false);
                holder = new ViewHolder(v);
            }
            return  holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder mainHolder, int position) {
            if(mainHolder instanceof ViewHolderHeader){
                final ViewHolderHeader holder = (ViewHolderHeader) mainHolder;
                holder.tvBodyQuestion.setLinkToAccountGuesta(questionEntity.getQuestionText(),getActivity());
                holder.tvDateQuestion.setText(simpleDateFormat.format(new Date(questionEntity.getPublishedDateLong())));
                holder.tvCountAnswerlar.setText(String.valueOf(questionEntity.getStateQuestion().getAnswer()));
                holder.tvCountKorilgan.setText(String.valueOf(questionEntity.getStateQuestion().getViews()));
                holder.tvCountQiziqishlar.setText(String.valueOf(questionEntity.getStateQuestion().getSubsribers()));
                holder.tvCountYurak.setText(String.valueOf(questionEntity.getStateQuestion().getLikes()));
                if(!itItsSynced){
                    itItsSynced = true;
                    rootRef.child("Questions/"+questionEntity.getKeyQuestion()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        questionEntity.setFromSnapshot(dataSnapshot);
                        holder.tvBodyQuestion.setLinkToAccountGuesta(questionEntity.getQuestionText(),getActivity());
                        holder.tvDateQuestion.setText(simpleDateFormat.format(new Date(questionEntity.getPublishedDateLong())));
                        holder.tvCountAnswerlar.setText(String.valueOf(questionEntity.getStateQuestion().getAnswer()));
                        holder.tvCountKorilgan.setText(String.valueOf(questionEntity.getStateQuestion().getViews()));
                        holder.tvCountQiziqishlar.setText(String.valueOf(questionEntity.getStateQuestion().getSubsribers()));
                        holder.tvCountYurak.setText(String.valueOf(questionEntity.getStateQuestion().getLikes()));

                        EventBus.getDefault().post(new EventMessage(questionEntity,"changeitem","all"));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                }

                if(!firstPartDownloaded){
                    LaodingViewBlue loadBlue = new LaodingViewBlue(getContext());
                    loadBlue.startAnim(dpToPx(40, getActivity()), dpToPx(40, getActivity()));
                    holder.loadAnswers.addView(loadBlue);
                }
                else holder.loadAnswers.setVisibility(View.GONE);

            }
            else {
                final ViewHolder holderAnswer = (ViewHolder) mainHolder;
                final Answer answer = (Answer) questionAnswers.get(position);
                holderAnswer.tvDateAnswer.setText(simpleDateFormat.format(new Date(answer.getPublishedDateLong())));
                holderAnswer.tvQoshiladiAns.setText(String.valueOf((answer.getLikes()<0)?0:answer.getLikes()));
                if(answer.getStateTrusnes()==StatesLikesSubs.TRUSTED){
                    holderAnswer.tvBodyAnswer.setBackgroundResource(R.drawable.green_stroke);
                    holderAnswer.tvBodyAnswer.setTypeface(null, Typeface.BOLD);
                    holderAnswer.tvJavobQoniqarsiz.setTextColor(Color.parseColor("#404040"));
                    holderAnswer.tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons);
                    holderAnswer.tvJavobQoniqarli.setTextColor(Color.parseColor("#19b915"));
                    holderAnswer.tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons_green_stroke);
                }
                else if(answer.getStateTrusnes()==StatesLikesSubs.FAKE){
                    holderAnswer.tvBodyAnswer.setBackgroundResource(R.drawable.red_stroke);
                    holderAnswer.tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                    holderAnswer.tvBodyAnswer.setTextColor(Color.parseColor("#d3d3d3"));
                    holderAnswer.tvJavobQoniqarsiz.setTextColor(Color.parseColor("#ff3505"));
                    holderAnswer.tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons_red);
                    holderAnswer.tvJavobQoniqarli.setTextColor(Color.parseColor("#404040"));
                    holderAnswer.tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons);

                }
                else {
                    holderAnswer.tvBodyAnswer.setBackgroundResource(R.drawable.emp);
                    holderAnswer.tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                    holderAnswer.tvJavobQoniqarsiz.setTextColor(Color.parseColor("#404040"));
                    holderAnswer.tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons);
                    holderAnswer.tvJavobQoniqarli.setTextColor(Color.parseColor("#404040"));
                    holderAnswer.tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons);
                }

                holderAnswer.tvBodyAnswer.setLinkToAccountGuesta(answer.getBodyAnswer(),getActivity());

                if(usersCacheList.get(answer.getWriterUID())!=null){
                    UserStatus userStatus = usersCacheList.get(answer.getWriterUID());
                    holderAnswer.tvNickAns.setText("@"+userStatus.getNickName());
                    holderAnswer.tvPeriodAns.setText(userStatus.getWeek()+" - "+getString(R.string.xaftada));
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
                            holderAnswer.tvPeriodAns.setText(userStatus.getWeek()+" - "+getString(R.string.xaftada));
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
        ImageView ivDeleteAns;
        TextView tvJavobQoniqarsiz;
        TextView tvJavobQoniqarli;

        public ViewHolder(View itemView) {
            super(itemView);

            tvBodyAnswer = (LinkerTextView) itemView.findViewById(R.id.tvBodyAnswer);
            tvDateAnswer = (TextView) itemView.findViewById(R.id.tvDateAnswer);
            tvNickAns = (TextView) itemView.findViewById(R.id.tvNickAns);
            tvPeriodAns = (TextView) itemView.findViewById(R.id.tvPeriodAns);
            tvBilimdonligiAns = (TextView) itemView.findViewById(R.id.tvBilimdonligiAns);
            tvQoshiladiAns = (TextView) itemView.findViewById(R.id.tvQoshiladiAns);
            ivAuthorAns = (CircleImageView) itemView.findViewById(R.id.ivAuthorAns);
            ivDeleteAns = (ImageView) itemView.findViewById(R.id.ivDeleteAns);
            tvJavobQoniqarsiz = (TextView) itemView.findViewById(R.id.tvJavobQoniqarsiz);
            tvJavobQoniqarli = (TextView) itemView.findViewById(R.id.tvJavobQoniqarli);
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


            ivDeleteAns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Answer answeri = (Answer) questionAnswers.get(getAdapterPosition());
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.sovoli_ochirmoqchimisz)
                            .setPositiveButton(R.string.ochirish, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()).removeValue();
                                    questionAnswers.remove(getAdapterPosition());
                                    adapterForOpenedQuestion.notifyItemRemoved(getAdapterPosition());
                                    dialog.cancel();

                                }
                            }).setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            tvJavobQoniqarsiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Answer answeri = (Answer) questionAnswers.get(getAdapterPosition());
                    if(answeri.getStateTrusnes()==StatesLikesSubs.FAKE){
                        tvBodyAnswer.setBackgroundResource(R.drawable.emp);
                        tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                        tvBodyAnswer.setTextColor(Color.parseColor("#262626"));

                        tvJavobQoniqarsiz.setTextColor(Color.parseColor("#404040"));
                        tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons);
                        tvJavobQoniqarli.setTextColor(Color.parseColor("#404040"));
                        tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/stateTrusnes").setValue(StatesLikesSubs.NOT_SELECTED);
                        answeri.setStateTrusnes(StatesLikesSubs.NOT_SELECTED);

                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPriorityDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p-6f*30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                    }
                    else if (answeri.getStateTrusnes()==StatesLikesSubs.NOT_SELECTED){
                        tvBodyAnswer.setBackgroundResource(R.drawable.red_stroke);
                        tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                        tvBodyAnswer.setTextColor(Color.parseColor("#d3d3d3"));
                        tvJavobQoniqarsiz.setTextColor(Color.parseColor("#ff3505"));
                        tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons_red);
                        tvJavobQoniqarli.setTextColor(Color.parseColor("#404040"));
                        tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/stateTrusnes").setValue(StatesLikesSubs.FAKE);
                        answeri.setStateTrusnes(StatesLikesSubs.FAKE);

                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPriorityDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p+6f*30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                    }
                    else if (answeri.getStateTrusnes()==StatesLikesSubs.TRUSTED){
                        tvBodyAnswer.setBackgroundResource(R.drawable.red_stroke);
                        tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                        tvBodyAnswer.setTextColor(Color.parseColor("#d3d3d3"));
                        tvJavobQoniqarsiz.setTextColor(Color.parseColor("#ff3505"));
                        tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons_red);
                        tvJavobQoniqarli.setTextColor(Color.parseColor("#404040"));
                        tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/stateTrusnes").setValue(StatesLikesSubs.FAKE);
                        answeri.setStateTrusnes(StatesLikesSubs.FAKE);

                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPriorityDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p+12f*30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                    }
                    questionAnswers.set(getAdapterPosition(),answeri);
                }
            });
            tvJavobQoniqarli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Answer answeri = (Answer) questionAnswers.get(getAdapterPosition());
                    if(answeri.getStateTrusnes() == StatesLikesSubs.TRUSTED ){
                        tvBodyAnswer.setBackgroundResource(R.drawable.emp);
                        tvBodyAnswer.setTypeface(null, Typeface.NORMAL);
                        tvBodyAnswer.setTextColor(Color.parseColor("#262626"));

                        tvJavobQoniqarsiz.setTextColor(Color.parseColor("#404040"));
                        tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons);
                        tvJavobQoniqarli.setTextColor(Color.parseColor("#404040"));

                        tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/stateTrusnes").setValue(StatesLikesSubs.NOT_SELECTED);
                        answeri.setStateTrusnes(StatesLikesSubs.NOT_SELECTED);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPriorityDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p+6f*30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                    }
                    else if(answeri.getStateTrusnes() == StatesLikesSubs.NOT_SELECTED ) {
                        tvBodyAnswer.setBackgroundResource(R.drawable.green_stroke);
                        tvBodyAnswer.setTypeface(null, Typeface.BOLD);
                        tvJavobQoniqarsiz.setTextColor(Color.parseColor("#404040"));
                        tvBodyAnswer.setTextColor(Color.parseColor("#262626"));

                        tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons);
                        tvJavobQoniqarli.setTextColor(Color.parseColor("#19b915"));
                        tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons_green_stroke);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/stateTrusnes").setValue(StatesLikesSubs.TRUSTED);
                        answeri.setStateTrusnes(StatesLikesSubs.TRUSTED);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPriorityDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p-6f*30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });

                    }
                    else if(answeri.getStateTrusnes() == StatesLikesSubs.FAKE ) {
                        tvBodyAnswer.setBackgroundResource(R.drawable.green_stroke);
                        tvBodyAnswer.setTypeface(null, Typeface.BOLD);
                        tvJavobQoniqarsiz.setTextColor(Color.parseColor("#404040"));
                        tvBodyAnswer.setTextColor(Color.parseColor("#262626"));

                        tvJavobQoniqarsiz.setBackgroundResource(R.drawable.diaolog_buttons);
                        tvJavobQoniqarli.setTextColor(Color.parseColor("#19b915"));
                        tvJavobQoniqarli.setBackgroundResource(R.drawable.diaolog_buttons_green_stroke);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/stateTrusnes").setValue(StatesLikesSubs.TRUSTED);
                        answeri.setStateTrusnes(StatesLikesSubs.TRUSTED);
                        rootRef.child("Answers/"+questionEntity.getKeyQuestion()+"/"+answeri.getKeyAnswer()+"/priority").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                long p = answeri.getPriorityDateLong();
                                if(mutableData.getValue()!=null)
                                    p =   mutableData.getValue(Long.class);
                                mutableData.setValue(p-12f*30f*24f*60f*60f*1000f);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });

                    }
                    questionAnswers.set(getAdapterPosition(),answeri);
                }
            });

        }
    }
    public class ViewHolderHeader extends RecyclerView.ViewHolder{
        LinkerTextView tvBodyQuestion;
        TextView tvDateQuestion;
        ImageView ivContentImage;
        FrameLayout togone;
        ProgressBar loading;
        TextView tvCountYurak;
        TextView tvCountQiziqishlar;
        TextView tvCountKorilgan;
        TextView tvCountAnswerlar;
        ImageView ivTrash;
        FrameLayout loadAnswers;
        public ViewHolderHeader(View itemView) {
            super(itemView);
            tvBodyQuestion = (LinkerTextView) itemView.findViewById(R.id.tvBodyQuestion);
            tvDateQuestion = (TextView) itemView.findViewById(R.id.tvDateQuestion);
            ivContentImage = (ImageView) itemView.findViewById(R.id.ivContentImage);
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            togone = (FrameLayout) itemView.findViewById(R.id.togone);
            loadAnswers = (FrameLayout) itemView.findViewById(R.id.loadAnswers);
            tvCountYurak = (TextView) itemView.findViewById(R.id.tvCountYurak);
            tvCountQiziqishlar = (TextView) itemView.findViewById(R.id.tvCountQiziqishlar);
            tvCountAnswerlar = (TextView) itemView.findViewById(R.id.tvCountAnswerlar);
            tvCountKorilgan = (TextView) itemView.findViewById(R.id.tvCountKorilgan);
            ivTrash = (ImageView) itemView.findViewById(R.id.ivTrash);
            ivTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.sovoli_ochirmoqchimisan)
                            .setPositiveButton(R.string.ochirish, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    rootRef.child("Questions/"+questionEntity.getKeyQuestion()).removeValue();
//                                    rootRef.child("UserQuestionList/"+firebaseUser.getUid()+"/"+questionEntity.getKeyQuestion()).removeValue();
//                                    rootRef.child("user-status/"+firebaseUser.getUid()+"/achivments/question").runTransaction(new Transaction.Handler() {
//                                        @Override
//                                        public Transaction.Result doTransaction(MutableData mutableData) {
//                                            long a = mutableData.getValue(Long.class);
//                                            mutableData.setValue(a-1);
//                                            return Transaction.success(mutableData);
//                                        }
//
//                                        @Override
//                                        public void onComplete(DatabaseError databaseError, boolean b,
//                                                               DataSnapshot dataSnapshot) {
//                                        }
//                                    });
//                                    ((HomilaDavri)getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();
                                    dialog.cancel();

                                }
                            }).setNegativeButton(R.string.ortgaa, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();

                }
            });
        }
    }
    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;}
}
