package codingalecr.cr.asteroides.utils;

import android.content.Context;
import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PreferenceScoreManager implements ScoreStorage {

    private static String PREFERENCES = "scores";
    private Context mContext;

    public PreferenceScoreManager(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int n = 9; n >= 1; n--) {
            editor.putString("score" + n,
                    preferences.getString("score" + (n - 1), ""));
        }
        editor.putString("score0", points + " " + name);
        editor.apply();
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        List<String> result = new ArrayList<>();
        SharedPreferences preferences = mContext.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE);
        for (int n = 0; n <= 9; n++) {
            String s = preferences.getString("score" + n, "");
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        return result;
    }

}
