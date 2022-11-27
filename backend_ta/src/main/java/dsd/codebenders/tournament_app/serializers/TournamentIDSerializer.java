package dsd.codebenders.tournament_app.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dsd.codebenders.tournament_app.entities.KnockoutTournament;

public class TournamentIDSerializer extends StdSerializer<KnockoutTournament> {
    public TournamentIDSerializer() {
        this(null);
    }

    public TournamentIDSerializer(Class<KnockoutTournament> t) {
        super(t);
    }

    @Override
    public void serialize(KnockoutTournament value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getID());
    }

    @Override
    public void serializeWithType(KnockoutTournament value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        serialize(value, gen, serializers);
    }
}
