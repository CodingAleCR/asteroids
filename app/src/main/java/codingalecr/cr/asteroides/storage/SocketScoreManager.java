package codingalecr.cr.asteroides.storage;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import codingalecr.cr.asteroides.R;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketScoreManager implements ScoreStorage {

    private final String mTelnetServer = "158.42.146.127";
    private Context mContext;

    public SocketScoreManager(Context context) {
        mContext = context;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        try {
            Socket socket = new Socket(mTelnetServer, 1234);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            output.print(points + " " + name);
            String response = reader.readLine();
            if (!response.equals("OK")) {
                Log.e("Asteroides", "Error: respuesta del servidor incorrecta.");
            }
            socket.close();
        } catch (ConnectException exception) {
            Toast.makeText(mContext, R.string.text_server_not_available, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("Asteroides", e.toString(), e);
        }
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        List<String> result = new ArrayList<>();
        try {
            Socket sk = new Socket(mTelnetServer, 1234);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            PrintWriter salida = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()), true);
            salida.println("PUNTUACIONES");
            int n = 0;
            String respuesta;
            do {
                respuesta = entrada.readLine();
                if (respuesta != null) {
                    result.add(respuesta);
                    n++;
                }
            } while (n < quantity && respuesta != null);
            sk.close();
        } catch (ConnectException exception) {
            Toast.makeText(mContext, R.string.text_server_not_available, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("Asteroides", e.toString(), e);
        }
        return result;
    }
}
