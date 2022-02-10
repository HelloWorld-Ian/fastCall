package Core.Server;

import Annotation.FastService;
import Core.Center.CallCenterConfiguration;
import Core.Center.CenterRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class ServiceInitConfiguration {
    Logger logger= LoggerFactory.getLogger(ServiceInitConfiguration.class);

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    CenterRegistry centerRegistry;

    @Autowired
    CallCenterConfiguration callCenterConfiguration;

    @PostConstruct
    private void init(){
        logger.info("Rpc server start scanning service provider ");
        Map<String,Object>providerBeans=applicationContext.getBeansWithAnnotation(FastService.class);
        if(null!=providerBeans&&!providerBeans.isEmpty()){
            providerBeans.entrySet().forEach(entry ->{
                initProviderBean(entry.getKey(),entry.getClass());
            });
        }
        startNettyServer(callCenterConfiguration.getPort());
    }

    private void initProviderBean(String beanName,Object bean){
        FastService fastService=applicationContext.findAnnotationOnBean(beanName,FastService.class);
        BeanFactory.addBeans(fastService.value(),bean);
    }

    private void startNettyServer(int port){
        ServerBootstrap b=new ServerBootstrap();
        EventLoopGroup bossGroup= new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();

        ServiceHandler handler=new ServiceHandler();
        try {
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(handler);
                        }
                    }).option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

                ChannelFuture f=b.bind(port).sync();

                logger.info("server start successfully on port {}",port);
                logger.info("server is attempting to register on call center");

                centerRegistry.register();
                f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("start server fail");
        } catch (KeeperException e) {
            e.printStackTrace();
            logger.error("server register to call center fail");
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
