package remote_webview.view;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

import remote_webview.RemoteZygoteActivity;
import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.utils.RemoteViewHandler;
import remote_webview.utils.StringUtil;

/**
 * the {@link remote_webview.service.binders.RemoteViewFactoryBinder} processor.
 * receive the order from binder and to create a real-view .
 *
 */

public class RemoteViewFactoryProcessor implements IGarbageCleanListener {

    private static volatile RemoteViewFactoryProcessor singleton;

    public static RemoteViewFactoryProcessor getInstance() {
        if(singleton == null) {
            synchronized (RemoteViewFactoryProcessor.class) {
                if(singleton == null) {
                    singleton = new RemoteViewFactoryProcessor();
                }
            }
        }
        return singleton;
    }

    private RemoteViewFactoryProcessor() {}

    private final HashMap<Integer, WebViewPresentation> viewCache = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createWithSurface(final WebViewCreationParamsModel creationParams, final Surface surface) {
        RemoteViewHandler.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final WebViewPresentation presentation;
                try {
                    final int surfaceId = creationParams.getSurfaceId();
                    presentation = RemoteZygoteActivity.generateWebViewPresentation(surfaceId,surface);
                    //todo cached presentation and need remove when it's disposed
                    viewCache.put(surfaceId, presentation);

                    presentation.createWithOrders(creationParams);
                    presentation.showWithUrl();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void dispatchTouchEvent(String surfaceId, MotionEvent event) {
        try {
            viewCache.get(surfaceId).dispatchTouchEvent(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanGarbage(int id) {
        try {
            viewCache.get(id).dispose();
        }catch (Exception e) {
            e.printStackTrace();
        }
        viewCache.remove(id);
    }

    @Override
    public void cleanAll() {
        viewCache.clear();
    }
}









