package softwareschreiber.chess.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {
	public static void main(String[] args) {
		ChessServer server = new ChessServer(3010);
		server.start();
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String in = sysIn.readLine();

				if (Objects.equals(in, "exit")) {
					server.stop(1000);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
