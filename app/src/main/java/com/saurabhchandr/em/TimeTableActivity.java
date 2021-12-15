package com.saurabhchandr.em;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.github.clemp6r.futuroid.FutureCallback;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saurabhchandr.em.Adapter.PaperAdapter;
import com.saurabhchandr.em.Model.NoticeData;
import com.saurabhchandr.em.Model.TimeTableData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TimeTableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Object> dataList = new ArrayList<>();
    private ProgressBar pBar;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = TimeTableActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt);
        loadInterstitialAds();
        init();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("timeTable").document(getIntent().getStringExtra("course")).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String url = documentSnapshot.getString("url");
                        GetDataFromHtml getDataFromHtml = new GetDataFromHtml();
                        getDataFromHtml.getData(url).addCallback(new FutureCallback<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> result) {
                                if(result != null) {
                                    loadRecyclerView();
                                    pBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(TimeTableActivity.this, R.string.def_err_message, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                pBar.setVisibility(View.GONE);
                                Toast.makeText(TimeTableActivity.this, R.string.def_err_message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TimeTableActivity.this, R.string.def_err_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadRecyclerView() {
        addAdViews();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
    private void addAdViews() {
        for(int i=4;i<dataList.size();i+=4) {
            dataList.add(i,new AdView(this));
        }
    }


    private void init() {
        recyclerView = findViewById(R.id.tt_recycler);
        adapter = new Adapter(dataList);
        layoutManager = new LinearLayoutManager(this);
        pBar = findViewById(R.id.tt_pBar);
    }

    class GetDataFromHtml {
        public Future<List<Object>> getData(String url) {
            return Async.submit(new Callable<List<Object>>() {
                @Override
                public List<Object> call() throws Exception {
                    Document document = Jsoup.connect(url).get();
                    String divId = url.substring(url.lastIndexOf("#")+1);
                    Element mainDiv = document.select("div[id="+divId+"]").first();
                    Element table = mainDiv.select("table").select("tbody").first();
                    for (Element tr:table.select("tr")) {
                        if (tr.childNodeSize() > 0) {
                            Elements tds = tr.select("td");
                            String title = tds.get(0).text();
                            String path = tds.get(1).select("a").attr("href").replace("../", "/");
                            String fullUrl = "https://csvtu.ac.in/ew" + path;
                            TimeTableData data = new TimeTableData(title, fullUrl);
                            dataList.add(data);
                        }
                    }

                    return dataList;
                }
            });
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<Object> dataList;
        private static final int VIEW_TYPE_AD = 0;
        private static final int VIEW_TYPE_CONTENT = 1;

        public Adapter(List<Object> dataList) {
            this.dataList = dataList;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0)
                return VIEW_TYPE_CONTENT;
            else
            if (position % 4 == 0)
                return VIEW_TYPE_AD;
            else
                return VIEW_TYPE_CONTENT;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if(viewType == VIEW_TYPE_AD) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_banner_frame,parent,false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tt_list, parent, false);
            }
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if(getItemViewType(position) == VIEW_TYPE_CONTENT && dataList.get(position) instanceof TimeTableData) {
                TimeTableData data = (TimeTableData) dataList.get(position);
                holder.title.setText(data.getTitle());

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLinkExt(holder.itemView.getContext(),data.getUrl());
                    }
                });

                PopupMenu.OnMenuItemClickListener menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_tt_share:
                                shareItem();
                                break;
                            case R.id.menu_tt_copy:
                                copyLink("Link",data.getUrl());
                        }
                        return true;
                    }

                    private void copyLink(String label,String text) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(label, text);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(TimeTableActivity.this, "Link copied to clipboard.", Toast.LENGTH_SHORT).show();
                    }

                    private void shareItem() {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,data.getTitle());
                        intent.putExtra(Intent.EXTRA_TEXT,"Time Table: "+data.getTitle()+"\nDirect link: "+data.getUrl()+
                                "\n"+"Get mCSVTU\nhttps://mcsvtu.page.link/app");
                        startActivity(Intent.createChooser(intent,"Share with"));
                    }
                };
                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(holder.itemView.getContext(), v);
                        popup.setOnMenuItemClickListener(menuItemClickListener);
                        popup.inflate(R.menu.menu_tt);
                        popup.show();
                    }
                });
            } else if(dataList.get(position) instanceof AdView) {
                holder.refreshAd(holder.itemView);
            }

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView title, subTitle;
            private LinearLayout layout;
            private ImageView menu;
            private NativeAd mNativeAd;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.tt_list_title);
                subTitle = itemView.findViewById(R.id.tt_list_subTitle);
                layout = itemView.findViewById(R.id.tt_list_container);
                menu = itemView.findViewById(R.id.tt_list_menu);
            }
            private void refreshAd(View view) {
                AdLoader.Builder builder = new AdLoader.Builder(view.getContext(), view.getContext().getString(R.string.native_ad_unit));
                builder.forNativeAd(nativeAd -> {
                    if (mNativeAd != null) {
                        mNativeAd.destroy();
                    }
                    mNativeAd = nativeAd;
                    FrameLayout frameLayout = view.findViewById(R.id.recycler_banner_frame);
                    NativeAdView adView = (NativeAdView) LayoutInflater.from(view.getContext()).inflate(R.layout.native_ad, null);
                    populateNativeAdView(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                });
                NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
                builder.withNativeAdOptions(adOptions);
                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        CardView cardView = view.findViewById(R.id.recycler_banner_card);
                        cardView.setVisibility(View.VISIBLE);
                    }
                }).build();
                adLoader.loadAd(new AdRequest.Builder().build());
            }
            private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
                adView.setMediaView(adView.findViewById(R.id.ad_media));
                adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
                adView.setBodyView(adView.findViewById(R.id.ad_body));
                adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
                adView.setIconView(adView.findViewById(R.id.ad_app_icon));
                adView.setPriceView(adView.findViewById(R.id.ad_price));
                adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
                adView.setStoreView(adView.findViewById(R.id.ad_store));
                adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
                ((TextView)adView.getHeadlineView()).setText(nativeAd.getHeadline());
                adView.setMediaView(adView.findViewById(R.id.ad_media));
                if (nativeAd.getBody() == null) {
                    adView.getBodyView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                }
                if (nativeAd.getCallToAction() == null) {
                    adView.getCallToActionView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getCallToActionView().setVisibility(View.VISIBLE);
                    ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }
                if (nativeAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }
                if (nativeAd.getPrice() == null) {
                    adView.getPriceView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
                }
                if (nativeAd.getStore() == null) {
                    adView.getStoreView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
                }
                if (nativeAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.INVISIBLE);
                } else {

                    ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }
                if (nativeAd.getAdvertiser() == null) {
                    adView.getAdvertiserView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                    adView.getAdvertiserView().setVisibility(View.VISIBLE);
                }
                adView.setNativeAd(nativeAd);
            }
        }
    }

    private void openLinkExt(Context context,String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        int colorInt = Color.parseColor("#188AE3"); // brand blue
        CustomTabColorSchemeParams defaultColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(colorInt)
                .setNavigationBarColor(colorInt)
                .build();
        builder.setDefaultColorSchemeParams(defaultColors);
        try {
            customTabsIntent.intent.setPackage("com.android.chrome");
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (ActivityNotFoundException e) {
            customTabsIntent.launchUrl(context, Uri.parse(url));
        }
    }

    @Override
    public void onBackPressed() {
        if(mInterstitialAd != null) {
            mInterstitialAd.show(TimeTableActivity.this);
        }
        super.onBackPressed();
    }

    private void loadInterstitialAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
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