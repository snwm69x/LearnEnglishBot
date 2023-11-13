package com.snwm.englishbot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "promoted_channel")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotedChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_link")
    private String channelLink;

    @Column(name = "channel_description")
    private String channelDescription;

}
