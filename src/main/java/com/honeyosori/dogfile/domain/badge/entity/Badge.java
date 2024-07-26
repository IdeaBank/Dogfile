package com.honeyosori.dogfile.domain.badge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @OneToMany(mappedBy = "badge")
    private List<OwnBadge> badgeList;

    public Badge(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
