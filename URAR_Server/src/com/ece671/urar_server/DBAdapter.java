package com.ece671.urar_server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DBAdapter {  
    public static final String KEY_ROWID = "_id";
    public static final String KEY_PORT = "Victim_id";  
    public static final String KEY_Location = "Location";  
    public static final String KEY_FLAG = "Flag";  
    //static final String TAG = "DBAdapter";  
  
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
			db.execSQL("CREATE TABLE "
			           + DATABASE_TABLE + "("
                       + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
                       + KEY_PORT + " TEXT NOT NULL , "
                       + KEY_Location + " TEXT NOT NULL , "
                       + KEY_FLAG + " TEXT NOT NULL );"
                       );
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXIST " + DATABASE_TABLE );
			onCreate(db);
			
		}
    	
    }
    
    public DBAdapter (Context c) {
    	
    	Victimcontext = c;
	}
    //open database
    public DBAdapter open() throws SQLException
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
	public long createntry(String Victim_id, String location) {
		// TODO Auto-generated method stub
		
		ContentValues VictimValues = new ContentValues();
		VictimValues.put(KEY_PORT, Victim_id);
		VictimValues.put(KEY_Location, location);
		return VictimDatabase.insert(DATABASE_TABLE, null, VictimValues);
	}
	//get all data from database
	public String getalldata() {
		// TODO Auto-generated method stub
		String [] VI = new String[]{KEY_ROWID,KEY_PORT,KEY_Location};
		String result = "";
		Cursor c = VictimDatabase.query(DATABASE_TABLE, VI, null, null, null, null, null);
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iVictim_id = c.getColumnIndex(KEY_PORT);
		int iLocation = c.getColumnIndex(KEY_Location);
		
		
		for(c.moveToFirst();!c.moveToLast();c.moveToNext())
		{
			result = result + c.getString(iRow) + ", " + c.getString(iVictim_id) + ", " + c.getString(iLocation) + "\n";
		}
		
		return result;
	}
	// get a pointed data from database
	public String getpoineddata(String Victim_id){
		
		String [] VI = new String[]{KEY_ROWID,KEY_PORT,KEY_Location};
		String result = "";
		Cursor userport = VictimDatabase.query(DATABASE_TABLE, VI,null, null, null, null, null);
		int iRow = userport.getColumnIndex(KEY_ROWID);
		int iVictim_id = userport.getColumnIndex(KEY_PORT);
		int iLocation = userport.getColumnIndex(KEY_Location);
		
		
		for(userport.moveToFirst();!userport.moveToLast();userport.moveToNext())
		{
			if(userport.getString(iVictim_id).equals(Victim_id))
			{
				result = result + userport.getString(iRow) + ", " + userport.getString(iVictim_id) + ", " + userport.getString(iLocation);
			}
		}
		
		
		return result;
		
	}
	
	//Edit info
	public void updateInfo(String Victim_id, String location)
	{
	  ContentValues newVictimValues = new ContentValues();	
	  newVictimValues.put(KEY_PORT,Victim_id);
	  newVictimValues.put(KEY_Location, location);
	  VictimDatabase.update(DATABASE_TABLE, newVictimValues,KEY_PORT + "=" + Victim_id, null);
	}
	
	
	
}