package remote_webview.service.binders;

import android.os.RemoteException;
import android.util.Log;


import remote_webview.IRemoteMethodChannelBinder;
import remote_webview.model.MethodModel;


/**
 * handle main-process's order
 */
public class RemoteMethodChannelBinder extends IRemoteMethodChannelBinder.Stub {
    @Override
    public void invokeMethod(MethodModel model) throws RemoteException {
        Log.e("remote service", model.toString());
    }
}
