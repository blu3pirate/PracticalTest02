package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private int port;
    private String query;
    private TextView showDataTextView;
    private ImageView imageView;

    private Socket socket;

    public ClientThread(int port, String query, TextView showDataTextView, ImageView img) {
        this.port = port;
        this.query = query;
        this.showDataTextView = showDataTextView;
        this.imageView = img;
    }

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", port);
            if (socket == null) {
                Log.e("abc", "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = getReader(socket);
            PrintWriter printWriter = getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("abc", "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            // We send the first querry to the communicaiton thread
            printWriter.println(query);
            printWriter.flush();

            StringBuilder finfin = new StringBuilder();
            String pokemonInfo;
            while ((pokemonInfo = bufferedReader.readLine()) != null) {
                Log.d("abc", pokemonInfo);
                finfin.append(pokemonInfo);
            }
            final String finalizedPokemonInfo = finfin.toString();
            Log.e("abc", "[CLIENT THREAD] Received: " + finalizedPokemonInfo);

            final String abilities = finalizedPokemonInfo.substring(finalizedPokemonInfo.indexOf("_:_") + 3);
            final String imgUrl = finalizedPokemonInfo.substring(0, finalizedPokemonInfo.indexOf("_:_"));

            showDataTextView.post(new Runnable() {
                @Override
                public void run() {
                    showDataTextView.setText("Abilities: " + abilities);
                    //ImageView
                    Picasso.get().load(imgUrl).into(imageView);

                }
            });
        } catch (IOException ioException) {
            Log.e("abc", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.d("abc", "nu merge");
                }
            }
        }
    }
}