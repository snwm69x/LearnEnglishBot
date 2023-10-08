package com.snwm.englishbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snwm.englishbot.entity.enums.UserType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"words"})
public class User {
    @Id
    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "userWords",
            joinColumns =
                    {@JoinColumn(name = "user_id", referencedColumnName = "chat_id")},
            inverseJoinColumns =
                    {@JoinColumn(name = "word_id", referencedColumnName = "id")})
    private List<Word> words;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
