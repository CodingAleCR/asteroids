package codingalecr.cr.asteroides.utils;

import android.content.Context;
import android.util.Log;
import codingalecr.cr.asteroides.models.Puntuacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonStorageScoreManager implements ScoreStorage {
    public class Clase {
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;
    }

    private static String FILE = "puntuaciones-gson.txt";
    private Context mContext;
    private String string; //Almacena puntuaciones en formato JSON
    private Gson gson = new Gson();
    private Type type = new TypeToken<Clase>() {
    }.getType();

    public GsonStorageScoreManager(Context context) {
        mContext = context;
        storeScore(45000, "Mi nombre", System.currentTimeMillis());
        storeScore(31000, "Otro nombre", System.currentTimeMillis());
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        string = leerString();
        Clase objeto;
        if (string == null) {
            objeto = new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }
        objeto.puntuaciones.add(new Puntuacion(points, name, date));
        string = gson.toJson(objeto, type);
        guardarString(string);
    }

    private void guardarString(String string) {
        try {
            FileOutputStream f = mContext.openFileOutput(FILE, Context.MODE_APPEND);
            f.write(string.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    private String leerString() {
        String json = "";
        try {
            FileInputStream f = mContext.openFileInput(FILE);
            BufferedReader input = new BufferedReader(new InputStreamReader(f));
            String line;
            do {
                line = input.readLine();
                if (line != null) {
                    json = line;
                }
            } while (line != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return json;
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        //string = leerString();
        Clase objeto;
        if (string == null) {
            objeto = new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }
}
