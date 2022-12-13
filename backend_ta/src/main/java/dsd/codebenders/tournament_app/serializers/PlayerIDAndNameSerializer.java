package dsd.codebenders.tournament_app.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dsd.codebenders.tournament_app.entities.Player;

public class PlayerIDAndNameSerializer extends StdSerializer<Player> {
    public PlayerIDAndNameSerializer() {
        this(null);
    }

    public PlayerIDAndNameSerializer(Class<Player> t) {
        super(t);
    }

    @Override
    public void serialize(Player player, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("id", player.getID());
        jsonGenerator.writeObjectField("name", player.getUsername());
        jsonGenerator.writeEndObject();
    }
}
