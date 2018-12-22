package codingalecr.cr.asteroides.storage;

import android.content.Context;
import android.os.AsyncTask;
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
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncSocketScoreManager implements ScoreStorage {

    private final String mTelnetServer = "158.42.146.127";
    private Context mContext;

    public AsyncSocketScoreManager(Context context) {
        mContext = context;
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        try {
            StoreScoreTask tarea = new StoreScoreTask();
            tarea.execute(String.valueOf(points), name,
                    String.valueOf(date));
            tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(mContext, "Tiempo excedido al conectar",
                    Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(mContext, "Error al conectar con servidor", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        try {
            FetchScoresTask tarea = new FetchScoresTask();
            tarea.execute(quantity);
            return tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(mContext, "Tiempo excedido al conectar", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(mContext, "Error al conectar con servidor", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    private class FetchScoresTask extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Integer... quantity) {
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
                } while (n < quantity[0] && respuesta != null);
                sk.close();
            } catch (Exception e) {
                Log.e("Asteroides", e.toString(), e);
            }
            return result;
        }
    }

    private class StoreScoreTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            try {
                Socket socket = new Socket(mTelnetServer, 1234);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                output.print(param[0] + " " + param[1]);
                String response = reader.readLine();
                if (!response.equals("OK")) {
                    Log.e("Asteroides", "Error: respuesta del servidor incorrecta.");
                }
                socket.close();
            } catch (Exception e) {
                Log.e("Asteroides", e.toString(), e);
            }
            return null;
        }
    }
}
