package softwareschreiber.chess.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.tinylog.Logger;

import softwareschreiber.chess.server.ssl.SslUtil;

public class Main {
	public static void main(String[] args) {
		ChessServer server = new ChessServer(3010);
		boolean useSsl = Boolean.parseBoolean(System.getenv("USE_SSL"));

		if (useSsl) {
			SSLContext sslContext = SslUtil.getContext();

			if (sslContext == null) {
				Logger.error("Failed to initialize SSL context. Exiting.");
				return;
			}

			server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
		}

		server.start();
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String in = sysIn.readLine();

				if (in == null) {
					continue;
				}

				if (in.equals("exit")) {
					server.stop(1000);
				} else if (in.startsWith("kick ")) {
					server.kick(in.substring("kick ".length()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
