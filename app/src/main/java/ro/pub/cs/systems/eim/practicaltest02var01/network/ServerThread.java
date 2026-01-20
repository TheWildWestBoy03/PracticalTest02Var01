package ro.pub.cs.systems.eim.practicaltest02var01.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ro.pub.cs.systems.eim.practicaltest02var01.R;
import ro.pub.cs.systems.eim.practicaltest02var01.general.Constants;

public class ServerThread extends Thread {
    private boolean isRunning = false;
    private EditText prefixOption;
    private Context context;
    private String prefix;
    public ServerThread(final EditText editText, final Context context) {
        this.prefixOption = editText;
        this.context = context;
    }
    public void startServer() {
        isRunning = true;
        prefix = prefixOption.getText().toString();
        this.start();
        Log.d(Constants.TAG, "StartServer: ");
    }

    public void run() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request autocompleteReq = null;

            String getReqUrl = Constants.WEB_SERVICE_ADDRESS + "?client=chrome&q=" + prefix;
            autocompleteReq = new Request.Builder().url(getReqUrl).build();

            Response response = client.newCall(autocompleteReq).execute();

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Log.d(Constants.TAG, responseBody);
                JSONArray jsonArray = new JSONArray(responseBody);
                JSONArray suggestions = jsonArray.getJSONArray(1);

                StringBuilder result = new StringBuilder();
                for (int i = 0; i < suggestions.length(); i++) {
                    result.append(suggestions.getString(i)).append("\n");
                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setPackage("ro.pub.cs.systems.eim.practicaltest02var01");
                broadcastIntent.setAction(Constants.ACTION);

                broadcastIntent.putExtra(Constants.BROADCAST_MESSAGE, new String(result));

                this.context.sendBroadcast(broadcastIntent);
            }
        } catch (IOException | JSONException e) {
            Log.d(Constants.TAG, "Error sending request ");
        }
    }

    public void stopThread() {
        this.stop();
        Log.d(Constants.TAG, "stopThread: ");
    }
}
