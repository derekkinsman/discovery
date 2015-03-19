package com.siberia.discovery.util;

import android.app.ListActivity;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.siberia.discovery.RemoteActivity;

public class DiscoverOUYAActivity extends ListActivity {
    private static final String TAG = DiscoverOUYAActivity.class.getSimpleName();

    private static final String SERVICE_TYPE = "_ouyaremote._tcp.";

    private NsdManager mNSDManager;
    private DiscoveryListener mDiscoveryListener;
    private ResolveListener mResolveListener;
    private ServiceAdapter mAdapter;

    private ProgressBar mScanningSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        mScanningSpinner = (ProgressBar) findViewById(R.id.discovery_spinner);

        mNSDManager = (NsdManager) getSystemService(NSD_SERVICE);
        mDiscoveryListener = new DiscoveryListener();
        mResolveListener = new ResolveListener();

        mAdapter = new ServiceAdapter(this);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mNSDManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mNSDManager.stopServiceDiscovery(mDiscoveryListener);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        final Intent intent = new Intent(this, RemoteActivity.class);
        NsdServiceInfo serviceInfo = mAdapter.getItem(position);
        intent.putExtra(RemoteActivity.EXTRA_SERVICE_INFO, serviceInfo);
        startActivity(intent);
    }

    private class DiscoveryListener implements NsdManager.DiscoveryListener {
        @Override
        public void onDiscoveryStarted(String serviceType) {
            Log.d(TAG, "Discovery started");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mScanningSpinner.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "Service found: " + serviceInfo);

            if(!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                Log.d(TAG, "Invalid service type: " + serviceInfo.getServiceType());
            } else {
                Log.d(TAG, "Found OUYARemote service");
                mNSDManager.resolveService(serviceInfo, mResolveListener);
            }
        }

        @Override
        public void onServiceLost(final NsdServiceInfo serviceInfo) {
            Log.d(TAG, "Service lost: " + serviceInfo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.remove(serviceInfo);
                }
            });
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.d(TAG, "Discovery stopped");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mScanningSpinner.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Start discovery failed (" + errorCode + ")");
            mNSDManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Stop discovery failed (" + errorCode + ")");
            mNSDManager.stopServiceDiscovery(this);
        }
    }

    private class ResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onServiceResolved(final NsdServiceInfo serviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Service resolved: " + serviceInfo);
                    mAdapter.add(serviceInfo);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.d(TAG, "Unable to resolve service: " + serviceInfo);
        }
    }
}
