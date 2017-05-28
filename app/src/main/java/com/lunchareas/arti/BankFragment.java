package com.lunchareas.arti;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BankFragment extends Fragment{

    double moneys;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        moneys = container.getContext().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE).getFloat("UserMoneys", 1000);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setUpLog() {

    }
}
