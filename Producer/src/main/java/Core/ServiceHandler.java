package Core;

import Core.Bo.FastRequest;
import Core.Bo.FastResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 执行服务，返回结果
 */

@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler<FastRequest> {

    private final Logger logger= LoggerFactory.getLogger(ServiceHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FastRequest request) throws Exception {
        logger.info("producer accept a request {}",request);

        FastResponse response=FastResponse.builder().build();
        response.setRequestId(request.getRequestId());
        try{
            Object res=handle(request);
            response.setRes(res);
        }catch (Exception e){
            response.setError(e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("netty exception caught",cause);
        ctx.close();
    }

    private Object handle(FastRequest request) throws Exception {
        String className=request.getClassName();
        Class<?>classObj=Class.forName(className);
        Object o=BeanFactory.getBean(classObj);
        String methodName= request.getMethodName();
        Class<?>[]paramsType=request.getParamsType();
        Object[]params=request.getParams();

        Method method= classObj.getMethod(methodName,paramsType);
        return method.invoke(o,params);
    }
}
