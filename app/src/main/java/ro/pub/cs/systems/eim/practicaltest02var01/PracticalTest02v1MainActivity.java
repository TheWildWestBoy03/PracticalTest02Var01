package ro.pub.cs.systems.eim.practicaltest02var01;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ro.pub.cs.systems.eim.practicaltest02var01.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var01.network.PracticalTestBroadcastReceiver;
import ro.pub.cs.systems.eim.practicaltest02var01.network.ServerThread;

public class PracticalTest02v1MainActivity extends AppCompatActivity {
    public IntentFilter intentFilter;
    public PracticalTestBroadcastReceiver broadcastReceiver;
    public boolean registeredReceiver = false;
    public ServerThread serverThread;
    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.search_button) {
                Log.d(Constants.TAG, "search autocomplete");
                EditText prefixEdit = (EditText) findViewById(R.id.prefix_edit);
                serverThread = new ServerThread(prefixEdit, getApplicationContext());
                serverThread.startServer();
            } else if (v.getId() == R.id.navbtn) {
                Intent secActIntent = new Intent(getApplicationContext(), PracticalTest02v1SecondaryActivity.class);
                startActivityForResult(secActIntent, Constants.REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.practical_test02_v1_activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView resultView = (TextView) findViewById(R.id.result);
        intentFilter = new IntentFilter();
        broadcastReceiver = new PracticalTestBroadcastReceiver(resultView);
        intentFilter.addAction(Constants.ACTION);

        Button generateBtn = (Button) findViewById(R.id.search_button);
        generateBtn.setOnClickListener(new ButtonClickListener());

        Button mapDisplayButton = (Button) findViewById(R.id.navbtn);
        mapDisplayButton.setOnClickListener(new ButtonClickListener());
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        if (!registeredReceiver) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED);
            } else {
                registerReceiver(broadcastReceiver, intentFilter);
            }
            registeredReceiver = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == Constants.REQUEST_CODE) {
            Toast.makeText(getApplicationContext(), "RESULT CODE IS: " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registeredReceiver) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (IllegalArgumentException e) {
                Log.w(Constants.TAG, "Receiver not registered", e);
            }
            registeredReceiver = false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(Constants.TAG, "onDestroy: ");
        serverThread.stopThread();
        super.onDestroy();
    }
}