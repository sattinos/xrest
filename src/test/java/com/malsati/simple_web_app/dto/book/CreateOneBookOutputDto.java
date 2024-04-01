package com.malsati.simple_web_app.dto.book;

import com.malsati.simple_web_app.dto.common.IdFullNameDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOneBookOutputDto {
    private Long id;
    private String title;
    private LocalDate publishDate;
    private Integer edition;
    private Integer volume;
    private String press;
    private Integer noPages;

    private List<IdFullNameDto> authors = new ArrayList<>();

    public CreateOneBookOutputDto(String title, LocalDate publishDate, Integer edition, Integer volume, String press, Integer noPages, Long id) {
        this.id = id;
        this.title = title;
        this.publishDate = publishDate;
        this.edition = edition;
        this.volume = volume;
        this.press = press;
        this.noPages = noPages;
    }
}