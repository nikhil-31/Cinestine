package in.nikhil.cinestine.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nikhil on 8/27/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movieDatabase";

    public static final String TABLE_MOVIE_DETAILS = "moviesDetails";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_RATING = "rating";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_DATE = "date";
    public static final String KEY_STATUS = "status";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_BACKDROP = "backdrop";
    public static final String KEY_VOTE_COUNT = "vote_count";
    public static final String KEY_TAG_LINE = "tag_line";
    public static final String KEY_RUN_TIME = "runtime";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_POPULARITY = "popularity";
    public static final String KEY_POSTER = "poster";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
