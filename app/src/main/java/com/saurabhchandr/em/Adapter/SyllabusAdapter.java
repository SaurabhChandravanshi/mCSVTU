package com.saurabhchandr.em.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saurabhchandr.em.Fragment.DownloadFragment;
import com.saurabhchandr.em.Model.Branch;
import com.saurabhchandr.em.Model.Semester;
import com.saurabhchandr.em.PDFActivity;
import com.saurabhchandr.em.R;

import java.io.File;
import java.util.List;

public class SyllabusAdapter extends RecyclerView.Adapter<SyllabusAdapter.MyViewHolder> {
    private final List<Branch> dataList;
    private final FragmentManager fManager;

    public SyllabusAdapter(List<Branch> dataList, FragmentManager fManager) {
        this.dataList = dataList;
        this.fManager = fManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_double_recycler_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Branch data = dataList.get(position);

        // Populate views with data.
        holder.title.setText(data.getName());
        holder.title.setCompoundDrawablePadding(15);
        holder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);

        SubAdapter subAdapter = new SubAdapter(data.getSemesters());
        holder.recyclerView.setAdapter(subAdapter);

        final boolean[] isExpanded = {false};
        holder.title.setOnClickListener(v -> {
            if (isExpanded[0]) {
                isExpanded[0] = false;
                holder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);
                holder.recyclerView.setVisibility(View.GONE);
            } else {
                isExpanded[0] = true;
                holder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_less_24,0);
                holder.recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.simple_double_recycler_list_title);
            recyclerView = itemView.findViewById(R.id.simple_double_recycler);

            RecyclerView.LayoutManager manager = new LinearLayoutManager(itemView.getContext());
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new DividerItemDecoration(itemView.getContext(),DividerItemDecoration.VERTICAL));
        }
    }

    class SubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<Semester> semesterList;


        public SubAdapter(List<Semester> semesterList) {
            this.semesterList = semesterList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_list,parent,false);
            return new SubViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SubViewHolder viewHolder = (SubViewHolder)holder;
            Semester data = semesterList.get(position);
            viewHolder.subTitle.setText(data.getSemName());

            viewHolder.subTitle.setOnClickListener(v -> {
                if(data.getSyllabus() == null) {
                    Toast.makeText(viewHolder.itemView.getContext(), "Could not find any file, Please report if issue persist after multiple tries.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String fileName = data.getSyllabus().substring(data.getSyllabus().lastIndexOf("/")+1);
                File file = new File(viewHolder.itemView.getContext().getFilesDir(),fileName);
                if(file.exists()) {
                    Intent pdfIntent  = new Intent(viewHolder.itemView.getContext(), PDFActivity.class);
                    pdfIntent.putExtra("filePath",fileName);
                    viewHolder.itemView.getContext().startActivity(pdfIntent);

                } else {
                    DownloadFragment fragment = new DownloadFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("url",data.getSyllabus());
                    bundle.putString("contentType","syllabus");
                    fragment.setArguments(bundle);
                    fragment.setCancelable(false);
                    fragment.show(fManager,fragment.getTag());
                }
            });
        }

        @Override
        public int getItemCount() {
            return semesterList.size();
        }

      class SubViewHolder extends RecyclerView.ViewHolder {
            private final TextView subTitle;
            public SubViewHolder(@NonNull View itemView) {
                super(itemView);
                subTitle = itemView.findViewById(R.id.sub_list_title);
            }
        }
    }
}
