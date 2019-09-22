package com.project.electrosolve;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class commonItems_Fragment extends Fragment {

    public commonItems_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        final ImageButton lineImageButton = getView().findViewById(R.id.lineButton);
        final ImageButton resistorImageButton = getView().findViewById(R.id.resistButton);
        final View lineView = getView().findViewById(R.id.viewLine);
        final View resistorView = getView().findViewById(R.id.viewResistor);
        resistorImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                resistorView.setBackgroundColor(getResources().getColor(R.color.colorOrange));
            }
        });
        lineImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineView.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                resistorView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            }
        });


        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common_items_, container, false);

    }

}
