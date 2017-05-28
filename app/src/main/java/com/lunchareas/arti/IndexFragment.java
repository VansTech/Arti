package com.lunchareas.arti;

/* IndexFragment.java
 * v1.0.0
 * 2017-05-28
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang
 * Full copyright information available in MainActivity.java
 */

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IndexFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.index_fragment, container, false);
    }
}
