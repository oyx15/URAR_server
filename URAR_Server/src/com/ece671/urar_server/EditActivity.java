package com.ece671.urar_server;


import com.ece671.urar_server.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends Activity{

	EditText victim_number;
	EditText victim_level;
	EditText victim_id;
	TextView victim_lat;
	TextView  victim_long;
	Button add;
	Button delete;
	Button show;
	public static  String VICTIM_NUMBER = "vn";
	public static  String VICTIM_LEVEL = "vl";
	public static  String VICTIM_ID = "vi";
	public static  String Tag = "tag";
	public String Location = "vic_loc";
	public static final String BROADCADT_Location = "broadcadt_location";
	public static final String Bundle_location = "Info";
	protected void onCreate(Bundle savedInstanceState)
	{ 
		getIntent();
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.change_info);
		Log.d("EditActivity","EditActivity Start");
		victim_number = (EditText)findViewById(R.id.input_vitim_number);
	    victim_level = (EditText)findViewById(R.id.input_victim_level);
	    victim_id = (EditText)findViewById(R.id.input_victim_id);
	    victim_lat = (TextView)findViewById(R.id.input_victim_latitude);
	    victim_long = (TextView)findViewById(R.id.input_victim_longtitude);
	    add = (Button)findViewById(R.id.victim_add);
	    delete = (Button)findViewById(R.id.victim_delete);
	    show = (Button)findViewById(R.id.Show_database);
	    add.setOnClickListener(myaddListener);
	    delete.setOnClickListener(mydeleteListener);
	    show.setOnClickListener(myshowListener);
	    Bundle getBundle= getIntent().getExtras();
	    String PoVictim = getBundle.getString(MainActivity.VICLOC);
	    Log.d("EditActivity_Povictim",PoVictim);
	    String[] PoVIF = PoVictim.split(",");
	    Log.d("Editactivity_length","" + PoVIF.length);
	    switch(PoVIF.length){  
	    case 2:
	    	Location = PoVictim;
	    	String[] poVictim_locStrings = Location.split(",");
	    	victim_lat.setText(poVictim_locStrings[0]);
	    	victim_long.setText(poVictim_locStrings[1]);
	    	break;
	    default:
	    	Location = PoVIF[1] + "," + PoVIF[2];
	    	Log.d("EditActivity",PoVIF[0]);
	    	Log.d("EditActivity",PoVIF[1]);
	    	Log.d("EditActivity",PoVIF[2]);
	    	Log.d("EditActivity",PoVIF[3]);
	    	Log.d("EditActivity",PoVIF[4]);
	    	Log.d("EditActivity",PoVIF[5]);
	    	String[] PoVIF5 = PoVIF[5].split(";");   			
	    	victim_id.setText(PoVIF[0]);
	    	victim_level.setText(PoVIF[4]);
	    	victim_number.setText(" "+PoVIF5[0]);
	    	victim_lat.setText(PoVIF[1]);
	    	victim_long.setText(PoVIF[2]);
	    	break;
	    }
	     
	}
	
	OnClickListener myaddListener = new OnClickListener(){
    	public void onClick(View v){
    		Log.d("EditActivity", "Start");
    		String tag = "" + 0;
        	String Vic_num = victim_number.getText().toString();
        	String Vic_lev = victim_level.getText().toString();
        	String Vic_id = victim_id.getText().toString();
        	Log.d("Vic_Info", Vic_num);
        	Log.d("Vic_Info", Vic_lev);
        	Log.d("Vic_Info", Vic_id);
        	
        	Bundle myBundle = new Bundle();
        	myBundle.putString(VICTIM_NUMBER, Vic_num);
        	myBundle.putString(VICTIM_LEVEL, Vic_lev);
        	myBundle.putString(VICTIM_ID, Vic_id);
        	myBundle.putString(Tag, tag);
        	
        	String Victim_id = Vic_id ;
        	String location = Location; 
        	String level = Vic_lev;
        	String numbers = Vic_num;
    		  //get from showlocation
    		Log.d("Vic_Loc", location);
    		
    		//insert data to database
    		if (Victim_id != null)
    		{
    		DB_Responder write = new DB_Responder(EditActivity.this);
    		Log.d("Open Database", "open");
    		write.open();
    		Log.d("Open Database", "Succeed");
    		write.createntry(Victim_id, location, tag, level, numbers);
    		write.close();
    		Log.d("Close Database", "Succeed");
    		}
        	
    		
    		String info = Victim_id +"," + location + "," +tag+ "," +level;
    		//read all data
    		DB_Responder readall = new DB_Responder(EditActivity.this);
    	    readall.open();
    	    Log.d("Read Database", "Open");
    		String VictimInfo = readall.getalldata();
    		Log.d("EditActivity_Read data", VictimInfo);
    		readall.close();
    		 Log.d("Read Database", "Close");
    		
    		// read a pointed data
    		DB_Responder readpointed = new DB_Responder(EditActivity.this);
    		readpointed.open();
    	    Log.d("Read Database_pointed", "Open");
    		String PointedInfo = readpointed.getpoineddata(Victim_id);
    		Log.d("EditActivity_Read data_a_pointe id", PointedInfo);
    		readpointed.close();
    		
    		//update a data
    		DB_Responder updatepointed = new DB_Responder(EditActivity.this);
    		updatepointed.open();
    	    Log.d("Update Database_pointed", "Open");
    		updatepointed.updateInfo(Victim_id, location, tag, level, numbers);
    		Log.d("EditActivity_update_a_pointe id", "done");
    		updatepointed.close();
    		

    		
    		Intent intent = new Intent(BROADCADT_Location);
			intent.putExtra(Bundle_location, info);
			Log.d("EditActivity","Sending Broadcast");
			sendBroadcast(intent);
			finish();	
    	}
    	};
    	
    OnClickListener mydeleteListener = new OnClickListener(){
    	public void onClick(View v){
    		String  tag = "" + 1; 
    		String Vic_num = victim_number.getText().toString();
    		String Vic_lev = victim_level.getText().toString();
    		String Vic_id = victim_id.getText().toString();
       
    		Bundle myBundle = new Bundle();
    		myBundle.putString(VICTIM_NUMBER, Vic_num);
    		myBundle.putString(VICTIM_LEVEL, Vic_lev);
    		myBundle.putString(VICTIM_ID, Vic_id);
    		myBundle.putString(Tag, tag);
      
    		String Victim_id = Vic_id ;
    		//String location = Location;
    		String level = Vic_lev;
        	String numbers = Vic_num;
    		String location = Location; 
     
    		if (Victim_id != null)
    		{
    			//update a data
        		DB_Responder updatepointed = new DB_Responder(EditActivity.this);
        		updatepointed.open();
        	    Log.d("Update Database_pointed", "Open");
        		updatepointed.updateInfo(Victim_id, location, tag, level, numbers);
        		Log.d("EditActivity_update_a_pointe id", "done");
        		updatepointed.close();
    		}
	        //read a pointed data
    		DB_Responder readpointed = new DB_Responder(EditActivity.this);
    		readpointed.open();
    	    Log.d("Read Database_pointed_delete", "Open");
    		String PointedInfo = readpointed.getpoineddata(Victim_id);
    		Log.d("EditActivity_Read data_a_pointe id_delete", PointedInfo);
    		readpointed.close();
    		//broadcast
    		Log.d("Broadcast_location",location);
    		String info = Victim_id +"," + location + "," + tag+ "," +level ;
    		Intent intent = new Intent(BROADCADT_Location);
			intent.putExtra(Bundle_location, info);
			Log.d("EditActivity","Sending Broadcast");
			sendBroadcast(intent);
	

    		finish();
    	}
    };
    
    OnClickListener myshowListener = new OnClickListener(){
    	public void onClick(View v){
    		Intent intent = new Intent(getApplicationContext(),Show_databse.class);
    		startActivity(intent);
    	}
    };
    	
}
