package mansour.abdullah.el7a2ny.NFCFragments;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;

import java.io.IOException;
import java.nio.charset.Charset;

import mansour.abdullah.el7a2ny.Listener;
import mansour.abdullah.el7a2ny.NFCActivity;
import mansour.abdullah.el7a2ny.R;

public class NFCWriteFragment extends DialogFragment
{
    public static final String TAG = NFCWriteFragment.class.getSimpleName();

    public static NFCWriteFragment newInstance() {

        return new NFCWriteFragment();
    }

    TextView mTvMessage;
    RotateLoading rotateLoading;
    Listener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        mTvMessage = view.findViewById(R.id.tv_message);
        rotateLoading = view.findViewById(R.id.rotateloading);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mListener = (NFCActivity)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef, String messageToWrite)
    {
        rotateLoading.start();
        writeToNfc(ndef,messageToWrite);
    }

    private void writeToNfc(Ndef ndef, String message){

        mTvMessage.setText(getString(R.string.message_write_progress));
        if (ndef != null) {

            try {
                ndef.connect();
                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", message.getBytes(Charset.forName("US-ASCII")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                //Write Successful
                mTvMessage.setText(getString(R.string.message_write_success));

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                mTvMessage.setText(getString(R.string.message_write_error));

            } finally {
                rotateLoading.stop();
            }

        }
    }
}
