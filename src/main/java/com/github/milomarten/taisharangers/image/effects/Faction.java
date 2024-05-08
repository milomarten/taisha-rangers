package com.github.milomarten.taisharangers.image.effects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Faction {
    NONE("frame.png"),
    POLICE("frame-police.png"),
    GANG("frame-gang.png"),
    BANK("frame-bank.png"),
    CORRUPTED("frame-corrupted.png")
    ;

    private final String filename;
}
