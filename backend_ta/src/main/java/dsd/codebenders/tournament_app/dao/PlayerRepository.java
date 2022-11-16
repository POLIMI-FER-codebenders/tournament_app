package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p FROM Player p WHERE p.username = ?1")
    Player findByUsername(String username);

    @Query("SELECT p FROM Player p WHERE p.email = ?1")
    Player findByEmail(String email);

}
