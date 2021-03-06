package com.siberia.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

public class Discovery extends CordovaPlugin
    implements
        Handler.Callback
{

  public static final String TAG = "Discovery";

  private List<NsdHelper> helpers = new ArrayList<NsdHelper>();
  private NsdHelper helper;

  // CordovaPlugin {{{
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("startDiscovery")) {
      if (args.length() < 2) {
        callbackContext.error("identify requires serviceName and serviceType as arguments");
      }
      final String serviceName = args.getString(0);
      final String serviceType = args.getString(1);
      Log.d(TAG, String.format("identify(%s, %s)", serviceName, serviceType));
      this.identify(serviceName, serviceType, callbackContext);
    }
    else if (action.equals("stopDiscovery")) {
      this.stopDiscovery();
    }
    else {
      callbackContext.error(String.format("Discovery - invalid action:", action));
      return false;
    }

    return true;
  }

  @Override
  public void onPause(final boolean multitasking) {
    super.onPause(multitasking);
    this.stopDiscovery();
  }
  // CordovaPlugin }}}

  // Handler.Callback {{{
  /**
   * Listen for notification that {@link NsdHelper} has completed a
   * discovery cycle so it can be cleared.
   *
   * @param message to examine.
   *
   * @return {@code true} if {@code message} has been handled.
   */
  public boolean handleMessage(final Message message) {
    final boolean result;
    switch (message.what) {
    case NsdHelper.MSG_STOPPED:
      this.helper = null;
      result = true;
      break;
    default:
      result = false;
      break;
    }
    return result;
  }
  // Handler.Callback }}}

  private void identify(final String serviceName,
                        final String serviceType,
                        final CallbackContext callbackContext) {
    if (this.helper != null) {
      this.helper.stopDiscovery();
    }
    final NsdHelper nsdHelper =
        new NsdHelper(cordova.getActivity(), this, callbackContext, serviceType, serviceName);
    this.helper = nsdHelper;
    nsdHelper.discoverServices();
  }

  private void stopDiscovery() {
    if (this.helper != null) {
      this.helper.stopDiscovery();
    }
  }

}