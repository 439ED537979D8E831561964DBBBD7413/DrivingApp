package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import mx.edu.cenidet.cenidetsdk.entities.User;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 3/16/2018.
 */

public class UserController implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback{
    private static String URL_BASE_HOST = ConfigServer.http_host.getPropiedad();
    private static String URL_BASE_LOGIN = ConfigServer.http_host_login.getPropiedad();
    private static String URL_BASE_NODE = ConfigServer.http_host_node.getPropiedad();
    private UsersServiceMethods uServiceMethods;
    private Context context;
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;

    public UserController(Context context, UsersServiceMethods uServiceMethods) {
        this.uServiceMethods = uServiceMethods;
        this.context = context;
    }
    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "readUser":
                uServiceMethods.readUser(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {
        switch (method){
            case "logInUser":
                uServiceMethods.logInUser(response);
                break;
            case "createUser":
                uServiceMethods.createUser(response);
                break;
        }
    }

    public interface UsersServiceMethods{
        void createUser(Response response);
        void readUser(Response response);
        void updateUser(Response response);
        void deleteUser(Response response);
        void logInUser(Response response);
        void logOutUser(Response response);
    }

    public void createUser(User user){
        method = "createUser";
        Response response = new Response();
        String URL = URL_BASE_HOST + ConfigServer.http_api.getPropiedad() +"/"+ ConfigServer.http_user.getPropiedad();
        String jsonString = response.parseObjectToJsonString(user);
       // JSONObject jsonLogInUser = response.parseJsonObject(user);
        Log.i("Status", "JSON CREATE USER: "+jsonString);
        mPOST = new MethodPOST(this);
        //JSONObject jsonLogInUser = response.parseJsonObject(jsonString);
        mPOST.execute(URL, jsonString.toString());
    }

    public void logInUser(String userID, String password, String typeUser){
        method = "logInUser";
        Response response = new Response();
        String URL,json;
        if(typeUser.equals("mobileUser")){
            URL = URL_BASE_HOST + ConfigServer.http_api.getPropiedad() +"/"+ ConfigServer.http_user.getPropiedad() +"/"+ ConfigServer.http_login.getPropiedad();

            json = "{\n" +
                    "\t\"phoneNumber\":\""+userID+"\",\n" +
                    "\t\"password\":\""+password+"\"\n" +
                    "}";
        }else{
            URL = URL_BASE_HOST + ConfigServer.http_api.getPropiedad() +"/"+ ConfigServer.http_security.getPropiedad() +"/"+ ConfigServer.http_login.getPropiedad();

            json = "{\n" +
                    "\t\"email\":\""+userID+"\",\n" +
                    "\t\"password\":\""+password+"\"\n" +
                    "}";
        }
        JSONObject jsonLogInUser = response.parseJsonObject(json);
        Log.i("Status", "JSON: "+jsonLogInUser);
        mPOST = new MethodPOST(this);
        mPOST.execute(URL, jsonLogInUser.toString());
    }
    public void readUser(String email){
        method = "readUser";
        String URL = URL_BASE_NODE + ConfigServer.http_user.getPropiedad()+"?email="+email;
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
