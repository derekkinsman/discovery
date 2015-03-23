package com.siberia.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class Discovery extends CordovaPlugin {

  public static final String TAG = "Discovery";

  NsdHelper mNsdHelper;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    String serviceName = args.getString(0);
    String serviceType = args.getString(1);
    mNsdHelper = new NsdHelper(cordova.getActivity(), callbackContext, serviceName);

    if (action.equals("identify")) {
      Log.d(TAG, "identify");
      mNsdHelper.discoverServices(serviceType);
    }

    else {
      callbackContext.error(String.format("Discovery - invalid action:", action));
      return false;
    }

    // PluginResult.Status status = PluginResult.Status.NO_RESULT;
    // PluginResult pluginResult = new PluginResult(status);
    // pluginResult.setKeepCallback(true);
    // callbackContext.sendPluginResult(pluginResult);
    return true;
  }

}
