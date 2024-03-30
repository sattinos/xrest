package com.malsati.simple_web_app.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOneBookOutputDto extends CreateOneBookInputDto {
    private Long id;

    public CreateOneBookOutputDto(String title,
                                  LocalDate publishDate,
                                  Integer edition,
                                  Integer volume,
                                  String press,
                                  Integer noPages,
                                  Long id) {
        super(title, publishDate, edition, volume, press, noPages);
        this.id = id;
    }
}