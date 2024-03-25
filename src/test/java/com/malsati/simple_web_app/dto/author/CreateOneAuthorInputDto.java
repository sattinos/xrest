package com.malsati.simple_web_app.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOneAuthorInputDto {
    private String fullName;
    private LocalDate birthDate;
    private Collection<Long> bookIds;
}
