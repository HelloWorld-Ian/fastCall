# fastCall<br>

### fastCall基本介绍<br>
fastCall是一个基于Java语言的分布式RPC框架demo，供RPC初学者学习使用 <br>
fastCall框架采用netty+zookeeper+springboot作为技术栈<br>
fastCall分为客户端、服务端和注册中心，并且通过注册中心实现了客户端与服务端之间的解耦，客户端用于接受请求，服务端用于提供服务，注册中心用于管理服务端集群，客户端与服务端之间的交互通过远程动态代理实现<br>

### fastCall RPC分布式框架的实现基础：<br>
1. Call ID映射，在远程调用的过程中，每个方法都需要对应一个具有唯一性的ID，这个ID在客户端和服务端中都是唯一确定的，客户端在做远程调用时必须附上这个ID，fastCall框架使用方法签名作为ID<br>
2. 序列化和反序列化，在参数传递和结果返回的过程中需要将传输的数据进行序列化和反序列化，以字节流的形式进行传输，fastCall采用protocol buffer作为序列化方式<br>
3. 网络传输，使用一套网络传输协议进行网络传输，例如HTTP、TCP、UDP等，fastCall采用netty框架实现网络传输<br>
4. 分布式实现，fastCall采用ZooKeeper作为注册中心实现分布式，分布式集群中的所有服务都要在ZooKeeper中进行注册，向ZooKeeper提供IP地址、端口等一系列信息，当发送方发送请求时，会从ZooKeeper注册中心中获取服务提供方的IP、端口，再向服务提供方请求服务

