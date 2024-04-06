package org.malsati.xrest.entities.audit.base_classes;

import org.malsati.xrest.entities.audit.interfaces.DeletionInfo;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;

@MappedSuperclass
public abstract class FullAuditEntity extends AuditEntity implements DeletionInfo {
    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    protected Boolean deleted = false;
    @Column(name = "deleted_at")
    protected LocalDate deletedAt;
    @Column(name = "deleted_by")
    protected String deletedBy;
    @Override
    public boolean getDeleted() {
        return deleted;
    }
    @Override
    public void setDeleted(boolean value) {
        deleted = value;
    }
    @Override
    public LocalDate getDeletedAt() {
        return deletedAt;
    }
    @Override
    public void setDeletedAt(LocalDate localDate) {
        deletedAt = localDate;
    }
    @Override
    public String getDeletedBy() {
        return deletedBy;
    }
    @Override
    public void setDeletedBy(String userName) {
        deletedBy = userName;
    }
}