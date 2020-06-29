package android.android.chronicpatients.ParamedicApp.ParamedicFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.balysv.materialripple.MaterialRippleLayout;

import android.android.chronicpatients.ParamedicApp.ParamedicNFCActivity;
import android.android.chronicpatients.R;

public class ParamedicNFC extends Fragment
{
    View view;

    Button scan_nfc;
    MaterialRippleLayout first_aid;

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

        scan_nfc = view.findViewById(R.id.scan_nfc);

        first_aid = view.findViewById(R.id.first_aid_card);

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
                Intent intent = new Intent(getContext(), ParamedicNFCActivity.class);
                startActivity(intent);
            }
        });

    }
}
