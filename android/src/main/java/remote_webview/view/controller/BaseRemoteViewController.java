package remote_webview.view.controller;

import android.os.Build;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.view.RemoteWebViewController;

/**
 * @author LiJiaqi
 * @date 2021/11/8
 * Description: Manipulate remote view.
 */
abstract public class BaseRemoteViewController {
    
    abstract public void create(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result);

    abstract public void touch(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result);

    abstract public void dispose(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result);


    /**
     * Must a int type from flutter.
     * 
     * If return -1 means occur an error.
     * @param methodCall called from flutter'side.
     * @return view's id.
     */
    protected long getViewId(@NonNull MethodCall methodCall) {
        long viewId = -1;
        try {
            viewId = (long)methodCall.argument("viewId");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return viewId;
    }
    
    protected void ensureValidAndroidVersion(int minSdkVersion) {
        if (Build.VERSION.SDK_INT < minSdkVersion) {
            throw new IllegalStateException("Trying to use platform views with API " + Build.VERSION.SDK_INT + ", required API level is: " + minSdkVersion);
        }
    }

    /**
     * Get error string from exception that throws.
     * @param exception {@linkplain RemoteWebViewController} throws.
     * @return
     */
    protected static String detailedExceptionString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
