package com.honeyosori.dogfile.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jdk.jfr.Timestamp;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "withdraw_waiting")
public class WithdrawWaiting {
    @Id
    @OneToOne
    public User user;

    @CreationTimestamp
    @Column(name = "request_at")
    public LocalDateTime requestAt;

    @Timestamp
    @Column(name = "withdraw_at")
    public LocalDateTime withdrawAt;

    public WithdrawWaiting(User user) {
        this.user = user;
        this.requestAt = LocalDateTime.now();
        this.withdrawAt = LocalDateTime.now().plusDays(7);
    }
}
