package mansour.abdullah.el7a2ny;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import mansour.abdullah.el7a2ny.DoctorApp.DoctorMainActivity;
import mansour.abdullah.el7a2ny.Models.PatientModel;
import mansour.abdullah.el7a2ny.NFCFragments.NFCReadFragment;
import mansour.abdullah.el7a2ny.NFCFragments.NFCWriteFragment;
import mansour.abdullah.el7a2ny.PateintApp.PatientMainActivity;

public class NFCActivity extends AppCompatActivity implements Listener
{
    String nfc_id;

    public static final String TAG = PatientMainActivity.class.getSimpleName();
    final static String EXTRA_PATIENT_KEY2 = "details_key";

    private EditText mEtMessage;
    private Button mBtWrite;
    private Button mBtRead;

    RotateLoading rotateLoading;

    Spinner bloodtypes;

    Button view_profile_btn;
    EditText patient_nfc,patient_name,patient_emergency,patient_bloodtype,patient_disease;

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        nfc_id = getIntent().getStringExtra(PatientDetailsActivity.PATIENT_NFC_KEY);

        initViews();
        if (!TextUtils.isEmpty(nfc_id))
        {
            return_scanned_data(nfc_id);
        }
        initNFC();
    }

    public void return_scanned_data(String patient_nfc_id)
    {
        rotateLoading.start();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mDatabase.child("Patients").child(patient_nfc_id).child(patient_nfc_id).addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Get user value
                        PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);

                        patient_nfc.setText(patientModel.getNFC_ID());
                        patient_name.setText(patientModel.getFullname());
                        patient_emergency.setText(patientModel.getClose_mobile_number());
                        bloodtypes.setSelection(patientModel.getBloodtypes());

                        rotateLoading.stop();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(getApplicationContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();
                    }
                });
    }

    private void initViews()
    {
        /*fullname = getIntent().getStringExtra(PatientDetailsActivity.PATIENT_NAME_KEY);
        emergency = getIntent().getStringExtra(PatientDetailsActivity.PATIENT_EMERGENCY_KEY);
        bloodtype = getIntent().getStringExtra(PatientDetailsActivity.PATIENT_BLOODTYPE_KEY);*/

        //mEtMessage = findViewById(R.id.et_message);
        mBtWrite = findViewById(R.id.btn_write);
        mBtRead = findViewById(R.id.btn_read);

        rotateLoading = findViewById(R.id.rotateloading);

        view_profile_btn = findViewById(R.id.view_profile_btn);
        patient_nfc = findViewById(R.id.patient_nfc_field);
        patient_name = findViewById(R.id.patient_name_field);
        patient_emergency = findViewById(R.id.patient_emergency_field);
        patient_bloodtype = findViewById(R.id.patient_bloodtype_field);
        patient_disease = findViewById(R.id.patient_disease_field);
        bloodtypes = findViewById(R.id.blood_spinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.bloodtypes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        bloodtypes.setAdapter(adapter1);

        patient_nfc.setEnabled(false);
        bloodtypes.setEnabled(false);
        /*patient_name.setEnabled(false);
        patient_emergency.setEnabled(false);
        patient_bloodtype.setEnabled(false);
        patient_disease.setEnabled(false);*/

        view_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(nfc_id))
                {
                    Toast.makeText(getApplicationContext(), "please sure that you scanned NFC", Toast.LENGTH_SHORT).show();
                } else
                    {
                        Intent intent = new Intent(getApplicationContext(), PatientDetailsActivity.class);
                        intent.putExtra(EXTRA_PATIENT_KEY2, nfc_id);
                        startActivity(intent);
                    }
            }
        });

        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWriteFragment();
            }
        });

        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReadFragment();
            }
        });
    }

    private void initNFC()
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void showWriteFragment()
    {
        isWrite = true;

        mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);

        if (mNfcWriteFragment == null)
        {
            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.show(getSupportFragmentManager(),NFCWriteFragment.TAG);
    }

    private void showReadFragment()
    {
        mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null)
        {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment.TAG);
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

    }

    @Override
    public void patient_name(String name)
    {

    }

    @Override
    public void patient_number(String number)
    {

    }

    @Override
    public void patient_bloodtype(String bloodtype)
    {

    }

    @Override
    public void patient_disease(String disease)
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null)
        {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed)
            {
                if (isWrite)
                {
                    String messageToWrite = mEtMessage.getText().toString();
                    mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite);
                } else
                    {
                        mNfcReadFragment = (NFCReadFragment)getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                        mNfcReadFragment.onNfcDetected(ndef);
                    }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (TextUtils.isEmpty(nfc_id))
        {
            super.onBackPressed();
        } else
            {
                Intent intent = new Intent(getApplicationContext(), DoctorMainActivity.class);
                startActivity(intent);
            }
    }
}
