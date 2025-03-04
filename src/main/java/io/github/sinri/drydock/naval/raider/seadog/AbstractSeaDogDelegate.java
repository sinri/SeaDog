package io.github.sinri.drydock.naval.raider.seadog;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import io.github.sinri.drydock.naval.raider.seadog.logger.FCIssueRecordCenter;
import io.github.sinri.drydock.naval.raider.seadog.tools.OSSTool;
import io.github.sinri.keel.logger.issue.record.KeelIssueRecord;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * 执行代理类的基类。
 * 继承本类以实现一个基于Keel环境的FC流作业。
 *
 * @since 1.0
 */
public abstract class AbstractSeaDogDelegate {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Context context;
    private final FCIssueRecordCenter issueRecordCenter;

    public AbstractSeaDogDelegate(InputStream inputStream, OutputStream outputStream, Context context) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.context = context;
        this.issueRecordCenter = new FCIssueRecordCenter(context.getLogger());
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

    /**
     * @return FC环境下的原生日志记录器
     */
    protected final FunctionComputeLogger getLogger() {
        return getContext().getLogger();
    }

    /**
     * 基于FC环境下的原生日志记录器，创建一个 KeelIssueRecorder。
     *
     * @param topic              日志记录器的主题
     * @param issueRecordBuilder 指定类型的日志记录的创建器
     * @param <R>                指定类型的日志记录
     * @return 所创建的 KeelIssueRecorder 实例
     */
    @Nonnull
    protected <R extends KeelIssueRecord<R>> KeelIssueRecorder<R> generateIssueRecorder(@Nonnull String topic, @Nonnull Supplier<R> issueRecordBuilder) {
        return this.issueRecordCenter.generateIssueRecorder(topic, issueRecordBuilder);
    }

    /**
     * 如需自定义Vertx运行环境参数，可重载此方法。
     *
     * @return 创建一个VertxOptions作为环境配置。
     */
    @Nonnull
    protected VertxOptions buildVertxOptions() {
        return new VertxOptions();
    }

    /**
     * 如需加载额外的配置文件，可重载此方法。
     * 比如使用打包时设定的本地{@code config.properties}文件，或使用远程配置服务获取配置信息。
     */
    @Nonnull
    protected Future<Void> loadConfiguration() {
        // to load the configuration from anywhere...
        //        Keel.getConfiguration().loadPropertiesFile("config.properties");
        return Future.succeededFuture();
    }

    /**
     * 执行代理的执行流程。
     * 注意，针对输入流和输出流的处理均应在 {@link AbstractSeaDogDelegate#asyncExecute()} 方法中处理。
     */
    public final void handle() {
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

    /**
     * 实现此方法，在所提供的 Keel with Vertx 环境下执行业务。
     *
     * @return 返回一个Future。如果失败，FC任务将抛出异常；是否有重试机制视实际FC配置决定。
     */
    @Nonnull
    protected abstract Future<Void> asyncExecute();

    /**
     * 根据指定的 OSS Endpoint，建立一个即用即释放的OSS工具环境，并按需异步返回执行结果。
     */
    @Nonnull
    protected <R> Future<R> withOssTool(@Nonnull String endpoint, @Nonnull Function<OSSTool, Future<R>> usage) {
        return Future.succeededFuture()
                     .compose(v -> {
                         OSSTool ossTool = new OSSTool(context, endpoint);
                         return usage.apply(ossTool);
                     });
    }

    /**
     * @return 所有环境变量之Map。
     */
    protected Map<String, String> readEnvironmentVariables() {
        return System.getenv();
    }

    /**
     * @param variableName 指定环境变量的名称
     * @return 指定环境变量
     */
    @Nullable
    protected String readEnvironmentVariable(@Nonnull String variableName) {
        return System.getenv(variableName);
    }
}
