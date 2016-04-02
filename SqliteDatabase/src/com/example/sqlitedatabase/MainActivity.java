package com.example.sqlitedatabase;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.sqlitedatabase.R;
public class MainActivity extends Activity {
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper=new MyDatabaseHelper(MainActivity.this, "BookStore.db", null, 1);
        Button btn=(Button)findViewById(R.id.btn_create);
        btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dbHelper.getWritableDatabase();
                
            }
        });
        
        Button btnAdd=(Button)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               SQLiteDatabase db=dbHelper.getWritableDatabase();
               ContentValues values=new ContentValues();
               values.put("name", "xxxxx");
               values.put("author", "da vinqi");
               values.put("price", 13.32);
               values.put("pages", 23);
               db.insert("Book", null, values);
               values.clear();
               
               values.put("name", "ssss");
               values.put("author", "aaaa");
               values.put("price", 12.32);
               values.put("pages", 232);
               db.insert("Book", null, values);
                
            }
        });
    }
}
