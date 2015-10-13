package com.toonsnet.tools.boatcommunicator;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private DbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbAdapter(this);
        dbHelper.open();

        //Clean all data
        dbHelper.deleteAllBoats(); // todo to remove
        //Add some data
        dbHelper.insertSomeBoats(); // todo to remove

        //Generate ListView from SQLite Database
        displayListView();

    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchAllBoats();

        // The desired columns to be bound
        String[] columns = new String[] {
                DbAdapter.TYPE_OF_BOAT,
                DbAdapter.BOAT_MODELL,
                DbAdapter.SAIL_NUMBER,
                DbAdapter.BOAT_NAME,
                DbAdapter.HARBOUR,
                DbAdapter.OWNER,
                DbAdapter.PHONE,
                DbAdapter.EMAIL,
                DbAdapter.COUNTRY,
                DbAdapter.KRYSSAR_KLUBBEN,
                DbAdapter.SEA_RESCUE
        };

        // the XML defined views which the data will be bound to
        int[] toXmlView = new int[] {
                R.id.typOfBoatSearchTextView,
                R.id.modellSearchTextView,
                R.id.sailNrSearchTextView,
                R.id.boatNameSearchTextView,
                R.id.harbourSearchTextView,
                R.id.ownerSearchTextView,
                R.id.phoneSearchTextView,
                R.id.emailSearchTextView,
                R.id.countrySearchTextView,
                R.id.kryssarKlubbenSearchTextView,
                R.id.seaRescueSearchTextView
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.search_result,
                cursor,
                columns,
                toXmlView,
                0);

        ListView listView = (ListView) findViewById(R.id.mainListView);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // // TODO: 2015-10-12 open new activity showing this boat

                // Get the state's capital from this row in the database.
                String owner = cursor.getString(cursor.getColumnIndexOrThrow("owner"));
                Toast.makeText(getApplicationContext(), owner, Toast.LENGTH_SHORT).show();
            }
        });

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchBoatsByFilter(constraint.toString());
            }
        });

    }
}