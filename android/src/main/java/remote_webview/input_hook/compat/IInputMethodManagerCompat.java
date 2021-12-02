package remote_webview.input_hook.compat;

import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;

import remote_webview.input_hook.util.ReflectUtil;


public class IInputMethodManagerCompat {

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("com.android.internal.view.IInputMethodManager");
        }
        return sClass;
    }

    public static Object asInterface( IBinder binder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("com.android.internal.view.IInputMethodManager$Stub");
        return ReflectUtil.invokeStaticMethod(clazz, "asInterface", new Class[]{IBinder.class}, new Object[]{binder});
    }

}
