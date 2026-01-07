package com.miruni.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DelaySituation {

    PHONE(1, "휴대폰을 사용할 때 (SNS, 게임 등)"),
    VIDEO(2, "넷플릭스, 드라마, 유튜브 등 영상 시청"),
    MEET_FRIENDS(4, "친구, 사람들을 만날 때"),
    TOO_MUCH_WORK(8, "다른 일이 너무 많을 때"),
    TOO_TIRED(16, "너무 피곤할 때");

    private final int value;
    private final String description;

    public static boolean isSet(long mask, DelaySituation situation) {
        return (mask & situation.getValue()) != 0;
    }

    public static long addToMask(long mask, DelaySituation situation) {
        return mask | situation.getValue();
    }

    public static long removeFromMask(long mask, DelaySituation situation) {
        return mask & ~situation.getValue();
    }

    public static Set<DelaySituation> fromMask(long mask) {
        return Arrays.stream(values())
            .filter(situation -> isSet(mask, situation))
            .collect(Collectors.toSet());
    }

    public static long createMask(Set<DelaySituation> situations) {
        long mask = 0;
        for (DelaySituation situation : situations) {
            mask = addToMask(mask, situation);
        }
        return mask;
    }
}

