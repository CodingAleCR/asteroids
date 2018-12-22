package codingalecr.cr.asteroides.storage;

import android.content.Context;
import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AssetsStorageScoreManager implements ScoreStorage {

    private Context mContext;

    public AssetsStorageScoreManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) { }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        List<String> result = new ArrayList<>();
        try {
            InputStream f = mContext.getAssets().open("file/puntuaciones.txt");
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
