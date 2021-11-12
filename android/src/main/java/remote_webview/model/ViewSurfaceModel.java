package remote_webview.model;

import android.view.Surface;

import androidx.annotation.CallSuper;


abstract public class ViewSurfaceModel {
    
    private final long id;
    
    private final Surface surface;

    protected ViewSurfaceModel(long id, Surface surface) {
        this.id = id;
        this.surface = surface;
    }

    public long getId() {
        return id;
    }

    public Surface getSurface() {
        return surface;
    }

    @CallSuper
    public void release() {
        surface.release();
    }

}
















