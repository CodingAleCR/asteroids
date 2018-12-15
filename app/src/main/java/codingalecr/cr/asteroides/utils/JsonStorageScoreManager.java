package codingalecr.cr.asteroides.utils;

import android.content.Context;
import android.content.SharedPreferences;
import codingalecr.cr.asteroides.models.Puntuacion;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonStorageScoreManager implements ScoreStorage {

    private String string; //Almacena puntuaciones en formato JSON
    private static String FILE = "scores-json.txt";
    private Context mContext;

    public JsonStorageScoreManager(Context context) {
        mContext = context;
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        List<Puntuacion> puntuaciones = leerJSon(string);
        puntuaciones.add(new Puntuacion(points, name, date));
        string = guardarJSon(puntuaciones);
        guardarString(string);
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        string = leerString();
        List<Puntuacion> puntuaciones = leerJSon(string);
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    private String guardarJSon(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private List<Puntuacion> leerJSon(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();
        if (string != null) {
            try {
                JSONArray json_array = new JSONArray(string);
                for (int i = 0; i < json_array.length(); i++) {
                    JSONObject objeto = json_array.getJSONObject(i);
                    puntuaciones.add(new Puntuacion(objeto.getInt("puntos"),
                            objeto.getString("nombre"), objeto.getLong("fecha")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return puntuaciones;
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
}
