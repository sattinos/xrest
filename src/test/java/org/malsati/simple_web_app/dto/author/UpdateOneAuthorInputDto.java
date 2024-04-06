package org.malsati.simple_web_app.dto.author;


import org.malsati.xrest.entities.audit.interfaces.IdentityInfo;

import java.time.LocalDate;
import java.util.Collection;

public class UpdateOneAuthorInputDto extends CreateOneAuthorOutputDto implements IdentityInfo<Long> {
    public UpdateOneAuthorInputDto(Long id, String fullName, LocalDate birthDate, Collection<Long> bookIds) {
        super(id, fullName, birthDate, bookIds);
    }

    public UpdateOneAuthorInputDto(Long id, LocalDate birthDate, Collection<Long> bookIds) {
        super(id, null, birthDate, bookIds);
    }

    public UpdateOneAuthorInputDto(Long id, Collection<Long> bookIds) {
        super(id, null, null, bookIds);
    }

    public UpdateOneAuthorInputDto() {
    }
}