package remote_webview.service.manager;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author LiJiaqi
 * @date 2021/11/27
 * Description: Manage all remote-view module.
 */

public class RemoteViewModuleManager {

    private static RemoteViewModuleManager instance;

    public static RemoteViewModuleManager getInstance() {
        if(instance == null) {
            synchronized (RemoteViewModuleManager.class) {
                if(instance == null) {
                    instance = new RemoteViewModuleManager();
                }
            }
        }
        return instance;
    }

    private PatrolDog dog = PatrolDog.getInstance();

    private ViewSavedInstance savedInstance = new ViewSavedInstance();

    Handler handler;

    private RemoteViewModuleManager() {
        handler = new Handler(callback);
    }


    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                //todo need patrol type (diff patrol time)
            }
            return true;
        }
    };


    public HashMap getSavedInstance() {
        return savedInstance.getSavedInstance();
    }

    public void setSavedInstance(HashMap status) {
        savedInstance.setSavedInstance(status);
    }


    private static class PatrolRequest {

        RemoteViewModuleManager manager;
        //unit: milliseconds
        int patrolTime;


        PatrolRequest(RemoteViewModuleManager moduleManager, int patrolTime) {
            this.manager = moduleManager;
            this.patrolTime = patrolTime;
        }
    }


    /**
     * Hold the view's saved instance for restore status in need.
     */
    private static class ViewSavedInstance {

        private AtomicBoolean isValid = new AtomicBoolean(false);

        private HashMap savedInstance;

        public boolean isValid() {
            return isValid.get();
        }

        public HashMap getSavedInstance() {
            return isValid.getAndSet(false) ? savedInstance : new HashMap();
        }

        public void setSavedInstance(HashMap savedInstance) {
            isValid.set(true);
            this.savedInstance = savedInstance;
        }
    }

    private static class PatrolDog extends Thread{

        private static final PatrolDog instance;

        static {
            instance = new PatrolDog();
            instance.start();
        }

        public static PatrolDog getInstance() {
            return instance;
        }

        private ArrayBlockingQueue<PatrolRequest> mQueue = new ArrayBlockingQueue<>(10);

        private void patrol() {
            PatrolRequest request;
            try {
                request = mQueue.take();
                //do patrol
                sleep(request.patrolTime);
            }catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            Message.obtain(request.manager.handler, 0);

        }

        @Override
        public void run() {
            while (true){
                patrol();
            }
        }
    }

}
