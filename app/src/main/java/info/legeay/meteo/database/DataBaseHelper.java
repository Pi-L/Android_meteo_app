package info.legeay.meteo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import info.legeay.meteo.model.City;


public class DataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";


    private static final String CREATE_TABLE_CITY = "CREATE TABLE "
            + CityContract.CityEntry.TABLE_NAME
            + "("
            + CityContract.CityEntry._ID + " INTEGER PRIMARY KEY,"
            + CityContract.CityEntry.KEY_FAV_POSITION + " INTEGER,"
            + CityContract.CityEntry.KEY_ID_CITY + " INTEGER,"
            + CityContract.CityEntry.KEY_NAME + " TEXT,"
            + CityContract.CityEntry.KEY_CURRENT_TEMPERATURE + " TEXT,"
            + CityContract.CityEntry.KEY_WEATHER_DESCRITPION + " TEXT,"
            + CityContract.CityEntry.KEY_ICON_ID + " INTEGER,"
            + CityContract.CityEntry.KEY_LAT + " DECIMAL (3, 10),"
            + CityContract.CityEntry.KEY_LON + " DECIMAL (3, 10)"
            + ")";

    private static final String DROP_TABLE_CITY = "DROP TABLE IF EXISTS " + CityContract.CityEntry.TABLE_NAME;


    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_CITY);
        onCreate(db);
    }

    public long insertRow(City city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CityContract.CityEntry.KEY_FAV_POSITION, city.getFavPosition());
        values.put(CityContract.CityEntry.KEY_ID_CITY, city.getId());
        values.put(CityContract.CityEntry.KEY_NAME, city.getName());
        values.put(CityContract.CityEntry.KEY_CURRENT_TEMPERATURE, city.getCurrentTemperature());
        values.put(CityContract.CityEntry.KEY_WEATHER_DESCRITPION, city.getWeatherDescription());
        values.put(CityContract.CityEntry.KEY_ICON_ID, city.getWeatherIconDrawableId());
        values.put(CityContract.CityEntry.KEY_LAT, city.getLat());
        values.put(CityContract.CityEntry.KEY_LON, city.getLon());

        long rowId = db.insert(CityContract.CityEntry.TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    public void insertAll(List<City> cityList) {
        SQLiteDatabase db = this.getWritableDatabase();

        cityList.forEach(city -> {

            ContentValues values = new ContentValues();
            values.put(CityContract.CityEntry.KEY_FAV_POSITION, city.getFavPosition());
            values.put(CityContract.CityEntry.KEY_ID_CITY, city.getId());
            values.put(CityContract.CityEntry.KEY_NAME, city.getName());
            values.put(CityContract.CityEntry.KEY_CURRENT_TEMPERATURE, city.getCurrentTemperature());
            values.put(CityContract.CityEntry.KEY_WEATHER_DESCRITPION, city.getWeatherDescription());
            values.put(CityContract.CityEntry.KEY_ICON_ID, city.getWeatherIconDrawableId());
            values.put(CityContract.CityEntry.KEY_LAT, city.getLat());
            values.put(CityContract.CityEntry.KEY_LON, city.getLon());

            long rowId = db.insert(CityContract.CityEntry.TABLE_NAME, null, values);
            city.setIdDb(rowId);
        });

        db.close();
    }

    public List<City> getCityList() {
        List<City> cityList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String request = "select * from "+CityContract.CityEntry.TABLE_NAME+" ORDER BY "+CityContract.CityEntry.KEY_FAV_POSITION+" ASC";

        Cursor cursor = db.rawQuery(request,null);
        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {

                long idDb = cursor.getLong(cursor.getColumnIndex(CityContract.CityEntry._ID));
                int favPosition = cursor.getInt(cursor.getColumnIndex(CityContract.CityEntry.KEY_FAV_POSITION));
                long id = cursor.getLong(cursor.getColumnIndex(CityContract.CityEntry.KEY_ID_CITY));
                String name = cursor.getString(cursor.getColumnIndex(CityContract.CityEntry.KEY_NAME));
                String weatherDescription = cursor.getString(cursor.getColumnIndex(CityContract.CityEntry.KEY_WEATHER_DESCRITPION));
                String currentTemperature = cursor.getString(cursor.getColumnIndex(CityContract.CityEntry.KEY_CURRENT_TEMPERATURE));
                int weatherIconDrawableId = cursor.getInt(cursor.getColumnIndex(CityContract.CityEntry.KEY_ICON_ID));
                double lat = cursor.getDouble(cursor.getColumnIndex(CityContract.CityEntry.KEY_LAT));
                double lon = cursor.getDouble(cursor.getColumnIndex(CityContract.CityEntry.KEY_LON));

                cityList.add(new City(idDb, favPosition, id, name, weatherDescription, currentTemperature, weatherIconDrawableId, lat, lon));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return cityList;
    }

    public void remove(long idDb) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = CityContract.CityEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(idDb) };
        db.delete(CityContract.CityEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ CityContract.CityEntry.TABLE_NAME);
        db.close();
    }

    public void updatePosition(long idDb, int position) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CityContract.CityEntry.KEY_FAV_POSITION, position);

        String selection = CityContract.CityEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(idDb) };

        db.update(
                CityContract.CityEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
    }

    public void updateAllPositions(List<City> cityList) {
        SQLiteDatabase db = this.getWritableDatabase();

        cityList.forEach(city -> {

            ContentValues values = new ContentValues();
            values.put(CityContract.CityEntry.KEY_FAV_POSITION, city.getFavPosition());

            String selection = CityContract.CityEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(city.getIdDb()) };

            db.update(
                    CityContract.CityEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        });

        db.close();
    }

    public void update(City city) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(CityContract.CityEntry.KEY_FAV_POSITION, city.getFavPosition());
        values.put(CityContract.CityEntry.KEY_ID_CITY, city.getId());
        values.put(CityContract.CityEntry.KEY_NAME, city.getName());
        values.put(CityContract.CityEntry.KEY_CURRENT_TEMPERATURE, city.getCurrentTemperature());
        values.put(CityContract.CityEntry.KEY_WEATHER_DESCRITPION, city.getWeatherDescription());
        values.put(CityContract.CityEntry.KEY_ICON_ID, city.getWeatherIconDrawableId());
        values.put(CityContract.CityEntry.KEY_LAT, city.getLat());
        values.put(CityContract.CityEntry.KEY_LON, city.getLon());

        String selection = CityContract.CityEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(city.getIdDb()) };

        db.update(
                CityContract.CityEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);


        db.close();
    }

    public void updateAll(List<City> cityList) {
        SQLiteDatabase db = this.getWritableDatabase();

        cityList.forEach(city -> {

            ContentValues values = new ContentValues();
            values.put(CityContract.CityEntry.KEY_FAV_POSITION, city.getFavPosition());
            values.put(CityContract.CityEntry.KEY_ID_CITY, city.getId());
            values.put(CityContract.CityEntry.KEY_NAME, city.getName());
            values.put(CityContract.CityEntry.KEY_CURRENT_TEMPERATURE, city.getCurrentTemperature());
            values.put(CityContract.CityEntry.KEY_WEATHER_DESCRITPION, city.getWeatherDescription());
            values.put(CityContract.CityEntry.KEY_ICON_ID, city.getWeatherIconDrawableId());
            values.put(CityContract.CityEntry.KEY_LAT, city.getLat());
            values.put(CityContract.CityEntry.KEY_LON, city.getLon());

            String selection = CityContract.CityEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(city.getIdDb()) };

            db.update(
                    CityContract.CityEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        });

        db.close();
    }
}
