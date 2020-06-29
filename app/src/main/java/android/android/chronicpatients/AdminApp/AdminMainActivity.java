package android.android.chronicpatients.AdminApp;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import android.android.chronicpatients.AdminApp.AdminFragments.DoctorsFragment;
import android.android.chronicpatients.AdminApp.AdminFragments.ParamedicsFragment;
import android.android.chronicpatients.AdminApp.AdminFragments.PatientsFragment;
import android.android.chronicpatients.AdminApp.AdminFragments.ProfileFragment;
import android.android.chronicpatients.AdminApp.AdminFragments.RequestsFragment;
import android.android.chronicpatients.R;

public class AdminMainActivity extends AppCompatActivity
{
    BottomNavigationView navigation;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        navigation = findViewById(R.id.navigation);

        fragmentManager = getSupportFragmentManager();

        Fragment requestsFragment = new RequestsFragment();
        loadFragment(requestsFragment);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.admin_requests:
                        //navigation.setBackgroundColor(getResources().getColor(R.color.blue_grey_700));
                        Fragment requestsFragment1 = new RequestsFragment();
                        loadFragment(requestsFragment1);
                        return true;
                    case R.id.admin_doctors:
                        //navigation.setBackgroundColor(getResources().getColor(R.color.blue_grey_700));
                        Fragment doctorsFragment = new DoctorsFragment();
                        loadFragment(doctorsFragment);
                        return true;
                    case R.id.admin_patients:
                        //navigation.setBackgroundColor(getResources().getColor(R.color.blue_grey_700));
                        Fragment patientsFragment = new PatientsFragment();
                        loadFragment(patientsFragment);
                        return true;
                    case R.id.admin_paramedics:
                        //navigation.setBackgroundColor(getResources().getColor(R.color.blue_grey_700));
                        Fragment paramedicsFragment = new ParamedicsFragment();
                        loadFragment(paramedicsFragment);
                        return true;
                    case R.id.admin_profile:
                        //navigation.setBackgroundColor(getResources().getColor(R.color.blue_grey_700));
                        Fragment profileFragment = new ProfileFragment();
                        loadFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment)
    {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);

        getFragmentManager().popBackStack();
        // Commit the transaction
        fragmentTransaction.commit();
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
