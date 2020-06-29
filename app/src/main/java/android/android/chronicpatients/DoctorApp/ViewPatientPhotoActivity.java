package android.android.chronicpatients.DoctorApp;

import android.android.chronicpatients.Models.PhotoModel;
import android.android.chronicpatients.R;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

public class ViewPatientPhotoActivity extends AppCompatActivity {

    ImageView image;
    static TextInputEditText comment;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String PATIENT_KEY;
    private String photoID;
    ImageButton addCommentBtn;
    private String doctorComment;
    ProgressDialog progressDialog;

    RotateLoading rotateloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_photo);

        PATIENT_KEY = getIntent().getStringExtra("patientID");
        photoID = getIntent().getStringExtra("photoID");

        rotateloading = findViewById(R.id.rotateloading);
        image = findViewById(R.id.photo);
        comment = findViewById(R.id.comment);
        addCommentBtn = findViewById(R.id.add_comment_ibtn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Photos").child(PATIENT_KEY).child(photoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PhotoModel photoModel = dataSnapshot.getValue(PhotoModel.class);

                String url = photoModel.getImageurl();
                String dcomment = photoModel.getDoctorComment();

                Picasso.with(getApplicationContext()).load(url).into(image);

                if (dcomment.equals("")) {
                    comment.setEnabled(true);
                    addCommentBtn.setVisibility(View.VISIBLE);
                } else {
                    comment.setText(dcomment);
                    comment.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctorComment = comment.getText().toString();

                if (TextUtils.isEmpty(doctorComment)) {
                    Toast.makeText(getApplicationContext(), "please enter your comment", Toast.LENGTH_SHORT).show();
                }

               /*progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setTitle("Saving Comment");
                progressDialog.setMessage("Please Wait Until Saving Comment ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);*/

                rotateloading.start();
                addComment();
            }
        });
    }

    private void addComment() {
        databaseReference.child("Photos").child(PATIENT_KEY).child(photoID).child("doctorComment").setValue(doctorComment);
        databaseReference.child("Photos").child(PATIENT_KEY).child(photoID).child("commented").setValue(1);
        //progressDialog.dismiss();
        rotateloading.stop();
        comment.setEnabled(false);
        addCommentBtn.setVisibility(View.GONE);
    }
}