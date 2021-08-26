package info.legeay.meteo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import info.legeay.meteo.dao.CityDAO;
import info.legeay.meteo.model.City;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * entities : Each entity corresponds to a table that will be created in the database
 * exportSchema : here false to avoid build warning - but true would be better (export schema into a directory)
 * [Note: When you modify the database schema, you'll need to update the version number and define a migration strategy]
 */
@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class MeteoDatabase extends RoomDatabase {

    public abstract CityDAO cityDAO();

    private static final String DB_NAME = "meteo_database";
    private static volatile MeteoDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 10;
    private static final ExecutorService dbExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static final Scheduler dbScheduler = Schedulers.from(dbExecutor);


    /**
     * Singleton
     * Create db the first time it is accessed
     * ExecutorService: with a fixed thread pool. to run database operations asynchronously on a background thread
     * @param context
     * @return
     */
    public static MeteoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MeteoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MeteoDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
