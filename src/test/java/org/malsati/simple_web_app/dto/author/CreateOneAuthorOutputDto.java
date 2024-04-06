package org.malsati.simple_web_app.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOneAuthorOutputDto extends CreateOneAuthorInputDto {
    private Long id;

    public CreateOneAuthorOutputDto(Long id, String fullName, LocalDate birthDate, Collection<Long> bookIds) {
        super(fullName, birthDate, bookIds);
        this.id = id;
    }
}
