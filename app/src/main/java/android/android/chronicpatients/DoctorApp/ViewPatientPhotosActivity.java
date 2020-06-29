package android.android.chronicpatients.DoctorApp;

import android.android.chronicpatients.ActivitiesAndFragments.PatientsFragment;
import android.android.chronicpatients.Models.PhotoModel;
import android.android.chronicpatients.PateintApp.PatientFragments.PatientPhotosFragment;
import android.android.chronicpatients.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

public class ViewPatientPhotosActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String PATIENT_KEY;
    List<PhotoModel> list;

    PhotoAdapter adapter;

    ImageView default_image;
    TextView default_txt;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_photos);

        PATIENT_KEY = getIntent().getStringExtra("patientID");

        recyclerView = findViewById(R.id.photos_recyclerview);
        default_image = findViewById(R.id.no_photo_img);
        default_txt = findViewById(R.id.no_photo_txt);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        list = new ArrayList<>();
        adapter = new PhotoAdapter(list);

        getData();
        setDefault();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setDefault();
                getData();
            }
        });
    }

    private void getData() {
        databaseReference.child("Photos").child(PATIENT_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PhotoModel photoModel = snapshot.getValue(PhotoModel.class);
                    list.add(photoModel);
                }

                adapter = new PhotoAdapter(list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setDefault() {
        databaseReference.child("Photos").child(PATIENT_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    getData();
                    swipeRefreshLayout.setRefreshing(false);
                    default_image.setVisibility(View.INVISIBLE);
                    default_txt.setVisibility(View.INVISIBLE);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    default_image.setVisibility(View.VISIBLE);
                    default_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.photoVH> {
        List<PhotoModel> photoModels;

        PhotoAdapter(List<PhotoModel> photoModels) {
            this.photoModels = photoModels;
        }

        @NonNull
        @Override
        public PhotoAdapter.photoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_photos, parent, false);
            return new PhotoAdapter.photoVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoAdapter.photoVH holder, final int position) {
            final PhotoModel photoModel = photoModels.get(position);
            final String photoID = photoModel.getPhotoID();
            Picasso.with(getApplicationContext()).load(photoModel.getImageurl()).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext() , ViewPatientPhotoActivity.class);
                    i.putExtra("patientID",PATIENT_KEY);
                    i.putExtra("photoID",photoID);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return photoModels.size();
        }

        class photoVH extends RecyclerView.ViewHolder {
            ImageView imageView;

            @SuppressLint("CutPasteId")
            photoVH(@NonNull View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.doctor_profile_picture);
            }
        }
    }
}
