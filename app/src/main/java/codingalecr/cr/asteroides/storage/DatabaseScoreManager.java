package codingalecr.cr.asteroides.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DatabaseScoreManager extends SQLiteOpenHelper implements ScoreStorage {

    public DatabaseScoreManager(Context context) {
        super(context, "scores", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE puntuaciones (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "puntos INTEGER, nombre TEXT, fecha BIGINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void storeScore(int points, @NotNull String name, long date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO puntuaciones VALUES ( null, " +
                points + ", '" + name + "', " + date + ")");
        db.close();
    }

    @NotNull
    @Override
    public List<String> scoresList(int quantity) {
        List<String> result = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String[] CAMPOS = {"puntos", "nombre"};
        Cursor cursor = db.query("puntuaciones", CAMPOS, null, null,
                null, null, "puntos DESC", Integer.toString(quantity));
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(0) + " " + cursor.getString(1));
        }
        cursor.close();
        db.close();
        return result;
    }
}
