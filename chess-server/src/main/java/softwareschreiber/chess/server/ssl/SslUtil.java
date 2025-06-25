package softwareschreiber.chess.server.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Objects;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class SslUtil {
	public static SSLContext getContext() {
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
