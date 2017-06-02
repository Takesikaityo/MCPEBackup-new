package jp.takesi.mcpebackup;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class DeveloperActivity extends AppCompatActivity {

    // Define variables for custom tabs and its builder
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        setTitle(R.string.developer);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize intentBuilder
        intentBuilder = new CustomTabsIntent.Builder();

        // Set toolbar(tab) color of your chrome browser
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Define entry and exit animation
        intentBuilder.setExitAnimations(this, R.anim.right_to_left_end, R.anim.left_to_right_end);
        intentBuilder.setStartAnimations(this, R.anim.left_to_right_start, R.anim.right_to_left_start);
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // build it by setting up all
        customTabsIntent = intentBuilder.build();

        ImageButton google = (ImageButton) findViewById(R.id.gPlusIcon);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(getApplicationContext(), Uri.parse("https://plus.google.com/105250560911974727147"));
            }
        });

        ImageButton twitter = (ImageButton) findViewById(R.id.twitterIcon);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(getApplicationContext(), Uri.parse("https://twitter.com/takesi_kaityo"));
            }
        });

        ImageButton youtube = (ImageButton) findViewById(R.id.githubIcon);
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                customTabsIntent.launchUrl(getApplicationContext(), Uri.parse("https://github.com/takesikaityo"));
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
