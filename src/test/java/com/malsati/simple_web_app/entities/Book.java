package com.malsati.simple_web_app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.malsati.xrest.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
@Entity
@Table(name = "Book")
public class Book extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String title;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    private int edition;
    private int volume;
    private String press;

    @Column(name = "no_pages")
    private int noPages;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = true)
    @JsonBackReference
    private Author author;

    public Book(Long id) {
        this.id = id;
    }
}