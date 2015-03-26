package com.siberia.plugin;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.String;
import java.net.InetAddress;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class NsdHelper implements NsdManager.DiscoveryListener {

  public static final String TAG = "NsdHelper";
  public static final int MSG_STOPPED = 2734980;

  private final Handler handler;
  private final CallbackContext mCallbackContext;
  private final NsdManager mNsdManager;
  private final String serviceName;
  private final String serviceType;

  NsdManager.ResolveListener mResolveListener;
  NsdManager.DiscoveryListener mDiscoveryListener = this;

  public NsdHelper(final Context context,
                   final Handler.Callback handlerCallback,
                   final CallbackContext callbackContext,
                   final String serviceType,
                   final String serviceName) {

    this.handler = new Handler(handlerCallback);
    this.mCallbackContext = callbackContext;
    this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    this.serviceName = serviceName;
    this.serviceType = serviceType;
  }

  // NsdHelper API {{{
  public void discoverServices() {
    this.mNsdManager.discoverServices(
      this.serviceType,
      NsdManager.PROTOCOL_DNS_SD,
      this
    );
  }

  public void stopDiscovery() {
    this.mNsdManager.stopServiceDiscovery(this);
  }
  // NsdHelper API }}}

  // NsdManager.DiscoveryListener {{{
  @Override
  public void onDiscoveryStarted(final String regType) {
    Log.d(TAG, String.format("Service discovery started: %s", regType));
  }

  @Override
  public void onServiceFound(final NsdServiceInfo service) {
    // A service was found!  Do something with it.
    Log.d(TAG, "Service discovery success" + service);
    if (!service.getServiceType().equals(this.serviceType)) {
      // Service type is the string containing the protocol and
      // transport layer for this service.
      Log.d(TAG, String.format("Unknown Service Type: %s", service.getServiceType()));
    } else if (service.getServiceName().equals(this.serviceName)) {
      // The name of the service tells the user what they'd be
      // connecting to. It could be "Bob's Chat App".
      Log.d(TAG, "Same machine: " + this.serviceName);
    } else if (service.getServiceName().contains(this.serviceName)) {
      mNsdManager.resolveService(service, new ResolveListener(this.serviceName, mCallbackContext));
    }
  }

  @Override
  public void onServiceLost(final NsdServiceInfo service) {
    Log.w(TAG, "Service lost" + service);
  }

  @Override
  public void onDiscoveryStopped(final String serviceType) {
    Log.i(TAG, "Discovery stopped: " + serviceType);
    this.handler.obtainMessage(MSG_STOPPED).sendToTarget();
  }

  @Override
  public void onStartDiscoveryFailed(final String serviceType, final int errorCode) {
    Log.e(TAG, "Start Discovery failed: Error code:" + errorCode);
    this.mNsdManager.stopServiceDiscovery(this);
  }

  @Override
  public void onStopDiscoveryFailed(final String serviceType, final int errorCode) {
    Log.e(TAG, "Stop Discovery failed: Error code:" + errorCode);
    this.mNsdManager.stopServiceDiscovery(this);
  }

  private class ResolveListener implements NsdManager.ResolveListener {

    public String serviceName;
    public CallbackContext mCallbackContext;

    public ResolveListener(String serviceName, CallbackContext callbackContext) {
      this.serviceName = serviceName;
      this.mCallbackContext = callbackContext;
    }

    @Override
    public void onResolveFailed(final NsdServiceInfo serviceInfo, final int errorCode) {
      // Called when the resolve fails.  Use the error code to debug.
      Log.e(TAG, "Resolve failed" + errorCode);
    }

    @Override
    public void onServiceResolved(final NsdServiceInfo serviceInfo) {
      Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

      if (serviceInfo.getServiceName().equals(this.serviceName)) {
        Log.d(TAG, "Same IP.");
        return;
      }
      final int port = serviceInfo.getPort();
      final InetAddress host = serviceInfo.getHost();
      final String hostAddress = host.getHostAddress();

      final JSONObject data = new JSONObject();
      try {
        data.put("port", port);
        data.put("host", hostAddress);
      } catch (JSONException e) {
        Log.wtf(TAG, "Couldn't put data into JSONObject", e);
      }

      final PluginResult result = new PluginResult(PluginResult.Status.OK, data);
      result.setKeepCallback(true);
      this.mCallbackContext.sendPluginResult(result);
    }
  }
}