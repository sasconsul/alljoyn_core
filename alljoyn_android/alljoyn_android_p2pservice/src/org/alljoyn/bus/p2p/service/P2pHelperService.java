/*
 * Copyright 2012, Qualcomm Innovation Center, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.alljoyn.bus.p2p.service;

import android.content.Context;

import android.util.Log;

class P2pHelperService implements P2pInterface {
    private static final String TAG = "P2pHelperService";

    private native boolean jniOnCreate();
    private native void jniOnDestroy();
    private native void jniOnFoundAdvertisedName(String name, String namePrefix, String guid, String device);
    private native void jniOnLostAdvertisedName(String name, String namePrefix, String guid, String device);
    private native void jniOnLinkEstablished(int handle);
    private native void jniOnLinkError(int handle, int error);
    private native void jniOnLinkLost(int handle);

    private boolean jniReady = false;

    private P2pManager mP2pManager = null;

    public P2pHelperService(Context context) {
        System.loadLibrary("P2pHelperService");
        jniReady = jniOnCreate();
        if (jniReady) {
            mP2pManager = new P2pManager(context, this);
        }
    }

    public synchronized void shutdown() {
        if (mP2pManager != null) {
            mP2pManager.shutdown();
            mP2pManager = null;
        }
        jniReady = false;
        jniOnDestroy();
    }

    protected void finalize() throws Throwable {
        if (jniReady) {
            shutdown();
        }
    }

    public void busFailed() {
        jniReady = false;
        // Try to restart bus?
    }

    public int FindAdvertisedName(String namePrefix) {
        Log.i(TAG, "FindAdvertisedName(" + namePrefix + "): Received RPC call");
        return mP2pManager.findAdvertisedName(namePrefix);
    }

    public int CancelFindAdvertisedName(String namePrefix) {
        Log.i(TAG, "CancelFindAdvertisedName(" + namePrefix + "): Received RPC call");
        return mP2pManager.cancelFindAdvertisedName(namePrefix);
    }

    public int AdvertiseName(String name, String guid) {
        Log.i(TAG, "AdvertiseName(" + name + ", " + guid + "): Received RPC call");
        return mP2pManager.advertiseName(name, guid);
    }

    public int CancelAdvertiseName(String name, String guid) {
        Log.i(TAG, "CancelAdvertiseName(" + name + ", " + guid + "): Received RPC call");
        return mP2pManager.cancelAdvertiseName(name, guid);
    }

    public int EstablishLink(String device, int groupOwnerIntent) {
        Log.i(TAG, "EstablishLink(" + device + ", " + groupOwnerIntent + "): Received RPC call");
        return mP2pManager.establishLink(device, groupOwnerIntent);
    }

    public int ReleaseLink(int handle) {
        Log.i(TAG, "ReleaseLink(" + handle + "): Received RPC call");
        return mP2pManager.releaseLink(handle);
    }

    public String GetInterfaceNameFromHandle(int handle) {
        Log.i(TAG, "GetInterfaceNamefromHandle(" + handle + "): Received RPC call");
        return mP2pManager.getInterfaceNameFromHandle(handle);
    }

    public void OnFoundAdvertisedName(String name, String namePrefix, String guid, String device) {
        if (jniReady) {
            Log.i(TAG, "OnFoundAdvertisedName(" + name + ", " + namePrefix + ", " + guid + ", " + device + "): Sending signal");
            jniOnFoundAdvertisedName(name, namePrefix, guid, device);
        } else {
            Log.e(TAG, "OnFoundAdvertisedName() not sent, JNI not available");
        }
    }

    public void OnLostAdvertisedName(String name, String namePrefix, String guid, String device) {
        if (jniReady) {
            Log.i(TAG, "OnLostAdvertisedName(" + name + ", " + namePrefix + ", " + guid + ", " + device + "): Sending signal");
            jniOnLostAdvertisedName(name, namePrefix, guid, device);
        } else {
            Log.e(TAG, "OnLostAdvertisedName() not sent, JNI not available");
        }
    }

    public void OnLinkEstablished(int handle) {
        if (jniReady) {
            Log.i(TAG, "OnLinkEstablished(" + handle + "): Sending signal");
            jniOnLinkEstablished(handle);
        } else {
            Log.e(TAG, "OnLinkEstablished() not sent, JNI not available");
        }
    }

    public void OnLinkError(int handle, int error) {
        if (jniReady) {
            Log.i(TAG, "OnLinkError(" + handle + ", " + error + "): Sending signal");
            jniOnLinkError(handle, error);
        } else {
            Log.e(TAG, "OnLinkError() not sent, JNI not available");
        }
    }

    public void OnLinkLost(int handle) {
        if (jniReady) {
            Log.i(TAG, "OnLinkLost(" + handle + "): Sending signal");
            jniOnLinkLost(handle);
        } else {
            Log.e(TAG, "OnLinkLost() not sent, JNI not available");
        }
    }

    public boolean isReady() {
        return jniReady;
    }
}
