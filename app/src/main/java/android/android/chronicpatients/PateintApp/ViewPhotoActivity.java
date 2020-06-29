package android.android.chronicpatients.PateintApp;

import android.android.chronicpatients.Models.PhotoModel;
import android.android.chronicpatients.R;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewPhotoActivity extends AppCompatActivity {

    ImageView image;
    static EditText comment;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String photoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        photoID = getIntent().getStringExtra("photoID");

        image = findViewById(R.id.photo);
        comment = findViewById(R.id.comment);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        comment.setEnabled(false);

        databaseReference.child("Photos").child(getUID()).child(photoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                PhotoModel photoModel = dataSnapshot.getValue(PhotoModel.class);

                String url = photoModel.getImageurl();
                String dComment = photoModel.getDoctorComment();

                Picasso.with(getApplicationContext()).load(url).into(image);
                comment.setText(dComment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }
}