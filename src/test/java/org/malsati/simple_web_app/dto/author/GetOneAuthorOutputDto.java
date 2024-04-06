package org.malsati.simple_web_app.dto.author;

import org.malsati.simple_web_app.dto.book.GetOneBookOutputDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOneAuthorOutputDto {
    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private Collection<GetOneBookOutputDto> books;
}
