package Core;

import Center.CallCenterApplication;
import FastFramer.Message.FastRequest;
import FastFramer.Message.FastResponse;
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

    private final CallCenterApplication application;

    public ServiceHandler(CallCenterApplication application){
        this.application=application;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server {} connection +1",application.getServerName());
        application.updateLoad(1);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server {} connection -1",application.getServerName());
        application.updateLoad(-1);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FastRequest request) throws Exception {
        logger.info("provider accept a request {}",request);

        FastResponse response= FastResponse.builder().build();
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
        String interfaceClassName=request.getInterfaceClass();
        String referenceName= request.getReferenceClass();
        Object o= ServiceFactory.getService(interfaceClassName,referenceName);
        if(o==null){
            return null;
        }
        String methodName= request.getMethodName();
        Class<?>[]paramsType=request.getParamsType();
        Object[]params=request.getParams();
        Method method= o.getClass().getMethod(methodName,paramsType);
        return method.invoke(o,params);
    }
}
