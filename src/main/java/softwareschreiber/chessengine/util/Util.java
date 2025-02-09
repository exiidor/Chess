package softwareschreiber.chessengine.util;

import java.nio.file.Path;
import java.net.URISyntaxException;
import java.net.URL;

public class Util {
	public static Path getResource(String slashPrefixedResourcePath) {
		try {
			URL url = Util.class.getResource(slashPrefixedResourcePath);

			if (url == null) {
				throw new IllegalArgumentException("Resource not found: " + slashPrefixedResourcePath);
			}

			return Path.of(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
