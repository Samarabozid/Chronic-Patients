package mansour.abdullah.el7a2ny.DoctorApp.DoctorFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.util.Calendar;

import mansour.abdullah.el7a2ny.AbdullahNFCActivity;
import mansour.abdullah.el7a2ny.Models.DoctorModel;
import mansour.abdullah.el7a2ny.Models.PatientModel;
import mansour.abdullah.el7a2ny.NFCActivity;
import mansour.abdullah.el7a2ny.PateintApp.PatientFragments.PatientProfileFragment;
import mansour.abdullah.el7a2ny.R;
import mansour.abdullah.el7a2ny.RegisterActivity;

import static android.app.Activity.RESULT_OK;

public class DoctorProfileFragment extends Fragment
{
    View view;

    Button edit_profile_btn,signout_btn,savechanges_btn;

    ImageView profilepicture,callmobile;
    TextView fullname_txt,nfcid_txt;
    static EditText email_field,fullname_field,mobile_field,address_field;
    String mobile,profile_image_url,special;

    String full_name_txt,email_txt,mobile_txt,address_txt;

    RotateLoading rotateLoading;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    String selected_placeimaeURL = "";
    Uri photoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.doctor_profile_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        edit_profile_btn = view.findViewById(R.id.edit_profile_btn);
        signout_btn = view.findViewById(R.id.signout_btn);
        savechanges_btn = view.findViewById(R.id.savechanges_btn);

        fullname_txt = view.findViewById(R.id.fullname_txt);
        nfcid_txt = view.findViewById(R.id.nfcid_txt);

        profilepicture = view.findViewById(R.id.patient_profile_picture);
        callmobile = view.findViewById(R.id.phonenumber_btn);

        email_field = view.findViewById(R.id.email_field);
        fullname_field = view.findViewById(R.id.fullname_field);
        mobile_field = view.findViewById(R.id.mobile_field);
        address_field = view.findViewById(R.id.address_field);

        rotateLoading = view.findViewById(R.id.rotateloading);

        email_field.setEnabled(false);
        fullname_field.setEnabled(false);
        mobile_field.setEnabled(false);
        address_field.setEnabled(false);
        profilepicture.setEnabled(false);
        savechanges_btn.setEnabled(false);

        callmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone(mobile);
            }
        });

        savechanges_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v)
            {
                full_name_txt = fullname_field.getText().toString();
                email_txt = email_field.getText().toString();
                mobile_txt = mobile_field.getText().toString();
                address_txt = address_field.getText().toString();

                if (TextUtils.isEmpty(full_name_txt))
                {
                    Toast.makeText(getContext(), "please enter your full name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email_txt))
                {
                    Toast.makeText(getContext(), "please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile_txt))
                {
                    Toast.makeText(getContext(), "please enter your mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address_txt))
                {
                    Toast.makeText(getContext(), "please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                fullname_field.setEnabled(false);
                mobile_field.setEnabled(false);
                address_field.setEnabled(false);
                profilepicture.setEnabled(false);
                savechanges_btn.setEnabled(false);
                edit_profile_btn.setEnabled(true);

                if (photoPath == null)
                {
                    UpdatePatientProfile(full_name_txt,email_txt,mobile_txt,special,address_txt,profile_image_url);
                } else
                {
                    uploadImage(full_name_txt,email_txt,mobile_txt,special,address_txt);
                }
            }
        });

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1,1)
                        .start(getContext(), DoctorProfileFragment.this);
            }
        });

        signout_btn.setOnClickListener(new View.OnClickListener() {
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

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname_field.setEnabled(true);
                mobile_field.setEnabled(true);
                address_field.setEnabled(true);
                profilepicture.setEnabled(true);
                savechanges_btn.setEnabled(true);
                edit_profile_btn.setEnabled(false);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        rotateLoading.start();

        returndata();
    }

    public void returndata()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        final String userId = user.getUid();

        mDatabase.child("AllUsers").child("Doctors").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // Get user value
                        DoctorModel doctorModel = dataSnapshot.getValue(DoctorModel.class);

                        special = doctorModel.getSpecialization();
                        nfcid_txt.setText(special);

                        mobile = doctorModel.getMobilenumber();
                        mobile_field.setText(mobile);

                        email_field.setText(doctorModel.getEmail());

                        fullname_txt.setText(doctorModel.getFullname());
                        fullname_field.setText(doctorModel.getFullname());

                        address_field.setText(doctorModel.getAddress());

                        profile_image_url = doctorModel.getImageurl();

                        Picasso.get()
                                .load(profile_image_url)
                                .placeholder(R.drawable.doctor2)
                                .error(R.drawable.doctor2)
                                .into(profilepicture);

                        rotateLoading.stop();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(getContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();
                    }
                });
    }

    private void dialContactPhone(final String phoneNumber)
    {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void UpdatePatientProfile(String fullname, String email, String mobile, String spec, String address, String imageurl)
    {
        DoctorModel patientModel = new DoctorModel(fullname,email,mobile,spec,address,imageurl);

        databaseReference.child("Doctors").child(special).child(getUid()).setValue(patientModel);
        databaseReference.child("AllUsers").child("Doctors").child(getUid()).setValue(patientModel);

        Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();

        if (photoPath == null)
        {
            returndata();
        }
    }

    private void uploadImage(final String fullname, final String email, final String mobile, final String spec, final String address)
    {
        rotateLoading.start();

        UploadTask uploadTask;

        final StorageReference ref = storageReference.child("images/" + photoPath.getLastPathSegment());

        uploadTask = ref.putFile(photoPath);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();

                rotateLoading.stop();

                selected_placeimaeURL = downloadUri.toString();

                UpdatePatientProfile(fullname,email,mobile,spec,address,selected_placeimaeURL);

                Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();

                returndata();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                if (result != null)
                {
                    photoPath = result.getUri();

                    Picasso.get()
                            .load(photoPath)
                            .placeholder(R.drawable.patient2)
                            .error(R.drawable.patient2)
                            .into(profilepicture);

                    selected_placeimaeURL = photoPath.toString();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    public String getUid()
    {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
