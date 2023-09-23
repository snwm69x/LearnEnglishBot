package com.snwm.englishbot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "words")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String translation;
    private String transcription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wordList")
    private User userData;
}
