package com.saurabhchandr.em;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.saurabhchandr.em.Fragment.ContentMainFragment;

import im.crisp.client.ChatActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInAnonymously();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

         //MediationTestSuite.launch(MainActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadHomePage();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void signInAnonymously() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInAnonymously();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_notif) {
            startActivity(new Intent(MainActivity.this, NoticeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dev_page)
            launchUrl("https://play.google.com/store/apps/dev?id=9018600825061407450");
        else if (id == R.id.nav_rate_app)
            launchUrl("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
        else if (id == R.id.nav_share)
            shareApp();
        else if(id == R.id.nav_privacy)
            launchUrl(getResources().getString(R.string.privacy_policy));
        else if (id == R.id.nav_terms)
            launchUrl(getResources().getString(R.string.terms_and_conditions));
        else if (id == R.id.nav_report_problem)
            startActivity(new Intent(MainActivity.this,ReportActivity.class));
        else if (id == R.id.nav_live_chat)
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        else if (id == R.id.nav_my_mcsvtu)
            startActivity(new Intent(MainActivity.this,SignUpActivity.class));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadHomePage() {
        Fragment fragment = new ContentMainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Download mCSVTU app");
        intent.putExtra(Intent.EXTRA_TEXT,"Get CSVTU Question Papers, Syllabus, Time Tables, Results info easily\nInstall mCSVTU\n"+
                "https://mcsvtu.page.link/app");
        startActivity(Intent.createChooser(intent,"Share Via"));
    }

    private void launchUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void viewFeatures(View view) {
        Intent intent = new Intent(MainActivity.this,FeatureActivity.class);
        intent.putExtra("course",view.getTag().toString());
        startActivity(intent);
    }
}