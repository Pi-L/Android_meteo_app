package info.legeay.meteo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import info.legeay.meteo.model.City;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

// todo: add LiveData data holder ?
/**
 * By default, all queries must be executed on a separate thread
 */
@Dao
public interface CityDAO {


    @Query("SELECT * FROM city")
    public Single<List<City>> getAll();

    @Query("SELECT * FROM city ORDER BY fav_position ASC")
    public Single<List<City>> getAllSortedByFavPositionAsc();

    @Query("SELECT * FROM city WHERE fav_position = 0")
    public Single<City> getFirstFavorite();

    /**
     * return the target entity of the insert method or none if the method should use the parameter type entities
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Completable insert(City city);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public Completable insertAll(List<City> cityList);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public Completable update(City city);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public Completable updateAll(List<City> cityList);

    @Delete
    public Completable delete(City city);

    @Query("DELETE FROM city")
    public Completable deleteAll();

}
