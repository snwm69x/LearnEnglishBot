package com.snwm.englishbot.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_word_stats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWordStats {
    
    @EmbeddedId
    private UserWordStatsId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "word_id", insertable = false, updatable = false)
    private Word word;

    @Column(name = "correct_attempts")
    private Integer correctAttempts;

    @Column(name = "incorrect_attempts")
    private Integer incorrectAttempts;

    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;
}

