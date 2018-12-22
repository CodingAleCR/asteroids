package codingalecr.cr.asteroides.storage;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebServiceScoreManager implements ScoreStorage {

    private Context mContext;
    private String mServer;

    public WebServiceScoreManager(Context context, String server) {
        mContext = context;
        mServer = server;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        HttpURLConnection conexion = null;
        try {
            URL url = new URL(mServer + "nueva.php?"
                    + "puntos=" + points
                    + "&nombre=" + URLEncoder.encode(name, "UTF-8")
                    + "&fecha=" + date);
            conexion = (HttpURLConnection) url.openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (!linea.equals("OK")) {
                    Log.e("Asteroides", "Error en servicio Web nueva");
                }
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        } finally {
            if (conexion != null) conexion.disconnect();
        }
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        List<String> result = new ArrayList<>();
        HttpURLConnection conexion = null;
        try {
            URL url = new URL(mServer + "lista.php" + "?max=" + quantity);
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
