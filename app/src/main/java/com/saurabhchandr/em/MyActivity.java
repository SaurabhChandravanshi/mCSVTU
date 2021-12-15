package com.saurabhchandr.em;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.saurabhchandr.em.Fragment.ContentMyFragment;

import im.crisp.client.ChatActivity;

public class MyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String COURSE;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DrawerLayout drawer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        if(mAuth.getCurrentUser() == null || mAuth.getCurrentUser().isAnonymous()) {
            Intent intent = new Intent(MyActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(MyActivity.this);
        COURSE = preferences.getString("course",null);
        if(COURSE == null) {
            startActivity(new Intent(MyActivity.this,MainActivity.class));
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadHomePage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        COURSE = preferences.getString("course",null);
        if(COURSE == null) {
            startActivity(new Intent(MyActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


         if (id == R.id.my_nav_report_problem)
            startActivity(new Intent(MyActivity.this,ReportActivity.class));
        else if (id == R.id.my_nav_live_chat)
            startActivity(new Intent(MyActivity.this, ChatActivity.class));
        else if (id == R.id.my_nav_home) {
            startActivity(new Intent(MyActivity.this, MainActivity.class));
            finish();
        }
         else if (id == R.id.my_nav_upload_content)
             startActivity(new Intent(MyActivity.this, UploadActivity.class));
        else if(id == R.id.my_nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(MyActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_option,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.my_option_dev_page)
            launchUrl("https://play.google.com/store/apps/dev?id=9018600825061407450");
        else if (id == R.id.my_option_rate_app)
            launchUrl("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
        else if (id == R.id.my_option_share)
            shareApp();
        else if(id == R.id.my_option_privacy)
            launchUrl(getResources().getString(R.string.privacy_policy));
        else if (id == R.id.my_option_terms)
            launchUrl(getResources().getString(R.string.terms_and_conditions));
        else if (id == R.id.my_option_notice)
            startActivity(new Intent(MyActivity.this,NoticeActivity.class));
        else if (id == R.id.my_option_settings)
            startActivity(new Intent(MyActivity.this,SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void loadHomePage() {
        ContentMyFragment fragment = new ContentMyFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_my, fragment).commit();
    }

    public void viewResult(View view) {
        Intent intent = new Intent(MyActivity.this,ResultActivity.class);
        intent.putExtra("course",COURSE);
        startActivity(intent);
    }
    public void questionPaper(View view) {
        Intent intent = new Intent(MyActivity.this,QPActivity.class);
        intent.putExtra("course",COURSE);
        startActivity(intent);
    }
    public void syllabusAndScheme(View view) {
        Intent intent = new Intent(MyActivity.this,SyllabusActivity.class);
        intent.putExtra("course",COURSE);
        startActivity(intent);
    }

    public void viewTimeTable(View view) {
        Intent intent = new Intent(MyActivity.this,TimeTableActivity.class);
        intent.putExtra("course",COURSE);
        startActivity(intent);
    }

    private void launchUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Download mCSVTU app");
        intent.putExtra(Intent.EXTRA_TEXT,"Get CSVTU Question Papers, Syllabus, Time Tables, Results info easily\nInstall mCSVTU\n"+
                "https://mcsvtu.page.link/app");
        startActivity(Intent.createChooser(intent,"Share Via"));
    }
}