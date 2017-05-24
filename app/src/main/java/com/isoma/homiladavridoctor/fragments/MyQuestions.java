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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.QuestionEntity;
import com.isoma.homiladavridoctor.Entity.QuestionKeyWithEvent;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.systemic.HomilaConstants;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.LinearManagerWithOutEx;
import com.isoma.homiladavridoctor.utils.LinkerTextView;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MyQuestions extends Fragment {
    RecyclerView rvQuestions;
    TextView tvHint;
    TwinklingRefreshLayout refreshLayout;
    private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = dataset.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference STORAGEREF = storage.getReference();
    ArrayList<QuestionEntity> questionEntities;
    ArrayList<QuestionEntity> questionEntitiesTemp;
    FirebaseUser firebaseUser;
    long counter = 0;
    long geted = 0;
    final static long REFSHING_COUNT = 7;
    final static long LODING_COUNT = 5;
    private int windowWidth=0;
    private int sides=0;
    private SharedPreferences sPref;
    int lastDownloadedPosition =0;
    int started =0;
    MyQustionFragmentAdapter myQustionFragmentAdapter;
    ArrayList<QuestionKeyWithEvent> myQuestionsKeys;
    ArrayList<QuestionKeyWithEvent> myQuestionsKeysTemp;


    //TODO Zapisovat starie znaceniya na sharedPref PRI IZMENENIYA STATE QUESTION pokazat cto tut novogo sluvilos
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_questions, container, false);
        tvHint = (TextView) view.findViewById(R.id.tvHint);
        refreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.refreshLayout);
        rvQuestions = (RecyclerView) view.findViewById(R.id.recyclerQuestions);
        ProgressLayout headerView = new ProgressLayout(getContext());
        headerView.setColorSchemeColors(Color.parseColor("#0397da"));
        LoadingView loadingView = new LoadingView(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        refreshLayout.setBottomView(loadingView);
        refreshLayout.setHeaderView(headerView);
//        refreshLayout.setAutoLoadMore(true);
        questionEntities = new ArrayList<>();
        questionEntitiesTemp = new ArrayList<>();
        myQuestionsKeys = new ArrayList<>();
        myQuestionsKeysTemp = new ArrayList<>();
        myQustionFragmentAdapter = new MyQustionFragmentAdapter();
        sPref = getContext().getSharedPreferences("informat", MODE_PRIVATE);
        windowWidth = sPref.getInt(HomilaConstants.SAVED_WIDTH,0);
        sides = dpToPx(16,getContext());
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshLayout.setEnableLoadmore(false);

                if(NetworkUtils.isNetworkAvailable(getContext()))
                rootRef.child("/UserQuestionList/"+firebaseUser.getUid()).orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
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
                            return;
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
                            rootRef.child("Questions/"+myQuestionsKeysTemp.get(i).getQuestionKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    QuestionEntity questionEntity = new QuestionEntity();
                                    questionEntity.setFromSnapshot(dataSnapshot);
                                    questionEntitiesTemp.add(questionEntity);
                                    geted ++;
                                    if(counter == geted){
                                        geted = 0;
                                        refreshLayout.finishRefreshing();
                                        refreshLayout.setEnableLoadmore(true);
                                        questionEntities = (ArrayList<QuestionEntity>) questionEntitiesTemp.clone();
                                        questionEntitiesTemp.clear();
                                        myQuestionsKeys = (ArrayList<QuestionKeyWithEvent>) myQuestionsKeysTemp.clone();
                                        myQuestionsKeysTemp.clear();
                                        myQustionFragmentAdapter.notifyDataSetChanged();

                                        tvHint.setVisibility(View.GONE);
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

                    }
                });
                else {
                    refreshLayout.finishRefreshing();
                    Toast.makeText(getContext(), R.string.internet_connection_failed,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
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
                        rootRef.child("Questions/"+myQuestionsKeys.get(++lastDownloadedPosition).getQuestionKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                QuestionEntity questionEntity = new QuestionEntity();
                                questionEntity.setFromSnapshot(dataSnapshot);
                                questionEntities.add(questionEntity);
                                geted ++;
                                if(counter == geted){
                                    geted = 0;
                                    refreshLayout.finishLoadmore();
                                    myQustionFragmentAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    refreshLayout.setEnableLoadmore(false);
                    refreshLayout.finishLoadmore();
                }
            }
        });
        rvQuestions.setLayoutManager(new LinearManagerWithOutEx(getContext()));
        rvQuestions.setAdapter(myQustionFragmentAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        rvQuestions.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.startRefresh();
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAdded(EventMessage eventMessage) {
        if(eventMessage.getForClass().equals("all")||eventMessage.getForClass().equals("MyQuestions")){
            if(eventMessage.getStatus().equals("added")){
                refreshLayout.onRefresh(refreshLayout);
                tvHint.setVisibility(View.GONE);
            }
            else if (eventMessage.getStatus().equals("changeitem")){
                QuestionEntity fromOutside = (QuestionEntity) eventMessage.getObjectForTransfer();
                for(int i= 0;i<questionEntities.size();i++){
                    if(questionEntities.get(i).getKeyQuestion().equals(fromOutside.getKeyQuestion())){
                        questionEntities.set(i,fromOutside);
                        myQustionFragmentAdapter.notifyItemChanged(i);
                    }
                }
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
            else if(eventMessage.getStatus().equals("newAnswer")){
                refreshLayout.onRefresh(refreshLayout);
                tvHint.setVisibility(View.GONE);
            }
        }
    }

    class MyQustionFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        public MyQustionFragmentAdapter(){
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            if (viewType == 1) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_question_item_adding, parent, false);
                vh = new AddingViewHolder(v);
            } else if (viewType == 0) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_question_item, parent, false);
                vh = new MyQuestionViewHolder(v);
            }
            return  vh;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof AddingViewHolder){

            }
            else {
                final QuestionEntity questionEntity = questionEntities.get(position);
                final MyQuestionViewHolder  myQuestionViewHolder = (MyQuestionViewHolder) holder;
                if(position==0)
                    myQuestionViewHolder.infirstgone.setVisibility(View.GONE);
                myQuestionViewHolder.tvBodyQuestion.setLinkToAccountGuesta(questionEntity.getQuestionText(),getActivity());
                myQuestionViewHolder.tvDateQuestion.setText(simpleDateFormat.format(new Date(questionEntity.getPublishedDateLong())));
                myQuestionViewHolder.tvCountYurak.setText(String.valueOf(questionEntity.getStateQuestion().getLikes()));
                myQuestionViewHolder.tvCountQiziqishlar.setText(String.valueOf(questionEntity.getStateQuestion().getSubsribers()));
                myQuestionViewHolder.tvCountKorilgan.setText(String.valueOf(questionEntity.getStateQuestion().getViews()));
                myQuestionViewHolder.tvCountAnswerlar.setText(String.valueOf(questionEntity.getStateQuestion().getAnswer()));

                if(myQuestionsKeys.get(position).getNewEvent() !=0){
                    myQuestionViewHolder.tvOpen.setText("+"+String.valueOf(myQuestionsKeys.get(position).getNewEvent())+" "+getString(R.string.javob));
                    myQuestionViewHolder.tvOpen.setTextColor(Color.parseColor("#19b915"));

                }else {
                    myQuestionViewHolder.tvOpen.setText(R.string.korish);
                    myQuestionViewHolder.tvOpen.setTextColor(Color.parseColor("#262626"));
                }

                if(!questionEntity.getPhotoID().isEmpty()){
                    myQuestionViewHolder.togone.setVisibility(View.VISIBLE);
                    myQuestionViewHolder.loading.setVisibility(View.VISIBLE);
                    File extStore = Environment.getExternalStorageDirectory();
                    File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/" + questionEntity.getPhotoID()+ ".jpg");
                    if (myFile.exists()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(myFile.getAbsolutePath(), options);
                        myQuestionViewHolder.ivContentImage.setImageBitmap(bitmap);
                        myQuestionViewHolder.loading.setVisibility(View.GONE);
                        if (windowWidth!=0) {
                            myQuestionViewHolder.ivContentImage.setMaxHeight( windowWidth-sides );
                        }

                    }
                    else {
                        if(questionEntity.getThumbnail()!=null) {
                            if(!questionEntity.getThumbnail().isEmpty()) {
                                Bitmap bitmap_thunbal = CommonOperations.StringToBitMap(questionEntity.getThumbnail());
                                myQuestionViewHolder.ivContentImage.setImageBitmap(CommonOperations.fastblur(bitmap_thunbal, bitmap_thunbal.getWidth(), bitmap_thunbal.getHeight(), 3));

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
                                        myQuestionViewHolder.ivContentImage.setImageBitmap(bitmap);
                                        myQuestionViewHolder.loading.setVisibility(View.GONE);
                                        if (windowWidth!=0) {
                                            myQuestionViewHolder.ivContentImage.setMaxHeight( windowWidth-sides );
                                        }


                                    }
                                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        myQuestionViewHolder.loading.setProgress((int)progress);
                                    }
                                });


                            } catch (Exception ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }



                }
                else {
                    myQuestionViewHolder.togone.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return questionEntities.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 1)
                tvHint.setVisibility(View.GONE);
            return (position>=questionEntities.size())?1:0;
        }

        class AddingViewHolder extends RecyclerView.ViewHolder {
            TextView tvAddQuestion;


            public AddingViewHolder(View itemView) {
                super(itemView);
                tvAddQuestion = (TextView) itemView.findViewById(R.id.tvAddQuestion);
                tvAddQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HomilaDavri)getActivity()).getPaFragmentManager().displayFragment(new AddQuestionFragment());
                    }
                });
            }
        }
        class MyQuestionViewHolder extends RecyclerView.ViewHolder {
            LinkerTextView tvBodyQuestion;
            TextView tvOpen;
            TextView tvDateQuestion;
            TextView tvCountYurak;
            TextView tvCountQiziqishlar;
            TextView tvCountKorilgan;
            TextView tvCountAnswerlar;
            ImageView ivContentImage;
            ProgressBar loading;
            FrameLayout togone;
            View infirstgone;
            public MyQuestionViewHolder(View itemView) {
                super(itemView);
                tvBodyQuestion = (LinkerTextView) itemView.findViewById(R.id.tvBodyQuestion);
                tvOpen = (TextView) itemView.findViewById(R.id.tvOpen);
                tvDateQuestion = (TextView) itemView.findViewById(R.id.tvDateQuestion);
                tvCountYurak = (TextView) itemView.findViewById(R.id.tvCountYurak);
                tvCountQiziqishlar = (TextView) itemView.findViewById(R.id.tvCountQiziqishlar);
                tvCountKorilgan = (TextView) itemView.findViewById(R.id.tvCountKorilgan);
                tvCountAnswerlar = (TextView) itemView.findViewById(R.id.tvCountAnswerlar);
                togone = (FrameLayout) itemView.findViewById(R.id.togone);
                ivContentImage = (ImageView) itemView.findViewById(R.id.ivContentImage);
                loading = (ProgressBar) itemView.findViewById(R.id.loading);
                infirstgone =(View) itemView.findViewById(R.id.infirstgone);

                tvOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(questionEntities.size()==0)
                        {
                            refreshLayout.startRefresh();
                            return;
                        }
                        OpenedMyQuestionFragment openedMyQuestionFragment = new OpenedMyQuestionFragment();
                        Bundle bundle = new Bundle();
                        QuestionEntity questionEntity = questionEntities.get(getAdapterPosition());

                        bundle.putString(OpenedQuestionFragment.QUESTION_ENTITY,new Gson().toJson(questionEntity));
                        openedMyQuestionFragment.setArguments(bundle);
                        ((HomilaDavri) getActivity()).getPaFragmentManager().displayFragment(openedMyQuestionFragment);
                    }
                });

            }
        }
    }
    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
