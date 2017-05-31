package com.lunchareas.arti;

/* MainActivity.java
 * v1.1.0
 * 2017-05-30
 *
 * Arti - Buy/sell stocks in an artificial environment
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang, Emil Tu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ArtiData data = new ArtiData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set initial view
        goToScreen();

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Change the logo font
        TextView title = (TextView) findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Bree.ttf");
        title.setTypeface(typeface);

        setUpBottomBarListener();
    }

    private void setUpBottomBarListener() {
        findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new StockFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .commit();

                getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit().putInt("LastVisitedScreen", 0).apply();
            }
        });

        findViewById(R.id.btn_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new BankFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .commit();

                getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit().putInt("LastVisitedScreen", 1).apply();
            }
        });

        findViewById(R.id.btn_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new NewsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .commit();

                getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit().putInt("LastVisitedScreen", 2).apply();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stock: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Stock");

                final EditText input = new EditText(this);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.addStock(input.getText().toString());
                        recreate();
                        goToScreen();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return true;
            }
            case R.id.delete_stock: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Remove Stock");

                final EditText input = new EditText(this);
                builder.setView(input);

                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.removeStock(input.getText().toString());
                        recreate();
                        goToScreen();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return true;
            }
        }
        return false;
    }

    private void goToScreen() {
        int toSet = getSharedPreferences("PREFERENCES", MODE_PRIVATE).getInt("LastVisitedScreen", 0);

        if (toSet == 0) {
            Fragment frag = new StockFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, frag)
                    .commit();
        } else if (toSet == 1) {
            Fragment frag = new BankFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, frag)
                    .commit();
        } else if (toSet == 2) {
            Fragment frag = new NewsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, frag)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSharedPreferences("PREFERENCES", MODE_PRIVATE).getInt("LastVisitedScreen", 0) != 2) super.onBackPressed();
        else if (!((NewsFragment) getFragmentManager().findFragmentById(R.id.content_frame)).onBackPressed()) super.onBackPressed();
    }
}
