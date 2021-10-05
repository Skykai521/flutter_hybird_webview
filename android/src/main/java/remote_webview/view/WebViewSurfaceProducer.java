package remote_webview.view;

import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import remote_webview.model.ViewSurfaceModel;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.model.WebViewSurfaceClient;
import remote_webview.service.RemoteServicePresenter;


public class WebViewSurfaceProducer {

    public static WebViewSurfaceProducer producer = new WebViewSurfaceProducer();

    private WebViewSurfaceProducer() {
    }

    private FlutterPlugin.FlutterPluginBinding flutterPluginBinding;

    public void holdFlutterBinding(FlutterPlugin.FlutterPluginBinding binding) {
        this.flutterPluginBinding = binding;
    }

    public final HashMap<Integer, ViewSurfaceModel> surfaceModelCache = new HashMap<>();


    public int buildGeneralWebViewSurface(Map<String, Object> params) {

        final WebViewSurfaceClient surfaceModel = new WebViewSurfaceClient.Builder(flutterPluginBinding.getApplicationContext())
                .init(flutterPluginBinding.getTextureRegistry().createSurfaceTexture())
                .build(flutterPluginBinding);
        //todo test model
        WebViewCreationParamsModel paramsModel = new WebViewCreationParamsModel(
                surfaceModel.getId(), false,
                new HashMap<String, String>(),null,1,"",
                "https://www.jd.com/"
        );
        try {
            RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder()
                    .createWithSurface(paramsModel,surfaceModel.getSurface());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        cacheViewSurfaceModel(surfaceModel);

        return surfaceModel.getId();
    }

    public void cacheViewSurfaceModel(ViewSurfaceModel surfaceModel) {
        surfaceModelCache.put(surfaceModel.getId(), surfaceModel);
    }

    public void remoteViewSurfaceModel(int id) {
        surfaceModelCache.remove(id);
    }


}







