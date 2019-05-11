package co.ga.freshpotatoes.domain.entity;

import javax.persistence.*;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "films")
public class Film implements Comparable {
    private long id;
    private Genre genre;
    private String title;
    private String tagline;
    private int revenue;
    private int budget;
    private int runtime;
    private String status;
    private String originalLanguage;
    private String releaseDate;
    private Set<Artist> artists;

    public Film() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    @JsonIgnore
    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    @JsonIgnore
    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    @JsonIgnore
    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @JsonIgnore
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    @Column(name = "original_language")
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    @Column(name = "release_date")
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @ManyToOne
    @JoinColumn(name = "genre_id")
    @JsonIgnore
    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    @Transient
    @JsonProperty("genre")
    public String getGenreName() {
      return genre.getName();
    }


    @ManyToMany(mappedBy = "films")
    @JsonIgnore
    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public int compareTo(Object o) {
        Film f = (Film)o;
        if ((f.getId() - this.getId()) < 0) return -1;
        if ((f.getId() - this.getId()) > 0) return 1;
        return 0;
    }

    @Override
    public String toString() {
        String result = String.format(
                "Film[" +
                        "id=%d, " +
                        "genre_id='%d', " +
                        "title='%s', " +
                        "tagline='%s', " +
                        "revenue='%d', " +
                        "budget='%d', " +
                        "runtime='%d', " +
                        "status='%s', " +
                        "original_language='%s', " +
                        "release_date='%s'" +
                "]",
                id,
                genre.getId(),
                title,
                tagline,
                revenue,
                budget,
                runtime,
                status,
                originalLanguage,
                releaseDate);

        if (artists != null) {
            for (Artist artist : artists) {
                result += String.format("Artist[id='%d']", artist.getId());
            }
        }
        return result;
    }
}
