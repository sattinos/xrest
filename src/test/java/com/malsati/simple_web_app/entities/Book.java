package com.malsati.simple_web_app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.malsati.xrest.entities.audit.base_classes.CreateEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "Book")
public class Book extends CreateEntity {
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

    @ManyToMany(mappedBy = "books")
    @JsonBackReference
    private List<Author> authors;

    public Book(Long id) {
        this.id = id;
    }
}