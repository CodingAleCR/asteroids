package codingalecr.cr.asteroides.utils;

import android.content.Context;
import android.content.SharedPreferences;
import codingalecr.cr.asteroides.models.Puntuacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

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
        SharedPreferences preferences = mContext.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("json", string);
        editor.apply();
    }

    private String leerString() {
        SharedPreferences preferences = mContext.getSharedPreferences(
                FILE, Context.MODE_PRIVATE);
        return preferences.getString("json", null);
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        string = leerString();
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
