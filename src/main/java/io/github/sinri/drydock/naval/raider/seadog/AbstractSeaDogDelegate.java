package io.github.sinri.drydock.naval.raider.seadog;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;

import java.io.InputStream;
import java.io.OutputStream;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * @since 1.0
 */
public abstract class AbstractSeaDogDelegate {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Context context;

    public AbstractSeaDogDelegate(InputStream inputStream, OutputStream outputStream, Context context) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.context = context;
    }

    protected final Context getContext() {
        return context;
    }

    protected final InputStream getInputStream() {
        return inputStream;
    }

    protected final OutputStream getOutputStream() {
        return outputStream;
    }

    public FunctionComputeLogger getLogger() {
        return getContext().getLogger();
    }

    protected VertxOptions buildVertxOptions() {
        return new VertxOptions();
    }

    protected Future<Void> loadConfiguration() {
        // to load the configuration from anywhere...
        //        Keel.getConfiguration().loadPropertiesFile("config.properties");
        return Future.succeededFuture();
    }

    public void handle() {
        VertxOptions vertxOptions = buildVertxOptions();
        getLogger().debug("Vertx Options Built");
        Keel.initializeVertxStandalone(vertxOptions);
        Keel.pseudoAwait(promise -> {
            Future.succeededFuture()
                  .compose(v -> {
                      getLogger().debug("Vertx Started");
                      return loadConfiguration();
                  })
                  .compose(v -> {
                      getLogger().debug("Configuration Loaded");
                      return asyncExecute();
                  })
                  .onComplete(ar -> {
                      if (ar.succeeded()) {
                          getLogger().debug("Promise Complete");
                          promise.complete();
                      } else {
                          getLogger().error("Promise Fail with " + ar.cause().getMessage());
                          promise.fail(ar.cause());
                      }
                  });
        });
        Keel.getVertx().close(promise -> {
            getLogger().debug("Vertx Closed");
        });
    }

    protected abstract Future<Void> asyncExecute();
}
