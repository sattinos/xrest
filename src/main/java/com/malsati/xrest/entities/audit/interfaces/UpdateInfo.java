package com.malsati.xrest.entities.audit.interfaces;

import java.time.LocalDate;

public interface UpdateInfo {
    LocalDate getUpdatedAt();
    String getUpdateBy();
}
