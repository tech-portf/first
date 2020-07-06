package com.websarva.wings.android.databasesample;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.SQLData;

public class MainActivity extends AppCompatActivity {
    int _cocktailId = -1;
    String _cocktailName = "";
    TextView _tvCocktailName;
    Button _btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvCocktail = findViewById(R.id.lvCocktail);
        _tvCocktailName = findViewById(R.id.tvCacktailName);
        _btnSave = findViewById(R.id.btnSave);
        lvCocktail.setOnItemClickListener(new ListItemClickListener());


    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            _cocktailId = position;
            _cocktailName = (String) parent.getItemAtPosition(position);
            _tvCocktailName.setText(_cocktailName);
            _btnSave.setEnabled(true);


            DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                String sql = "SELECT * FROM cocktailmemo WHERE _id = " + _cocktailId;
                Cursor cursor = db.rawQuery(sql, null);
                String note = "";

                while(cursor.moveToNext()){
                    int idNote = cursor.getColumnIndex("note");
                    note = cursor.getString(idNote);
                }
                EditText et = findViewById(R.id.etNote);
                et.setText(note);
            }
            finally {
                db.close();
            }
        }
    }

    public  void onSaveButtonClick(View view){
        EditText etNote = findViewById(R.id.etNote);
        String note = etNote.getText().toString();

        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            String sqlDelete = "DELETE FROM cocktailmemo WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(sqlDelete);

            stmt.bindLong(1, _cocktailId);
            stmt.executeUpdateDelete();

            String sqlInsert = "INSERT INTO cocktailmemo (_id, name, note) VALUES (?, ?, ?)";
            stmt = db.compileStatement(sqlInsert);

            stmt.bindLong(1, _cocktailId);
            stmt.bindString(2, _cocktailName);
            stmt.bindString(3, note);
            stmt.executeInsert();
        }
        finally {
            db.close();
        }

        _tvCocktailName.setText(getString(R.string.tv_name));
        etNote.setText("");
        _btnSave.setEnabled(false);
    }
}
