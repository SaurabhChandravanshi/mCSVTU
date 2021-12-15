package com.saurabhchandr.em.ui.main;

import android.content.ActivityNotFoundException;
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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.saurabhchandr.em.Model.ResultListData;
import com.saurabhchandr.em.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private final List<Object> dataList = new ArrayList<>();
    private ProgressBar pBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;

    public static PlaceholderFragment newInstance(int position,String url) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putInt("position",position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null) {
            getDataFromHtmlSource(getArguments());
        }
    }

    private void getDataFromHtmlSource(Bundle arguments) {
        GetResultDataService service = new GetResultDataService();
        service.getResultData(arguments.getString("url"))
                .addCallback(new FutureCallback<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> result) {
                        if(result != null)
                            loadRecyclerView();
                        pBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        pBar.setVisibility(View.GONE);
                    }
                });
    }

    private void loadRecyclerView() {
        addAddViews();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void addAddViews() {
        for (int i=5;i<dataList.size();i+=5) {
            if(getContext() != null)
                dataList.add(i,new AdView(getContext()));
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_result ,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pBar = view.findViewById(R.id.fragment_result_pBar);
        recyclerView = view.findViewById(R.id.fragment_result_recycler);
        adapter = new Adapter(dataList);
        layoutManager = new LinearLayoutManager(view.getContext());
    }

    public class GetResultDataService {

        public Future<List<Object>> getResultData(final String url) {
            return Async.submit(() -> {

                Document document;
                try {
                    document = Jsoup.connect(url).get();
                    int index = Integer.parseInt(url.substring(url.lastIndexOf("=")+1));
                    Element table = document.select("table").get(index);
                      for (Element row : table.select("tbody").select("tr")) {

                            Elements tds = row.select("td");
                            String title = tds.get(0).select("a").text();
                            String category = tds.get(1).text();
                            String updateDate = tds.get(2).text();
                            String link =tds.get(3).select("a").attr("data-downloadurl");
                            ResultListData data = new ResultListData(title,category,updateDate,link);
                            dataList.add(data);
                        }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return dataList;
            });
        }
    }

    static class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private final List<Object> dataList;
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
                if (position % 5 == 0)
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list, parent, false);
            }
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if(getItemViewType(position) == VIEW_TYPE_CONTENT && dataList.get(position) instanceof ResultListData) {
                ResultListData data = (ResultListData) dataList.get(position);
                holder.title.setText(data.getTitle());
                holder.category.setText(data.getCategory());
                holder.date.setText(data.getUpdateDate());

                Typeface typeface = ResourcesCompat.getFont(holder.itemView.getContext(),R.font.sans_regular);
                holder.date.setTypeface(typeface,Typeface.BOLD);
                holder.title.setTypeface(typeface);
                holder.category.setTypeface(typeface);

                holder.title.setCompoundDrawablePadding(15);
                holder.category.setCompoundDrawablePadding(15);

                holder.category.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_category_24,0,0,0);
                holder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_error_outline_24,0,0,0);

                holder.cardView.setOnClickListener(v -> {
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
                        customTabsIntent.launchUrl(holder.itemView.getContext(), Uri.parse(data.getLink()));
                    } catch (ActivityNotFoundException e) {
                        customTabsIntent.launchUrl(holder.itemView.getContext(), Uri.parse(data.getLink()));
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
        static class MyViewHolder extends RecyclerView.ViewHolder {
            private final CardView cardView;
            private final TextView title;
            private final TextView date;
            private final TextView category;
            private NativeAd mNativeAd;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.result_list_title);
                date = itemView.findViewById(R.id.result_list_release_date);
                category = itemView.findViewById(R.id.result_list_category);
                cardView = itemView.findViewById(R.id.result_list_card);
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
}