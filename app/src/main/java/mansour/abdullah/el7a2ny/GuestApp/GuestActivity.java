package mansour.abdullah.el7a2ny.GuestApp;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mansour.abdullah.el7a2ny.Listener;
import mansour.abdullah.el7a2ny.Models.LocationModel;
import mansour.abdullah.el7a2ny.NFCFragments.NFCReadFragment;
import mansour.abdullah.el7a2ny.NFCFragments.NFCReadFragment3;
import mansour.abdullah.el7a2ny.ParamedicApp.ParamedicFragments.ParamedicNFC;
import mansour.abdullah.el7a2ny.R;

public class GuestActivity extends AppCompatActivity implements Listener
{
    public static final String TAG = ParamedicNFC.class.getSimpleName();

    MaterialRippleLayout first_aid_card;
    Button scan_nfc,sendlocation;
    EditText patient_notes;

    String nfcid,namee,emergencyy,bloodtypee,diseasee,noote;

    FusedLocationProviderClient mFusedLocationClient;
    Location my_location;

    NFCReadFragment3 mNfcReadFragment;

    boolean isDialogDisplayed = false;
    boolean isWrite = false;

    NfcAdapter mNfcAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @TargetApi(Build.VERSION_CODES.P)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        first_aid_card = findViewById(R.id.first_aid_card);
        scan_nfc = findViewById(R.id.btn_read);
        sendlocation = findViewById(R.id.send_location_btn);
        patient_notes = findViewById(R.id.patient_note_field);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        initNFC();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>()
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

        first_aid_card.setOnClickListener(new View.OnClickListener()
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
                NfcManager manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
                NfcAdapter adapter = manager.getDefaultAdapter();
                if (adapter != null && adapter.isEnabled())
                {
                    // adapter exists and is enabled.
                    showReadFragment();
                } else
                {
                    Toast.makeText(getApplicationContext(), "please check that NFC is enabled firstly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String latitude = Double.toString(my_location.getLatitude());
                String longitude = Double.toString(my_location.getLongitude());

                if (TextUtils.isEmpty(nfcid))
                {
                    Toast.makeText(getApplicationContext(), "please scan NFC firstly", Toast.LENGTH_SHORT).show();
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
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
    }

    private void showReadFragment()
    {
        mNfcReadFragment = (NFCReadFragment3) getSupportFragmentManager().findFragmentByTag(NFCReadFragment3.TAG);

        if (mNfcReadFragment == null)
        {

            mNfcReadFragment = NFCReadFragment3.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment3.TAG);
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
        nfcid = id;
    }

    @Override
    public void patient_name(String name)
    {
        namee = name;
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
                getApplicationContext(), 0, new Intent(getApplicationContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(GuestActivity.this, pendingIntent, nfcIntentFilter, null);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(GuestActivity.this);
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed)
            {
                if (isWrite)
                {

                } else
                {
                    mNfcReadFragment = (NFCReadFragment3)getSupportFragmentManager().findFragmentByTag(NFCReadFragment3.TAG);
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

        Toast.makeText(getApplicationContext(), "Location Sent", Toast.LENGTH_SHORT).show();
    }
}