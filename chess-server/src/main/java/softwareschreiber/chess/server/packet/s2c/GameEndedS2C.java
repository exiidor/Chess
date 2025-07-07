package softwareschreiber.chess.server.packet.s2c;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.engine.MateKind;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.s2c.GameEndedS2C.Data;

public record GameEndedS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.GameEndedS2C;
	}

	public record Data(MateKind mateKind, @Nullable UserInfo winner) { }
}
