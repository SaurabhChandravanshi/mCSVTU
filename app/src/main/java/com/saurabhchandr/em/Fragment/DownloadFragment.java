package com.saurabhchandr.em.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.github.clemp6r.futuroid.FutureCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.saurabhchandr.em.PDFActivity;
import com.saurabhchandr.em.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import static androidx.core.content.FileProvider.getUriForFile;

public class DownloadFragment extends  BottomSheetDialogFragment {
    private static String FILE_URL;
    private static String CONTENT_TYPE;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            FILE_URL = getArguments().getString("url");
            CONTENT_TYPE = getArguments().getString("contentType");

            if(FILE_URL.toLowerCase().endsWith(".pdf") || CONTENT_TYPE.equals("syllabus") || CONTENT_TYPE.equals("scheme")) {
                downloadFile(FILE_URL);
            } else {
                getDownloadLink(FILE_URL);
            }
        }
    }

    private void downloadFile(String pdfUrl) {
        String fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/")+1);
        File outFile = new File(getContext().getFilesDir(),fileName);
        DownloadService service = new DownloadService();
        service.download(pdfUrl,outFile)
                .addCallback(new FutureCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        if(result != null) {
                            Intent pdfIntent  = new Intent(getContext(), PDFActivity.class);
                            pdfIntent.putExtra("filePath",fileName);
                            getContext().startActivity(pdfIntent);
                        }
                        else
                            Toast.makeText(getContext(), "This file can't be downloaded, Please report if issue persist after multiple tries.", Toast.LENGTH_SHORT).show();
                            dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getContext(), "This file can't be downloaded, Please report if issue persist after multiple tries. ", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }
    public static class DownloadService {
        public Future<File> download(final String thisUrl, File outputFile) {
            return Async.submit(new Callable<File>() {
                @Override
                public File call() {
                    int count;
                    try {
                        URL url = new URL(thisUrl);
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        // this will be useful so that you can show a tipical 0-100%
                        // progress bar
                        int lenghtOfFile = connection.getContentLength();

                        // download the file
                        InputStream input = new BufferedInputStream(url.openStream(),
                                8192);

                        // Output stream
                        OutputStream output = new FileOutputStream(outputFile);

                        byte[] data = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            output.write(data, 0, count);
                        }

                        // flushing output
                        output.flush();

                        // closing streams
                        output.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    return outputFile;
                }
            });
        }
    }
    private void getDownloadLink(String url) {
        GetDownloadLink link = new GetDownloadLink();
        link.getLink(url).addCallback(new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(result != null && result.toLowerCase().endsWith(".pdf")) {
                    downloadFile(result);
                } else {
                    Toast.makeText(getContext(), "This file can't be downloaded, Please report if issue persist after multiple tries.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "This file can't be downloaded, Please report if issue persist after multiple tries.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
    public static class GetDownloadLink {
        public Future<String> getLink(String pageUrl) {
            return Async.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String pdfUrl  = null;
                    try {
                        Document document = Jsoup.connect(pageUrl).get();
                        Element div = document.select("div[class=card]").get(0);
                        pdfUrl = "https://www.csvtuonline.com/papers/"+div.select("a").attr("href");
                        return pdfUrl;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return pdfUrl;
                }
            });
        }
    }
}
