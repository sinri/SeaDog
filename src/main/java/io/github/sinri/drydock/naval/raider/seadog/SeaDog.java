package io.github.sinri.drydock.naval.raider.seadog;

import com.aliyun.fc.runtime.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 应对阿里云FC 2.0的事件请求处理程序。使用时应建立一个类以继承本类，并提供相应的代理类（继承AbstractSeaDogDelegate）。
 * <p>
 * 函数计算 FC 2.0 的事件请求处理程序（Event Handler） 对应的处理程序接口，基于 StreamRequestHandler 实现。
 * </p>
 * <p>
 * 以流的方式接收输入的event事件并返回执行结果。您需要从输入流中读取调用函数时的输入，处理完成后把函数执行结果写到输出流中来返回。
 * </p>
 *
 * @see <a href="https://help.aliyun.com/zh/functioncompute/fc-2-0/user-guide/event-handlers-2">函数计算 FC 2.0 操作指南
 *         代码开发 Java事件请求处理程序（Event Handler）</a>
 * @since 1.0
 */
public abstract class SeaDog<D extends AbstractSeaDogDelegate>
        implements FunctionInitializer, StreamRequestHandler, PreStopHandler, PreFreezeHandler {
    /**
     * <p>
     * 初始化回调程序（Initializer回调）是在函数实例启动成功之后，运行请求处理程序（Handler）之前执行。 函数计算保证在一个实例生命周期内，成功且只成功执行一次Initializer回调。
     * 例如您的Initializer回调第一次执行失败了，系统会重试，直到成功为止，然后再执行您的请求处理程序。 因此，您在实现Initializer回调时，需要保证它被重复调用时的正确性。
     * </p>
     */
    @Override
    public void initialize(Context context) throws IOException {

    }

    /**
     * 创建一个与本类的泛型类型参数对应的AbstractSeaDogDelegate类的实现类的实例。
     * 简单地说，就是创建符合本类要求的执行代理类实例。
     *
     * @param inputStream  输入流
     * @param outputStream 输出流
     * @param context      上下文
     * @return 创建的符合本类要求的执行代理类实例
     */
    protected abstract D createDelegate(InputStream inputStream, OutputStream outputStream, Context context);

    @Override
    public final void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        AbstractSeaDogDelegate delegate = createDelegate(inputStream, outputStream, context);
        delegate.handle();
    }

    /**
     * 预停止回调程序（PreStop回调）在函数实例销毁前执行。
     * <p>
     * 在每次函数计算决定停止当前函数实例前，函数计算服务会调用HTTP {@code GET /pre-stop} 路径，
     * 扩展开发者负责实现相应逻辑以确保完成实例释放前的必要操作，如关闭数据库链接，以及上报、更新状态等。
     * </p>
     */
    @Override
    public void preStop(Context context) throws IOException {

    }

    /**
     * 预冻结回调程序（PreFreeze回调）在函数实例冻结前执行。
     * <p>
     * 在每次函数计算服务决定冷冻当前函数实例前，函数计算服务会调用HTTP {@code GET /pre-freeze}路径，扩展开发者负责实现相应逻辑以确保完成实例冷冻前的必要操作，例如等待指标发送成功等。
     * 函数调用InvokeFunction的时间不包含PreFreeze hook的执行时间。
     * </p>
     */
    @Override
    public void preFreeze(Context context) throws IOException {

    }
}
