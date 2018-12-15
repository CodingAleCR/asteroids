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
        private ArrayList<Puntuacion> scores = new ArrayList<>();
        private boolean isStored;
    }

    private static String FILE = "scores-gson.txt";
    private Context mContext;
    private String mString; //Almacena scores en formato JSON
    private Gson mGson = new Gson();
    private Type mType = new TypeToken<Clase>() {
    }.getType();

    public GsonStorageScoreManager(Context context) {
        mContext = context;
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        mString = leerString();
        Clase objeto;
        if (mString == null) {
            objeto = new Clase();
        } else {
            objeto = mGson.fromJson(mString, mType);
        }
        objeto.scores.add(new Puntuacion(points, name, date));
        mString = mGson.toJson(objeto, mType);
        guardarString(mString);
    }

    private void guardarString(String string) {
        SharedPreferences preferences = mContext.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("gson", string);
        editor.apply();
    }

    private String leerString() {
        SharedPreferences preferences = mContext.getSharedPreferences(
                FILE, Context.MODE_PRIVATE);
        return preferences.getString("gson", null);
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        mString = leerString();
        Clase objeto;
        if (mString == null) {
            objeto = new Clase();
        } else {
            objeto = mGson.fromJson(mString, mType);
        }
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.scores) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }
}
