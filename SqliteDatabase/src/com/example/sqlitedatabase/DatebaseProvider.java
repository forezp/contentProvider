package com.example.sqlitedatabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatebaseProvider extends ContentProvider {

   // content://com.example.sqlitedatabase.provider/book

    public static final String AUTHORITY="com.example.sqlitedatabase.provider";
    private static final int BOOK_DIR = 0;
    private static final int BOOK_ITEM = 2;
    private static UriMatcher uirMatcher;
    private MyDatabaseHelper dbHelper;

    static {
        uirMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uirMatcher.addURI(AUTHORITY, "book", BOOK_DIR);
        uirMatcher.addURI(AUTHORITY, "book/#", BOOK_ITEM);
    }
    @Override
    public boolean onCreate() {
        dbHelper=new MyDatabaseHelper(getContext(), "BookStore", null, 2);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=null;
        switch (uirMatcher.match(uri)) {
            case BOOK_DIR:

                cursor =db.query("Book",  projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BOOK_ITEM:

                String bookId=uri.getPathSegments().get(1);
                cursor=db.query("Book",  projection, "id=?", new String [] {bookId}, null, null, sortOrder);
            default:
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
       switch (uirMatcher.match(uri)) {
        case BOOK_DIR:
            return "vnd.android.cursor.dir/vnd.com.example.sqlitedatabase.provider.book";
        case BOOK_ITEM:
            return "vnd.android.cursor.item/vnd.com.example.sqlitedatabase.provider.book";

        default:
            break;
    }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Uri uriReturn=null;
        switch (uirMatcher.match(uri)) {
            case BOOK_DIR:
            case BOOK_ITEM:
                long newBookId=db.insert("Book", null, values);
                uriReturn=Uri.parse("content://"+AUTHORITY+"/book/"+newBookId);
                break;

            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int deleteRow=0;
        switch (uirMatcher.match(uri)) {
            case BOOK_DIR:
                deleteRow=db.delete("Book",  selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId=uri.getPathSegments().get(1);
                deleteRow=db.delete("Book",  "id=?", new String []{bookId});
            default:
                break;
        }
        return deleteRow;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int updateRow=0;
        switch (uirMatcher.match(uri)) {
            case BOOK_DIR:
                updateRow=db.update("Book", values, selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId=uri.getPathSegments().get(1);
                updateRow=db.update("Book", values, "id=?", new String []{bookId});
            default:
                break;
        }
        return updateRow;
    }

}
