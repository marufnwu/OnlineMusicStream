package com.maruf.is.rex.audiostory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.maruf.is.rex.audiostory.Adapters.FragmentAdapter;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Database.RoomDatabase;
import com.maruf.is.rex.audiostory.Fragments.ContainerFragment;
import com.maruf.is.rex.audiostory.Model.Audio;

import java.util.List;

import io.reactivex.Flowable;

public class MainActivity extends AppCompatActivity {



    ViewPager viewPager;
    FragmentAdapter fragmentAdapter;

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            // JSON here
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.mainViewpager);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(1);

        Common.roomDatabase = RoomDatabase.getInstance(this);
        if (Common.roomDatabase==null){
            Log.d("LocalDB", "RoomDatbase null");

        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
