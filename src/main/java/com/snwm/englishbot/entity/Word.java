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
    @Column(name = "word_id")
    private Long id;
    @Column(name = "word")
    private String word;
    @Column(name = "translation")
    private String translation;
    @Column(name = "transcription")
    private String transcription;
}
