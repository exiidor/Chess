package softwareschreiber.chess.server.util;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.Objects;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.tinylog.Logger;

import softwareschreiber.chess.server.packet.Packet;

public class Util {
	public static String ipPlusPort(InetSocketAddress address) {
		return address.getHostName() + ":" + address.getPort();
	}

	public static void failedToSerialize(Packet<?> packet, Exception exception) {
		Logger.error(exception, "Failed to serialize {} packet \"{}\"", packet.type().name(), packet);
	}

	public static SSLContext getSslContext() {
		SSLContext context;
		String keystorePath = Objects.requireNonNull(System.getenv("KEYSTORE_PATH"));
		String keystorePassword = Objects.requireNonNull(System.getenv("KEYSTORE_PASSWORD"));

		try {
			// Load the keystore
			KeyStore keyStore = KeyStore.getInstance("PKCS12");

			try (FileInputStream fis = new FileInputStream(keystorePath)) {
				keyStore.load(fis, keystorePassword.toCharArray());
			}

			// Set up key manager factory with the keystore
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keyStore, keystorePassword.toCharArray());

			// Initialize the SSL context
			context = SSLContext.getInstance("TLS");
			context.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return context;
	}
}
