package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "withdraw_waiting")
public class WithdrawWaiting {
    @Id
    @Column(name = "user_id")
    private String id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "request_at")
    private LocalDateTime requestAt;

    @Timestamp
    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;

    public WithdrawWaiting(User user) {
        this.user = user;
        this.requestAt = LocalDateTime.now();
        this.withdrawAt = LocalDateTime.now().plusDays(7);
    }
}
