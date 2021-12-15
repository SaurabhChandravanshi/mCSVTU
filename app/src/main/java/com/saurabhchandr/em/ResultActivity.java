package com.saurabhchandr.em;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saurabhchandr.em.Model.ResultData;
import com.saurabhchandr.em.ui.main.SectionsPagerAdapter;

public class ResultActivity extends AppCompatActivity {

    private static final int[] TAB_TITLES = new int[]{R.string.regular,R.string.re_totaling,R.string.re_val,R.string.re_re_val};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView actionTitle = findViewById(R.id.result_title);
        actionTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_back_24,0,0,0);
        actionTitle.setCompoundDrawablePadding(60);
        actionTitle.setOnClickListener(v -> finish());

        getDataFromServer();

    }

    private void getDataFromServer() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("result").document(getIntent().getStringExtra("course")).get()
                .addOnSuccessListener(documentSnapshot -> {
                    ResultData data = documentSnapshot.toObject(ResultData.class);
                    if(data != null)
                        loadTabs(data);
                    else
                        Toast.makeText(ResultActivity.this, R.string.def_err_message, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> Toast.makeText(ResultActivity.this, R.string.def_err_message, Toast.LENGTH_LONG).show());
    }

    private void loadTabs(ResultData data) {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),getLifecycle(),data);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        viewPager.setAdapter(sectionsPagerAdapter);
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(getResources().getString(TAB_TITLES[position]))
        ).attach();
    }
}