package com.ece671.urar_server;



import com.ece671.urar_server.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class Show_databse extends Activity{
	
private ListView dataBaseListView;
TextView showdata;	
	

protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_database);
		showdata = (TextView)findViewById(R.id.show_database);
		
		DB_Responder showalldata = new DB_Responder(Show_databse.this);
		showalldata.open();
	    Log.d("Read Database", "Open");
		String VictimInfo = showalldata.getalldata();
		Log.d("EditActivity_Read data", VictimInfo);
		showalldata.close();
		 Log.d("Read Database", "Close");
		showdata.setText(VictimInfo);
		
		
		
	}

}
