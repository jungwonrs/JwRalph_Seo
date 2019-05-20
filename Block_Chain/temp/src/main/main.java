package main;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NioSocketAcceptor acceptor = new NioSocketAcceptor();

		acceptor.getFilterChain().addLast("logger", new LoggingFilter());

		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));

		acceptor.setHandler(new handler()); // 핸들러는 구현해줘야 한다.

		try {
			acceptor.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8080));
		}catch(Exception e) {
			
		}
	}

}
