package remote_webview.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.CountDownLatch;

import remote_webview.IBinderPool;

public abstract class ProcessServicePresenter {

    private Context mAppContext;

    protected IBinderPool mBinderPool;

    private CountDownLatch mConnectionBinderPoolCountDownLatch;

    ProcessServicePresenter(Context context){
        this.mAppContext = context;
    }

    /**
     * the service.class that will connect
     * @return service.class
     */
    public abstract Class<Service> getServiceClass();


    /**
     * connect remote service.
     *
     * it's recommended to connect it in a new thread.
     */
    public void initConnectService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectRemoteService();
            }
        }).start();
    }

    /**
     *
     * @param binderCode represents a Binder from remote-web-service
     * @return IBinder
     */
    public IBinder queryBinderByCode(int binderCode) {
        IBinder binder = null;
        try {
            if(mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    /**
     * connect remote service.
     */
    private synchronized void connectRemoteService() {
        mConnectionBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mAppContext, getServiceClass());
        mAppContext.bindService(service,serviceConnection,Context.BIND_AUTO_CREATE);
        try {
            mConnectionBinderPoolCountDownLatch.await();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     * TODO : think about really need do this at this scene;
     */
    private final IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool = null;
            connectRemoteService();
        }
    };


}
