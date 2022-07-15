package com.demo.withfootball.modules.community.event;

import com.demo.withfootball.modules.community.Community;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommunityUpdateEvent{

    private final Community community;

    private final String message;

}
