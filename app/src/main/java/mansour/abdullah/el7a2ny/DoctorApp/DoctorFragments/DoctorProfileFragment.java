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

import mansour.abdullah.el7a2ny.AbdullahNFCActivity;
import mansour.abdullah.el7a2ny.NFCActivity;
import mansour.abdullah.el7a2ny.R;
import mansour.abdullah.el7a2ny.RegisterActivity;

public class DoctorProfileFragment extends Fragment
{
    View view;
    Button NFC,NFC_abdullah,signout;

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
        NFC_abdullah = view.findViewById(R.id.abdullah_nfc_activity_btn);
        signout = view.findViewById(R.id.signout_btn);

        NFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /*Intent intent = new Intent(getContext(), NFCActivity.class);
                startActivity(intent);*/
                //FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), NFCActivity.class);
                startActivity(intent);
            }
        });

        NFC_abdullah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /*Intent intent = new Intent(getContext(), NFCActivity.class);
                startActivity(intent);*/
                //FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), AbdullahNFCActivity.class);
                startActivity(intent);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
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
