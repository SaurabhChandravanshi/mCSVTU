package com.saurabhchandr.em.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saurabhchandr.em.Model.Branch;
import com.saurabhchandr.em.Model.Semester;
import com.saurabhchandr.em.PaperActivity;
import com.saurabhchandr.em.R;

import java.util.List;

public class QPAdapter  extends RecyclerView.Adapter<QPAdapter.MyViewHolder> {
    private List<Branch> dataList;

    public QPAdapter(List<Branch> dataList) {
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_double_recycler_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder)holder;
        Branch data = dataList.get(position);

        // Populate views with data.
        viewHolder.title.setText(data.getName());
        viewHolder.title.setCompoundDrawablePadding(15);
        viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);

        SubAdapter subAdapter = new SubAdapter(data.getSemesters());
        viewHolder.recyclerView.setAdapter(subAdapter);
        final boolean[] isExpanded = {false};
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded[0]) {
                    isExpanded[0] = false;
                    viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_more_24,0);
                    viewHolder.recyclerView.setVisibility(View.GONE);
                } else {
                    isExpanded[0] = true;
                    viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_expand_less_24,0);
                    viewHolder.recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final RecyclerView recyclerView;
        private final LinearLayout container;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.simple_double_recycler_list_title);
            recyclerView = itemView.findViewById(R.id.simple_double_recycler);
            container = itemView.findViewById(R.id.simple_double_recycler_list);

            RecyclerView.LayoutManager manager = new LinearLayoutManager(itemView.getContext());
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new DividerItemDecoration(itemView.getContext(),DividerItemDecoration.VERTICAL));
        }
    }

    static class SubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Semester> semesterList;

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

            viewHolder.subTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(viewHolder.itemView.getContext(), PaperActivity.class);
                    intent.putExtra("url",data.getPaper());
                    intent.putExtra("semName",data.getSemName());
                    viewHolder.itemView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return semesterList.size();
        }

        static class SubViewHolder extends RecyclerView.ViewHolder {
            private TextView subTitle;
            public SubViewHolder(@NonNull View itemView) {
                super(itemView);
                subTitle = itemView.findViewById(R.id.sub_list_title);
            }
        }
    }
}
