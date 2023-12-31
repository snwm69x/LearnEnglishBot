package com.snwm.englishbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snwm.englishbot.entity.enums.WordLevel;
import com.snwm.englishbot.entity.enums.WordType;
import com.snwm.englishbot.utils.TranslateConverter;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "words")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties({"users"})
public class Word {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Word cannot be empty")
    private String word;

    @Column
    @Convert(converter = TranslateConverter.class)
    @NotBlank(message = "Translation cannot be empty")
    private List<String> translation;

    @Column
    private String transcription;

    @ManyToMany(fetch = FetchType.EAGER,
    mappedBy = "words", 
    cascade = {CascadeType.DETACH, 
            CascadeType.MERGE, 
            CascadeType.PERSIST, 
            CascadeType.REFRESH})
    private List<User> users;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "WordLevel cannot be empty")
    private WordLevel  wordLevel;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "WordType cannot be empty")
    private WordType wordType;
}