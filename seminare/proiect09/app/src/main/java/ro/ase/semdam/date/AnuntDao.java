package ro.ase.semdam.date;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ro.ase.semdam.domeniu.Anunt;

@Dao
public interface AnuntDao {

    @Query("SELECT * FROM anunturi")
    List<Anunt> getAll();

    @Insert
    long insert(Anunt anunt);

    @Update
    void update(Anunt anunt);

    @Delete
    void delete(Anunt anunt);
}