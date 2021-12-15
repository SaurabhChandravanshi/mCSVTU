package com.saurabhchandr.em;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.github.clemp6r.futuroid.FutureCallback;
import com.google.android.gms.ads.AdView;
import com.saurabhchandr.em.Adapter.PaperAdapter;
import com.saurabhchandr.em.Model.Paper;
import com.saurabhchandr.em.Model.ResultListData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PaperActivity extends AppCompatActivity {

    private List<Object> dataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PaperAdapter adapter;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);


        init();
        GetDataService service = new GetDataService();
        service.getPaperData(getIntent().getStringExtra("url"))
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

    private void init() {
        recyclerView = findViewById(R.id.paper_recycler);
        adapter = new PaperAdapter(dataList,getSupportFragmentManager());
        layoutManager = new LinearLayoutManager(this);
        pBar = findViewById(R.id.papper_pBar);
    }
    private void loadRecyclerView() {

        addAdViews();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void addAdViews() {
        for(int i=8;i<dataList.size();i+=8) {
            dataList.add(i,new AdView(this));
        }
    }


    public class GetDataService {

        public Future<List<Object>> getPaperData(final String url) {
            return Async.submit(new Callable<List<Object>>() {
                @Override
                public List<Object> call() {

                    Document document = null;
                    try {
                        document = Jsoup.connect(url).get();
                        int index = Integer.parseInt(url.substring(url.indexOf("=")+1));
                        Element div = document.select("div[class=card]").get(index);
                        for (org.jsoup.nodes.Element row : div.select("a")) {

                            String title = row.text();
                            String url = row.attr("href");
                            String fileName = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."))+".pdf";
                            File file = new File(getApplicationContext().getFilesDir(),fileName);
                            Paper paper = new Paper(title,url,getIntent().getStringExtra("semName"),false);
                            if(file.exists())
                                paper = new Paper(title,url,getIntent().getStringExtra("semName"),true);
                            dataList.add(paper);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return dataList;
                }
            });
        }
    }
}