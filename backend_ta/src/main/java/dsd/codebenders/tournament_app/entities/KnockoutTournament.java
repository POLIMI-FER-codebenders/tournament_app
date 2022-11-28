package dsd.codebenders.tournament_app.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("KNOCKOUT")
public class KnockoutTournament extends Tournament {
}
