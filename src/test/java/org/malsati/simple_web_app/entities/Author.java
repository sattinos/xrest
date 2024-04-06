package org.malsati.simple_web_app.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.malsati.xrest.entities.audit.base_classes.FullAuditEntity;
import jakarta.persistence.*;
import lombok.Data;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "Author")
public class Author extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "full_name", unique = true)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToMany
    @JoinTable(
            name = "author_books",
            joinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    private List<Book> books;
}