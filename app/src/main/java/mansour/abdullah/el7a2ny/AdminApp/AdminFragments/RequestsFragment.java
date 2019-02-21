package mansour.abdullah.el7a2ny.AdminApp.AdminFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.victor.loading.rotate.RotateLoading;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import mansour.abdullah.el7a2ny.AdminApp.AdminPatientsDetailsActivity;
import mansour.abdullah.el7a2ny.Models.LocationModel;
import mansour.abdullah.el7a2ny.R;

public class RequestsFragment extends Fragment
{
    View view;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<LocationModel, AdminRequestsViewHolder> firebaseRecyclerAdapter;


    RotateLoading rotateLoading;

    public final static String ADMIN_EXTRA_REQUEST_KEY = "request_key";

    public static String uri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.admin_requests_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = view.findViewById(R.id.doctors_recyclerview);
        rotateLoading = view.findViewById(R.id.rotateloading);

        rotateLoading.start();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        DisplayallDoctors();
    }

    private void DisplayallDoctors()
    {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("AllRequests")
                .limitToLast(50);

        FirebaseRecyclerOptions<LocationModel> options =
                new FirebaseRecyclerOptions.Builder<LocationModel>()
                        .setQuery(query, LocationModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LocationModel, AdminRequestsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull AdminRequestsViewHolder holder, int position, @NonNull final LocationModel model)
            {
                rotateLoading.stop();

                final String key2 = model.getNfc_id();

                holder.BindPlaces(model);

                holder.doctor_mobile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialContactPhone(model.getEmergency());
                    }
                });

                holder.doctor_details.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), AdminPatientsDetailsActivity.class);
                        intent.putExtra(ADMIN_EXTRA_REQUEST_KEY, key2);
                        startActivity(intent);
                    }
                });

                holder.view_profile_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), AdminPatientsDetailsActivity.class);
                        intent.putExtra(ADMIN_EXTRA_REQUEST_KEY, key2);
                        startActivity(intent);
                    }
                });

                double latitude = Double.parseDouble(model.getLatitude());
                double longitude = Double.parseDouble(model.getLongitude());

                uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);

                holder.doctor_picture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public AdminRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.request_item, parent, false);
                return new AdminRequestsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1)))
                {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        rotateLoading.stop();
    }

    public static class AdminRequestsViewHolder extends RecyclerView.ViewHolder
    {
        ImageView doctor_mobile;
        CircleImageView doctor_picture;
        TextView doctor_name,doctor_specailty,disease_txt,notes_txt;
        MaterialRippleLayout doctor_details;
        Button view_profile_btn;

        AdminRequestsViewHolder(View itemView)
        {
            super(itemView);

            doctor_picture = itemView.findViewById(R.id.doctor_profile_picture);
            doctor_mobile = itemView.findViewById(R.id.phonenumber_btn);
            doctor_name = itemView.findViewById(R.id.doctor_fullname);
            doctor_specailty = itemView.findViewById(R.id.doctor_specialty);
            doctor_details = itemView.findViewById(R.id.details_btn);
            view_profile_btn = itemView.findViewById(R.id.view_profile_btn);
            disease_txt = itemView.findViewById(R.id.disease_txt);
            notes_txt = itemView.findViewById(R.id.note_txt);
        }

        void BindPlaces(final LocationModel locationModel)
        {
            doctor_name.setText(locationModel.getName());
            doctor_specailty.setText(locationModel.getNfc_id());
            disease_txt.setText(locationModel.getDisease());
            notes_txt.setText(locationModel.getNotes());
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (firebaseRecyclerAdapter != null)
        {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    private void dialContactPhone(final String phoneNumber)
    {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }
}
