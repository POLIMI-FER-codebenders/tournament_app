package dsd.codebenders.tournament_app.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dsd.codebenders.tournament_app.entities.GameClass;

import java.io.IOException;

public class GameClassIDFilenameAuthorSerializer extends StdSerializer<GameClass> {
    public GameClassIDFilenameAuthorSerializer() {
        this(null);
    }

    protected GameClassIDFilenameAuthorSerializer(Class<GameClass> t) {
        super(t);
    }

    @Override
    public void serialize(GameClass gameClass, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("id", gameClass.getId());
        jsonGenerator.writeObjectField("filename", gameClass.getFilename());
        jsonGenerator.writeObjectField("author", gameClass.getAuthor().getUsername());
        jsonGenerator.writeEndObject();
    }
}
