package ro.pub.cs.systems.eim.practicaltest02;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PracticalTest02MainActivity extends AppCompatActivity {

    TextView abilities;
    ImageView img;
    EditText pokename, serverPort;
    Button connect, get;

    ServerThread serverThread = null;
    ClientThread clientThread = null;

    String svPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = findViewById(R.id.serverPort);
        connect = findViewById(R.id.connect);
        get = findViewById(R.id.go);
        abilities = findViewById(R.id.abilities);
        img = findViewById(R.id.image);
        pokename = findViewById(R.id.name);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svPort = serverPort.getText().toString();
                if (svPort != null && !svPort.isEmpty()) {
                    serverThread = new ServerThread(Integer.parseInt(svPort));
                    serverThread.start();
                }
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = pokename.getText().toString();

                clientThread = new ClientThread(Integer.parseInt(svPort), query, abilities, img);
                clientThread.start();
            }
        });

    }

    @Override
    protected void onDestroy() {

        if (serverThread != null)
            serverThread.stopThread();
        super.onDestroy();


    }
}
