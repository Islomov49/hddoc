package com.isoma.homiladavridoctor.fragments.nextstep;//package com.isoma.homiladavridoctor.fragments.nextstep;
//
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.text.method.LinkMovementMethod;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.MutableData;
//import com.google.firebase.database.ServerValue;
//import com.google.firebase.database.Transaction;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FileDownloadTask;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.isoma.homiladavridoctor.R;
//import com.isoma.homiladavridoctor.systemic.HomilaConstants;
//import com.isoma.homiladavridoctor.utils.CircleProgressBar;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.isoma.homiladavridoctor.Entity.nextstep.Lentadeteli;
//import com.isoma.homiladavridoctor.utils.LinkerText;
//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class LentaFragment extends Fragment {
//    private RecyclerView recycler;
//    private HashMap<DatabaseReference, ChildEventListener> mListenerMap=new HashMap<>();
//    private Context context;
//    private AdapterCardLenta cardAdap;
//    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
//    DatabaseReference GENERAL = dataset.getReference();
//    private Handler handConnnection;
//    private ArrayList<Lentadeteli> statiacollectionOFLINE;
//    private SharedPreferences sPref;
//    private SharedPreferences.Editor ed  ;
//    private ValueEventListener status;
//    private ArrayList<Lentadeteli> statiacollectionONLINE;
//    private ArrayList<String> deleteSTATIYALA;
//    private boolean onlineKey=false;
//    private int checkIsHow=0;
//    private View Myfrag;
//    public LentaFragment() {
//    }
//    public LentaFragment shareStatiyalarFragment() {
//        return this;
//    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context =getActivity();
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        final View frag=inflater.inflate(R.layout.fragment_statiyalar, container, false);
//        Myfrag=frag;
//        final ActionBar aCT=((AppCompatActivity) getActivity()).getSupportActionBar();
//        deleteSTATIYALA=new ArrayList<>();
//        if (aCT!=null)
//            aCT.setTitle(R.string.boglanis);
//        GENERAL.child("deleteLentas").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//            for(DataSnapshot tempDelete:dataSnapshot.getChildren()){
//                deleteSTATIYALA.add(tempDelete.getKey());
//          }
//            }
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//            }
//        });
//
//
//        statiacollectionOFLINE = new ArrayList<Lentadeteli>();
//        statiacollectionONLINE = new ArrayList<Lentadeteli>();
//        recycler = (RecyclerView) frag.findViewById(R.id.my_recycler_view);
//        LinearLayoutManager llm = new LinearLayoutManager(context);
//        recycler.setLayoutManager(llm);
//        cardAdap=new AdapterCardLenta(statiacollectionOFLINE, context,recycler,frag);
//        recycler.setAdapter(cardAdap);
//        handConnnection=new Handler();
//        ReadFromDatabase(5);
//        checkIsHow=2;
//        GENERAL.child(".info/connected").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if (connected&&checkIsHow!=1) {
//                    Thread A11=new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                       deletelentas();
//                        }
//                    });
//                    A11.start();
//                    if (aCT != null)
//                        aCT.setTitle("");
//                    cardAdap.setLoaded();
//                    onlineKey = true;
//                    if (aCT != null)
//                        aCT.setTitle("Лента");
//                    Log.d("online", "OnCONECT/");
//                    statiacollectionONLINE.clear();
//                    cardAdap.setOnline();
//                    cardAdap.changelist(statiacollectionONLINE);
//                    recycler.scrollToPosition(0);
//                    readFromFirebase();
//                    checkIsHow = 1;
//                } else if(checkIsHow!=2) {
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//
//            }
//        });
//        cardAdap.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if(onlineKey){
//                    if(statiacollectionONLINE.size()!=0) {
//                        statiacollectionONLINE.add(null);
//                        cardAdap.notifyItemInserted(statiacollectionONLINE.size() - 1);
//                        Log.d("SQLITEB","BOTTOM FR");
//                        GENERAL.child("listoflenta").orderByChild("creatAt").endAt(statiacollectionONLINE.get(statiacollectionONLINE.size() - 2).getDateLastChangedLong() - 1).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                statiacollectionONLINE.remove(statiacollectionONLINE.size() - 1);
//                                cardAdap.notifyItemRemoved(statiacollectionONLINE.size());
//                                int t = statiacollectionONLINE.size();
//                                List<DataSnapshot> once=new ArrayList();
//                                for (DataSnapshot onc : dataSnapshot.getChildren()) {
//                                    once.add(0,onc);
//                                                        }
//                                for (DataSnapshot oncek:once)
//                                statiacollectionONLINE.add( oncek.getValue(Lentadeteli.class));
//
//                                cardAdap.notifyItemRangeInserted(t, (int) dataSnapshot.getChildrenCount());
//                                cardAdap.setLoaded();
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError firebaseError) {
//
//                            }
//                        });
//                    }
//                    else  {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                cardAdap.setLoaded();
//                            }
//                        },500);
//                    }
//                }
//                else{
//                    if(statiacollectionOFLINE.size()!=0){
//               statiacollectionOFLINE.add(null);
//                cardAdap.notifyItemInserted(statiacollectionOFLINE.size() - 1);
//                ReadFromDatabase(5);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        statiacollectionOFLINE.remove(positionStart);
//                        cardAdap.notifyItemRemoved(positionStart+1);
//                        cardAdap.setLoaded();
//
//                    }
//                },100);
//                Log.d("SQLITEB","BOTTOM");}
//                else  {   new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            cardAdap.setLoaded();
//                        }
//                    },500);
//                    }
//                   }}
//        });
//        return frag;
//    }
//    private long LASTCREAT=0;
//    private int positionSQLITE=0;
//    private int positionStart=0;
//    private void deletelentas(){
//        try {
//            sdb.delete(HomilaConstants.STOLB_TABLE_LENTA, null, null);
//        }
//        catch (Exception o){
//
//        }
//        }
//    public void ReadFromDatabase(int limit){
//        Cursor cursor = sdb.query(HomilaConstants.STOLB_TABLE_LENTA, new String[]{
//                        HomilaConstants.STOLB_ID, HomilaConstants.STOLB_KEY, HomilaConstants.STOLB_TEXT,
//                        HomilaConstants.STOLB_LIKES,
//                        HomilaConstants.STOLB_WRITEBY , HomilaConstants.STOLB_PHOTO, HomilaConstants.STOLB_CREATBY}, null,
//                null,
//                null,
//                null,
//                HomilaConstants.STOLB_CREATBY+" DESC"
//        );
//
//
//          int p=0;
//
//          if(statiacollectionOFLINE.size()!=0)
//              positionStart=statiacollectionOFLINE.size()-1;
//          else {positionStart=0;
//              positionSQLITE=0;}
//          cursor.move(positionSQLITE);
//          while (cursor.moveToNext()&&p<limit) {
//
//              Lentadeteli temp=new Lentadeteli(cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_KEY)),cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_TEXT)),
//                      cursor.getLong(cursor.getColumnIndex(HomilaConstants.STOLB_LIKES)),cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_WRITEBY)),
//                      cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_PHOTO)),cursor.getLong(cursor.getColumnIndex(HomilaConstants.STOLB_CREATBY)));
//              statiacollectionOFLINE.add(temp);
//              if(LASTCREAT==0){
//                  LASTCREAT=temp.getDateLastChangedLong();
//                  Log.d("SQLITEB",Long.toString(LASTCREAT));
//              }
//              Log.d("SQLITEB","READ : "+temp.getKEY());
//              p++;
//              positionSQLITE++;
//          }
//          cardAdap.notifyItemRangeInserted(positionStart,positionSQLITE);
//          cursor.moveToFirst();
//          cursor.close();
//    }
//    boolean keyF=false;
//    int position=0;
//
//    public void readFromFirebase(){
//
//        GENERAL.child("listoflenta").orderByChild("creatAt").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                List<DataSnapshot> once=new ArrayList();
//                for (DataSnapshot onc : dataSnapshot.getChildren()) {
//                    once.add(0,onc);
//                }
//                for (DataSnapshot oncek:once)
//                    statiacollectionONLINE.add( oncek.getValue(Lentadeteli.class));
//
//                if((int) dataSnapshot.getChildrenCount()==5){
//                   keyF=true;
//
//                }
//                cardAdap.notifyItemRangeInserted(0, (int) dataSnapshot.getChildrenCount());
//                recycler.scrollToPosition(0);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//
//            }
//        });
//    }
//    public boolean KeyNotExist(String empNo) {
//
//        Cursor cursorik = null;
//
//        try{
//            cursorik = sdb.rawQuery("SELECT "+ HomilaConstants.STOLB_KEY+" FROM "+ HomilaConstants.STOLB_TABLE_LENTA+" WHERE "+ HomilaConstants.STOLB_KEY+"=?", new String[] {empNo + ""});
//            // cursorik = sdb.query(HomilaConstants.STOLB_TABLE_STATIYA, new String[] { HomilaConstants.STOLB_KEY }, HomilaConstants.STOLB_KEY + "=?",
//                    //new String[] { empNo }, null, null, null, null);
//            Log.d("SQLITEB",Integer.toString(cursorik.getCount()) );
//            if(cursorik.getCount() > 0) {
//                cursorik.moveToFirst();
//                return false;
//            }
//            return true;
//        }finally {
//            cursorik.close();
//        }
//    }
//    public void WriteToBASE(){
//        if(statiacollectionONLINE.size()!=0){
//   for(int t=statiacollectionONLINE.size()-1;t>=0;t--){
//       Lentadeteli temp=statiacollectionONLINE.get(t);
//        if(KeyNotExist(temp.getKEY())){
//            ContentValues cv = new ContentValues();
//            cv.put(HomilaConstants.STOLB_KEY, temp.getKEY());
//            cv.put(HomilaConstants.STOLB_CREATBY, temp.getDateLastChangedLong());
//            cv.put(HomilaConstants.STOLB_LIKES, temp.getLikes());
//            cv.put(HomilaConstants.STOLB_PHOTO, temp.getPhoto());
//            cv.put(HomilaConstants.STOLB_TEXT, temp.getText());
//            cv.put(HomilaConstants.STOLB_WRITEBY, temp.getWriteBy());
//            sdb.insert(HomilaConstants.STOLB_TABLE_LENTA, null, cv);
//            Log.d("SQLITEB","WRITE : "+temp.getKEY());
//        }
//       else {
//            Log.d("SQLITEB","NOT WRITE : "+temp.getKEY());
//        }
//   }}
//        else     Log.d("SQLITEB","EMPTY LIST");
//
//    }
//
//
//    @Override
//    public void onResume(){
//        super.onResume();
//
//    }
//    @Override
//    public void onDetach() {
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name_main);
//        //GENERAL.child("listofarticles").orderByChild("creatAt").limitToLast(3).removeEventListener(tempListner  );
//        cardAdap.DeleteListners();
//        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mListenerMap.entrySet()) {
//            DatabaseReference ref = entry.getKey();
//            ChildEventListener listener = entry.getValue();
//            ref.removeEventListener(listener);
//        }
//        WriteToBASE();
//
//         super.onDetach();
//    }
//
//
//
//    class AdapterCardLenta extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        private ArrayList<Lentadeteli> cards;
//        private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
//        private DatabaseReference GENERAL = dataset.getReference();
//        private Context context;
//        private HashMap<DatabaseReference, ValueEventListener> mListenerMap=new HashMap<>();
//        private boolean Online = false;
//        private SharedPreferences sPref;
//        private SharedPreferences.Editor ed ;
//        private String myname;
//        private InfoMaqolaFragment nInfo;
//        private final int VIEW_ITEM = 1;
//        private final int VIEW_PROG = 0;
//        private View frag;
//        private int visibleThreshold = 2;
//        private int lastVisibleItem, totalItemCount;
//        private boolean loading;
//        private OnLoadMoreListener onLoadMoreListener;
//        private FirebaseStorage storage = FirebaseStorage.getInstance();
//        private StorageReference STORAGEREF = storage.getReferenceFromUrl("gs://project-36196244065594115.appspot.com");
//
//        public void setOnline(){
//            Online=true;
//
//        }
//        public void setOffline(){
//            Online=false;
//
//        }
//
//        public int dpToPx(int dp, Context context) {
//            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//            int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//            return px;
//        }
//        public void changelist(ArrayList<Lentadeteli> persons){
//            this.cards=persons;
//            notifyDataSetChanged();
//        }
//        private int windowWidth = 0;
//        private int sides = 0;
//        public AdapterCardLenta(ArrayList<Lentadeteli> persons, Context context, RecyclerView recyclerView, View frag){
//            this.cards = persons;
//            this.context = context;
//            sPref = this.context.getSharedPreferences("informat", Context.MODE_PRIVATE);
//            ed = sPref.edit();
//            windowWidth =sPref.getInt(HomilaConstants.SAVED_WIDTH,0);
//            sides = dpToPx(16,context);
//            myname = sPref.getString(HomilaConstants.USER_NAME,"Anonymous");
//            nInfo = new InfoMaqolaFragment();
//            this.frag=frag;
//            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//
//                        totalItemCount = linearLayoutManager.getItemCount();
//                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//
//                        // Log.d("SQLITEB",">>> total item count: "+Integer.toString(totalItemCount)+"\n>>> last visible item: "+Integer.toString(lastVisibleItem));
//                        if (!loading &&(totalItemCount <= (lastVisibleItem + visibleThreshold)) ) {
//                            if (onLoadMoreListener != null) {
//                                onLoadMoreListener.onLoadMore();
//                            }
//                            loading = true;
//                        }
//                    }
//                });
//            }
//
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return cards.get(position) != null ? VIEW_ITEM : VIEW_PROG;
//        }
//
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            RecyclerView.ViewHolder vh;
//            if (viewType == VIEW_ITEM) {
//                View v = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.card_item_lenta, parent, false);
//
//                vh = new StatviewHolder(v);
//            } else {
//                View v = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.progres_item, parent, false);
//                vh = new ProgressViewHolder(v);
//            }
//            return vh;
//
//
//
//        }
//
//        public  class ProgressViewHolder extends RecyclerView.ViewHolder {
//            public ProgressBar progressBar;
//
//            public ProgressViewHolder(View v) {
//                super(v);
//                progressBar = (ProgressBar) v.findViewById(R.id.proggg);
//            }
//        }
//
//        public void setLoaded() {
//            loading = false;
//        }
//        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
//            this.onLoadMoreListener = onLoadMoreListener;
//        }
//
//
//        @Override
//        public void onBindViewHolder( final RecyclerView.ViewHolder holdeer, int position) {
//            if (holdeer instanceof StatviewHolder) {
//
//                final StatviewHolder holder=(StatviewHolder) holdeer;
//                if (position==0)
//                    holder.ifFirst.setVisibility(View.VISIBLE);
//                final Lentadeteli A1=cards.get(position);
//                File extStore = Environment.getExternalStorageDirectory();
//                File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/" + A1.getPhoto()+ ".jpg");
//                if (myFile.exists()) {
//                    Picasso.with(context)
//                            .load(myFile)
//                            .into(holder.ivMainImage, new Callback() {
//                                @Override
//                                public void onSuccess() {
//                                    holder.flToGone.setVisibility(View.GONE);
//                                    holder.pbLoading.setVisibility(View.GONE);
//                                    if (windowWidth !=0) {
//                                        holder.ivMainImage.setMaxHeight( windowWidth - sides);
//                                    }
//                                    holder.ivMainImage.setVisibility(View.VISIBLE);
//                                }
//
//                                @Override
//                                public void onError() {
//
//                                }
//                            });
//                } else {
//                    if(Online) {
//                        try {
//                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/");
//                            if (!Aa.exists())
//                                Aa.mkdirs();
//                            STORAGEREF.child("lentabuck/" + A1.getPhoto()).getFile(new File(extStore.getAbsolutePath() + "/Homila/cache/" + A1.getPhoto() + ".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                    Picasso.with(context)
//                                            .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/" + A1.getPhoto() + ".jpg"))
//                                            .into(holder.ivMainImage, new Callback() {
//                                                @Override
//                                                public void onSuccess() {
//                                                    holder.flToGone.setVisibility(View.GONE);
//                                                    holder.pbLoading.setVisibility(View.GONE);
//                                                    if (windowWidth !=0) {
//                                                        holder.ivMainImage.setMaxHeight( windowWidth - sides);
//
//                                                    }
//                                                    holder.ivMainImage.setVisibility(View.VISIBLE);
//                                                }
//
//                                                @Override
//                                                public void onError() {
//
//                                                }
//                                            });
//                                }
//                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//                                @Override
//                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                                    holder.pbLoading.setProgress((int)progress);
//                                }
//                            });
//                        } catch (Exception ex) {
//                            Thread.currentThread().interrupt();
//                        }
//                    }
//                }
//                File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" +A1.getWriteBy()+ ".jpg");
//                if (myFileForCache.exists()) {
//                    Picasso.with(context)
//                            .load(myFileForCache)
//                            .into(holder.cibAuthPhoto);
//                }
//                else {
//                    if(Online) {
//                        GENERAL.child("user-status/"+A1.getWriteBy()+"/avatar").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(final DataSnapshot dataSnapshot) {
//                                try {
//                                    Log.e("awsS3",dataSnapshot.getValue(String.class));
//                                    File Aa=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/");
//                                    if(!Aa.exists())
//                                        Aa.mkdirs();
//                                    STORAGEREF.child("users/"+ A1.getWriteBy()+"/cache/"+dataSnapshot.getValue(String.class)).getFile( new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/"+A1.getWriteBy()+".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                            Picasso.with(context)
//                                                    .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/"+A1.getWriteBy()+".jpg"))
//                                                    .into(holder.cibAuthPhoto);
//                                        }
//                                    });
//                                } catch (Exception ex) {
//                                    Thread.currentThread().interrupt();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError firebaseError) {
//
//                            }
//                        });
//                    }
//                }
//
//
//                holder.tvAuthName.setText(LinkerText.addClickablePart("@" + A1.getWriteBy(), new LinkerText.doSomethink() {
//                    @Override
//                    public void clickedA(String textView) {
//                        Toast.makeText(context,textView,Toast.LENGTH_SHORT).show();
//                    }
//                }));
//
//                holder.tvAuthName.setMovementMethod(LinkMovementMethod.getInstance());
//                holder.tvAuthName.setHighlightColor(Color.TRANSPARENT);
//
//
//                Date AAa = (new Date());
//                AAa.setTime(A1.getDateLastChangedLong());
//                holder.tvCreateAt.setText((new SimpleDateFormat("dd.MM.yy HH:mm")).format(AAa));
//                holder.tvBody.setText(A1.getText());
//
//                if(!sPref.getBoolean(Long.toString(A1.getDateLastChangedLong()),true)){
//                    holder.ivHearts.setImageResource(R.drawable.heart);
//                }
//
//
//                holder.ivHearts.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(sPref.getBoolean(Long.toString(A1.getDateLastChangedLong()),true)){
//
//                            holder.ivHearts.setImageResource(R.drawable.heart);
//                            if (Online)
//                                GENERAL.child("/Likes/Lentas/"+A1.getKEY()+"/likes").runTransaction(new Transaction.Handler() {
//                                    @Override
//                                    public Transaction.Result doTransaction(MutableData currentData) {
//                                        if (currentData.getValue() == null) {
//                                            currentData.setValue(1);
//                                        } else {
//                                            currentData.setValue((Long) currentData.getValue() + 1);
//                                        }
//                                        ed.putBoolean(Long.toString(A1.getDateLastChangedLong()),false);
//                                        ed.commit();
//                                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
//                                    }
//
//                                    @Override
//                                    public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                                        Log.d("Firebase",Boolean.toString(sPref.getBoolean(Long.toString(A1.getDateLastChangedLong()),true)));
//                                        GENERAL.child("/Likes/Lentas/"+A1.getKEY()+"/WhoLikeIt/"+myname).setValue(ServerValue.TIMESTAMP);
//                                    }
//                                });
//                        }
//                        else{
//                            holder.ivHearts.setImageResource(R.drawable.emptyheart);
//                            if (Online)
//                                GENERAL.child("/Likes/Lentas/"+A1.getKEY()+"/likes").runTransaction(new Transaction.Handler() {
//                                    @Override
//                                    public Transaction.Result doTransaction(MutableData currentData) {
//                                        if (currentData.getValue() == null) {
//                                            currentData.setValue(1);
//                                        } else {
//                                            currentData.setValue((Long) currentData.getValue() - 1);
//                                        }
//
//                                        ed.putBoolean(Long.toString(A1.getDateLastChangedLong()),true);
//                                        ed.commit();
//                                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
//                                    }
//
//                                    @Override
//                                    public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                                        Log.d("Firebase",Boolean.toString(sPref.getBoolean(Long.toString(A1.getDateLastChangedLong()),true)));
//                                        GENERAL.child("/Likes/Lentas/"+A1.getKEY()+"/WhoLikeIt/"+myname).removeValue();
//
//
//                                    }
//                                });
//                        }
//                    }
//                });
//
//
//
//                if(Online)
//                    GENERAL.child("/Likes/Lentas/"+A1.getKEY()+"/likes").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            mListenerMap.put(dataSnapshot.getRef(),this);
//                            holder.tvHearts.setText(Long.toString((dataSnapshot.getValue(Long.class)==null)?0l:dataSnapshot.getValue(Long.class)));
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError firebaseError) {
//
//                        }
//                    });
//                final List<DataSnapshot> mComentsWeHave;
//                mComentsWeHave=new ArrayList<>();
//
//                holder.ivComments.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        FragmentTransaction fragmentTransaction_jinstest = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction_jinstest.add(R.id.frame,(new InfoLentaFragment()).sharenformat(A1,frag,true,mComentsWeHave) , "nInfOO");
//                        fragmentTransaction_jinstest.commit();
//
//                    }
//                });
//                holder.llToInfo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        FragmentTransaction fragmentTransaction_jinstest = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
//
//                        fragmentTransaction_jinstest.add(R.id.frame,(new InfoLentaFragment()).sharenformat(A1,frag,false,mComentsWeHave) , "nInfOO");
//                        fragmentTransaction_jinstest.commit();
//                    }
//                });
//
//                GENERAL.child("messageRooms/Comments/"+A1.getKEY()).orderByChild("createAt").limitToLast(3).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        mListenerMap.put(dataSnapshot.getRef(),this);
//                        holder.llComments.removeAllViews();
//                        mComentsWeHave.clear();
//                        for (DataSnapshot datasnap:dataSnapshot.getChildren()){
//                            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            final TextView tv=new TextView(context);
//                            lparams.setMargins(16,4,16,0);
//                            tv.setLayoutParams(lparams);
//                            tv.setTextColor(Color.parseColor("#89000000"));
//                            tv.setTextSize(14);
//                            String mText="@" + datasnap.child("whoIm").getValue(String.class)+" "+datasnap.child("glavText").getValue(String.class);
//                            mComentsWeHave.add(datasnap);
//                            tv.setText(LinkerText.addClickablePart(getSafeSubstring(mText,150) , new LinkerText.doSomethink() {
//                                @Override
//                                public void clickedA(String textView) {
//                                    Toast.makeText(context,textView,Toast.LENGTH_SHORT).show();
//                                }
//                            }));
//                            tv.setMovementMethod(LinkMovementMethod.getInstance());
//                            tv.setHighlightColor(Color.TRANSPARENT);
//
//
//
//                            holder.llComments.addView(tv);
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError firebaseError) {
//
//                    }
//                });
//
//
//            } else {
//                ((ProgressViewHolder) holdeer).progressBar.setIndeterminate(true);
//            }
//
//        }
//
//        public String getSafeSubstring(String s, int maxLength){
//            if(!TextUtils.isEmpty(s)){
//                if(s.length() >= maxLength){
//                    int t;
//                    int siz=s.length();
//                    for(t=maxLength;t<siz;t++){
//                        if(s.charAt(t)==' '||s.charAt(t)=='.'||s.charAt(t)=='@'){
//                            break;
//                        }
//                    }
//                    if(t==siz-1){
//                        return s;
//                    }
//                    return s.substring(0, t)+" ...";
//                }
//            }
//            return s;
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return cards.size();
//        }
//
//        private class StatviewHolder extends RecyclerView.ViewHolder {
//            private ImageView ivMainImage, ivHearts, ivComments;
//            private TextView tvBody, tvAuthName, tvCreateAt, tvHearts;
//            private LinearLayout llToInfo, llComments;
//            private FrameLayout ifFirst;
//            private CircleImageView cibAuthPhoto;
//            private CircleProgressBar pbLoading;
//            private FrameLayout flToGone;
//            StatviewHolder(View itemView){
//                super(itemView);
//                ifFirst=(FrameLayout) itemView.findViewById(R.id.ifFirst);
//                ivMainImage=(ImageView) itemView.findViewById(R.id.photostat);
//                ivComments =(ImageView) itemView.findViewById(R.id.coommentariya);
//                ivHearts =(ImageView) itemView.findViewById(R.id.emptyheart);
//                llComments =(LinearLayout) itemView.findViewById(R.id.comentik);
//                tvBody =(TextView) itemView.findViewById(R.id.bodyst);
//                tvAuthName =(TextView) itemView.findViewById(R.id.authname);
//                tvCreateAt =(TextView) itemView.findViewById(R.id.creatat);
//                tvHearts =(TextView) itemView.findViewById(R.id.hearts);
//                cibAuthPhoto =(CircleImageView) itemView.findViewById(R.id.authphoto);
//                llToInfo =(LinearLayout) itemView.findViewById(R.id.toInfo);
//                pbLoading =(CircleProgressBar) itemView.findViewById(R.id.loading);
//                flToGone =(FrameLayout) itemView.findViewById(R.id.togone);
//            }
//        }
//
//        public void DeleteListners(){
//
//            for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenerMap.entrySet()) {
//                DatabaseReference ref = entry.getKey();
//                ValueEventListener listener = entry.getValue();
//                ref.removeEventListener(listener);
//                Log.d("removeListners","is Remove");
//            }
//        }
//
//    }
//
//    public interface OnLoadMoreListener {
//        void onLoadMore();
//    }
//
//
//}
