package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.serializers.PlayerIDAndNameSerializer;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "game_class")
@JsonIgnoreProperties(value = {"data"}, allowSetters = true)
public class GameClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonSerialize(using = PlayerIDAndNameSerializer.class)
    private Player author;

    @OneToMany(mappedBy = "realClass", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CDGameClass> cdGameClasses;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    @JsonIgnore
    private byte[] data;

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public Player getAuthor() {
        return author;
    }

    public byte[] getData() {
        return data;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
