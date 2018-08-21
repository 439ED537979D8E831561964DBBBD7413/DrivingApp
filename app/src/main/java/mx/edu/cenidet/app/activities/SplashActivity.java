package mx.edu.cenidet.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.controllers.DeviceTokenControllerSdk;
import mx.edu.cenidet.cenidetsdk.controllers.ZoneControllerSdk;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.utilities.ConstantSdk;
import mx.edu.cenidet.app.R;
import www.fiware.org.ngsi.datamodel.entity.Zone;
import www.fiware.org.ngsi.utilities.ApplicationPreferences;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;

public class SplashActivity extends AppCompatActivity implements
        DeviceTokenControllerSdk.DeviceTokenServiceMethods {
    private Intent mIntent;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<Zone> listZone;
    private ZoneControllerSdk zoneControllerSdk;

    //Envío del token de firebase
    private DeviceTokenControllerSdk deviceTokenControllerSdk;
    private String fcmToken;
    private Context context;
    private ApplicationPreferences appPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //objeto que utilizaremos para llamar a los metodos de la gestion del token de firebase
        appPreferences = new ApplicationPreferences();
        deviceTokenControllerSdk = new DeviceTokenControllerSdk(context, this);
        fcmToken = appPreferences.getPreferenceString(
                getApplicationContext(),
                ConstantSdk.STATIC_PREFERENCES,
                ConstantSdk.PREFERENCE_KEY_FCMTOKEN
        );
        Log.d("Alertas", "Token in SPLASH" + fcmToken);

        if (!fcmToken.equals("") || fcmToken != null){

            String userType = appPreferences.getPreferenceString(getApplicationContext(),ConstantSdk.PREFERENCE_NAME_GENERAL,ConstantSdk.PREFERENCE_USER_TYPE);
            String preference = "All";
            if (userType.equals("mobileUser")){
                preference = "traffic";
            }
            deviceTokenControllerSdk.createDeviceToken(fcmToken, new DevicePropertiesFunctions().getDeviceId(context), preference);
        }



        if(isEnableGPS()){
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            showGPSDisabledAlert();
            Log.i("Status ", "Inactivo gps");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isEnableGPS()){
            //Inicia el servicio del GPS
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            Log.i("Status ", "Inactivo gps");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume splash", "-----------------------------------------------------------------------------");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause splash", "-----------------------------------------------------------------------------");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("onPostResume splash", "-----------------------------------------------------------------------------");
    }

    private boolean isEnableGPS(){
        LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            return true;
        }else{
            return false;
        }
    }

    private void showGPSDisabledAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_alert_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.button_enable_alert_gps,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void createDeviceToken(Response response) {
        Log.i("STATUS", "Firebase Service Create: CODE: "+ response.getHttpCode());
        Log.i("STATUS", "Firebase Service Create: BODY: "+ response.getBodyString());
        switch (response.getHttpCode()){
            case 201:
            case 200:
                Log.i("STATUS: ", "El token se genero exitosamente...!");
                break;
            case 400:
                Log.i("STATUS: ", "Tokenk incorrecto...!");
                break;
        }

    }

    @Override
    public void readDeviceToken(Response response) {

    }

    @Override
    public void updateDeviceToken(Response response) {

    }


}
