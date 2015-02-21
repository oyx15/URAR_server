package com.ece671.urar_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import ece671.reportserver.Dijkstra;
import ece671.reportserver.Edge;
import ece671.reportserver.EditActivity;
import ece671.reportserver.GMapV2Direction;
import ece671.reportserver.GetDirectionsAsyncTask;
import ece671.reportserver.Landmarks;
import ece671.reportserver.MainActivity;
import ece671.reportserver.MyPoint;
import ece671.reportserver.R;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.common.collect.ImmutableSet;

import android.R.integer;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements IOnLandmarkSelectedListener, BeaconConsumer, LocationListener{
	
	
	//Parameters for googleMap
	public static int landmarknum;
	private GoogleMap mMap;
	String mUrl = "http://percept.ecs.umass.edu/course/marcusbasement/{z}/{x}/{y}.png";
	
	double marcusLat = 42.393985;
	double marcusLng = -72.528622;
	int knowlesZoom = 25;
	
	public int currentMode;

	public final String ACTION_VIEW_MAP = "Overview Map";
	public final String ACTION_ADD_LANDMARKS = "Add Landmarks";
	public final String ACTION_REMOVE_LANDMARKS = "Remove Landmarks";

	public final int MODE_VIEW = 0;
	public final int MODE_ADD_LANDMARK = 1;
	public final int MODE_GENERATE_PATH = 2;
	public final int MODE_REMOVE_LANDMARK = 3;
	public final int MODE_SELECT_PATH = 4;

	public String title = "Title ";
	public int titleNumber = 0;
	private IOnLandmarkSelectedListener landmarkListener;
	public Uri imageUri;
	private Landmarks landmarks;	
	private Activity activity;
	
	//Parameters for iBeacon
	/** A set of valid Gimbal beacon identifier */
	private final Set<String> validGimbalIdentifiers = ImmutableSet.of("00100001", "10011101");

	/** Log for TagSearchingActivity. */
	private static final String TAG_SEARCHING_ACTIVITY_LOG = "TAG_SEA_ACT_LOG";

	private List<Beacon> discoveredBeaconList;
	public List<Beacon> strongestBeaconList;
	public String[] bcnTitle = new String[17];
	public Double[] bcnLat = new Double[17];
	public Double[] bcnLong = new Double[17];
	public double myLatitude = 0;
	public double myLongitude = 0;
	public Marker myMarker;
	public Marker infoMarker;
	public Marker[]  VicMarker = null;
	public int MarkerIndex = 0;
	Button edit_victim;
	Button myCurrentLoc;
	//parameters for client
	public String	AccidentType;
    int n = 0;
    public Marker VictimMarker;
    public int accident_type=1;
    public int level = 2;
    public int othervictim = 3;
    public int latitudeIndex = 4;
    public int longitutdeIndex = 5;
    public int flagindex = 6;
    public String[] VictimArray;
    public String OtherVictim;
    public String Level;
    public String Victiminformation;
    public String  Location;
    public String Flag;
    //parameters for responder
    public String victim_number;
    public String victim_level;
    public String victim_id;
    public String tag;
    public String vicloc;
    public static String VICLOC = "vicloc";
    private ServerSocket serverSocket;
	// designate a port
	public static final int SERVERPORT = 9096;
	Thread threadSocketServer = new Thread(new ServerThread());
	LocationReceiver locationReceiver;
	
	public Marker marker;
	public ArrayList<LatLng> nodeList = new ArrayList<LatLng>();
	public ArrayList<LatLng> wayList = new ArrayList<LatLng>();
	public ArrayList<MyPoint> source = new ArrayList<MyPoint>();
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	private Polyline newPolyline;
	boolean setFlag = false;
	boolean vicFlag = false;
	public LatLng myLocation;
	public LatLng vicLocation;
	private Point point1;
	private int width, height;
	Circle myCircle = null;
	Circle vicCircle = null;
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	boolean gmapFlag = false;
	boolean indoorFlag = true;
	
	/** The map used for storing discovered beacons */
	protected HashMap<String, Beacon> discoveredBeaconMap;
	/** Declare and initiate the a BeaconManager object.*/
	private BeaconManager beaconManager = BeaconManager
			.getInstanceForApplication(this);
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edit_victim =(Button) findViewById(R.id.Change_Info);
		edit_victim.setOnClickListener(myeditListener);
		myCurrentLoc =(Button) findViewById(R.id.myLocation);
		myCurrentLoc.setOnClickListener(myLocationListener);
		try {
            landmarkListener = (IOnLandmarkSelectedListener) this;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
		
		discoveredBeaconMap = new HashMap<String, Beacon>();
		discoveredBeaconList = new ArrayList<Beacon>();
		strongestBeaconList = new ArrayList<Beacon>();	
		setupMap();
		Log.d("setup","set up map done");
		getNode();//set up nodeList, source and edge
		getSreenDimanstions();
		
		IntentFilter locationFilter;
	    locationFilter = new  IntentFilter(EditActivity.BROADCADT_Location);
	    locationReceiver = new LocationReceiver();
	    registerReceiver(locationReceiver, locationFilter);
	    threadSocketServer.start();
		
	    //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
	    
	}
	
	private OnClickListener myLocationListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//setFlag = false;
			indoorFlag = !indoorFlag;
			//indoorFlag = true;
		}
		
	};
	
	public class LocationReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
		    Log.d("Showlocation","Broadcast Received");
			Bundle bundle = intent.getExtras();
		     vicloc = bundle.getString(EditActivity.Bundle_location);
		    Log.d("Broadcast received_info",vicloc);
		    String viclocationString = vicloc;
		     //Log.d("ADD_INFO",viclocationString);
		    
		     if (viclocationString != null)
		     {
		    	 String[] addvic = viclocationString.split(",");
		    	 String addId = addvic[0];
		    	 String addLat = addvic[1];
		    	 String addLon = addvic[2];
		    	 String addtag = addvic[3];
		    	 String addlevel = addvic[4];
		    	 //String vicloc = addLat + "," +addLon;
		    	// handlerResponder.sendMessage(Message.obtain(handlerResponder,0,viclocationString));
		    	 
		    	 
		    	 if(addtag.equals("0"))
		    	 {
		    		 LatLng position1 = new LatLng(Double.parseDouble(addLat), Double.parseDouble(addLon));
		    		 if(addlevel.equals("1"))
		    		 {
		    			 infoMarker = mMap.addMarker(new MarkerOptions().position(position1).title(addId).draggable(true)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
							landmarks.addMarker(addId, infoMarker);
		    		 }
		    		 else if(addlevel.equals("2"))
		    		 {
		    		 infoMarker = mMap.addMarker(new MarkerOptions().position(position1).title(addId).draggable(true)
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
					landmarks.addMarker(addId, infoMarker);
		    		 }
		    		 else {
		    			 infoMarker = mMap.addMarker(new MarkerOptions().position(position1).title(addId).draggable(true)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
							landmarks.addMarker(addId, infoMarker);
					}
					Toast.makeText(getApplicationContext(), "Add Marker!!!" ,Toast.LENGTH_SHORT).show();
					Log.d("Creat Marker[]", "" +MarkerIndex);
					String infoMarker_id = infoMarker.getId();
					Log.d("Get infoMarker_id",infoMarker_id);
		    	 }
		    	 else {
		    		//infoMarker.remove();
		    		
		    		landmarks.removeMarker(addId);
                    
					Log.d("Delete Marker", "Succeed");
				}
		     }
		}
    	
    }
	
	OnClickListener myeditListener = new OnClickListener(){
    	public void onClick(View v){
    		
    		String myLat = Double.toString(myLatitude);
    		String myLon = Double.toString(myLongitude);
    		String location = myLat + "," + myLon;
    		EditVictim(location);		
    	}
    };
	
    public class ServerThread implements Runnable{
		 boolean flag = true;
        
		@Override
		public void run() {
			Log.d("MainActivity","Thread is running");
			try {
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while ( flag ){
			beginSocketCommunication();	
			 n = n+1;
			//Log.d("Thread runtime",""+n);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}	 
	 }
	 
	private void beginSocketCommunication(){
		try {
			
			Log.d("MainActivity","Communication is running");
			//serverSocket = new ServerSocket(SERVERPORT);
			if(serverSocket.accept() != null)
			{
				Socket client = serverSocket.accept();
				BufferedReader in = new BufferedReader( new InputStreamReader(client.getInputStream()));
				Victiminformation = in.readLine();
				Log.d("beginSocketCommunication","communications begin");
				Log.d("Communication Information",Victiminformation);
				if (Victiminformation != null)
				{
					VictimArray = Victiminformation.split("@");	
					AccidentType=VictimArray[accident_type];
			
					OtherVictim = VictimArray[othervictim];
					Level = VictimArray[level];
					Flag = VictimArray[flagindex];
					double VictimLat = Double.parseDouble(VictimArray[latitudeIndex]);
					double VictimLong = Double.parseDouble(VictimArray[longitutdeIndex]);
					Location = "" + VictimLat + "," + VictimLong;
					
					Log.d("Communication accident_type",AccidentType);
					Log.d("Communication OtherVictim",OtherVictim);
					Log.d("Communication Level",Level);
					Log.d("Comunication Location",Location);
					Log.d("Comunication Flag",Flag);
					
					// Edit victim info in database
					String Victim_id = "" + SERVERPORT;
					String location = Location;
					String tag = Flag;
					String numbers = "" + 0;
					if (Flag.equals("0"))
					{
					//write data to database
					
					DB_Responder write = new DB_Responder(MainActivity.this);
					Log.d("Mainactivity_Open Database", "open");
					write.open();
					Log.d("Mainactivity_Open Database", "Succeed");
					write.createntry(Victim_id, location, tag, Level, numbers);
					write.close();
					Log.d("Mainactivity_Close Database", "Succeed");
					}
					else {
						DB_Responder updatepointed = new DB_Responder(MainActivity.this);
			    		updatepointed.open();
			    	    Log.d("MainActivity_Update Database_pointed", "Open");
			    		updatepointed.updateInfo(Victim_id, location, tag, Level, numbers);
			    		Log.d("MainActivity_update_a_pointe id", "done");
			    		updatepointed.close();
					}
					

					if (Level.equals("0"))
					{
						if (OtherVictim.equals("0"))
						{
	
							handlermarker.sendMessage(Message.obtain(handlermarker,0,Location+ "," + Flag));
						}
						else
						{
							handlermarker.sendMessage(Message.obtain(handlermarker,1,Location + "," + Flag));
						}
				
					}
					else if (Level.equals("1"))
					{
						handlermarker.sendMessage(Message.obtain(handlermarker,2,Location+ "," + Flag));
					}
					else
					{
						handlermarker.sendMessage(Message.obtain(handlermarker,3,Location+ "," + Flag));
					}
					Arrays.fill(VictimArray, null);
					Log.d("VictimArray","ClearArray");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Socket",e.toString());
			e.printStackTrace();
		}
		
		
	}
	
	Handler handlermarker =new Handler(){
		public void handleMessage(Message msg) {
			
			String loc=(String) msg.obj;
			String[] VictimLocation = loc.split(",");
			String Victim_id = "" + SERVERPORT;
			int victimlatitude = 0;
			int victimlongtitude = 1;
			int victimflag = 2;
			double Lat = Double.parseDouble(VictimLocation[victimlatitude]);
			double Long = Double.parseDouble(VictimLocation[victimlongtitude]);
			LatLng position = new LatLng(Lat,Long);
			String Victimflag = VictimLocation[victimflag];
			Toast.makeText(getApplicationContext(), "Emergency!!!",Toast.LENGTH_SHORT).show();
			
			if(Victimflag.equals("0"))
			{
				if(VictimMarker != null)
				{
				VictimMarker.remove();
				}
				if(msg.what == 0)
				{
				
				VictimMarker = mMap.addMarker(new MarkerOptions().position(position).title(Victim_id).draggable(true)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				landmarks.addMarker(Victim_id, VictimMarker);
				}	
				if(msg.what == 1)
				{
				
				VictimMarker = mMap.addMarker(new MarkerOptions().position(position).title(Victim_id).draggable(true)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
				landmarks.addMarker(Victim_id, VictimMarker);
				}	
				if(msg.what == 2)
				{
				
				VictimMarker = mMap.addMarker(new MarkerOptions().position(position).title(Victim_id).draggable(true)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
				landmarks.addMarker(Victim_id, VictimMarker);
				}	
				if(msg.what == 3)
				{
				
				VictimMarker = mMap.addMarker(new MarkerOptions().position(position).title(Victim_id).draggable(true)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				landmarks.addMarker(Victim_id, VictimMarker);
				}	
			
			}
			else {
				
				landmarks.removeMarker(Victim_id);
				
			}
			}
	};
	
	public String readOneVictim(String Victim_id){
		//read a pointed victim data from database
		DB_Responder readpointed = new DB_Responder(MainActivity.this);
		readpointed.open();
		String PoVictim = readpointed.getpoineddata(Victim_id);
		readpointed.close();
		Log.d("read info", PoVictim);
		return PoVictim;
	}
	
	protected void onResume() {
		super.onResume();
		beaconManager.getBeaconParsers().add(new BeaconParser()
		.setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
		beaconManager.bind(this);
		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
            	final String VicLoc = String.valueOf(arg0.latitude) + "," + String.valueOf(arg0.longitude);
            	
            	AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
	               .setMessage("Do you want to add a victim?")
	               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   EditVictim(VicLoc);
	                   }
	               })
	               .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               }).create();
            	builder.show();
          
            }
        });
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				final LatLng location = arg0;
            	AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
	               .setMessage("Do you want to add your location here?")
	               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   setFlag = true;
	                	   myLocation = location;
	                	   //use this flag to indicate that we want to see the path from current location or a set point
	                	   Log.d("my location:", ""+myLocation.latitude + myLocation.longitude);
	                	   if(setFlag == true){
	                		   if(myMarker != null){
		                		   myMarker.remove();
		                	   }
		                	   myMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("myLocation").draggable(true)
		       						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		       				   landmarks.addMarker("myLocation", myMarker);
	                	   } 
	                   }
	               })
	               .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               }).create();
            	builder.show();
			}
        });
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(Marker arg0) {
				// TODO Auto-generated method stub
				String id = arg0.getTitle();
				
				if(id.startsWith("Dis") || id.startsWith("my")){
					if(id.startsWith("Dis")){
						 AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Warning")
					        .setMessage("Please select a right victim!!!")
					        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
				                   public void onClick(DialogInterface dialog, int id) {
				                	  // finish();
				                   }
				               }).create();
					        dialog.show();
						}
					if(id.startsWith("my")){
						myLocation = arg0.getPosition();
				        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				        .setMessage("Do you what to update your current location?")
				        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   setFlag = false;
			                   }
			               })
			               .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   
			                   }
			               }).create();
				        dialog.show();
						}	
				}else{
					vicLocation = arg0.getPosition();
					final String PoVictim = readOneVictim(id);
					Log.d("victim infomtion:",PoVictim);
			        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
			        .setMessage("What do you want to do with this victim?")
			        .setNegativeButton("Edit/Delect Victim", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   EditVictim(PoVictim);
		                   }
		               })
		               .setPositiveButton("Find THIS Victim", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   //String[] PoVIF = PoVictim.split(",");
		                	   vicFlag = true;
		                	   //LatLng vic = new LatLng(42.39398619218224,-72.52872716635466);
		                	   if(vicLocation != null && myLocation != null){
		                		   if(wayList != null){
		                			   wayList.clear();
		                		   }
		                		   
		                		   if(indoorFlag){ //when isindoor return true
		                			   wayList = findOneVictim(myLocation, vicLocation);
		                			   if(wayList != null){
		                				   getPath();
		                				}
		                		   }else{
		                			   LatLng door = getNearestDoor(vicLocation);
		                			   //get the first part outdoor  path 
		                			   findDirections(myLocation.latitude,myLocation.longitude,door.latitude,door.longitude,GMapV2Direction.MODE_DRIVING );
		                			   //wayList.addAll(findOneVictim(door, vicLocation));
		                		   } 
		                	   }
		                   }
		               }).create();
			        dialog.show();
				}	
			}	 
		 });
	}

	private boolean knowMyLocation(LatLng myloc) {
		// TODO Auto-generated method stub
		boolean isindoor = true;
		if((myloc.latitude>42.395||myloc.latitude<42.393) && (myloc.longitude>-72.528||myloc.longitude<-72.529)){
			isindoor = false;
		}
		return isindoor;
	}
	
	public double getRaduis(LatLng loc, int id){
		LatLng loc1 = nodeList.get(id);
	    double R=6370856;
	    double radLat1 = loc.latitude*Math.PI/180;
	    double radLat2 = loc1.latitude*Math.PI/180;
	    double a = radLat1 - radLat2;
	    double b = (loc.longitude-loc1.longitude)*Math.PI/180;
	    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+
	    		Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	    s = s * R;
	    s = Math.round(s * 10000) / 10000;
	    return s;
	}
	
	private int getNearestNode(LatLng vic) {
		// TODO Auto-generated method stub
		int node = 0;
		for(int j=0; j<nodeList.size()-1; j++){
			double dist1 = getWeight(vic, nodeList.get(node));
			double dist2 = getWeight(vic, nodeList.get(j));
			if(dist1 > dist2){
				node = j;
			}
		}
		return node;
	}
	
	public ArrayList<LatLng> findOneVictim(LatLng myloc, LatLng vic){
		ArrayList<LatLng> lineList = new ArrayList<LatLng>();
		
	 	int vicId = getNearestNode(vic);
	 	int myId = getNearestNode(myloc);
	 	Log.d("nearst node id to my position:",""+myId);
	 	  
		Log.d("get shortest path:","begin to use Dijkstra algorithm");
		Dijkstra d = new Dijkstra(); 
		MyPoint s1 = source.get(myId);
		MyPoint e2 = source.get(vicId);
		
		if(myCircle !=null){
			myCircle.remove();
		}
		if(vicCircle !=null){
			vicCircle.remove();
		}
		if(indoorFlag){
			myCircle = mMap.addCircle(new CircleOptions().center(myloc).radius(getRaduis(myloc, myId))
					.zIndex(1).strokeColor(Color.LTGRAY).fillColor(Color.TRANSPARENT));
		}
		//use circle to indicate the error
		vicCircle = mMap.addCircle(new CircleOptions().center(vic).radius(getRaduis(vic, vicId))
				.zIndex(1).strokeColor(Color.LTGRAY).fillColor(Color.TRANSPARENT));
		
		Log.d("startX",String.valueOf(s1.getX())+String.valueOf(s1.getY()));
		Log.d("endX:",String.valueOf(e2.getX())+String.valueOf(e2.getY()));
		
		Stack<Integer> points = d.dijkstra(source, edges, s1, e2);
		Log.d("get shortest path:",String.valueOf(points.size()));
		
		while (points.size() > 0) {
			int p = points.pop();
			lineList.add(new LatLng(source.get(p).getX(), source.get(p).getY()));
			Log.d("shortest path:",""+p);
		}
		return lineList;
	}
	
	public void getPath(){
		if(newPolyline != null){
			newPolyline.remove();
		}
		PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.BLACK).visible(true).zIndex(1);
		rectLine.addAll(wayList);
		newPolyline = mMap.addPolyline(rectLine);
	}
	
	private double getWeight(LatLng point1, LatLng point2) {
		// TODO Auto-generated method stub
		double distance = (point1.latitude - point2.latitude) * (point1.latitude  - point2.latitude )
				+ (point1.longitude  - point2.longitude) * (point1.longitude - point2.longitude);
		System.out.print(String.valueOf(distance));
		return distance;
	}
	
	public void getNode(){
		Log.d("get nodes and find way","begin to find way");
		double[] node = {
				//42.393465218213095,-72.52829231321812,//door3
				//42.39350359809696-72.52832148224115,
				42.39353479721108,-72.52833690494299,
				42.393562282132095,-72.5283533334732,
				42.39359521449908,-72.52837345004082,
				42.39363111818778,-72.52839524298906,
				42.39365241277961,-72.52841033041477,
				42.39368732599017,-72.52843078225851,
				42.3937128299544,-72.5284468755126,
				42.39373932435287,-72.52846330404282,
				42.39376482829596,-72.5284793972969,
				42.39379899861124,-72.52849884331226,
				42.39383019757852,-72.52851862460375,
				42.39386461546888,-72.528540417552,
				42.393884176707495,-72.52855114638805,
				42.39391512802195,-72.52857092767954,
				42.39393766056931,-72.52858467400074,
				42.39396935468813,-72.52860344946384,
				42.39399411570731,-72.52861987799406,
				42.39403571419756,-72.52864435315132, //19
				
				42.39405676104225,-72.52865809947252,
				42.394094892954634,-72.52868123352528,
				42.39412510133632,-72.52870034426451,
				42.39416199515981,-72.52872180193663,
				42.394187003712396,-72.52873755991459,
				42.39421968814229,-72.52875801175833,
				42.39420408875741,-72.52878550440073,
				42.39418328957152,-72.52882204949856,
				42.394166699739756,-72.52885423600674,//28, door1
				
				42.394021848037205,-72.52866748720407,//29
				42.394001791621285,-72.52870235592127,
				42.39398619218224,-72.52872716635466,
				//42.39397628777448,-72.52874091267586 //door2
				};
		for(int i=0; i<node.length/2; i++){
			nodeList.add(new LatLng(node[2*i], node[2*i+1]));
		}	
		Log.d("number of nodes:",String.valueOf(nodeList.size()));
		for(int i=0; i<nodeList.size(); i++){
			if(i==26){
				MyPoint n1 = new MyPoint(i, nodeList.get(i).latitude,nodeList.get(i).longitude);
				source.add(n1);
				i = i +1;
			}
			MyPoint n1 = new MyPoint(i, nodeList.get(i).latitude,nodeList.get(i).longitude);
			source.add(n1);
			
			if(i < nodeList.size()-1){
				MyPoint n2 = new MyPoint(i+1, nodeList.get(i+1).latitude,nodeList.get(i+1).longitude);
				double weight = getWeight(new LatLng(n1.getX(), n1.getY()),new LatLng(n2.getX(), n2.getY()));
				edges.add(new Edge(n1, n2, weight));
				edges.add(new Edge(n2, n1, weight));
			}
		}
		double weight = getWeight(new LatLng(source.get(17).getX(), source.get(17).getY()),
				new LatLng(source.get(27).getX(), source.get(27).getY()));
		edges.add(new Edge(source.get(17), source.get(27), weight));
		edges.add(new Edge(source.get(27), source.get(17), weight));
	}
	
	private void EditVictim(String VicLoc){
		Bundle myBundle = new Bundle();
	    myBundle.putString(VICLOC, VicLoc);
 	    Intent intent = new Intent(getApplicationContext(),EditActivity.class);
		intent.putExtras(myBundle);
		startActivity(intent);		
		Log.d("Showlocation","Start EditActivity");
 	   //flagAddVictim = true;
	 }
	
	@Override
	protected void onPause() {
		super.onPause();
		beaconManager.unbind(this);
		 //threadSocketServer.interrupt();
		 //Log.d("Thread", "interrupt");
		 //try {
			//serverSocket.close();
			//Log.d("Socket", "closed");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		
	}
	
	@Override
	protected  void  onStop() {
		super.onStop();
		//threadSocketServer.interrupt();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		threadSocketServer.interrupt();
	}
	
	private void InitializeMarker(){

		String marcusBeacons = "" +
		"@Dis001,42.39354668258381,-72.52833187580109," +
		"@Dis002,42.39359447166392,-72.52839054912329," +
		"@Dis003,42.393661574404135,-72.52840027213097," +
		"@Dis004,42.39370936339676,-72.52845861017704," +
		"@Dis005,42.39377498034809,-72.52846665680408," +
		"@Dis006,42.39382895952455,-72.52852533012629," +
		"@Dis007,42.39389135741379,-72.52853605896235," +
		"@Dis008,42.393932460751394,-72.52859741449356," +
		"@Dis009,42.393999315520105,-72.52860512584448," +
		"@Dis010,42.39404537098601,-72.52866346389055," +
		"@Dis011,42.394103806904866,-72.52867016941309," +
		"@Dis012,42.39417412802318,-72.52873621881008," +
		"@Dis013,42.39423256382214,-72.52873655408621," +
		"@Dis014,42.39422736402869,-72.52877611666918," +
		"@Dis015,42.39416001428392,-72.52882674336433," +
		"@Dis016,42.39402060998703,-72.52868391573429," +
		"@Dis017,42.39397727821532,-72.52871174365282,";

		String[] marcusBeaconsArray = marcusBeacons.split("@");
		int index = 0;
		for(String marcusBeacon : marcusBeaconsArray){
			
			if(marcusBeacon.equals("")){
				continue;
			}
			int titleIndex = 0;
			int latitudeIndex = 1;
			int longitutdeIndex = 2;
		
			String[] beaconComponents = marcusBeacon.split(",");
			String beaconTitle = beaconComponents[titleIndex];
			double beaconLat = Double.parseDouble(beaconComponents[latitudeIndex]);
			double beaconLong = Double.parseDouble(beaconComponents[longitutdeIndex]);
			
			bcnTitle[index] = beaconTitle;
			bcnLat[index] = beaconLat;
			bcnLong[index] = beaconLong;
			index = index + 1;
			System.out.println(beaconTitle);
			System.out.println(beaconLong);
			System.out.println(beaconLat);
			
			LatLng position = new LatLng(beaconLat,beaconLong);
			Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(beaconTitle).draggable(true)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			landmarks.addMarker(beaconTitle, marker);
		}
		
	}
	
	int num = 1;
	private void showMyLocation(){
		Double[] distance = new Double[3];
		Double[] Lat = new Double[3];
		Double[] Long = new Double[3];
		
		if(strongestBeaconList.size() < 2){
			Toast.makeText(getApplicationContext(), "We get less than two beacons!", Toast.LENGTH_LONG).show();
		}else if(strongestBeaconList.size() >= 2){
			double weight;
			weight = Math.PI * 6370856 / 180;
			//calculate distance and my location
			for(int n=0; n<strongestBeaconList.size();n++){
				Beacon bcn = strongestBeaconList.get(n);
				String minor = bcn.getId3().toString();
				int rssi = bcn.getRssi();
				int minorInt = Integer.parseInt(minor);
					
				double Lat1 = bcnLat[minorInt-1];
				double Long1 = bcnLong[minorInt-1];
				double dist = 0.008459 * rssi * rssi + 0.6711* rssi + 15.32;
			    dist = dist * 0.3048  / weight;
					
				distance[n] = dist;
				Lat[n] = Lat1;
				Long[n] = Long1;
			}	
			
			double a = (distance[0]*distance[0] - distance[1]*distance[1] - Lat[0]*Lat[0] + Lat[1]*Lat[1] 
						- Long[0]*Long[0] + Long[1]*Long[1]) / (-2 * (Lat[0] - Lat[1]));
			double b = - (Long[0] - Long[1]) / (Lat[0] - Lat[1]);
			double i = 1 + b*b;
			double j = 2*(a*b - b*Lat[0] - Long[0]);
			double k = Lat[0]*Lat[0] - 2*a*Lat[0] + a*a + Long[0]*Long[0] - distance[0]*distance[0];
			double theta = j*j - 4*i*k;
			
			if(theta > 0){
				theta = Math.sqrt(theta);
				double x_long1 = (-j + theta) / (2*i);
				double x_lat1 = a + b*(x_long1);
				double x_long2= (-j - theta) / (2*i);
				double x_lat2 = a + b*(x_long2);
				
				if(strongestBeaconList.size() == 2){
					myLatitude = (x_lat1 + x_lat2)/2;
					myLongitude = (x_long1 + x_long2)/2;
				}else if(strongestBeaconList.size() == 3){
					if((Lat[2] - x_lat1) * ( Lat[2] - x_lat1) + (Long[2] - x_long1)*(Long[2] - x_long1) == distance[2]*distance[2]){
						myLatitude=x_lat1;
						myLongitude=x_long1;
						Log.d("My location: ", "" + myLatitude + myLongitude);
					}
					else{
						myLatitude=x_lat2;
						myLongitude=x_long2;
						Log.d("My location: ", "" + myLatitude + myLongitude);
					}
				}
		
			}else if(theta == 0){
				double x_long = (-j) / (2*i);
				double x_lat = a + b*(x_long);
				myLatitude=x_lat;
				myLongitude=x_long;
				Log.d("My location: ", "" + myLatitude+ myLongitude);
				
			}
			//else if(theta < 0){
				//System.out.println("We can not get the location.");
				//Toast.makeText(getApplicationContext(), "We can not get your location!", Toast.LENGTH_LONG).show();
			//}
			Log.d("My location: ", "" + myLatitude + myLongitude);
			if(myLatitude != 0){
				myLocation = new LatLng(myLatitude, myLongitude);
				if(myMarker != null){
					myMarker.remove();
				}
				myMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("myLocation").draggable(true)
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
				landmarks.addMarker("myLocation", myMarker);
			}			
		}
			
	}

	private void setupMap(){
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		changeMapPositionAndZoom(new LatLng(marcusLat,marcusLng), knowlesZoom);
		MyUrlTileProvider mTileProvider = new MyUrlTileProvider(256, 256, mUrl);
		mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mTileProvider).zIndex(0));
		//mMap.setMyLocationEnabled(true);
	    // display all the landmarks
		landmarks = new Landmarks();
		InitializeMarker();
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item){
		return true;
	}
	
	public class MyUrlTileProvider extends UrlTileProvider {

		private String baseUrl;

		public MyUrlTileProvider(int width, int height, String url) {
		    super(width, height);
		    this.baseUrl = url;
		}

		@Override
		public URL getTileUrl(int x, int y, int zoom) {
		    try {
		        return new URL(baseUrl.replace("{z}", ""+zoom).replace("{x}",""+x).replace("{y}",""+y));
		    } catch (MalformedURLException e) {
		        e.printStackTrace();
		    }
		    return null;
		}
	}
	
	private void changeMapPositionAndZoom(LatLng moveToPosition, int zoomLevel){
		changeMapPosition(moveToPosition);
		changeMapZoom(zoomLevel);
	}
	
	private void changeMapPosition(LatLng moveToPosition){
		CameraUpdate center = CameraUpdateFactory.newLatLng(moveToPosition);
		mMap.moveCamera(center);
	}
	
	private void changeMapZoom(int zoomLevel){
		CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
		mMap.animateCamera(zoom);
	}

	@Override
	public void onLandmarkSelected(Marker landmark) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onModeChange() {
		// TODO Auto-generated method stub	
	}
	
	private void getStrongestRSSI() {
		// TODO Auto-generated method stub
		int index1, index2;
		for(index1=0; index1<discoveredBeaconList.size()-1; index1++){
			for(index2=0; index2<discoveredBeaconList.size()-1; index2++){
				Beacon bcn1 = discoveredBeaconList.get(index2);
				Beacon bcn2 = discoveredBeaconList.get(index2+1);
				System.out.println(bcn1.getId3().toString());
				if(bcn1.getRssi() < bcn2.getRssi())
				{
					discoveredBeaconList.set(index2, bcn2);
					discoveredBeaconList.set(index2+1, bcn1);
				}
			}
		}
		
		if(discoveredBeaconList.size()>=3){
			for(index1=0; index1<3; index1++){
				strongestBeaconList.add(index1, discoveredBeaconList.get(index1));
			}
		}else if(discoveredBeaconList.size()==2){
			for(index1=0; index1<discoveredBeaconList.size()-1; index1++){
				strongestBeaconList.add(index1, discoveredBeaconList.get(index1));
			}
		}else{
			Toast.makeText(getApplicationContext(), "No beacon or just one beacon was discovered.", Toast.LENGTH_LONG).show();
		}	
	}

	/**
	 * Refresh the list of beacon according to current values in the map and
	 * then notify the list UI to change.
	 */
	int addnum = 1;
	private void updateDiscoveredList() {
		discoveredBeaconList.clear();
		strongestBeaconList.clear();
		Iterator<Beacon> bIter = discoveredBeaconMap.values().iterator();
		while (bIter.hasNext()) {
			discoveredBeaconList.add(bIter.next());
			//getStrongestRSSI();
		}
		getStrongestRSSI();
		Log.d("Discover strongest beacons.", ""+ strongestBeaconList.size());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(indoorFlag){
					showMyLocation();
					if(vicFlag == true){
						if(vicLocation != null && myLocation != null){
							if(wayList != null){
	                			   wayList.clear();
	                		   }
							wayList = findOneVictim(myLocation, vicLocation);
	                		if(wayList != null){
	                			getPath();
	                			}
	                	   }
					}
				}
			}
		});
	}

	@Override
	public void onBeaconServiceConnect() {
		// TODO Auto-generated method stub
		beaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<Beacon> beacons,
					Region region) {
				if (beacons.size() > 0) {
					Log.i(TAG_SEARCHING_ACTIVITY_LOG, "Found " + beacons.size()
							+ "beacons");
					for (Iterator<Beacon> bIterator = beacons.iterator(); bIterator
							.hasNext();) {
						final Beacon beacon = bIterator.next();
						if (isGimbalTag(beacon)) {
							String major = beacon.getId2().toString();
							if(Double.parseDouble(major) == 100){
								// generate the HashMap key, which is the
								// combination of tag's UUID, Major and Minor; But
								// you can always choose your own key
								final String key = new StringBuilder()
										.append(beacon.getId1())
										.append(beacon.getId2())
										.append(beacon.getId3()).toString();
								discoveredBeaconMap.put(key, beacon);
							}
							
						}
					}
					updateDiscoveredList();
				}
			}
		});

		try {
			beaconManager.startRangingBeaconsInRegion(new Region(
					"myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
		}
	}
	
	/**
	 * A filter check whether the detected beacon is a Gimbal tag used for
	 * project.
	 * 
	 * @param beacon
	 *            The detected beacon
	 * @return Whether the beacon is a Gimbal tag for project or not.
	 */
	private boolean isGimbalTag(Beacon beacon) {
		final String uuid = beacon.getId1().toString();
		final String tagIdentifier = uuid.split("-")[0];
		if (validGimbalIdentifiers.contains(tagIdentifier)) {
			return true;
		}
		return false;
	}
	
	public LatLng getNearestDoor(LatLng destination){
		LatLng marcus1 = new LatLng(42.394166699739756,-72.52885423600674);
		LatLng marcus2 = new LatLng(42.39397628777448,-72.52874091267586);
		LatLng marcus3 = new LatLng(42.393465218213095,-72.52829231321812);
		LatLng door = marcus1;
		double dist1 = getWeight(marcus1,destination);
		double dist2 = getWeight(marcus2,destination);
		double dist3 = getWeight(marcus3,destination);
		if(dist2<=dist1 && dist2<=dist3){
			door = marcus2;
		}else if(dist3<=dist1 && dist3<=dist2){
			door = marcus1;
		}
		return door;
	}
	
	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		Log.d("handle asyntask","is execute");
		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{          
			wayList.add(directionPoints.get(i));
		}
		LatLng door = getNearestDoor(vicLocation);
		wayList.addAll(findOneVictim(door, vicLocation));
		if(wayList != null){
			   getPath();
			}
	}
	
	private void getSreenDimanstions()
	{
		Display display = getWindowManager().getDefaultDisplay();
		if(point1 != null){
			display.getSize(point1);
			width = point1.x;
			height = point1.y;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
		Log.e("function executed?","is executed");
		Map<String, String> mMap = new HashMap<String, String>();
		mMap.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		mMap.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		mMap.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		mMap.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		mMap.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);
		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(mMap);	
	}

	@Override
	public void onLocationChanged(android.location.Location arg0) {
		// TODO Auto-generated method stub
		if(gmapFlag == true){
			myLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
		}
		
		
	}
}
