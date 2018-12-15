package codingalecr.cr.asteroides.utils;

import android.content.Context;
import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IntStorageScoreManager implements ScoreStorage {

    private static String FILE = "puntuaciones.txt";
    private Context mContext;

    public IntStorageScoreManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        try {
            FileOutputStream f = mContext.openFileOutput(FILE, Context.MODE_APPEND);
            String texto = points + " " + name + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        List<String> result = new ArrayList<>();
        try {
            FileInputStream f = mContext.openFileInput(FILE);
            BufferedReader input = new BufferedReader(new InputStreamReader(f));
            int n = 0;
            String line;
            do {
                line = input.readLine();
                if (line != null) {
                    result.add(line);
                    n++;
                }
            } while (n < quantity && line != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }
}
