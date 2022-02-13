package Core.Client;

import Core.Pojo.FastRequest;
import Core.Pojo.FastResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class Client extends SimpleChannelInboundHandler<FastResponse> {

    private final Logger logger= LoggerFactory.getLogger(Client.class);

    private final String host;
    private final Integer port;

    CompletableFuture<String>future=new CompletableFuture<>();

    public Client(String host, Integer port){
        this.host=host;
        this.port=port;
    }

    /**
     * 远程调用获取的response
     */
    private FastResponse response;

    public FastResponse remoteExecute(FastRequest request){
        EventLoopGroup workerGroup= new NioEventLoopGroup();
        Bootstrap bootstrap= new Bootstrap();

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new FastDecoder(FastResponse.class))
                                .addLast(new FastEncoder(FastRequest.class))
                                .addLast(Client.this);
                    }
                }).option(ChannelOption.SO_KEEPALIVE,true);
        try {
            ChannelFuture future=bootstrap.connect(new InetSocketAddress(host,port)).sync();
            future.channel().writeAndFlush(request).sync();

            future.get();

            future.channel().closeFuture().sync();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error("execute error");
            return null;
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FastResponse fastResponse) throws Exception {
        this.response=fastResponse;
        future.complete("");
    }
}
