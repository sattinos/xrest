package com.malsati.xrest.entities.audit.interfaces;

import java.time.LocalDate;

public interface CreationInfo {
    LocalDate getCreatedAt();
    String getCreatedBy();
}
