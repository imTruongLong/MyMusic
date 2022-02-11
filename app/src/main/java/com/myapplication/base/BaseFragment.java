package com.myapplication.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<T extends ViewModel,Binding extends ViewBinding> extends Fragment {
    protected T mModel;
    protected Context mContext;
    private Binding binding;

    protected abstract Binding getBinding();

    private void initBinding(){
        this.binding = getBinding();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.initBinding();
        mModel = new ViewModelProvider(this).get(getClassViewModel());
        initEvents();
        return binding.getRoot();
    }

    protected abstract void initEvents();

    protected abstract Class<T> getClassViewModel();

}
