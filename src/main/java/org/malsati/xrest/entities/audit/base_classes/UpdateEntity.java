package org.malsati.xrest.entities.audit.base_classes;

import org.malsati.xrest.entities.audit.interfaces.UpdateInfo;

import jakarta.persistence.Column;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDate;

@MappedSuperclass
public class UpdateEntity implements UpdateInfo {
    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDate updatedAt;
    @LastModifiedBy
    @Column(name = "updated_by")
    protected String updatedBy;
    @Override
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    @Override
    public String getUpdateBy() {
        return updatedBy;
    }
}