package com.honeyosori.dogfile.domain.user.entity;

import com.honeyosori.dogfile.domain.user.identity.BlockIdentity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Block {
    @EmbeddedId
    private BlockIdentity blockIdentity;

    public Block(BlockIdentity blockIdentity) {
        this.blockIdentity = blockIdentity;
    }
}
