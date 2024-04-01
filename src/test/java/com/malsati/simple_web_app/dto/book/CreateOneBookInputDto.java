package com.malsati.simple_web_app.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOneBookInputDto {
    private String title;
    private LocalDate publishDate;
    private Integer edition;
    private Integer volume;
    private String press;
    private Integer noPages;
    private List<Long> authorsIds = new ArrayList<>();

    public CreateOneBookInputDto(String title, LocalDate publishDate, Integer edition, Integer volume, String press, Integer noPages) {
        this.title = title;
        this.publishDate = publishDate;
        this.edition = edition;
        this.volume = volume;
        this.press = press;
        this.noPages = noPages;
    }
}