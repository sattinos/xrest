package com.malsati.simple_web_app.dto.book;

import com.malsati.xrest.entities.audit.interfaces.IdentityInfo;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
public class UpdateOneBookInputDto extends CreateOneBookOutputDto implements IdentityInfo<Long> {

    public UpdateOneBookInputDto(String title,
                                 LocalDate publishDate,
                                 Integer edition,
                                 Integer volume,
                                 String press,
                                 Integer noPages,
                                 Long id) {
        super(title, publishDate, edition, volume, press, noPages, id);
    }

    public UpdateOneBookInputDto(String title,
                                 LocalDate publishDate,
                                 int edition,
                                 int volume,
                                 Long id) {
        super(title, publishDate, edition, volume, null , null, id);
    }

    public UpdateOneBookInputDto(int edition,
                                 int volume,
                                 String press,
                                 int noPages,
                                 Long id) {
        super(null, null, edition, volume, press, noPages, id);
    }

    public UpdateOneBookInputDto(String title,
                                 Integer edition,
                                 Integer volume,
                                 Long id) {
        super(title, null, edition, volume, null, null, id);
    }
}
