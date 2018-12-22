package codingalecr.cr.asteroides.storage;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncWebServiceScoreManager implements ScoreStorage {

    private Context mContext;
    private String mServer;

    public AsyncWebServiceScoreManager(Context context, String server) {
        mContext = context;
        mServer = server;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
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
            //Copia el código que antes estaba en listaPuntuaciones()
            List<String> result = new ArrayList<>();
            HttpURLConnection conexion = null;
            try {
                URL url = new URL(mServer + "lista.php" + "?max=" + quantity[0]);
                conexion = (HttpURLConnection) url.openConnection();
                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    String linea = reader.readLine();
                    while (!linea.equals("")) {
                        result.add(linea);
                        linea = reader.readLine();
                    }
                    reader.close();
                } else {
                    Log.e("Asteroides", conexion.getResponseMessage());
                }
            } catch (Exception e) {
                Log.e("Asteroides", e.getMessage(), e);
            } finally {
                if (conexion != null) conexion.disconnect();
            }
            return result;
        }
    }

    private class StoreScoreTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            try {
                URL url = new URL(mServer + "nueva.php?"
                        + "puntos=" + param[0]
                        + "&nombre=" + URLEncoder.encode(param[1], "UTF-8")
                        + "&fecha=" + param[2]);
                //Copia el código que antes estaba en guardarPuntuaciones
                HttpURLConnection conexion = null;
                try {
                    conexion = (HttpURLConnection) url.openConnection();
                    if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                        String linea = reader.readLine();
                        if (!linea.equals("OK")) {
                            Log.e("Asteroides", "Error en servicio Web nueva");
                            cancel(true);
                        }
                    } else {
                        Log.e("Asteroides", conexion.getResponseMessage());
                        cancel(true);
                    }
                } catch (Exception e) {
                    Log.e("Asteroides", e.getMessage(), e);
                    cancel(true);
                } finally {
                    if (conexion != null) conexion.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                cancel(true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

    }
}
