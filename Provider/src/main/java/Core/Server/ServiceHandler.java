package Core.Server;

import Core.Center.CallCenter;
import Core.Center.CenterRegistry;
import Core.Pojo.FastRequest;
import Core.Pojo.FastResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 执行服务，返回结果
 */

@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler<FastRequest> {

    private final Logger logger= LoggerFactory.getLogger(ServiceHandler.class);

    private final CenterRegistry centerRegistry;

    public ServiceHandler(CenterRegistry centerRegistry){
        this.centerRegistry=centerRegistry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server {} connection +1",centerRegistry.getServerAddress());
        centerRegistry.editLoad(1);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server {} connection -1",centerRegistry.getServerAddress());
        centerRegistry.editLoad(-1);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FastRequest request) throws Exception {
        logger.info("provider accept a request {}",request);

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
        Object o= BeanFactory.getBean(classObj);
        String methodName= request.getMethodName();
        Class<?>[]paramsType=request.getParamsType();
        Object[]params=request.getParams();

        Method method= classObj.getMethod(methodName,paramsType);
        return method.invoke(o,params);
    }
}
