package remote_webview.service.hub;

import android.util.LongSparseArray;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;

import io.flutter.plugin.common.MethodChannel;
import remote_webview.garbage_collect.MainGarbageCollector;
import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;

public class MainBinderCommHub extends BinderCommunicateHub implements IGarbageCleanListener {
    
    private static volatile MainBinderCommHub singleton;
    
    public static MainBinderCommHub getInstance() {
        if(singleton == null) {
            synchronized (MainBinderCommHub.class) {
                if(singleton == null) {
                    singleton = new MainBinderCommHub();
                }
            }
        }
        return singleton;
    }

    private final MethodChannel.Result defaultResultCallback = new MethodChannel.Result() {
        @Override
        public void success(@Nullable Object o) {

        }

        @Override
        public void error(String s, @Nullable String s1, @Nullable Object o) {

        }

        @Override
        public void notImplemented() {

        }
    };

    //Cache the flutter-methodCall's resultCallback
    private final LongSparseArray<MethodChannel.Result> resultCallbackCache
            = new LongSparseArray<MethodChannel.Result>();
    
    private MainBinderCommHub() {
        MainGarbageCollector.getInstance().registerCollectListener(this);
    }

    /**
     *
     * @param id invoke timestamp.
     */
    public void cacheResultCallback(long id, MethodChannel.Result result) {
        resultCallbackCache.put(id, result);
    }

    /**
     *
     * @param id invoke timestamp.
     */
    public void removeCacheResultCallback(long id) {
        resultCallbackCache.remove(id);
    }

    public void remoteCacheCallback() {
        //todo
    }


    /**
     * In main process, the invoke-method's resultCallback will pass 2 thread
     * that is platform thread and flutter thread, so there must override this method and recall
     * flutter's result callback in it, to ensure the correct association of MethodCall and
     * MethodChannel.Result.
     *
     * @see #resultCallbackCache
     * @see  io.flutter.plugin.common.MethodChannel
     * @see  io.flutter.plugin.common.MethodChannel.Result
     *
     * @param id : invoke-method timeStamp as an id for linked  {@linkplain IMockMethodResult}.
     * @param call {@linkplain MockMethodCall.id} is came from surface's id, and marked a view.
     * @throws NullPointerException
     */
    @Override
    protected void invokeMethodById(final long id, MockMethodCall call) throws NullPointerException {
        Objects.requireNonNull(methodHandlerSlot.get(call.id)).onMethodCall(call, new IMockMethodResult() {
            @Override
            public void success(@Nullable HashMap var1) {
                //result to flutter
                resultCallbackCache.get(id, defaultResultCallback).success(var1);

                Objects.requireNonNull(methodResultCallbackSlog.get(id)).success(var1);
                removeMethodResultCallbackById(id);
            }

            @Override
            public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
                resultCallbackCache.get(id, defaultResultCallback).error(var1, var2, var3);

                Objects.requireNonNull(methodResultCallbackSlog.get(id)).error(var1, var2, var3);
                removeMethodResultCallbackById(id);
            }

            @Override
            public void notImplemented() {
                resultCallbackCache.get(id, defaultResultCallback).notImplemented();
            }
        });
    }

    @Override
    public void cleanGarbage(long id) {
        plugOutMethodHandler(id);
    }

    @Override
    public void cleanAll() {
        cleanAllMethodHandler();
        cleanAllMethodResultCallback();
    }
}
