package com.malsati.xrest.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@MappedSuperclass
public abstract class BaseEntity<TKeyType extends Serializable> {
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    protected LocalDate createdAt;

    @CreatedBy
    protected String createdBy;
    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDate updatedAt;
    @LastModifiedBy
    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "is_deleted")
    protected boolean isDeleted = false;

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        this.isDeleted = deleted != null ? deleted : false;
    }

    // TODO-Malsati: Think of an automated way to find the user who deleted
    protected LocalDate deletedAt;

    protected Long deletedBy;
}