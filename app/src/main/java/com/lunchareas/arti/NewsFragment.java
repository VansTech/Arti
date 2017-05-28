package com.lunchareas.arti;

/* NewsFragment.java
 * v1.0.0
 * 2017-05-28
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang
 * Full copyright information available in MainActivity.java
 */

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class NewsFragment extends Fragment {

    private ArtiData data;
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        data = new ArtiData(this.getActivity());

        //setUp(view);

        webView = (WebView) view.findViewById(R.id.news_view);
        //webView.loadUrl("https://www.google.com/m/search#q=apple&tbm=nws");
        webView.loadUrl("https://www.google.com/webhp?tbm=nws");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        return view;
    }

    public boolean onBackPressed() {
        if (webView.copyBackForwardList().getCurrentIndex() > 0) {
            webView.goBack();
            return true;
        } else {
            return false;
        }
    }

    private void setUp(View view){
        new NewsFetch().execute();
    }

    private class NewsFetch extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myParser = xmlFactoryObject.newPullParser();

                URLConnection connection = new URL("https://news.google.com/news?q=apple&output=rss").openConnection();
                myParser.setInput(connection.getInputStream(), null);

                int event = myParser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.END_TAG:
                            System.out.println(name);
                            if (name.equals("title")) {
                                System.out.println("Title!!!!");
                            }
                            break;
                    }
                    event = myParser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
