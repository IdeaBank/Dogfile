package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.user.identity.BlockIdentity;
import jakarta.persistence.*;

@Entity
public class Block {
    @EmbeddedId
    private BlockIdentity blockIdentity;
}
