package com.developer.hrg.nooadmin.message_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.hrg.nooadmin.Models.Chanel;
import com.developer.hrg.nooadmin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends Fragment {

    public static final int GALLERY_REQUEST=100;
    public static String CHANEL_PARCABLE = "chanel";
    Chanel chanel ;

    public AudioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio, container, false);
    }

}
