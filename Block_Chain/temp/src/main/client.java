package main;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

public class client {
	
	public client() {
		connector.setConnectTimeoutMillis(15 * 1000);

		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(textLineCodecFactory));

		connector.setHandler(new handler()); // 핸들러는 구현해줘야 한다.

		ConnectFuture future = connector.connect(new InetSocketAddress("127.0.0.1", 8080)); // 실제 접속

		future.awaitUninterruptibly(); // 접속 기다림

		IoSession session = future.getSession(); // 접속되면 세션을 가져옴

		session.getCloseFuture().awaitUninterruptibly(); // 세션이 닫힐 때까지 기다림

		connector.dispose(); // 접속을 해제함
	}
}
