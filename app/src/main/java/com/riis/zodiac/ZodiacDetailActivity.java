package com.riis.zodiac;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import android.widget.Toast;
import android .database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class ZodiacDetailActivity extends Activity {

    public static final String EXTRA_SIGN = "ZodiacSign";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zodiac_detail);

        int signNum = (Integer) getIntent().getExtras().get(EXTRA_SIGN);

        // Attempt to create a new cursor
        try {
            SQLiteOpenHelper zodiacDatabaseHelper = new ZodiacDatabaseHelper(this);
            SQLiteDatabase db = zodiacDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("ZODIAC",
                    new String[] {"NAME","MONTH","DESCRIPTION","SIGN"},
                    "_id = ?",
                    new String[] {Integer.toString(signNum)},
                    null,null,null);

            // Move to the first record in the cursor
            if(cursor.moveToFirst()) {
                // Get the details
                String nameText = cursor.getString(0);
                String monthText = cursor.getString(1);
                String descriptionText = cursor.getString(2);
                String signText = cursor.getString(3);

                // Set the name
                TextView name = (TextView) findViewById(R.id.name);
                name.setText(nameText);

                // Set the month
                TextView month = (TextView) findViewById(R.id.month);
                month.setText(monthText);

                // Set the description
                TextView description = (TextView) findViewById(R.id.description);
                description.setText(descriptionText);

                // Set the sign
                TextView symbol = (TextView) findViewById(R.id.symbol);
                symbol.setText(signText);
            }

            // Close the cursor and database
            cursor.close();
            db.close();
        } catch(SQLiteException e) {
            // If there is an error then let the user know
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {
        String yourJsonStringUrl = "http://a.knrz.co/horoscope-api/current/";
        String horoscope = "";

        public AsyncTaskParseJson(Zodiac sign) {
            yourJsonStringUrl += sign.getName().toLowerCase();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {
            try {
                // instantiate our json parser
                JsonParser jParser = new JsonParser();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);
                horoscope = json.getString("prediction");
                horoscope = URLDecoder.decode(horoscope);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            TextView display = (TextView) findViewById(R.id.daily);
            display.setText(horoscope);
        }
    }
}