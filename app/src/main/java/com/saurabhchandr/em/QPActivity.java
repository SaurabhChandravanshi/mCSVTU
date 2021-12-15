package com.saurabhchandr.em;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saurabhchandr.em.Adapter.QPAdapter;
import com.saurabhchandr.em.Model.Branch;
import com.saurabhchandr.em.Model.BranchList;
import com.saurabhchandr.em.Model.Semester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QPActivity extends AppCompatActivity {

    private static String COURSE;
    private List<Branch> dataList = new ArrayList<>();
    private RecyclerView recyclrerView;
    private RecyclerView.LayoutManager layoutManager;
    private QPAdapter adapter;
    private ProgressBar pBar;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = QPActivity.class.getSimpleName();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qp);
        loadInterstitialAds();

        COURSE = getIntent().getStringExtra("course");
        if(COURSE == null)
            finish();

        init();
        getDataFromServer();
    }
    private void init() {
        recyclerView = findViewById(R.id.qp_recycler);
        adapter = new QPAdapter(dataList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        pBar = findViewById(R.id.qp_pBar);
    }

    private void getDataFromServer() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("courses").document(COURSE).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        BranchList list = documentSnapshot.toObject(BranchList.class);
                        if(list != null && list.getBranches() != null && list.getBranches().size() > 0) {
                            for (Branch branch:list.getBranches()) {
                                for (int i=0;i<branch.getSemesters().size();i++) {
                                    Iterator<Semester> iterator = branch.getSemesters().iterator();
                                    while (iterator.hasNext()) {
                                        Semester semester = iterator.next();
                                        if(semester.getPaper() == null)
                                            iterator.remove();
                                    }
                                }
                                if(branch.getSemesters().size() > 0)
                                    dataList.add(branch);
                            }
                            loadRecyclerView();
                        }
                        else {
                            Toast.makeText(QPActivity.this, "Sorry for inconvenience but currently we can't operate, Please report if issue persist after multiple tries.", Toast.LENGTH_LONG).show();
                        }
                        pBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QPActivity.this, "Sorry for inconvenience but currently we can't operate, Please report if issue persist after multiple tries.", Toast.LENGTH_LONG).show();
                        pBar.setVisibility(View.GONE);
                    }
                });
    }

    private void loadRecyclerView() {
        if(dataList.size() >0) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void onBackPressed() {
        if(mInterstitialAd != null) {
            mInterstitialAd.show(QPActivity.this);
        }
        super.onBackPressed();
    }

    private void loadInterstitialAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getResources().getString(R.string.interstitial_ad_unit), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
}