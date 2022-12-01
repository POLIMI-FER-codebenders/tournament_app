package dsd.codebenders.tournament_app.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dsd.codebenders.tournament_app.entities.Tournament;

public class TournamentIDSerializer extends StdSerializer<Tournament> {
    public TournamentIDSerializer() {
        this(null);
    }

    public TournamentIDSerializer(Class<Tournament> t) {
        super(t);
    }

    @Override
    public void serialize(Tournament value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getID());
    }

    @Override
    public void serializeWithType(Tournament value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
    }
}
