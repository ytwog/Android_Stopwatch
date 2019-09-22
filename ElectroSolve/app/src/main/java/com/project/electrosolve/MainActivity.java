package com.project.electrosolve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ScrollingView;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ElementAdapter elementAdapter;
    ImageButton lineImageButton, resistorImageButton, deleteImageButton;
    TabLayout itemTab;
    ViewPager itemView;
    View lineView, resistorView;
    BroadcastReceiver Create1_broadcastReceiver;
    View drawingF;
    int ItemInfo = 0;


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.Create1_broadcastReceiver);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        itemTab = findViewById(R.id.itemTabLayout);
        itemView = findViewById(R.id.itemViewPager);
        drawingF = findViewById(R.id.Draw_fragment);
        deleteImageButton = findViewById(R.id.deleteButton);

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                resistorView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                ItemInfo = -2;

                Intent intentToFragment = new Intent(Intent.ACTION_SENDTO);
                intentToFragment.putExtra("itemChosen", ItemInfo);
                getApplicationContext().sendBroadcast(intentToFragment);
            }
        });

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        elementAdapter = new ElementAdapter(getSupportFragmentManager(), itemTab.getTabCount());
        itemView.setAdapter(elementAdapter);
        itemTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                itemView.setCurrentItem(tab.getPosition());
                updateCommonItemsBar(tab.getPosition(), toolbar);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateCommonItemsBar(tab.getPosition(), toolbar);
            }
        });
        itemView.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(itemTab));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorOrange));
        itemTab.setBackgroundColor(getResources().getColor(R.color.colorOrange));

        Create1_broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                itemTab.getTabAt(0).select();
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SENDTO);
        registerReceiver(Create1_broadcastReceiver, filter);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_save) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    void updateCommonItemsBar(int pos, Toolbar toolbar) {
        switch(pos){
            case 0:
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                itemTab.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                lineImageButton = findViewById(R.id.lineButton);
                resistorImageButton = findViewById(R.id.resistButton);
                lineView = findViewById(R.id.viewLine);
                resistorView = findViewById(R.id.viewResistor);
                resistorImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lineView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        resistorView.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                        ItemInfo = 1;

                        Intent intentToFragment = new Intent(Intent.ACTION_SENDTO);
                        intentToFragment.putExtra("itemChosen", ItemInfo);
                        getApplicationContext().sendBroadcast(intentToFragment);
                    }
                });
                lineImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lineView.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                        resistorView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        ItemInfo = 0;

                        Intent intentToFragment = new Intent(Intent.ACTION_SENDTO);
                        intentToFragment.putExtra("itemChosen", ItemInfo);
                        getApplicationContext().sendBroadcast(intentToFragment);
                    }
                });
                return;
            case 1:
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                itemTab.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                return;
            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                itemTab.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                return;
        }
    }
}

