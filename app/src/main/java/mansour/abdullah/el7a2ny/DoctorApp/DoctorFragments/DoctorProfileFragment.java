package mansour.abdullah.el7a2ny.DoctorApp.DoctorFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import mansour.abdullah.el7a2ny.R;
import mansour.abdullah.el7a2ny.RegisterActivity;

public class DoctorProfileFragment extends Fragment
{
    View view;
    Button NFC;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.doctor_profile_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NFC = view.findViewById(R.id.nfc_activity_btn);

        NFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /*Intent intent = new Intent(getContext(), NFCActivity.class);
                startActivity(intent);*/
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
