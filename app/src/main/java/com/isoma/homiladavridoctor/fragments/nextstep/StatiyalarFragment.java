package com.isoma.homiladavridoctor.fragments.nextstep;//package com.isoma.homiladavridoctor.fragments.nextstep;
//
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
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
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
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
//import com.isoma.homiladavridoctor.Entity.nextstep.MaqolaEntity;
//import com.isoma.homiladavridoctor.R;
//import com.isoma.homiladavridoctor.systemic.HomilaConstants;
//import com.isoma.homiladavridoctor.utils.CommonOperations;
//import com.isoma.homiladavridoctor.utils.LinkerText;
//import com.squareup.picasso.Picasso;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class StatiyalarFragment extends Fragment {
//    private RecyclerView recycler;
//    private HashMap<DatabaseReference, ChildEventListener> mListenerMap=new HashMap<>();
//    private Context context;
//    private AdapterCardStatiyalar cardAdap;
//    FirebaseDatabase dataset= FirebaseDatabase.getInstance();
//    DatabaseReference GENERAL = dataset.getReference();
//    private Handler handConnnection;
//    private ArrayList<MaqolaEntity> statiacollectionOFLINE;
//    private SharedPreferences sPref;
//    private SharedPreferences.Editor ed  ;
//    private ValueEventListener status;
//    private ArrayList<MaqolaEntity> statiacollectionONLINE;
//    private ArrayList<String> deleteSTATIYALA;
//    private boolean onlineKey=false;
//    private boolean afterResult=false;
//    private static final String DB_NAME = "homila.db";
//    private View Myfrag;
//    public StatiyalarFragment() {
//        // Required empty public constructor
//    }
//
//    public StatiyalarFragment shareStatiyalarFragment() {
//        return this;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context =getActivity();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        final View frag=inflater.inflate(R.layout.fragment_statiyalar, container, false);
//        Myfrag=frag;
//        final ActionBar aCT=((AppCompatActivity) getActivity()).getSupportActionBar();
//        deleteSTATIYALA=new ArrayList<>();
//        if (aCT!=null)
//            aCT.setTitle(R.string.boglanis);
//
//        GENERAL.child("deleteArticles").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//            for(DataSnapshot tempDelete:dataSnapshot.getChildren()){
//                deleteSTATIYALA.add(tempDelete.getKey());
//          }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//
//            }
//        });
//
//        Log.d("testMaqola", "onCreate");
//
//
//        statiacollectionOFLINE=new ArrayList<MaqolaEntity>();
//         statiacollectionONLINE = new ArrayList<MaqolaEntity>();
//        recycler = (RecyclerView) frag.findViewById(R.id.my_recycler_view);
//        LinearLayoutManager llm = new LinearLayoutManager(context);
//        recycler.setLayoutManager(llm);
//        cardAdap=new AdapterCardStatiyalar(statiacollectionOFLINE, context,recycler,frag);
//        recycler.setAdapter(cardAdap);
//        handConnnection=new Handler();
//
//
//        GENERAL.child(".info/connected").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                boolean connected = dataSnapshot.getValue(Boolean.class);
//                if (connected&&!onlineKey) {
//                    Log.d("testMaqola", "Connect listner: Online");
//                    cardAdap.setLoaded();
//                    if (aCT != null) {
//                        aCT.setTitle(R.string.maqaa);
//                    }
//
//                    statiacollectionONLINE.clear();
//                    statiacollectionOFLINE.clear();
//                    cardAdap.setOnline();
//                    cardAdap.changelist(statiacollectionONLINE);
//                    recycler.scrollToPosition(0);
//                    readFromFirebase();
//                    afterResult = true;
//                    onlineKey = true;
//
//                } else  if(!onlineKey){
//                    Log.d("testMaqola", "Connect listner: Ofline");
//                    cardAdap.setLoaded();
//                    onlineKey = false;
//                    if (aCT != null)
//                        aCT.setTitle("Офлайн");
//
//
//                    statiacollectionOFLINE.clear();
//                    recycler.scrollToPosition(0);
//                    cardAdap.setOffline();
//                    cardAdap.changelist(statiacollectionOFLINE);
//                    ReadFromDatabase(5);
//                    afterResult = true;
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//                Log.d("testMaqola", "Connect listner: Canceled with:" +firebaseError.getMessage());
//            }
//        });
//
//
//        cardAdap.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                if(afterResult)
//                if(onlineKey){
//                    if(statiacollectionONLINE.size()!=0) {
//                        Log.d("testMaqola", "cardAdap.setOnLoadMoreListener: onlineKey  + statiacollectionONLINE.size()!=0");
//                        statiacollectionONLINE.add(null);
//                        cardAdap.notifyItemInserted(statiacollectionONLINE.size() - 1);
//                        Log.d("SQLITEB","BOTTOM FR");
//                        GENERAL.child("unchecked/maqolalar/").orderByChild("creatAt").endAt(statiacollectionONLINE.get(statiacollectionONLINE.size() - 2).getDateLastChangedLong() - 1).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                statiacollectionONLINE.remove(statiacollectionONLINE.size() - 1);
//                                cardAdap.notifyItemRemoved(statiacollectionONLINE.size());
//                                Log.d("testMaqola", "cardAdap.setOnLoadMoreListener: onlineKey  + statiacollectionONLINE.size()!=0 + result count: "+ dataSnapshot.getChildrenCount());
//                                int t = statiacollectionONLINE.size();
//                                List<DataSnapshot> once=new ArrayList();
//                                for (DataSnapshot onc : dataSnapshot.getChildren()) {
//                                    once.add(0,onc);
//                                }
//                                for (DataSnapshot oncek:once) {
//                                    MaqolaEntity statiyala = new MaqolaEntity(oncek.child("text").getValue(String.class), oncek.child("tema").getValue(String.class), oncek.child("writeByUID").getValue(String.class),  oncek.child("photoId").getValue(String.class), oncek.child("creatAt").getValue(Long.class),false,oncek.child("relations").getValue(Double.class),oncek.child("thumbnail").getValue(String.class));
//                                    statiacollectionONLINE.add(statiyala);
//                                }
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
//
//                            }
//                        },500);
//                        Log.d("testMaqola", "cardAdap.setOnLoadMoreListener: onlineKey  + statiacollectionONLINE.size()==0 Handler");
//                    }
//                }
//                else{
//                    if(statiacollectionOFLINE.size()!=0){
//                        Log.d("testMaqola", "cardAdap.setOnLoadMoreListener: ofline  + statiacollectionONLINE.size()!=0 Handler");
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
//
//                        }
//                    },500);
//                        Log.d("testMaqola", "cardAdap.setOnLoadMoreListener: ofline  + statiacollectionONLINE.size()==0 Handler");
//
//                    }
//                   }}
//        });
//
//
//
//
//        // Inflate the layout for this fragment
//        return frag;
//    }
//
//
//    private long LASTCREAT=0;
//    private int positionSQLITE=0;
//    private int positionStart=0;
//
////    public void ReadFromDatabase(int limit){
////        Cursor cursor = sdb.query(HomilaConstants.STOLB_TABLE_STATIYA, new String[]{
////                        HomilaConstants.STOLB_ID, HomilaConstants.STOLB_KEY, HomilaConstants.STOLB_THEME, HomilaConstants.STOLB_TEXT,
////                        HomilaConstants.STOLB_LIKES,
////                        HomilaConstants.STOLB_WRITEBY , HomilaConstants.STOLB_PHOTO, HomilaConstants.STOLB_CREATBY,HomilaConstants.STOLB_THUMBNAIL}, null,
////                null,
////                null,
////                null,
////                HomilaConstants.STOLB_CREATBY+" DESC"
////        );
////
////
////          int p=0;
////
////          if(statiacollectionOFLINE.size()!=0)
////              positionStart=statiacollectionOFLINE.size()-1;
////          else {positionStart=0;
////              positionSQLITE=0;}
////          cursor.move(positionSQLITE);
////        Log.d("testMaqola", "ReadFromDatabase");
////          while (cursor.moveToNext()&&p<limit) {
////
////              MaqolaEntity temp=new MaqolaEntity(cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_TEXT)), cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_THEME)), "",
////                      cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_PHOTO)),cursor.getLong(cursor.getColumnIndex(HomilaConstants.STOLB_CREATBY)),false,1,cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_THUMBNAIL)));
////              temp.setLikeCount(cursor.getLong(cursor.getColumnIndex(HomilaConstants.STOLB_LIKES)));
////              temp.setWriteBy(cursor.getString(cursor.getColumnIndex(HomilaConstants.STOLB_WRITEBY)));
////
////              statiacollectionOFLINE.add(temp);
////
////              if(LASTCREAT==0){
////                  LASTCREAT=temp.getDateLastChangedLong();
////              }
////              p++;
////              positionSQLITE++;
////          }
////
////
////          cardAdap.notifyItemRangeInserted(positionStart,positionSQLITE);
////
////          cursor.moveToFirst();
////          cursor.close();
////
////
////
////
////
////    }
//
//    int position=0;
//
//    public void readFromFirebase(){
//        Log.d("testMaqola", "ReadFromFirebase: method start");
//        GENERAL.child("unchecked/maqolalar/").orderByChild("creatAt").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<DataSnapshot> once=new ArrayList();
//                for (DataSnapshot onc : dataSnapshot.getChildren()) {
//                    once.add(0,onc);
//                }
//                for (DataSnapshot oncek:once) {
//                    MaqolaEntity statiyala = new MaqolaEntity(oncek.child("text").getValue(String.class), oncek.child("tema").getValue(String.class), oncek.child("writeByUID").getValue(String.class),  oncek.child("photoId").getValue(String.class), oncek.child("creatAt").getValue(Long.class),false,oncek.child("relations").getValue(Double.class),oncek.child("thumbnail").getValue(String.class));
//                    statiacollectionONLINE.add(statiyala);
//                }
//                if((int) dataSnapshot.getChildrenCount()==5){
//
//
//                }
//                Log.d("testMaqola", "ReadFromFirebase: result recived:"+dataSnapshot.getChildrenCount());
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
//        Log.d("testMaqola", "KeyNotExist");
//        try{
//
//            cursorik = sdb.rawQuery("SELECT "+ HomilaConstants.STOLB_KEY+" FROM "+ HomilaConstants.STOLB_TABLE_STATIYA+" WHERE "+ HomilaConstants.STOLB_KEY+"=?", new String[] {empNo + ""});
//
//            Log.d("SQLITEB",Integer.toString(cursorik.getCount()) );
//            if(cursorik.getCount() > 0) {
//
//                cursorik.moveToFirst();
//                return false;
//            }
//
//            return true;
//        }finally {
//
//            cursorik.close();
//        }
//    }
//
//    public void WriteToBASE(){
//        Log.d("testMaqola", "WriteToBASE");
//        if(statiacollectionONLINE.size()!=0){
//
//   for(int t=statiacollectionONLINE.size()-1;t>=0;t--){
//       MaqolaEntity temp=statiacollectionONLINE.get(t);
//        if(KeyNotExist(temp.getPhotoId())){
//            ContentValues cv = new ContentValues();
//
//            cv.put(HomilaConstants.STOLB_KEY, temp.getPhotoId());
//            cv.put(HomilaConstants.STOLB_CREATBY, temp.getDateLastChangedLong());
//            cv.put(HomilaConstants.STOLB_THUMBNAIL, temp.getThumbnail());
//            cv.put(HomilaConstants.STOLB_LIKES, temp.getLikeCount());
//            cv.put(HomilaConstants.STOLB_PHOTO, temp.getPhotoId());
//            cv.put(HomilaConstants.STOLB_TEXT, temp.getText());
//            cv.put(HomilaConstants.STOLB_THEME, temp.getTema());
//            cv.put(HomilaConstants.STOLB_WRITEBY, temp.getWriteBy());
//            sdb.insert(HomilaConstants.STOLB_TABLE_STATIYA, null, cv);
//        }
//       else {
//
//        }
//
//
//
//
//
//   }}
//        else     Log.d("SQLITEB","EMPTY LIST");
//
//    }
//
//    void deleteItems(){
//        Log.d("testMaqola", "deleteItems");
//        for (String tempDelete:deleteSTATIYALA)
//
//            if (sdb.delete(HomilaConstants.STOLB_TABLE_STATIYA, HomilaConstants.STOLB_KEY +" = ?", new String[] { tempDelete })>0) {
//                Log.d("SQLITEB", "Sucsess delete " + tempDelete);
//            }
//        else      Log.d("SQLITEB", "SOMETHINK WRONG delete " + tempDelete);
//
//    }
//    @Override
//    public void onResume(){
//        super.onResume();
//
//    }
//    @Override
//    public void onDetach() {
//        Log.d("testMaqola", "onDetach");
//        cardAdap.DeleteListners();
//        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mListenerMap.entrySet()) {
//            DatabaseReference ref = entry.getKey();
//            ChildEventListener listener = entry.getValue();
//            ref.removeEventListener(listener);
//        }
//        WriteToBASE();
//        deleteItems();
//
//
//         super.onDetach();
//        // now cleaning up!
//    }
//    public interface OnLoadMoreListener {
//        void onLoadMore();
//    }
//    public class AdapterCardStatiyalar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        private ArrayList<MaqolaEntity> cards;
//        private FirebaseDatabase dataset= FirebaseDatabase.getInstance();
//        private DatabaseReference GENERAL = dataset.getReference();
//        private Context context;
//        private HashMap<DatabaseReference, ValueEventListener> mListenerMap=new HashMap<>();
//        private boolean online =false;
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
//        private int windowWidth=0;
//        private int sides=0;
//        public void setOnline(){
//            Log.d("testMaqola", "Adapter: setOnline");
//            online =true;
//        }
//        public void setOffline(){
//            online =false;
//            Log.d("testMaqola", "Adapter: setOnline");
//        }
//
//        public void changelist(ArrayList<MaqolaEntity> persons){
//            Log.d("testMaqola", "Adapter: changelist");
//            this.cards=persons;
//            notifyDataSetChanged();
//        }
//        public AdapterCardStatiyalar(ArrayList<MaqolaEntity> persons, Context context, RecyclerView recyclerView, View frag){
//            Log.d("testMaqola", "Adapter: Constructor AdapterCardStatiyalar");
//
//            this.cards = persons;
//            this.context=context;
//            sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);
//            ed = sPref.edit();
//            windowWidth = sPref.getInt(HomilaConstants.SAVED_WIDTH,0);
//            sides = dpToPx(16,context);
//
//            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//            if(firebaseUser!=null){
//
//                myname=firebaseUser.getUid();
//            }
//            else {
//                myname = UUID.randomUUID().toString();
//            }
//            nInfo=new InfoMaqolaFragment();
//            this.frag=frag;
//            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//                        totalItemCount = linearLayoutManager.getItemCount();
//                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
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
//        }
//
//
//        @Override
//        public int getItemViewType(int position) {
//            return cards.get(position) != null ? VIEW_ITEM : VIEW_PROG;
//        }
//        public int dpToPx(int dp, Context context) {
//            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//            int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//            return px;
//        }
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            RecyclerView.ViewHolder vh;
//            if (viewType == VIEW_ITEM) {
//                View v = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.card_item, parent, false);
//                vh = new StatviewHolder(v);
//            } else {
//                View v = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.progres_item, parent, false);
//                vh = new ProgressViewHolder(v);
//            }
//            return vh;
//        }
//
//        private  class ProgressViewHolder extends RecyclerView.ViewHolder {
//            private ProgressBar progressBar;
//            ProgressViewHolder(View v) {
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
//        @Override
//        public void onBindViewHolder(final RecyclerView.ViewHolder holdeer, final int position) {
//            if (holdeer instanceof StatviewHolder) {
//                final StatviewHolder holder=(StatviewHolder) holdeer;
//                if (position==0)
//                    holder.ifFirst.setVisibility(View.VISIBLE);
//                final MaqolaEntity card=cards.get(position);
//                Log.d("testMaqola", "Adapter: onBindViewHolder item: "+position+"  - "+card.getPhotoId());
//
////                holder.flToGone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(((double)windowWidth)/card.getRelations())));
//                //   Log.d("Firebase",A1.getKEY());
//                holder.ivMainImage.setVisibility(View.VISIBLE);
//                if (windowWidth!=0) {
//                    holder.ivMainImage.setMaxHeight( windowWidth-sides );
//                }
//                if(card.getThumbnail()!=null) {
//                    if(!card.getThumbnail().isEmpty()) {
//                        Bitmap bitmap_thunbal = CommonOperations.StringToBitMap(card.getThumbnail());
//                        holder.ivMainImage.setImageBitmap(CommonOperations.fastblur(bitmap_thunbal, bitmap_thunbal.getWidth(), bitmap_thunbal.getHeight(), 3));
//                        holder.ivMainImage.setImageBitmap(bitmap_thunbal);
//                    }
//                }
//                holder.pbLoading.setVisibility(View.VISIBLE);
//                File extStore = Environment.getExternalStorageDirectory();
//                File myFile = new File(extStore.getAbsolutePath() + "/Homila/cache/" + card.getPhotoId()+ ".jpg");
//                if (myFile.exists()) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                    Bitmap bitmap = BitmapFactory.decodeFile(myFile.getAbsolutePath(), options);
//                    holder.ivMainImage.setImageBitmap(bitmap);
//
//                    if (windowWidth!=0) {
//                        holder.ivMainImage.setMaxHeight( windowWidth-sides );
//                    }
//
//                } else {
//                    if(online) {
//                        try {
//                            File Aa = new File(extStore.getAbsolutePath() + "/Homila/cache/");
//                            if (!Aa.exists())
//                                Aa.mkdirs();
//                            STORAGEREF.child("statiyabuck/" + card.getPhotoId()).getFile(new File(extStore.getAbsolutePath() + "/Homila/cache/" + card.getPhotoId() + ".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                    Log.d("testMaqola", "Adapter: onBindViewHolder : statiyabuck photo downloaded"+card.getPhotoId());
//                                    BitmapFactory.Options options = new BitmapFactory.Options();
//                                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                                    Bitmap bitmap = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/" + card.getPhotoId() + ".jpg").getAbsolutePath(), options);
//
//                                    holder.ivMainImage.setImageBitmap(bitmap);
//
//                                    holder.pbLoading.setVisibility(View.GONE);
//                                    if (windowWidth!=0) {
//                                        holder.ivMainImage.setMaxHeight( windowWidth-sides );
//                                    }
//
//
//                                }
//                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
//                                @Override
//                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                                    holder.pbLoading.setProgress((int)progress);
//                                }
//                            });
//
//
//                        } catch (Exception ex) {
//                            Thread.currentThread().interrupt();
//                        }
//                    }
//                }
//                File myFileForCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/" +card.getWriteByUID()+ ".jpg");
//                if (myFileForCache.exists()) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                    Bitmap bitmap = BitmapFactory.decodeFile(myFileForCache.getAbsolutePath(), options);
//                    holder.civAuthPhoto.setImageBitmap(bitmap);
//                }
//                else {
//                    if(online) {
//
//                        GENERAL.child("user-status/"+card.getWriteByUID()+"/avatar").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(final DataSnapshot dataSnapshot) {
//                                try {
//                                    Log.d("testMaqola", "Adapter: onBindViewHolder : user-status photo downloaded"+card.getWriteByUID());
//                                    File Aa=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/");
//                                    if(!Aa.exists())
//                                        Aa.mkdirs();
//                                    STORAGEREF.child("users/" + card.getWriteByUID() + "/" +dataSnapshot.getValue(String.class)).getFile( new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/"+card.getWriteByUID()+".jpg")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                                            Picasso.with(context)
//                                                    .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Homila/cache/mini/"+card.getWriteByUID()+".jpg"))
//                                                    .into(holder.civAuthPhoto);
//                                        }
//                                    });
//
//                                } catch (Exception ex) {
//                                    Thread.currentThread().interrupt();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError firebaseError) {
//                            }
//                        });
//                    }
//                }
//                if(online) {
//                    GENERAL.child("user-status/" + card.getWriteByUID() + "/nickName").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            holder.tvAuthName.setText(LinkerText.addClickablePart("@" + dataSnapshot.getValue(String.class), new LinkerText.doSomethink() {
//                                @Override
//                                public void clickedA(String textView) {
//                                    Toast.makeText(context, textView, Toast.LENGTH_SHORT).show();
//                                }
//                            }));
//                            cards.get(position).setWriteBy(dataSnapshot.getValue(String.class));
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//                else {
//                    holder.tvAuthName.setText(LinkerText.addClickablePart("@" + card.getWriteBy(), new LinkerText.doSomethink() {
//                        @Override
//                        public void clickedA(String textView) {
//                            Toast.makeText(context, textView, Toast.LENGTH_SHORT).show();
//                        }
//                    }));
//                }
//
//                holder.tvAuthName.setMovementMethod(LinkMovementMethod.getInstance());
//                holder.tvAuthName.setHighlightColor(Color.TRANSPARENT);
//                Date AAa = (new Date());
//                AAa.setTime(card.getDateLastChangedLong());
//                holder.tvCreateAt.setText((new SimpleDateFormat("dd.MM.yy HH:mm")).format(AAa));
//                holder.tvBody.setText(card.getText());
//                if(!sPref.getBoolean(card.getPhotoId(),false)){
//                    holder.ivHearts.setImageResource(R.drawable.heart);
//                }
//                else {
//                    holder.ivHearts.setImageResource(R.drawable.emptyheart);
//                }
//
//
//            GENERAL.child("/Likes/Articles/"+card.getPhotoId()+"/WhoLikeIt/"+myname).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.getValue()!=null){
//                        holder.ivHearts.setImageResource(R.drawable.heart);
//                        ed.putBoolean(card.getPhotoId(),true);
//                        ed.commit();
//                    }
//                    else {
//                        holder.ivHearts.setImageResource(R.drawable.emptyheart);
//                        ed.putBoolean(card.getPhotoId(),false);
//                        ed.commit();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//                holder.ivHearts.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!sPref.getBoolean(card.getPhotoId(),false)){
//
//                            holder.ivHearts.setImageResource(R.drawable.heart);
//                            if (online)
//                                GENERAL.child("/Likes/Articles/"+card.getPhotoId()+"/likes").runTransaction(new Transaction.Handler() {
//                                    @Override
//                                    public Transaction.Result doTransaction(MutableData currentData) {
//                                        if (currentData.getValue() == null) {
//                                            currentData.setValue(1);
//                                        } else {
//                                            currentData.setValue((Long) currentData.getValue() + 1);
//                                        }
//                                        ed.putBoolean(card.getPhotoId(),true);
//                                        ed.commit();
//                                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
//                                    }
//
//                                    @Override
//                                    public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                                        GENERAL.child("/Likes/Articles/"+card.getPhotoId()+"/WhoLikeIt/"+myname).setValue(ServerValue.TIMESTAMP);
//                                    }
//                                });
//                        }
//                        else{
//                            holder.ivHearts.setImageResource(R.drawable.emptyheart);
//                            if (online)
//                                GENERAL.child("/Likes/Articles/"+card.getPhotoId()+"/likes").runTransaction(new Transaction.Handler() {
//                                    @Override
//                                    public Transaction.Result doTransaction(MutableData currentData) {
//                                        if (currentData.getValue() == null) {
//                                            currentData.setValue(1);
//                                        } else {
//                                            currentData.setValue((Long) currentData.getValue() - 1);
//                                        }
//                                        ed.putBoolean(card.getPhotoId(),false);
//                                        ed.commit();
//                                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
//                                    }
//                                    @Override
//                                    public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                                        Log.d("Firebase",Boolean.toString(sPref.getBoolean(Long.toString(card.getDateLastChangedLong()),true)));
//                                        GENERAL.child("/Likes/Articles/"+card.getPhotoId()+"/WhoLikeIt/"+myname).removeValue();
//                                    }
//                                });
//                        }
//                    }
//                });
//                holder.tvTheme.setText(card.getTema());
//                if(online)
//                    GENERAL.child("/Likes/Articles/"+card.getPhotoId()+"/likes").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            mListenerMap.put(dataSnapshot.getRef(),this);
//                            holder.tvHeartsName.setText(Long.toString((dataSnapshot.getValue(Long.class)==null)?0l:dataSnapshot.getValue(Long.class)));
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError firebaseError) {
//
//                        }
//                    });
//                holder.llToInfo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        android.support.v4.app.FragmentTransaction fragmentTransaction_jinstest = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction_jinstest.add(R.id.frame,(new InfoMaqolaFragment()).sharenformat(card,frag) , "nInfOO");
//                        fragmentTransaction_jinstest.commit();
//                    }
//                });
//
//            } else {
//                ((ProgressViewHolder) holdeer).progressBar.setIndeterminate(true);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return cards.size();
//        }
//
//        private class StatviewHolder extends RecyclerView.ViewHolder {
//            private ImageView ivMainImage,ivHearts;
//            private TextView tvTheme, tvBody, tvAuthName, tvCreateAt, tvHeartsName;
//            private LinearLayout llToInfo;
//            private FrameLayout ifFirst;
//            private CircleImageView civAuthPhoto;
//            private ProgressBar pbLoading;
//            private FrameLayout flToGone;
//            StatviewHolder(View itemView){
//                super(itemView);
//                ifFirst=(FrameLayout) itemView.findViewById(R.id.ifFirst);
//                ivMainImage=(ImageView) itemView.findViewById(R.id.photostat);
//                ivHearts=(ImageView) itemView.findViewById(R.id.emptyheart);
//                tvTheme =(TextView) itemView.findViewById(R.id.themest);
//                tvBody =(TextView) itemView.findViewById(R.id.bodyst);
//                tvAuthName =(TextView) itemView.findViewById(R.id.authname);
//                tvCreateAt =(TextView) itemView.findViewById(R.id.creatat);
//                tvHeartsName =(TextView) itemView.findViewById(R.id.hearts);
//                civAuthPhoto =(CircleImageView) itemView.findViewById(R.id.authphoto);
//                llToInfo =(LinearLayout) itemView.findViewById(R.id.toInfo);
//                pbLoading =(ProgressBar) itemView.findViewById(R.id.loading);
//                flToGone=(FrameLayout) itemView.findViewById(R.id.togone);
//            }
//
//        }
//        public void DeleteListners(){
//            for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenerMap.entrySet()) {
//                DatabaseReference ref = entry.getKey();
//                ValueEventListener listener = entry.getValue();
//                ref.removeEventListener(listener);
//            }
//        }
//    }
//
//
//}
