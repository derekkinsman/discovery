package com.siberia.plugin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class CallbackContextHandler extends Handler {
 
   private CallbackContext callbackContext;
        
    public CallbackContextHandler(final CallbackContext callbackContext) {
        super();
        this.callbackContext = callbackContext();
    }
        
    @Override
    public void handleMessage(Message msg) {
        String type = msg.getData().getString("type");
        String message = msg.getData().getString("msg");
        JSONObject data = new JSONObject();

        try {
            data.put("type", new String(type));
            data.put("data", new String(message));
        } catch(JSONException e) {
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
        result.setKeepCallback(true);
        cbc.sendPluginResult(result);
    }
    
    public void sendNotification(final String type, final String msg) {
        Bundle messageBundle = new Bundle();
        messageBundle.putString("type", type);
        messageBundle.putString("msg", msg);
        Message message = new Message();
        message.setData(messageBundle);
        this.sendMessage(message);
    }

};