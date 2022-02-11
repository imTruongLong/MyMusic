package com.myapplication.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

public abstract class BaseAct<T extends ViewModel,Binding extends ViewBinding> extends AppCompatActivity {
    protected T mModel;
    Binding binding;

    public abstract Binding getBinding();

    private void initBinding(){
        binding = getBinding();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initBinding();
        setContentView(binding.getRoot());
        mModel= new ViewModelProvider(this).get(getClassViewModel());
        initViews();
    }

    protected abstract void initViews();

    protected abstract Class<T> getClassViewModel();


}
