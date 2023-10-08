package com.snwm.englishbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snwm.englishbot.entity.enums.WordLevel;
import lombok.*;

import javax.persistence.*;
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
    private String word;
    @Column
    private String translation;
    @Column
    private String transcription;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "words", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<User> users;

    @Enumerated
    private WordLevel wordLevel;
}