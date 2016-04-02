package com.example.providertest;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private String newId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button add=(Button)findViewById(R.id.add_data);
        add.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("content://com.example.sqlitedatabase.provider/book");
                ContentValues values=new ContentValues();
                values.put("name", "xxxx");
                values.put("author", "kim");
                values.put("pages", 200);
                values.put("price", 12.32);
                Uri newUri=getContentResolver().insert(uri, values);
                newId=newUri.getPathSegments().get(1);
                
            }
        });
        
        Button btnquery=(Button)findViewById(R.id.query_data);
        btnquery.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("content://com.example.sqlitedatabase.provider/book");
                Cursor cursor=getContentResolver().query(uri, null, null, null, null);
                if(cursor!=null){
                    while (cursor.moveToNext()) {
                       String name =cursor.getString(cursor.getColumnIndex("name"));
                       String author =cursor.getString(cursor.getColumnIndex("author"));
                       Toast.makeText(getApplicationContext(), "Ãû×Ö:"+name+"--"+"×÷Õß:"+author, Toast.LENGTH_LONG).show();
                        
                    }
                }
                
            }
        });
    }

   
}
