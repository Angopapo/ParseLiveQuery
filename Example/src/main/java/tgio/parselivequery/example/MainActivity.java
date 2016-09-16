package tgio.parselivequery.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;

import org.json.JSONObject;

import tgio.parselivequery.LiveQueryClient;
import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

public class MainActivity extends AppCompatActivity {

    private TextView resultView;
    private Button mConnectButton;
    private Button mDisconnectButton;
    private Button mUnsubscribeButton;
    private Button mSendButton;
    private EditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = (TextView) findViewById(R.id.resultView);
        mUnsubscribeButton = (Button) findViewById(R.id.unsubscribe);
        mConnectButton = (Button) findViewById(R.id.connect);
        mDisconnectButton = (Button) findViewById(R.id.disconnect);
        mSendButton = (Button) findViewById(R.id.send);
        mMessageEditText = (EditText) findViewById(R.id.message);

        //  Connection, when starts or by Connect Button
        //LiveQueryClient.connect();

        LiveQueryClient.on(LiveQueryEvent.CONNECTED, new OnListener() {
            @Override
            public void on(final JSONObject object) {
                //  Subscribe to any event if you need as soon as connect to server
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultView.append(object.toString() + "\n");
                    }
                });

            }
        });

        LiveQueryClient.on(LiveQueryEvent.SUBSCRIBED, new OnListener() {
            @Override
            public void on(final JSONObject object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultView.append(object.toString() + "\n");
                    }
                });
            }
        });

        //  Subscription
        final Subscription subscription = new BaseQuery.Builder("Message")
                .where("body", "asd")
                .addField("body")
                .build()
                .subscribe();

        //  Listen
        subscription.on(LiveQueryEvent.CREATE, new OnListener() {
            @Override
            public void on(final JSONObject object) {
                Log.e("CREATE", object.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultView.append(object.toString() + "\n");
                    }
                });
            }
        });

        //  Listen ALL events
        subscription.on(LiveQueryEvent.ALL, new OnListener() {
            @Override
            public void on(final JSONObject object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //resultView.append(object.toString() + "\n");
                    }
                });
            }
        });

        //  Unsubscribe
        mUnsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.unsubscribe();
            }
        });

        //  Connect
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveQueryClient.connect();
            }
        });

        //  Disconnect
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveQueryClient.disconnect();
            }
        });

        //  Send Message for testing, text must be "asd"
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString().trim();
                mMessageEditText.setText("");
                if (message.length() > 0) {
                    ParseObject po = new ParseObject("Message");
                    po.put("body", message);
                    po.saveInBackground();
                }
            }
        });
    }
}
