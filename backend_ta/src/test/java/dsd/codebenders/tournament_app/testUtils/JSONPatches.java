package dsd.codebenders.tournament_app.testUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dsd.codebenders.tournament_app.entities.Player;

public class JSONPatches extends SimpleModule {
    public JSONPatches() {
        super("JSONPatches");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Player.class, IgnoreNothing.class);
    }
}

@JsonIgnoreProperties
class IgnoreNothing {
}