package com.qmd.jzen.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qmd.jzen.R;
import com.qmd.jzen.adapters.UrlLoadingAdapter;
import com.qmd.jzen.network.MusicDownload;

public class UrlLoadingFragment extends Fragment {
    Context mContext;
    RecyclerView recyclerView_urlLoading;
    UrlLoadingAdapter adapter;

    public UrlLoadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void initView(View view) {
        mContext = requireActivity();
        recyclerView_urlLoading = view.findViewById(R.id.recyclerView_urlloading);
        adapter = new UrlLoadingAdapter(mContext);
        recyclerView_urlLoading.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView_urlLoading.setAdapter(adapter);
        MusicDownload.getStateChange().observe(getViewLifecycleOwner(), entity -> adapter.update());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_url_loading, container, false);
        initView(v);
        return v;
    }
}