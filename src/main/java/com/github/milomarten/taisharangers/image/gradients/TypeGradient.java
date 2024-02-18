package com.github.milomarten.taisharangers.image.gradients;

import com.github.milomarten.taisharangers.image.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Color table based on Pokemon types.
 * Only handles the normal 18 types. PokeAPI supports Unknown and Shadow types, but no Pokemon have them.
 */
@RequiredArgsConstructor
@Getter
public enum TypeGradient {
    NORMAL(new Color(224, 238, 224), new Color(176, 187, 160)),
    FIGHTING(new Color(208, 153, 128), new Color(176, 85, 64)),
    FLYING(new Color(144, 187, 240), new Color(96, 153, 240)),
    POISON(new Color(192, 136, 176), new Color(160, 85, 144)),
    GROUND(new Color(224, 204, 144), new Color(208, 187, 180)),
    ROCK(new Color(208, 204, 144), new Color(176, 170, 96)),
    BUG(new Color(192, 221, 96), new Color(160, 187, 32)),
    GHOST(new Color(144, 153, 192), new Color(96, 102, 176)),
    STEEL(new Color(192, 204, 192), new Color(160, 170, 176)),
    FIRE(new Color(240, 136, 112), new Color(240, 68, 32)),
    WATER(new Color(112, 187, 240), new Color(48, 153, 240)),
    GRASS(new Color(160, 221, 128), new Color(112, 204, 80)),
    ELECTRIC(new Color(240, 221, 112), new Color(240, 204, 48)),
    PSYCHIC(new Color(240, 153, 176), new Color(240, 85, 144)),
    ICE(new Color(160, 238, 240), new Color(112, 221, 240)),
    DRAGON(new Color(160, 153, 224), new Color(112, 102, 224)),
    DARK(new Color(160, 153, 128), new Color(112, 85, 64)),
    FAIRY(new Color(240, 204, 240), new Color(240, 170, 240))
    ;
    private final Color lighter;
    private final Color darker;
}
