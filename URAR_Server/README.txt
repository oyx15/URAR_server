Urban Research and Rescue_Server(Responder):
       Android Application

*******************Functionality*************************
1. Communicate with client and receive data from client
2. Add Victitims and their information to database
3. Save, read and edit Victims' Information in the database
4. Use Algorithm to set up  a way for responder to find victims


******************Libraries********************
This project needs two libraries:
1) Google Play Services Lib (need to have the google key)
   Please follow the instruction on this website: https://developers.google.com/maps/documentation/android/start
2) Android beacon library

*****************Communication with Client App******************
Here, we are using Socket to communicate, so the client and server should run under the same Network.
And, before running, both APPs need to reset the IP address.
