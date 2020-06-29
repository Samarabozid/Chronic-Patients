package android.android.chronicpatients.PateintApp;

import android.android.chronicpatients.PateintApp.PatientFragments.PatientProfileFragment;
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

import android.android.chronicpatients.ActivitiesAndFragments.DoctorsFragment;
import android.android.chronicpatients.R;
import android.android.chronicpatients.ActivitiesAndFragments.SignupFragment;

public class PatientMainActivity extends AppCompatActivity
{
    BottomNavigationView navigation;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_activity_main);

        navigation = findViewById(R.id.navigation);

        fragmentManager = getSupportFragmentManager();

        int i = getIntent().getIntExtra(SignupFragment.EXTRA_PROFILE_TAG, 0);

        if (i == 123)
        {
            Fragment profile = new PatientProfileFragment();
            loadFragment(profile);
            navigation.setSelectedItemId(R.id.profile);
        } else
            {
                Fragment doctors = new DoctorsFragment();
                loadFragment(doctors);
            }

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.doctors:
                        Fragment doctors = new DoctorsFragment();
                        loadFragment(doctors);
                        return true;
                    case R.id.profile:
                        Fragment profile = new PatientProfileFragment();
                        loadFragment(profile);
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
