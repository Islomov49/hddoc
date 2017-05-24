package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.isoma.homiladavridoctor.Entity.ChatsEntity;
import com.isoma.homiladavridoctor.Entity.EventMessage;
import com.isoma.homiladavridoctor.Entity.MessageChating;
import com.isoma.homiladavridoctor.Entity.TempRoomAndPushedValue;
import com.isoma.homiladavridoctor.Entity.UserStatus;
import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.CommonOperations;
import com.isoma.homiladavridoctor.utils.LaodingViewBlue;
import com.isoma.homiladavridoctor.utils.LinearManagerWithOutEx;
import com.isoma.homiladavridoctor.utils.LinkerTextView;
import com.isoma.homiladavridoctor.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.isoma.homiladavridoctor.R.id.frameSendOrLoading;

public class ChatingFragment extends Fragment {

    public final static String USER_STATUS= "userStatus";
    public final static String CHATS_ENTITY= "userStatus";
    public final static String FROM_GUEST= "fromGuest";
    ArrayList<MessageChating> messageChatings;
    RecyclerView rvChating;
    private SharedPreferences sPref;
    ArrayList<MessageChating> messageChatingsTemp;
    FirebaseUser firebaseUser;
    ChatsEntity chatsEntity;
    UserStatus partner;
    UserStatus me;
    boolean fromGuest=  false;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getInstance().getReference();
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference rootRef = dataset.getReference();
    boolean dataRecived = false;
    Handler handler;
    EditText etAnswer;
    ImageView ivSendAnswer;
    LaodingViewBlue loadingViewBlue;
    FrameLayout frameSendOrLoading;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chating, container, false);
        rvChating = (RecyclerView) view.findViewById(R.id.rvChating);
        etAnswer = (EditText) view.findViewById(R.id.etAnswer);
        ivSendAnswer = (ImageView) view.findViewById(R.id.ivSendAnswer);
        frameSendOrLoading = (FrameLayout) view.findViewById(R.id.frameSendOrLoading);
        sPref = getActivity().getSharedPreferences("informat", getActivity().MODE_PRIVATE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        me = new UserStatus();
        me.setUserUID(firebaseUser.getUid());
        me.setNickName(sPref.getString(AccountFragment.LAST_NICKNAME,""));
        me.setAvatar(sPref.getString(AccountFragment.LAST_AVATAR,""));

        messageChatings = new ArrayList<>();


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
                    rvChating.scrollToPosition(messageChatings.size()-1);
                }
            }
        });

        ivSendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partner==null) return;
                if(!etAnswer.getText().toString().isEmpty()){
                    if(loadingViewBlue == null) {
                        loadingViewBlue = new LaodingViewBlue(getContext());
                        loadingViewBlue.startAnim(dpToPx(40, getActivity()), dpToPx(40, getActivity()));
                        frameSendOrLoading.addView(loadingViewBlue);
                    }
                    loadingViewBlue.setVisibility(View.VISIBLE);
                    ivSendAnswer.setVisibility(View.GONE);
                    final MessageChating messageChating = new MessageChating(etAnswer.getText().toString(),firebaseUser.getUid(),partner.getUserUID());
                    messageChating.setDate(System.currentTimeMillis());
                    String roomID;
                    if(chatsEntity != null)
                    roomID = chatsEntity.getRoomId();
                    else { roomID = CommonOperations.xorToKey(partner.getUserUID(),firebaseUser.getUid());
                    roomID = roomID.substring(0,roomID.length()-1);}
                    DatabaseReference pushValue = rootRef.push();
                    messageChating.setPushValue(pushValue.getKey());
                    rootRef.child("Rooms/"+roomID+"/"+messageChating.getPushValue()).setValue(messageChating).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ivSendAnswer.setVisibility(View.VISIBLE);
                            loadingViewBlue.setVisibility(View.GONE);
                            etAnswer.setText("");
                            messageChatings.add(messageChating);

                            messagesAdapter.notifyItemInserted(messageChatings.size()-1);
                            rvChating.scrollToPosition(messageChatings.size()-1);

                        }
                    });


                }else {
                    etAnswer.setError(getString(R.string.bosh_habar));
                }
            }
        });
        messageChatingsTemp = new ArrayList<MessageChating>();

        LinearLayoutManager linearLayoutManager = new LinearManagerWithOutEx(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChating.setLayoutManager(linearLayoutManager);
        messagesAdapter = new AdapterForChat();
        rvChating.setAdapter(messagesAdapter);

        if(getArguments()!=null){
            fromGuest  = getArguments().getBoolean(FROM_GUEST,false);
            if(fromGuest){
                partner = new Gson().fromJson(getArguments().getString(USER_STATUS),UserStatus.class);
                rootRef.child("Chats/"+firebaseUser.getUid()+"/"+partner.getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null){
                            chatsEntity = new ChatsEntity();
                            chatsEntity.setFromSnapshot(dataSnapshot);
                            refreshList();
                        }
                        //TODO SETADAPTER
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else {
                chatsEntity = new Gson().fromJson(getArguments().getString(CHATS_ENTITY), ChatsEntity.class);
                rootRef.child("user-status/"+chatsEntity.getKeyCompanion()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null){
                            partner = new UserStatus();
                            partner.setFromSnapshot(dataSnapshot);
                            refreshList();
                        }
                        //TODO SETADAPTER
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else ((HomilaDavri) getActivity()).getPaFragmentManager().getFragmentManager().popBackStack();


        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        refreshList();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAdded(EventMessage eventMessage) {
        if(eventMessage.getForClass().equals("all")||eventMessage.getForClass().equals("MyQuestions")){
                if(eventMessage.getStatus().equals("newMessage")) {
                    TempRoomAndPushedValue tempRoomAndPushedValue = (TempRoomAndPushedValue) eventMessage.getObjectForTransfer();
                    getOneLastQuestion(tempRoomAndPushedValue.getPushedValue(),tempRoomAndPushedValue.getRoomId());
                }
        }
    }



    AdapterForChat messagesAdapter;
    public void getOneLastQuestion(String keyQuestion,String roomID){
        if(chatsEntity.getRoomId().equals(roomID))
        rootRef.child("Rooms/"+chatsEntity.getRoomId()+"/"+keyQuestion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                MessageChating messageChating = new MessageChating();
                messageChating.setFromSnapshot(dataSnapshot);
//                messageChating.isReaded(rootRef, chatsEntity.getRoomId());
                messageChatings.add(messageChating);
                messagesAdapter.notifyItemInserted(messageChatings.size()-1);
                rvChating.scrollToPosition(messageChatings.size()-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getLastTwenty(){
        rootRef.child("Rooms/"+chatsEntity.getRoomId()).orderByChild("date").limitToLast(20).endAt(messageChatings.get(1).getDateLong()-1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                messageChatingsTemp.clear();
                if(dataSnapshots.getValue() != null){
                    for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                        MessageChating messageChating = new MessageChating();
                        messageChating.setFromSnapshot(dataSnapshot);
//                        messageChating.isReaded(rootRef, chatsEntity.getRoomId());
                        messageChatingsTemp.add(0, messageChating);
                    }
                    for(MessageChating messageChating: messageChatingsTemp)
                        messageChatings.add(1,messageChating);
                    if(dataSnapshots.getChildrenCount() !=20){
                        messageChatings.remove(0);
                    }
                    messagesAdapter.notifyDataSetChanged();
                }else {
                    messageChatings.remove(0);
                    messagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    boolean itIsReaded = false;
    public void refreshList(){
        if(chatsEntity!=null)
        rootRef.child("Rooms/"+chatsEntity.getRoomId()).orderByChild("date").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                messageChatings.clear();
                if(dataSnapshots.getValue()!=null) {
                    for (DataSnapshot dataSnapshot : dataSnapshots.getChildren()) {
                        MessageChating messageChating = new MessageChating();
                        messageChating.setFromSnapshot(dataSnapshot);
//                        messageChating.isReaded(rootRef, chatsEntity.getRoomId());
                        messageChatings.add( messageChating);
                    }
                    if(messageChatings.size()==20){
                        messageChatings.add(0,null);
                    }
                    itIsReaded = true;
                    rootRef.child("Chats/"+firebaseUser.getUid()+"/"+partner.getUserUID()+"/newMessages").setValue(0l);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public class AdapterForChat extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;
            RecyclerView.ViewHolder holder = null;
            if(viewType==0){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_from_me_item, parent, false);
                holder = new ViewHolderFromMe(v);
            }
            else if(viewType == 1){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_from_other_item, parent, false);
                holder = new ViewHolderFromHer(v);
            }
            else if(viewType == 2){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_load_more, parent, false);
                holder = new ViewHolderLoadMore(v);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder mainHolder, int position) {
            if(mainHolder instanceof ViewHolderFromMe){
                MessageChating messageChating = messageChatings.get(position);
                final ViewHolderFromMe viewHolderFromMe = (ViewHolderFromMe) mainHolder;

                viewHolderFromMe.tvBodyMessage.setLinkToAccountGuesta(messageChating.getMessage(),getActivity());
                viewHolderFromMe.tvDateQuestion.setText(simpleDateFormat.format(new Date(messageChating.getDateLong())));

                File extStore = Environment.getExternalStorageDirectory();
                final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + me.getAvatar()+ ".jpg");
                if (myFile.exists()) {
                    Picasso.with(getContext())
                            .load(myFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(viewHolderFromMe.ivAuMessage);
                }
                else {
                    if(NetworkUtils.isNetworkAvailable(getContext())) {
                        try {
                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                            if (!Aa.exists())
                                Aa.mkdirs();
                            final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + me.getAvatar() + ".jpg");
                            STORAGEREF.child("users/" + me.getUserUID()+"/"+me.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(getContext())
                                            .load(file)
                                            .placeholder(R.drawable.avatar)
                                            .error(R.drawable.avatar)
                                            .into(viewHolderFromMe.ivAuMessage);
                                }
                            });

                        } catch (Exception ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

            }
            else if(mainHolder instanceof ViewHolderFromHer){
                final ViewHolderFromHer viewHolderFromHer = (ViewHolderFromHer) mainHolder;
                MessageChating messageChating = messageChatings.get(position);

                viewHolderFromHer.tvBodyMessagee.setLinkToAccountGuesta(messageChating.getMessage(),getActivity());
                viewHolderFromHer.tvDateQuestionn.setText(simpleDateFormat.format(new Date(messageChating.getDateLong())));
                if(partner==null) return;

                File extStore = Environment.getExternalStorageDirectory();
                final File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + partner.getAvatar()+ ".jpg");
                if (myFile.exists()) {
                    Picasso.with(getContext())
                            .load(myFile)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(viewHolderFromHer.ivAuthorMessage);
                }
                else {
                    if(NetworkUtils.isNetworkAvailable(getContext())) {
                        try {
                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/");
                            if (!Aa.exists())
                                Aa.mkdirs();
                            final File file = new File(extStore.getAbsolutePath() + "/Homila/cache/mini/" + me.getAvatar() + ".jpg");
                            STORAGEREF.child("users/" + me.getUserUID()+"/"+me.getAvatar()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(getContext())
                                            .load(file)
                                            .placeholder(R.drawable.avatar)
                                            .error(R.drawable.avatar)
                                            .into(viewHolderFromHer.ivAuthorMessage);
                                }
                            });

                        } catch (Exception ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

            }
            else if(mainHolder instanceof ViewHolderLoadMore){

            }

        }

        @Override
        public int getItemCount() {
            return messageChatings.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(messageChatings.get(position) == null)
                return 2;
            return (messageChatings.get(position).getWriterUID().equals(firebaseUser.getUid()))?0:1;
        }
    }
    public class ViewHolderFromMe extends RecyclerView.ViewHolder{
        CircleImageView ivAuMessage;
        LinkerTextView tvBodyMessage;
        TextView tvDateQuestion;
        public ViewHolderFromMe(View itemView) {
            super(itemView);
            ivAuMessage = (CircleImageView) itemView.findViewById(R.id.ivAuMessage);
            tvBodyMessage = (LinkerTextView) itemView.findViewById(R.id.tvBodyMessage);
            tvDateQuestion = (TextView) itemView.findViewById(R.id.tvDateQuestion);
        }
    }
    public class ViewHolderFromHer extends RecyclerView.ViewHolder{
        CircleImageView ivAuthorMessage;
        LinkerTextView tvBodyMessagee;
        TextView tvDateQuestionn;
        public ViewHolderFromHer(View itemView) {
            super(itemView);
            ivAuthorMessage = (CircleImageView) itemView.findViewById(R.id.ivAuthorMessage);
            tvBodyMessagee = (LinkerTextView) itemView.findViewById(R.id.tvBodyMessagee);
            tvDateQuestionn = (TextView) itemView.findViewById(R.id.tvDateQuestionn);
        }
    }

    public class ViewHolderLoadMore extends RecyclerView.ViewHolder{

        public ViewHolderLoadMore(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getLastTwenty();
                }
            });
        }
    }

    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void onDestroy() {
        if(itIsReaded)
        rootRef.child("Chats/"+firebaseUser.getUid()+"/"+partner.getUserUID()+"/newMessages").setValue(0l);

        super.onDestroy();

    }
}
