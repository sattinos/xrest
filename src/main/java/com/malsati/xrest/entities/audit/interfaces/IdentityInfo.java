package com.malsati.xrest.entities.audit.interfaces;

import java.io.Serializable;

public interface IdentityInfo<TKeyType extends Serializable> {
    TKeyType getId();
}
