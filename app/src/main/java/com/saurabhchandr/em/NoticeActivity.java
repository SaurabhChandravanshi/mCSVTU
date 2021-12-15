package com.saurabhchandr.em;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.github.clemp6r.futuroid.FutureCallback;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.saurabhchandr.em.Model.NoticeData;
import com.saurabhchandr.em.ui.main.PlaceholderFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class NoticeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Object> dataList = new ArrayList<>();
    private ProgressBar pBar;
    private NativeAd mNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        init();

        GetDataFromHtml getDataFromHtml = new GetDataFromHtml();
        getDataFromHtml.getData("https://csvtu.ac.in/ew/notices/").addCallback(new FutureCallback<List<Object>>() {
            @Override
            public void onSuccess(List<Object> result) {
                if(result != null) {
                    addAdViews();
                    loadRecyclerView();
                    pBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(NoticeActivity.this, R.string.def_err_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                pBar.setVisibility(View.GONE);
                Toast.makeText(NoticeActivity.this, R.string.def_err_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void addAdViews() {
        for (int i=4;i<dataList.size();i+=4) {
            dataList.add(i,new AdView(NoticeActivity.this));
        }
    }

    private void init() {
        recyclerView = findViewById(R.id.notice_recycler);
        adapter = new Adapter(dataList);
        layoutManager = new LinearLayoutManager(this);
        pBar = findViewById(R.id.notice_pBar);
    }

     class GetDataFromHtml {
        public Future<List<Object>> getData(String url) {
            return Async.submit(new Callable<List<Object>>() {
                @Override
                public List<Object> call() throws Exception {
                    Document document = Jsoup.connect(url).get();
                    for (Element element : document.select("div[class=news-content]")) {
                        Element dateDiv = element.select("div[class=date-post]").first();
                        String day = dateDiv.select("h2").text();
                        String monthYear = dateDiv.select("p").text();
                        String date = day + " " + monthYear;
                        Element contentDiv = element.select("div[class=post-content-text]").first();
                        String title = contentDiv.select("h3").text();
                        String url = contentDiv.select("a").attr("href");
                        NoticeData data = new NoticeData(title,date,url);
                        dataList.add(data);
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
                return VIEW_TYPE_CONTENT;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if(viewType == VIEW_TYPE_AD) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_banner_frame,parent,false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list, parent, false);
            }
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
           if (dataList.get(position) instanceof NoticeData && getItemViewType(position) == VIEW_TYPE_CONTENT) {
               NoticeData data = (NoticeData) dataList.get(position);
               holder.title.setText(data.getTitle());
               holder.date.setText(data.getDate());

               Typeface typeface = ResourcesCompat.getFont(holder.itemView.getContext(),R.font.metropolis_regular);
               holder.title.setTypeface(typeface,Typeface.BOLD);

               holder.layout.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       openLinkExt(holder.itemView.getContext(),data.getUrl());
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
            private TextView title,date;
            private LinearLayout layout;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.notice_list_title);
                date = itemView.findViewById(R.id.notice_list_date);
                layout = itemView.findViewById(R.id.notice_list_container);
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
                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
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
}