package Core;

import Center.CallCenterApplication;
import FastFramer.Message.FastRequest;
import FastFramer.Message.FastResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
public class FastServer {
    Logger logger= LoggerFactory.getLogger(FastServer.class);

    private CallCenterApplication application;

    private ServiceFactory serviceFactory;

    private FastServer(){}

    protected static FastServer newInstance(){
        return new FastServer();
    }

    public void start(int port){
        ServerBootstrap b=new ServerBootstrap();
        EventLoopGroup bossGroup= new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();

        ServiceHandler handler=new ServiceHandler(application);
        try {
            // bossGroup、workerGroup 主从设计模式
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new FastDecoder(FastRequest.class))
                                    .addLast(new FastEncoder(FastResponse.class))
                                    .addLast(handler);
                        }
                    }).option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

                ChannelFuture f=b.bind(port).sync();

                logger.info("server start successfully on port {}",port);
                logger.info("server is attempting to register on call center");

                application.register();

                f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("server register to call center fail");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
