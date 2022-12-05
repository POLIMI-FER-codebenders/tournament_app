package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.serializers.PlayerIDAndNameSerializer;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Table(name = "game_class")
public class GameClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonSerialize(using = PlayerIDAndNameSerializer.class)
    private Player author;

    @Basic(fetch = FetchType.LAZY)
    @Lob
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
