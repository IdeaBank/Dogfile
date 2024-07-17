package com.honeyosori.dogfile.domain.badge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String description;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OwnBadge> badgeList;

    public Badge(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
