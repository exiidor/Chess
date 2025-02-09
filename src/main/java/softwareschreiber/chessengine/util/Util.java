package softwareschreiber.chessengine.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

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
