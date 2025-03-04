package io.github.sinri.drydock.naval.raider.seadog.sample;

import com.aliyun.fc.runtime.Context;
import io.github.sinri.drydock.naval.raider.seadog.AbstractSeaDogDelegate;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class SeaDogSampleDelegate extends AbstractSeaDogDelegate {
    public SeaDogSampleDelegate(InputStream inputStream, OutputStream outputStream, Context context) {
        super(inputStream, outputStream, context);
    }

    @Nonnull
    @Override
    protected Future<Void> asyncExecute() {
        getLogger().info("io.github.sinri.drydock.naval.raider.seadog.sample.SeaDogSampleDelegate.asyncExecute");

        String inputAsString;
        try {
            byte[] bytes = getInputStream().readAllBytes();
            inputAsString = new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("input: " + inputAsString);
        String env1 = this.readEnvironmentVariable("env1");
        getLogger().info("env1: " + env1);


        KeelIssueRecorder<KeelEventLog> issueRecorder = generateIssueRecorder("IssueRecorder", KeelEventLog::new);
        Map<String, String> envMap = readEnvironmentVariables();
        JsonObject envObj = new JsonObject();
        envMap.forEach(envObj::put);
        issueRecorder.notice("env",envObj);

        return Keel.asyncCallStepwise(3, i -> {
                       getLogger().info("i: " + i);
                       return Keel.asyncSleep(1000L);
                   })
                   .compose(v -> {
                       getLogger().info("done");
                       try {
                           getOutputStream().write("OK".getBytes(StandardCharsets.UTF_8));
                       } catch (IOException e) {
                           throw new RuntimeException(e);
                       }
                       return Future.succeededFuture();
                   });
    }
}
