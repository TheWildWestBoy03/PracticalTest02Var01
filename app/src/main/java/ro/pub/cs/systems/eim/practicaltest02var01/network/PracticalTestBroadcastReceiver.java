package ro.pub.cs.systems.eim.practicaltest02var01.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import ro.pub.cs.systems.eim.practicaltest02var01.general.Constants;

public class PracticalTestBroadcastReceiver extends BroadcastReceiver {
    public TextView resultText;

    public PracticalTestBroadcastReceiver(final TextView textView) {
        this.resultText = textView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent.getStringExtra(Constants.BROADCAST_MESSAGE);
        this.resultText.setText(result);
    }
}
