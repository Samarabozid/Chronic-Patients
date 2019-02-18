package mansour.abdullah.el7a2ny.ParamedicApp.ParamedicFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mansour.abdullah.el7a2ny.R;

public class ParamedicNFC extends Fragment
{
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.paramedic_nfc_fragment, container, false);

        return view;
    }
}
