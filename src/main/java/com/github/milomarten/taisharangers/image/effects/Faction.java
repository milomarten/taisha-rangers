package com.github.milomarten.taisharangers.image.effects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Faction {
    NONE("frame.png"),
    POLICE("frame-police.png");

    private final String filename;
}
