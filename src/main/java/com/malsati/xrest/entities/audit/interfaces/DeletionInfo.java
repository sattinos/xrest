package com.malsati.xrest.entities.audit.interfaces;

import java.time.LocalDate;

public interface DeletionInfo {
    boolean getDeleted();
    void setDeleted(boolean value);
    LocalDate getDeletedAt();
    void setDeletedAt(LocalDate localDate);
    String getDeletedBy();
    void setDeletedBy(String userName);
}