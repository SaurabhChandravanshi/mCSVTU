package com.saurabhchandr.em.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.saurabhchandr.em.Fragment.DownloadFragment;
import com.saurabhchandr.em.Model.Paper;
import com.saurabhchandr.em.PDFActivity;
import com.saurabhchandr.em.R;
import com.saurabhchandr.em.ui.main.PlaceholderFragment;

import java.io.File;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.MyViewHolder> {
    private final List<Object> dataList;
    private final FragmentManager fManager;
    private static final int VIEW_TYPE_AD = 0;
    private static final int VIEW_TYPE_CONTENT = 1;

    public PaperAdapter(List<Object> dataList, FragmentManager fManager) {
        this.dataList = dataList;
        this.fManager = fManager;
    }
    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return VIEW_TYPE_CONTENT;
        else
        if (position % 8 == 0)
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paper_list, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(getItemViewType(position) == VIEW_TYPE_CONTENT && dataList.get(position) instanceof Paper) {
            Paper data = (Paper) dataList.get(position);
            holder.title.setText(data.getTitle());
            holder.subTitle.setText(data.getSemName());

            String fileName = data.getUrl().substring(data.getUrl().lastIndexOf("/")+1,data.getUrl().lastIndexOf("."))+".pdf";
            File file = new File(holder.itemView.getContext().getFilesDir(),fileName);
            if(data.isDownloaded()) {
                holder.subTitle.setText("Downloaded");
                PopupMenu.OnMenuItemClickListener menuItemClickListener  =  new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_paper_share:
                                shareFile(holder.itemView.getContext(),file);
                                break;
                            case R.id.menu_paper_remove:
                                removeFile();
                        }
                        return true;
                    }

                    private void removeFile() {
                        if(file.delete()) {
                            Toast.makeText(holder.itemView.getContext(), "Space freed up.", Toast.LENGTH_SHORT).show();
                            ((Paper) dataList.get(position)).setDownloaded(false);
                            notifyDataSetChanged();
                        } else
                            Toast.makeText(holder.itemView.getContext(), "Failed to free up space.", Toast.LENGTH_SHORT).show();
                    }
                };

                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(holder.itemView.getContext(), v);
                        popup.setOnMenuItemClickListener(menuItemClickListener);
                        popup.inflate(R.menu.menu_paper_adapter);
                        popup.show();
                    }
                });
            }

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.getUrl() == null) {
                        Toast.makeText(holder.itemView.getContext(), "Could not find any file, Please report if issue persist after multiple tries.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(file.exists()) {
                        Intent pdfIntent  = new Intent(holder.itemView.getContext(), PDFActivity.class);
                        pdfIntent.putExtra("filePath",fileName);
                        holder.itemView.getContext().startActivity(pdfIntent);
                    } else {
                        DownloadFragment fragment = new DownloadFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("url",data.getUrl());
                        bundle.putString("contentType","paper");
                        fragment.setArguments(bundle);
                        fragment.setCancelable(false);
                        fragment.show(fManager,fragment.getTag());
                    }
                }
            });
        } else if(dataList.get(position) instanceof AdView) {
            holder.refreshAd(holder.itemView);
        }
    }

    private void shareFile(Context context,File file) {
        Uri contentUri = getUriForFile(context, context.getPackageName(), file);
        Intent shareContentIntent = new Intent(Intent.ACTION_SEND,contentUri);
        shareContentIntent.setDataAndType(contentUri,"application/pdf");
        shareContentIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
        shareContentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareContentIntent,"Share with"));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title,subTitle;
        private LinearLayout container;
        private ImageView menu;
        private NativeAd mNativeAd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.paper_list_title);
            subTitle = itemView.findViewById(R.id.paper_list_sub_title);
            container = itemView.findViewById(R.id.paper_list_container);
            menu = itemView.findViewById(R.id.paper_list_menu);
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
