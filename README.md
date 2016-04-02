# contentProvider

* 安卓四大组件之一ContentProvider 应用非常广泛，只要用于读取系统的联系人、图库等，调用系统的数据非常简单，
只需要系统调系统的提供的URI，即可读取数据。
* 在实际的开发中ContentProvider出来调用系统数据外，调用其他应用程序的数据比较少见。

##简介
内容提供者，主要应用于数据共享，提供完整了机制，保证了共享数据的安全性。
本例有两个工程，1.SqliteDatabase 工程，它主要包括provider 和provider操作的数据库。
      2.providertest 工程，对1工程的provider进行了操作。
## 首先来介绍ContentProvier
####uri 
Uri指定了将要操作的ContentProvider，其实可以把一个Uri看作是一个网址，我们把Uri分为三部分。
* 第一部分是"content://"。可以看作是网址中的"http://"。
* 第二部分是主机名或authority，用于唯一标识这个ContentProvider，外部应用需要根据这个标识来找到它。可以看作是网址中的主机名，比如"blog.csdn.net"。
* 第三部分是路径名，用来表示将要操作的数据。可以看作网址中细分的内容路径。

####ContentProvider
Android提供了一些主要数据类型的ContentProvider，比如音频、视频、图片和私人通讯录等。可在android.provider包
下面找到一些Android提供的ContentProvider。通过获得这些ContentProvider可以查询它们包含的数据，当然前提是已获
得适当的读取权限。

* public boolean onCreate() 在创建ContentProvider时调用
* public Cursor query(Uri, String[], String, String[], String) 用于查询指定Uri的ContentProvider，返回一个Cursor
* public Uri insert(Uri, ContentValues) 用于添加数据到指定Uri的ContentProvider中
* public int update(Uri, ContentValues, String, String[]) 用于更新指定Uri的ContentProvider中的数据
* public int delete(Uri, String, String[]) 用于从指定Uri的ContentProvider中删除数据
* public String getType(Uri) 用于返回指定的Uri中的数据的MIME类型
* 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头。
例如：要得到所有person记录的Uri为content://contacts/person，那么返回的MIME类型字符串为
"vnd.android.cursor.dir/person"。
* 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开头。
例如：要得到id为10的person记录的Uri为content://contacts/person/10，那么返回的MIME类型字符串应
为"vnd.android.cursor.item/person"。


直接上代码：本例子，采用内容提供者共享sqlite的数据库。
```java
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

```

####ContentResolver 
当外部应用需要对ContentProvider中的数据进行添加、删除、修改和查询操作时，可以使用ContentResolver类来完成，
要获取ContentResolver对象，可以使用Context提供的getContentResolver()方法。


      ContentResolver cr = getContentResolver();  
      
      ContentResolver提供的方法和ContentProvider提供的方法对应的有以下几个方法。
      public Uri insert(Uri uri, ContentValues values) 用于添加数据到指定Uri的ContentProvider中。
      public int delete(Uri uri, String selection, String[] selectionArgs) 用于从指定Uri的ContentProvider中删除数据。
      public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 用于更新
指定Uri的ContentProvider中的数据。
      public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
用于查询指定Uri的ContentProvider。

```java
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
                       Toast.makeText(getApplicationContext(), "名字:"+name+"--"+"作者:"+author, Toast.LENGTH_LONG).show();
                        
                    }
                }
                
            }
        });
    }

   
}


```

# 本例中用了SqliteDatabase
介绍下sqlitedatabase

####SQLiteOpenHelper

该类是SQLiteDatabase一个辅助类。这个类主要生成一  个数据库，并对数据库的版本进行管理。当在程序当中调用这个类的方法getWritableDatabase()或者 getReadableDatabase()方法的时候，如果当时没有数据，那么Android系统就会自动生成一个数据库。 SQLiteOpenHelper 是一个抽象类，我们通常需要继承它，并且实现里面的3个函数：

      1.onCreate（SQLiteDatabase）

在数据库第一次生成的时候会调用这个方法，也就是说，只有在创建数据库的时候才会调用，当然也有一些其它的情况，一般我们在这个方法里边生成数据库表。

      2.  onUpgrade（SQLiteDatabase，int，int） 
当数据库需要升级的时候，Android系统会主动的调用这个方法。一般我们在这个方法里边删除数据表，并建立新的数据表，当然是否还需要做其他的操作，完全取决于应用的需求。

      3.  onOpen（SQLiteDatabase）：

```java
public class MyDatabaseHelper extends SQLiteOpenHelper{
    
    private Context mContext;
    public  static final String CREATE_BOOK="create table Book(id integer primary key autoincrement , author text, price real ,pages integer ,name text)";
    public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        //Toast.makeText(mContext, "create succeeded", Toast.LENGTH_SHORT).show();
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }

}

```

####SQLiteDatabase 的CURD
*  sqliteHelper 的getWritableDatabase()或者getReadableDatabase 用于创建或者升级数据库，不仅如此
还会返回sqliteDatabase对象。
* 增加。只需insert方法，需要三个参数，第一个参数表名，第二个参数一般用不上直接null，第三个参数ContentValues对象
* 更新。update()
* 删除 delete （）
* 查询 query() 返回Cursor对象   String name=cursor.getString(cursor.getColumnIndex("name"));  while(cursor.hasNext()){}
  
*  直接用execSql();
*  db.rawQuery("select * from Book",null);


```java

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

```

