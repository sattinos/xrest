package com.malsati.simple_web_app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "Book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    protected LocalDate createdAt;

    @Column(name = "created_by")
    @CreatedBy
    protected String createdBy;

    private String title;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    private int edition;
    private int volume;
    private String press;

    @Column(name = "no_pages")
    private int noPages;

    @ManyToMany(mappedBy = "books")
    @JsonBackReference
    private List<Author> authors;

    public Book(Long id) {
        this.id = id;
    }

    public void set(String title,
                    Integer edition,
                    Integer volume,
                    String press,
                    Integer noPages) {
        if( title != null) {
            this.title = title;
        }
        if( edition != null) {
            this.edition = edition;
        }
        if( volume != null) {
            this.volume = volume;
        }
        if( press != null) {
            this.press = press;
        }

        if( noPages != null) {
            this.noPages = noPages;
        }
    }
}