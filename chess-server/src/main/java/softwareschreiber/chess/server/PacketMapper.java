package softwareschreiber.chess.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.serdes.PieceSerializer;

public class PacketMapper {
	private final ObjectMapper mapper = JsonMapper.builder()
			.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
			.addModule(new PieceSerializer.Module())
			.build();

	public String toString(Packet<?> packet) {
		try {
			return mapper.writeValueAsString(packet);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize %s packet: %s".formatted(packet.getClass().getSimpleName(), e));
		}
	}

	private JsonNode parse(String json) {
		try {
			return mapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to parse JSON: " + json, e);
		}
	}

	public PacketType getType(String json) {
		return getType(parse(json));
	}

	private PacketType getType(JsonNode node) {
		JsonNode packetTypeJsonNode = node.get("type");

		if (packetTypeJsonNode == null) {
			throw new RuntimeException("Invalid packet: \"" + node.toPrettyString() + "\"");
		}

		return PacketType.fromJsonName(packetTypeJsonNode.asText());
	}

	public <T extends Packet<?>> T fromString(String json, Class<T> packetClass) {
		JsonNode node = parse(json);
		PacketType type = getType(node);

		assert packetClass == type.packetClass();

		try {
			return mapper.treeToValue(node, packetClass);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to parse %s packet:\n%s".formatted(type.jsonName(), node.toPrettyString()), e);
		}
	}
}
