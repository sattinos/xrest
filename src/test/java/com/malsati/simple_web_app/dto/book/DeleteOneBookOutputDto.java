package com.malsati.simple_web_app.dto.book;

import java.time.LocalDate;

public class DeleteOneBookOutputDto extends UpdateOneBookInputDto {
    public DeleteOneBookOutputDto(String title, LocalDate publishDate, int edition, int volume, String press, int noPages, Long id) {
        super(title, publishDate, edition, volume, press, noPages, id);
    }
}
