package softwareschreiber.chess.server.packet.serdes;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.server.packet.data.component.PiecePojo;

public class PieceSerializer extends StdSerializer<Piece> {
	public PieceSerializer() {
		super(Piece.class);
	}

	@Override
	public void serialize(Piece value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		PiecePojo pojo = new PiecePojo(value);
		gen.writePOJO(pojo);
	}

	public static class Module extends SimpleModule {
		public Module() {
			super("PieceSerializerModule");
			addSerializer(Piece.class, new PieceSerializer());
		}
	}
}
