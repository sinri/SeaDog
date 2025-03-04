package io.github.sinri.drydock.naval.raider.seadog.logger;

import com.aliyun.fc.runtime.FunctionComputeLogger;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.logger.issue.record.KeelIssueRecord;
import io.github.sinri.keel.logger.issue.recorder.adapter.KeelIssueRecorderAdapter;
import io.github.sinri.keel.logger.issue.recorder.render.KeelIssueRecordRender;
import io.vertx.core.Promise;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @since 1.0
 */
public class FCIssueRecordCenter implements KeelIssueRecordCenter {
    private final FCIssueRecorderAdapter fcIssueRecorderAdapter;

    public FCIssueRecordCenter(FunctionComputeLogger functionComputeLogger) {
        this.fcIssueRecorderAdapter = new FCIssueRecorderAdapter(functionComputeLogger);
    }

    @Nonnull
    @Override
    public KeelIssueRecorderAdapter getAdapter() {
        return this.fcIssueRecorderAdapter;
    }

    private static class FCIssueRecorderAdapter implements KeelIssueRecorderAdapter {
        private final FunctionComputeLogger functionComputeLogger;

        public FCIssueRecorderAdapter(FunctionComputeLogger functionComputeLogger) {
            this.functionComputeLogger = functionComputeLogger;
        }

        @Override
        public KeelIssueRecordRender<?> issueRecordRender() {
            //return KeelIssueRecordRender.renderForJsonObject();
            return KeelIssueRecordRender.renderForString();
        }

        @Override
        public void record(@Nonnull String topic, @Nullable KeelIssueRecord<?> issueRecord) {
            if (issueRecord != null) {
                String s = issueRecordRender().renderIssueRecord(topic, issueRecord).toString();
                switch (issueRecord.level()) {
                    case DEBUG:
                        functionComputeLogger.debug(s);
                        break;
                    case INFO:
                    case NOTICE:
                        functionComputeLogger.info(s);
                        break;
                    case WARNING:
                        functionComputeLogger.warn(s);
                        break;
                    case ERROR:
                        functionComputeLogger.error(s);
                        break;
                    case FATAL:
                        functionComputeLogger.fatal(s);
                        break;
                    //                    default:
                    //                        functionComputeLogger.trace(s);
                }
            }
        }

        @Override
        public void close(@Nonnull Promise<Void> promise) {
            // no effect, just ignore
            promise.fail("FunctionComputeLogger is not closeable.");
        }

        @Override
        public boolean isStopped() {
            return false;
        }

        @Override
        public boolean isClosed() {
            return false;
        }
    }
}
