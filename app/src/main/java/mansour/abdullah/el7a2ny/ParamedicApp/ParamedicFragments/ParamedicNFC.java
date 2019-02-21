package mansour.abdullah.el7a2ny.ParamedicApp.ParamedicFragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import mansour.abdullah.el7a2ny.Listener;
import mansour.abdullah.el7a2ny.Models.LocationModel;
import mansour.abdullah.el7a2ny.NFCFragments.NFCReadFragment;
import mansour.abdullah.el7a2ny.NFCFragments.NFCWriteFragment;
import mansour.abdullah.el7a2ny.PateintApp.PatientMainActivity;
import mansour.abdullah.el7a2ny.R;

public class ParamedicNFC extends Fragment implements Listener
{
    View view;

    public static final String TAG = ParamedicNFC.class.getSimpleName();

    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private NfcAdapter mNfcAdapter;

    FusedLocationProviderClient mFusedLocationClient;
    Location my_location;

    String nfcid,namee,emergencyy,bloodtypee,diseasee,noote;

    Button scan_nfc,send_location;
    EditText patient_nfc,patient_name,patient_note;
    MaterialRippleLayout first_aid;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.paramedic_nfc_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        scan_nfc = view.findViewById(R.id.btn_read);
        send_location = view.findViewById(R.id.send_location_btn);

        patient_nfc = view.findViewById(R.id.patient_nfc_field);
        patient_name = view.findViewById(R.id.patient_name_field);
        patient_note = view.findViewById(R.id.patient_note_field);

        first_aid = view.findViewById(R.id.first_aid_card);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null)
                        {
                            // Logic to handle location object
                            my_location = location;
                        }
                    }
                });

        initNFC();

        first_aid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = "https://first-aid-product.com/free-first-aid-guide.html";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        scan_nfc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NfcManager manager = (NfcManager) getContext().getSystemService(Context.NFC_SERVICE);
                NfcAdapter adapter = manager.getDefaultAdapter();
                if (adapter != null && adapter.isEnabled())
                {
                    // adapter exists and is enabled.
                    showReadFragment();
                } else
                {
                    Toast.makeText(getContext(), "please check that NFC is enabled firstly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        send_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String latitude = Double.toString(my_location.getLatitude());
                String longitude = Double.toString(my_location.getLongitude());

                nfcid = patient_nfc.getText().toString();
                namee = patient_name.getText().toString();
                noote = patient_note.getText().toString();

                if (TextUtils.isEmpty(nfcid))
                {
                    Toast.makeText(getContext(), "please scan NFC firstly", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(noote))
                {
                    noote = "Hurry Up ...";
                }

                sendRequest(nfcid, namee ,emergencyy,bloodtypee,diseasee,noote,latitude,longitude);
            }
        });
    }

    private void initNFC()
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
    }

    private void showReadFragment()
    {
        mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null)
        {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getFragmentManager(),NFCReadFragment.TAG);
    }

    @Override
    public void onDialogDisplayed()
    {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed()
    {
        isDialogDisplayed = false;
        isWrite = false;
    }

    @Override
    public void nfc_id(String id)
    {
        patient_nfc.setText(id);
    }

    @Override
    public void patient_name(String name)
    {
        patient_name.setText(name);
    }

    @Override
    public void patient_number(String number)
    {
        emergencyy = number;
    }

    @Override
    public void patient_bloodtype(String bloodtype)
    {
        bloodtypee = bloodtype;
    }

    @Override
    public void patient_disease(String disease)
    {
        diseasee = disease;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getContext(), 0, new Intent(getContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, nfcIntentFilter, null);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(getActivity());
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null)
        {
            Toast.makeText(getContext(), getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed)
            {
                if (isWrite)
                {

                } else
                {
                    mNfcReadFragment = (NFCReadFragment)getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    mNfcReadFragment.onNfcDetected(ndef);
                }
            }
        }
    }

    public void sendRequest (String nfc_id, String full_name, String emergency_number, String blood_type, String disease_patient, String notes, String latitude, String longitude)
    {
        LocationModel locationModel = new LocationModel(nfc_id, full_name, emergency_number, blood_type, disease_patient, notes, latitude, longitude);

        String request_key = databaseReference.child("AllRequests").push().getKey();

        databaseReference.child("AllRequests").child(request_key).setValue(locationModel);
    }
}
