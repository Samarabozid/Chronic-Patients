package android.android.chronicpatients.ActivitiesAndFragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.android.chronicpatients.AdminApp.AdminMainActivity;
import android.android.chronicpatients.DoctorApp.DoctorMainActivity;
import android.android.chronicpatients.ParamedicApp.ParamedicMainActivity;
import android.android.chronicpatients.PateintApp.PatientMainActivity;
import android.android.chronicpatients.R;

public class NoInternetActivity extends AppCompatActivity
{
    Button try_again_btn;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        try_again_btn = findViewById(R.id.try_again_btn);

        i = getIntent().getIntExtra("doctor",0);

        try_again_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                final boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (isConnected)
                {
                    if (i == 0)
                    {
                        Intent intent = new Intent(getApplicationContext(), DoctorMainActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            startActivity(intent,
                                    ActivityOptions.makeSceneTransitionAnimation(NoInternetActivity.this).toBundle());
                        }
                    } else if (i == 1)
                    {
                        Intent intent = new Intent(getApplicationContext(), PatientMainActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            startActivity(intent,
                                    ActivityOptions.makeSceneTransitionAnimation(NoInternetActivity.this).toBundle());
                        }
                    } else if (i == 2)
                    {
                        Intent intent = new Intent(getApplicationContext(), ParamedicMainActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            startActivity(intent,
                                    ActivityOptions.makeSceneTransitionAnimation(NoInternetActivity.this).toBundle());
                        }
                    } else if (i == 3)
                    {
                        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            startActivity(intent,
                                    ActivityOptions.makeSceneTransitionAnimation(NoInternetActivity.this).toBundle());
                        }
                    } else if (i == 4)
                        {
                            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                startActivity(intent,
                                        ActivityOptions.makeSceneTransitionAnimation(NoInternetActivity.this).toBundle());
                            }
                        }
                } else
                    {
                        Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private long exitTime = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finishAffinity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        doExitApp();
    }
}
