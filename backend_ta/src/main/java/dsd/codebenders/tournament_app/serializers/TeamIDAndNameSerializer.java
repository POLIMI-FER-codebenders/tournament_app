package dsd.codebenders.tournament_app.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dsd.codebenders.tournament_app.entities.Team;

public class TeamIDAndNameSerializer extends StdSerializer<Team> {
    public TeamIDAndNameSerializer() {
        this(null);
    }

    public TeamIDAndNameSerializer(Class<Team> t) {
        super(t);
    }

    @Override
    public void serialize(Team team, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("id", team.getID());
        jsonGenerator.writeObjectField("name", team.getName());
        jsonGenerator.writeEndObject();
    }
}
