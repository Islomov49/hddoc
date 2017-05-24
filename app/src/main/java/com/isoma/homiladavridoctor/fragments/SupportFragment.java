package com.isoma.homiladavridoctor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.isoma.homiladavridoctor.Entity.ChatsEntity;
import com.isoma.homiladavridoctor.Entity.MessageChating;
import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.LaodingViewBlue;
import com.isoma.homiladavridoctor.utils.LinearManagerWithOutEx;
import com.isoma.homiladavridoctor.utils.LinkerTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SupportFragment extends Fragment {

    ArrayList<MessageChating> messageChatings;
    RecyclerView rvChating;
    private SharedPreferences sPref;
    ArrayList<MessageChating> messageChatingsTemp;
    FirebaseUser firebaseUser;
    ChatsEntity chatsEntity;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference STORAGEREF = storage.getInstance().getReference();
    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
    DatabaseReference rootRef = dataset.getReference();
    EditText etAnswer;
    ImageView ivSendAnswer;
    LaodingViewBlue loadingViewBlue;
    FrameLayout frameSendOrLoading;
    String firebaseUID;
    String roomID;
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


        messageChatings = new ArrayList<>();
        if(firebaseUser!=null)
            firebaseUID = firebaseUser.getUid();
        else firebaseUID = "anonimus";

        if(sPref.getString("firstMessageSupport","").equals("")){
            sPref.edit().putString("firstMessageSupport",rootRef.push().getKey()).apply();
        }
        roomID = sPref.getString("firstMessageSupport","");

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
                if(!etAnswer.getText().toString().isEmpty()){
                    if(loadingViewBlue == null) {
                        loadingViewBlue = new LaodingViewBlue(getContext());
                        loadingViewBlue.startAnim(dpToPx(40, getActivity()), dpToPx(40, getActivity()));
                        frameSendOrLoading.addView(loadingViewBlue);
                    }
                    loadingViewBlue.setVisibility(View.VISIBLE);
                    ivSendAnswer.setVisibility(View.GONE);
                    final MessageChating messageChating = new MessageChating(etAnswer.getText().toString(),firebaseUID,"forAdmin");
                    messageChating.setDate(System.currentTimeMillis());
                    rootRef.child("Support/"+roomID+"/writerUID").setValue(firebaseUID);
                    rootRef.child("Support/"+roomID+"/date").setValue(ServerValue.TIMESTAMP);

                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            rootRef.child("Support/"+roomID+"/newMessagesForAdmin").runTransaction(new Transaction.Handler() {
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
                    })).run();

                    DatabaseReference pushValue = rootRef.push();
                    messageChating.setPushValue(pushValue.getKey());
                    rootRef.child("Support/"+roomID+"/msg/"+messageChating.getPushValue()).setValue(messageChating).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        LinearManagerWithOutEx linearLayoutManager = new LinearManagerWithOutEx(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChating.setLayoutManager(linearLayoutManager);
        messagesAdapter = new AdapterForChat();
        rvChating.setAdapter(messagesAdapter);

        refreshList();
        return view;
    }




    AdapterForChat messagesAdapter;

    public void refreshList(){
        rootRef.child("Support/"+roomID+"/msg/").orderByChild("date").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    rootRef.child("Support/"+roomID+"/newMessages").setValue(0l);

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
                        .inflate(R.layout.message_from_me_item_to_admin, parent, false);
                holder = new ViewHolderFromMe(v);
            }
            else if(viewType == 1){
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_from_admin, parent, false);
                holder = new ViewHolderFromHer(v);
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



            }
            else if(mainHolder instanceof ViewHolderFromHer){
                final ViewHolderFromHer viewHolderFromHer = (ViewHolderFromHer) mainHolder;
                MessageChating messageChating = messageChatings.get(position);

                viewHolderFromHer.tvBodyMessagee.setLinkToAccountGuesta(messageChating.getMessage(),getActivity());
                viewHolderFromHer.tvDateQuestionn.setText(simpleDateFormat.format(new Date(messageChating.getDateLong())));



            }


        }

        @Override
        public int getItemCount() {
            return messageChatings.size();
        }

        @Override
        public int getItemViewType(int position) {
           return (messageChatings.get(position).getToUserUID().equals("forAdmin"))?0:1;
        }
    }
    public class ViewHolderFromMe extends RecyclerView.ViewHolder{
        LinkerTextView tvBodyMessage;
        TextView tvDateQuestion;
        public ViewHolderFromMe(View itemView) {
            super(itemView);
            tvBodyMessage = (LinkerTextView) itemView.findViewById(R.id.tvBodyMessage);
            tvDateQuestion = (TextView) itemView.findViewById(R.id.tvDateQuestion);
        }
    }
    public class ViewHolderFromHer extends RecyclerView.ViewHolder{
        LinkerTextView tvBodyMessagee;
        TextView tvDateQuestionn;
        public ViewHolderFromHer(View itemView) {
            super(itemView);
            tvBodyMessagee = (LinkerTextView) itemView.findViewById(R.id.tvBodyMessagee);
            tvDateQuestionn = (TextView) itemView.findViewById(R.id.tvDateQuestionn);
        }
    }


    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


}
