package com.riis.zodiac;

import android.app.ListActivity;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ListActivity {
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listSigns = getListView();

        // Try to get a list of the zodiac names and give them to the list view
        try {
            SQLiteOpenHelper zodiacDatabaseHelper = new ZodiacDatabaseHelper(this);
            db = zodiacDatabaseHelper.getReadableDatabase();

            cursor = db.query("ZODIAC",
                    new String[] {"_id", "NAME"},
                    null,null,null,null,null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,cursor,new String[]{"NAME"},new int[]{android.R.id.text1},0);
            listSigns.setAdapter(listAdapter);

        } catch(SQLiteException e) {
            // If we get an error notify the user
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Close the database and cursor during the onDestroy method
    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id) {
        Intent intent = new Intent(MainActivity.this, ZodiacDetailActivity.class);
        intent.putExtra(ZodiacDetailActivity.EXTRA_SIGN, (int) id);
        startActivity(intent);
    }
}
