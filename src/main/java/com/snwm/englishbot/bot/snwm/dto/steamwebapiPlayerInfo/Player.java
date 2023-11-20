package com.snwm.englishbot.bot.snwm.dto.steamwebapiPlayerInfo;

import lombok.Data;

@Data
public class Player {
    private String steamid;
    private Integer communityvisibilitystate;
    private Integer profilestate;
    private String personaname;
    private Integer commentpermission;
    private String profileurl;
    private String avatar;
    private String avatarmedium;
    private String avatarfull;
    private String avatarhash;
    private Long lastlogoff;
    private Integer personastate;
    private String primaryclanid;
    private Long timecreated;
    private Integer personastateflags;
}
