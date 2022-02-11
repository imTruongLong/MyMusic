package com.myapplication.view.splash;

import android.content.Intent;
import android.os.Handler;

import com.myapplication.base.BaseAct;
import com.myapplication.databinding.ActivitySplashBinding;
import com.myapplication.view.main.MainActivity;
import com.myapplication.view.splash.viewmodel.SplashViewModel;


public class SplashActivity extends BaseAct<SplashViewModel, ActivitySplashBinding> {
    private ActivitySplashBinding binding;


    @Override
    public ActivitySplashBinding getBinding() {
        return binding = ActivitySplashBinding.inflate(getLayoutInflater());
    }

    protected void initViews() {
        new Handler().postDelayed(this::gotoMainActivity, 3000);
    }

    @Override
    protected Class<SplashViewModel> getClassViewModel() {
        return SplashViewModel.class;
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
