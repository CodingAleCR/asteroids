package codingalecr.cr.asteroides.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import codingalecr.cr.asteroides.R;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExtStorageScoreManager implements ScoreStorage {

    private static String FILE = Environment.getExternalStorageDirectory() + "/puntuaciones.txt";
    private Context mContext;

    public ExtStorageScoreManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, R.string.text_external_storage_write_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(FILE, true);
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

        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED) &&
                !stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(mContext, R.string.text_external_storage_read_not_available, Toast.LENGTH_LONG).show();
            return result;
        }
        try {
            FileInputStream f = new FileInputStream(FILE);
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
