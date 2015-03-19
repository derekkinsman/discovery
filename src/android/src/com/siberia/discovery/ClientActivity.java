package com.siberia.discovery;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.content.Context;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.siberia.discovery.util.DiscoverOUYAActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */

public class ClientActivity extends CordovaPlugin {
  
  public static final String TAG = "Discovery";
  public static final String SERVICE_TYPE = "_http._tcp.";
  public static final String mServiceName = "ouya";

  Context mContext;

  /**
   * Constructor.
   */
  public ClientActivity() {
  }

  /**
   * Sets the context of the Command. This can then be used to do things like
   * get file paths associated with the Activity.
   *
   * @param cordova The context of the main Activity.
   * @param webView The CordovaWebView Cordova is running in.
   */
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d(TAG, "Discovery plugin initialising.");
    mContext = webView.getContext();
  }

  /**
   * Executes the request and returns PluginResult.
   *
   * @param action            The action to execute.
   * @param args              JSONArray of arguments for the plugin.
   * @param callbackContext   The callback id used when calling back into JavaScript.
   * @return                  True if the action was valid, false if not.
   */
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("identify")) {
      JSONObject opts = args.getJSONObject(0);
      Log.d(TAG, "Doing identify");
      Log.d(TAG, opts.toString());

      Intent intent = new Intent(this, DiscoverOUYAActivity.class);
      // doIdentify(opts, callbackContext);
      return true;
    }
     
    return false;
  }
}