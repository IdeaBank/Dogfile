package com.honeyosori.dogfile.domain.badge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column
    private String title;

    @Getter
    @Column
    private String description;

    @OneToMany(mappedBy = "badge")
    private List<OwnBadge> badgeList;

    public Badge(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
