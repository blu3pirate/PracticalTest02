package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public static BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }


    @Override
    public void run() {
        if (socket == null) {
            Log.e("abc", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        Log.d("abc", "Started Communication Thread");
        try {
            BufferedReader bufferedReader = getReader(socket);
            PrintWriter printWriter = getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e("abc", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("abc", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");


            // We read the first query sent in the ClientThread
            String query1 = bufferedReader.readLine();


            if (query1 == null || query1.isEmpty()) {
                Log.e("abc", "[COMMUNICATION THREAD] Error receiving parameters from client (query1 / information type!");
                return;
            }


            Log.i("abc", "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            // In case of POST change to HttpPost and remover the arghuments from the urkl
            HttpGet httpPost = new HttpGet("https://pokeapi.co/api/v2/pokemon/" + query1);
            List<NameValuePair> params = new ArrayList<>();

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
            if (pageSourceCode == null) {
                Log.e("abc", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }

            int st = pageSourceCode.indexOf("\"ability\":{\"name\":\"");
            int end = 0;
            String abilities = "";
            while (st != -1) {
                pageSourceCode = pageSourceCode.substring(st + "\"ability\":{\"name\":\"".length());
//                Log.d("abc", "st: " + st);
//                Log.d("abc", "Text: " + pageSourceCode);
                end = pageSourceCode.indexOf("\"");
//                Log.d("abc", "end: " + end);
                abilities += pageSourceCode.substring(0, end) + ", ";
                Log.d("abc", "Abilities: " + abilities);

                st = pageSourceCode.indexOf("\"ability\":{\"name\":\"");
            }
            st = pageSourceCode.indexOf("\"front_default\":\"");
            pageSourceCode = pageSourceCode.substring(st + "\"front_default\":\"".length());
            end = end = pageSourceCode.indexOf("\"");
//            Log.d("abc", "st, end: " + st + ", " + end);
            String imgUrl = pageSourceCode.substring(0, end);

            String result = imgUrl + "_:_" + abilities;

            // Send the data to the client
            printWriter.println(result);
            printWriter.flush();

            socket.close();
        }catch (Exception e){
            Log.d("abc", "Exceptie: + " + e);
        }
    }
}
