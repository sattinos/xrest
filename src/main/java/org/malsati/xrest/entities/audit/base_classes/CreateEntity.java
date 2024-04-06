package org.malsati.xrest.entities.audit.base_classes;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.malsati.xrest.entities.audit.interfaces.CreationInfo;

@MappedSuperclass
public abstract class CreateEntity implements CreationInfo {
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    protected LocalDate createdAt;
    @Column(name = "created_by")
    @CreatedBy
    protected String createdBy;
    @Override
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    @Override
    public String getCreatedBy() {
        return createdBy;
    }
}