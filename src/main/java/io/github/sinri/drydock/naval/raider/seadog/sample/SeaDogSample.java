package io.github.sinri.drydock.naval.raider.seadog.sample;

import com.aliyun.fc.runtime.Context;
import io.github.sinri.drydock.naval.raider.seadog.SeaDog;

import java.io.InputStream;
import java.io.OutputStream;


public class SeaDogSample extends SeaDog<SeaDogSampleDelegate> {

    @Override
    protected SeaDogSampleDelegate createDelegate(InputStream inputStream, OutputStream outputStream, Context context) {
        return new SeaDogSampleDelegate(inputStream, outputStream, context);
    }
}
