package com.lunchareas.arti;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArtiData data = new ArtiData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Change the logo font
        TextView title = (TextView) findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Bree.ttf");
        title.setTypeface(typeface);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_index: {
                data.addIndex("NASDAQ");
                recreate();
            }
            case R.id.delete_index: {

            }
            case R.id.add_stock: {

            }
            case R.id.delete_stock: {
                
            }
        }
        return false;
    }

    private void setUp() {
        List<String> existing = data.returnIndices();
    }

}
