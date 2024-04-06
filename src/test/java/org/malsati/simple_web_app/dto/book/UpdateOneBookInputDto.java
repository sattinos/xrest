package org.malsati.simple_web_app.dto.book;

import org.malsati.xrest.entities.audit.interfaces.IdentityInfo;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

@NoArgsConstructor
public class UpdateOneBookInputDto extends CreateOneBookInputDto implements IdentityInfo<Long> {
    private Long id;

    public UpdateOneBookInputDto(String title,
                                 LocalDate publishDate,
                                 Integer edition,
                                 Integer volume,
                                 String press,
                                 Integer noPages,
                                 Long id) {
        super(title, publishDate, edition, volume, press, noPages, null);
        this.id = id;
    }

    public UpdateOneBookInputDto(String title,
                                 LocalDate publishDate,
                                 int edition,
                                 int volume,
                                 Long id) {
        super(title, publishDate, edition, volume, null , null, null);
        this.id = id;
    }

    public UpdateOneBookInputDto(int edition,
                                 int volume,
                                 String press,
                                 int noPages,
                                 Long id) {
        super(null, null, edition, volume, press, noPages, null);
        this.id = id;
    }

    public UpdateOneBookInputDto(String title,
                                 Integer edition,
                                 Integer volume,
                                 Long id) {
        super(title, null, edition, volume, null, null, null);
        this.id = id;
    }

    public UpdateOneBookInputDto(String title,
                                 Integer edition,
                                 Integer volume,
                                 Long id,
                                 ArrayList<Long> authorsIds) {
        super(title, null, edition, volume, null, null, authorsIds);
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
