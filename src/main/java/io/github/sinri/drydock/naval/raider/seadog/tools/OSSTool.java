package io.github.sinri.drydock.naval.raider.seadog.tools;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.Credentials;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.*;
import io.vertx.core.Closeable;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.io.File;
import java.io.InputStream;
import java.util.function.Function;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * 建立对OSS的运用机制，供SeaDog的Delegate利用。
 *
 * @since 1.0
 */
public class OSSTool implements Closeable {
    public static String HangzhouInternalEndpoint = "https://oss-cn-hangzhou-internal.aliyuncs.com";
    public static String HangzhouExternalEndpoint = "https://oss-cn-hangzhou.aliyuncs.com";
    private final OSS ossClient;

    public OSSTool(Context context, String endpoint) {
        Credentials credentials = context.getExecutionCredentials();
        ossClient = new OSSClientBuilder()
                .build(
                        endpoint,
                        credentials.getAccessKeyId(),
                        credentials.getAccessKeySecret(),
                        credentials.getSecurityToken()
                );
    }

    /**
     * 可以通过这个方法获取原生的 OSS 类实例。但尽量不直接使用。
     *
     * @return 返回原生的 OSS 类实例。
     */
    public OSS getOssClient() {
        return ossClient;
    }

    @Override
    public void close(Promise<Void> completion) {
        ossClient.shutdown();
        completion.complete();
    }

    public Future<Void> uploadToOss(String bucket, String key, File file) {
        return Keel.executeBlocking(promise -> {
            try {
                PutObjectResult putObjectResult = getOssClient().putObject(new PutObjectRequest(bucket, key, file));
                ResponseMessage response = putObjectResult.getResponse();
                boolean successful = response.isSuccessful();
                if (successful) {
                    promise.complete();
                } else {
                    promise.fail("Failed to upload file to OSS, error response: " + response.getErrorResponseAsString());
                }
            } catch (Throwable throwable) {
                promise.fail(throwable);
            }
        });
    }

    public Future<Void> uploadToOss(String bucket, String key, InputStream inputStream) {
        return Keel.executeBlocking(promise -> {
            try {
                PutObjectResult putObjectResult = getOssClient().putObject(new PutObjectRequest(bucket, key, inputStream));
                ResponseMessage response = putObjectResult.getResponse();
                boolean successful = response.isSuccessful();
                if (successful) {
                    promise.complete();
                } else {
                    promise.fail("Failed to transfer inputStream to OSS, error response: " + response.getErrorResponseAsString());
                }
            } catch (Throwable throwable) {
                promise.fail(throwable);
            }
        });
    }

    public Future<Void> downloadFromOssToLocalFile(String bucket, String key, File file) {
        return Keel.executeBlocking(promise -> {
            try {
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
                ObjectMetadata objectMetadata = getOssClient().getObject(getObjectRequest, file);
                promise.complete();
            } catch (Throwable throwable) {
                promise.fail(throwable);
            }
        });
    }

    public Future<Void> downloadFromOssAsStream(String bucket, String key, Function<InputStream, Future<Void>> streamProcessor) {
        return Keel.executeBlocking(promise -> {
            try {
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
                OSSObject ossObject = getOssClient().getObject(getObjectRequest);
                InputStream objectContent = ossObject.getObjectContent();
                streamProcessor.apply(objectContent)
                               .onSuccess(result -> promise.complete())
                               .onFailure(promise::fail);
            } catch (Throwable throwable) {
                promise.fail(throwable);
            }
        });
    }

    public Future<Boolean> whetherFileExisted(String bucket, String key) {
        return Keel.executeBlocking(promise -> {
            try {
                boolean b = getOssClient().doesObjectExist(bucket, key);
                promise.complete(b);
            } catch (Throwable throwable) {
                promise.fail(throwable);
            }
        });
    }
}
