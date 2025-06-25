package softwareschreiber.chess.server.ssl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class SslUtil {
	public static SSLContext getContext() {
		SSLContext context;
		String password = "";
		String certPath = Objects.requireNonNull(System.getProperty("CERT_PATH"));
		String privateKeyPath = Objects.requireNonNull(System.getProperty("PRIVATE_KEY_PATH"));

		try {
			context = SSLContext.getInstance("TLS");

			byte[] certBytes = parseDerFromPem(
					getBytes(new File(certPath)),
					"-----BEGIN CERTIFICATE-----",
					"-----END CERTIFICATE-----");
			byte[] keyBytes = parseDerFromPem(
					getBytes(new File(privateKeyPath)),
					"-----BEGIN PRIVATE KEY-----",
					"-----END PRIVATE KEY-----");

			X509Certificate cert = generateCertificateFromDer(certBytes);
			RSAPrivateKey key = generatePrivateKeyFromDer(keyBytes);

			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(null);
			keystore.setCertificateEntry("cert-alias", cert);
			keystore.setKeyEntry("key-alias", key, password.toCharArray(), new Certificate[]{cert});

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, password.toCharArray());

			KeyManager[] km = kmf.getKeyManagers();

			context.init(km, null, null);
		} catch (Exception e) {
			context = null;
		}

		return context;
	}

	private static byte[] parseDerFromPem(byte[] pem, String beginDelimiter, String endDelimiter) {
		String data = new String(pem);
		String[] tokens = data.split(beginDelimiter);
		tokens = tokens[1].split(endDelimiter);

		return Base64.getDecoder().decode(tokens[0]);
	}

	private static RSAPrivateKey generatePrivateKeyFromDer(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");

		return (RSAPrivateKey) factory.generatePrivate(spec);
	}

	private static X509Certificate generateCertificateFromDer(byte[] certBytes) throws CertificateException {
		CertificateFactory factory = CertificateFactory.getInstance("X.509");

		return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
	}

	private static byte[] getBytes(File file) {
		byte[] bytesArray = new byte[(int) file.length()];
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			fis.read(bytesArray);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bytesArray;
	}
}
