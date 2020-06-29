package android.android.chronicpatients.PateintApp.PatientFragments;

import android.android.chronicpatients.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PatientProfileFragment extends Fragment {
    View view;
    FragmentPagerAdapter fragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.patient_profile_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);

        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]
                    {
                            new PatientDataFragment(),
                            new PatientPhotosFragment()
                    };
            private final String[] mFragmentNames = new String[]
                    {
                            "Data",
                            "Photos",
                    };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}