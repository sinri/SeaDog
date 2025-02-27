package io.github.sinri.drydock.naval.raider.seadog;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 函数计算 FC 2.0 的事件请求处理程序（Event Handler） 对应的处理程序接口，基于 StreamRequestHandler 实现。
 * <p>
 * 以流的方式接收输入的event事件并返回执行结果。您需要从输入流中读取调用函数时的输入，处理完成后把函数执行结果写到输出流中来返回。
 * </p>
 *
 * @see <a href="https://help.aliyun.com/zh/functioncompute/fc-2-0/user-guide/event-handlers-2">函数计算 FC 2.0 操作指南
 *         代码开发 Java事件请求处理程序（Event Handler）</a>
 * @since 1.0
 */
public abstract class SeaDog<D extends AbstractSeaDogDelegate> implements StreamRequestHandler {


    protected abstract D createDelegate(InputStream inputStream, OutputStream outputStream, Context context);

    @Override
    public final void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        AbstractSeaDogDelegate delegate = createDelegate(inputStream, outputStream, context);
        delegate.handle();
    }


}
