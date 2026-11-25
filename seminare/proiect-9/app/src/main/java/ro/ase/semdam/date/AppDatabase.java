package ro.ase.semdam.date;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ro.ase.semdam.domeniu.Anunt;
import ro.ase.semdam.domeniu.Categorie;

@Database(
        entities = {Anunt.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class, Categorie.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract AnuntDao anuntDao();

    private static volatile AppDatabase instanta;

    public static AppDatabase getInstance(Context context) {

        if (instanta == null) {
            synchronized (AppDatabase.class) {
                if (instanta == null) {

                    instanta = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "semdam.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return instanta;
    }
}