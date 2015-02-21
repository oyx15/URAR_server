package com.ece671.urar_server;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DB_Responder {  
    public static final String KEY_ROWID = "_id";
    public static final String KEY_VICTIMID = "Victim_id";  
    public static final String KEY_Location = "Location";  
    public static final String KEY_Level = "Level";  
    public static final String KEY_Numbers = "Numbers";  
    public static final String KEY_FLAG = "Flag";  
    //static final String TAG = "DB_Responder";  
  
    public static final String DATABASE_NAME = "Victim_DB";  
    public static final String DATABASE_TABLE = "Victim_TB";  
    static final int DATABASE_VERSION = 1;  
    private DBHelper Victimhelper;
    private final Context Victimcontext;
    private SQLiteDatabase VictimDatabase;
    
    
    private static class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			//db.execSQL("DROP TABLE IF EXIST " + DATABASE_TABLE );
			Log.d("DB_Responder_drop","Drop table");
			db.execSQL("CREATE TABLE "
			           + DATABASE_TABLE + "("
                       //+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
                       + KEY_VICTIMID + " INTEGER PRIMARY KEY , "
                       + KEY_Location + " TEXT NOT NULL , "
                       + KEY_FLAG + " TEXT NOT NULL , "
                       + KEY_Level + " TEXT NOT NULL , "
                       + KEY_Numbers + " TEXT NOT NULL ); "
                       );
		
			Log.d("DB_Responder_creat","creat table");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXIST " + DATABASE_TABLE );
			onCreate(db);
			
		}
    	
    }
    
    public DB_Responder (Context c) {
    	
    	Victimcontext = c;
	}
    //open database
    public DB_Responder open() throws SQLException
    {
    	Victimhelper = new DBHelper(Victimcontext);
    	VictimDatabase = Victimhelper.getWritableDatabase();
    	return this;
      }
    //close database
    public void close(){
    	Victimhelper.close();
    }
    //add info to database
	public long createntry(String Victim_id, String location, String tag, String level, String numbers) {
		// TODO Auto-generated method stub
		
		ContentValues VictimValues = new ContentValues();
		VictimValues.put(KEY_VICTIMID, Victim_id);
		VictimValues.put(KEY_Location, location);
		VictimValues.put(KEY_FLAG, tag);
		Log.d("DB_responder_insert_id", Victim_id);
		Log.d("DB_responder_insert_location", location);
		Log.d("DB_responder_insert_tag", tag);
		VictimValues.put(KEY_Level, level);
		VictimValues.put(KEY_Numbers, numbers);
		return VictimDatabase.insert(DATABASE_TABLE, null, VictimValues);
	}
	//get all data from database
	public String getalldata() {
		// TODO Auto-generated method stub
		String [] VI = new String[]{KEY_VICTIMID,KEY_Location,KEY_FLAG,KEY_Level,KEY_Numbers};
		String result = "";
		Cursor c = VictimDatabase.query(DATABASE_TABLE, VI, null, null, null, null, null);
		//int iRow = c.getColumnIndex(KEY_ROWID);
		//Log.d("DB_RESPONDER", "" + iRow);
		Log.d("DB_RESPONDER", ""+c.getColumnCount());
		Log.d("DB_RESPONDER_getcount", ""+c.getCount());
		int iVictim_id = c.getColumnIndex(KEY_VICTIMID);
		int iLocation = c.getColumnIndex(KEY_Location);
		int iFlag = c.getColumnIndex(KEY_FLAG);
		int iLevel = c.getColumnIndex(KEY_Level);
		int iNumbers = c.getColumnIndex(KEY_Numbers);
		//int iLevel = c.getColumnIndex(KEY_Level);
		//int iNumbers = c.getColumnIndex(KEY_Numbers);
		//Log.d("DB_Responder", c.getString(iRow));
		c.moveToFirst();
		result = result + c.getString(iVictim_id) + ", " + c.getString(iLocation) + ", "
				+c.getString(iFlag) + ", " + c.getString(iLevel) + ", " + c.getString(iNumbers)+";" +"\n";
		for(int i =1; i < c.getCount(); i++)
		//for(c.moveToFirst();!c.moveToLast();c.moveToNext())
		{
			c.moveToNext();
			result = result  + c.getString(iVictim_id) + ", " + c.getString(iLocation) + ", "
			+c.getString(iFlag) + ", " + c.getString(iLevel) + ", " + c.getString(iNumbers)+";" +"\n";
			//Log.d("DB_Responder_for", result);	
		}
		
		Log.d("DB_Responder", "Read all data");
		Log.d("DB_Responder", result);
		return result;
	}
	// get a pointed data from database
	public String getpoineddata(String Victim_id){
		
		String [] VI = new String[]{KEY_VICTIMID,KEY_Location,KEY_FLAG,KEY_Level,KEY_Numbers};
		String result = "";
		Cursor userport = VictimDatabase.query(DATABASE_TABLE, VI,KEY_VICTIMID + "=" + Victim_id, null, null, null, null);
        //int iRow = userport.getColumnIndex(KEY_ROWID);
		int iVictim_id = userport.getColumnIndex(KEY_VICTIMID);
		int iLocation = userport.getColumnIndex(KEY_Location);
		int iFlag = userport.getColumnIndex(KEY_FLAG);
		int iLevel = userport.getColumnIndex(KEY_Level);
		int iNumbers = userport.getColumnIndex(KEY_Numbers);
		
		if(userport.moveToFirst())
		{
				result = result +  userport.getString(iVictim_id) + ", " + userport.getString(iLocation)+", " 
						+ userport.getString(iFlag) + ", " + userport.getString(iLevel) + "," 
						+ userport.getString(iNumbers) + ";" + "\n";	
		}
	    Log.d("Find poined data", result);
		return result;
	}
	
	//Edit info
	public void updateInfo(String Victim_id, String location, String tag, String level, String numbers)
	{
	  ContentValues newVictimValues = new ContentValues();	
	  newVictimValues.put(KEY_VICTIMID,Victim_id);
	  newVictimValues.put(KEY_Location, location);
	  newVictimValues.put(KEY_FLAG, tag);
	  newVictimValues.put(KEY_Level, level);
	  newVictimValues.put(KEY_Numbers, numbers);
	  VictimDatabase.update(DATABASE_TABLE, newVictimValues,KEY_VICTIMID + "=" + Victim_id, null);
	}
	
	
	
}

  
 