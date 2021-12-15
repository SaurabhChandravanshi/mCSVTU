package com.saurabhchandr.em.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.saurabhchandr.em.R;


public class ContentMyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstantState) {
        View rootView = inflater.inflate(R.layout.content_my,container,false);
        return rootView;
    }
    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState) {

    }
}
