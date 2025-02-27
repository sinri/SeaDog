package io.github.sinri.drydock.naval.raider.seadog.sample;

import com.aliyun.fc.runtime.Context;
import io.github.sinri.drydock.naval.raider.seadog.AbstractSeaDogDelegate;
import io.vertx.core.Future;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class SeaDogSampleDelegate extends AbstractSeaDogDelegate {
    public SeaDogSampleDelegate(InputStream inputStream, OutputStream outputStream, Context context) {
        super(inputStream, outputStream, context);
    }

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
