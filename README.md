# contentProvider

* ��׿�Ĵ����֮һContentProvider Ӧ�÷ǳ��㷺��ֻҪ���ڶ�ȡϵͳ����ϵ�ˡ�ͼ��ȣ�����ϵͳ�����ݷǳ��򵥣�
ֻ��Ҫϵͳ��ϵͳ���ṩ��URI�����ɶ�ȡ���ݡ�
* ��ʵ�ʵĿ�����ContentProvider��������ϵͳ�����⣬��������Ӧ�ó�������ݱȽ��ټ���

##���
�����ṩ�ߣ���ҪӦ�������ݹ������ṩ�����˻��ƣ���֤�˹������ݵİ�ȫ�ԡ�

####uri 
Uriָ���˽�Ҫ������ContentProvider����ʵ���԰�һ��Uri������һ����ַ�����ǰ�Uri��Ϊ�����֡�
��һ������"content://"�����Կ�������ַ�е�"http://"��
�ڶ���������������authority������Ψһ��ʶ���ContentProvider���ⲿӦ����Ҫ���������ʶ���ҵ��������Կ�������ַ�е�������������"blog.csdn.net"��
����������·������������ʾ��Ҫ���������ݡ����Կ�����ַ��ϸ�ֵ�����·����

####ContentProvider
Android�ṩ��һЩ��Ҫ�������͵�ContentProvider��������Ƶ����Ƶ��ͼƬ��˽��ͨѶ¼�ȡ�����android.provider��
�����ҵ�һЩAndroid�ṩ��ContentProvider��ͨ�������ЩContentProvider���Բ�ѯ���ǰ��������ݣ���Ȼǰ�����ѻ�
���ʵ��Ķ�ȡȨ�ޡ�

public boolean onCreate() �ڴ���ContentProviderʱ����
public Cursor query(Uri, String[], String, String[], String) ���ڲ�ѯָ��Uri��ContentProvider������һ��Cursor
public Uri insert(Uri, ContentValues) �����������ݵ�ָ��Uri��ContentProvider��
public int update(Uri, ContentValues, String, String[]) ���ڸ���ָ��Uri��ContentProvider�е�����
public int delete(Uri, String, String[]) ���ڴ�ָ��Uri��ContentProvider��ɾ������
public String getType(Uri) ���ڷ���ָ����Uri�е����ݵ�MIME����
*����������������ڼ������ͣ���ôMIME�����ַ���Ӧ����vnd.android.cursor.dir/��ͷ��
���磺Ҫ�õ�����person��¼��UriΪcontent://contacts/person����ô���ص�MIME�����ַ���Ϊ
"vnd.android.cursor.dir/person"��
*���Ҫ�������������ڷǼ����������ݣ���ôMIME�����ַ���Ӧ����vnd.android.cursor.item/��ͷ��
���磺Ҫ�õ�idΪ10��person��¼��UriΪcontent://contacts/person/10����ô���ص�MIME�����ַ���Ӧ
Ϊ"vnd.android.cursor.item/person"��


ֱ���ϴ��룺�����ӣ����������ṩ�߹���sqlite�����ݿ⡣
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
���ⲿӦ����Ҫ��ContentProvider�е����ݽ������ӡ�ɾ�����޸ĺͲ�ѯ����ʱ������ʹ��ContentResolver������ɣ�
Ҫ��ȡContentResolver���󣬿���ʹ��Context�ṩ��getContentResolver()������

[java] view plain copy
ContentResolver cr = getContentResolver();  

ContentResolver�ṩ�ķ�����ContentProvider�ṩ�ķ�����Ӧ�������¼���������
public Uri insert(Uri uri, ContentValues values) �����������ݵ�ָ��Uri��ContentProvider�С�
public int delete(Uri uri, String selection, String[] selectionArgs) ���ڴ�ָ��Uri��ContentProvider��ɾ�����ݡ�
public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) ���ڸ���
ָ��Uri��ContentProvider�е����ݡ�
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
���ڲ�ѯָ��Uri��ContentProvider��

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
                       Toast.makeText(getApplicationContext(), "����:"+name+"--"+"����:"+author, Toast.LENGTH_LONG).show();
                        
                    }
                }
                
            }
        });
    }

   
}


```

# ����������SqliteDatabase
������sqlitedatabase

####SQLiteOpenHelper

������SQLiteDatabaseһ�������ࡣ�������Ҫ����һ  �����ݿ⣬�������ݿ�İ汾���й��������ڳ����е��������ķ���getWritableDatabase()���� getReadableDatabase()������ʱ�������ʱû�����ݣ���ôAndroidϵͳ�ͻ��Զ�����һ�����ݿ⡣ SQLiteOpenHelper ��һ�������࣬����ͨ����Ҫ�̳���������ʵ�������3��������

1.onCreate��SQLiteDatabase��

�����ݿ��һ�����ɵ�ʱ���������������Ҳ����˵��ֻ���ڴ������ݿ��ʱ��Ż���ã���ȻҲ��һЩ�����������һ�������������������������ݿ����

2.  onUpgrade��SQLiteDatabase��int��int�� 
�����ݿ���Ҫ������ʱ��Androidϵͳ�������ĵ������������һ������������������ɾ�����ݱ����������µ����ݱ�����Ȼ�Ƿ���Ҫ�������Ĳ�������ȫȡ����Ӧ�õ�����

3.  onOpen��SQLiteDatabase����

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

####SQLiteDatabase ��CURD
*  sqliteHelper ��getWritableDatabase()����getReadableDatabase ���ڴ��������������ݿ⣬�������
���᷵��sqliteDatabase����
* ���ӡ�ֻ��insert��������Ҫ������������һ�������������ڶ�������һ���ò���ֱ��null������������ContentValues����
* ���¡�update()
* ɾ�� delete ����
* ��ѯ query() ����Cursor����   String name=cursor.getString(cursor.getColumnIndex("name"));  while(cursor.hasNext()){}
  
*  ֱ����execSql();
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
