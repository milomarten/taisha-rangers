package com.github.milomarten.taisharangers.image;

import skaro.pokeapi.resource.pokemon.PokemonSprites;

public enum Gender {
    MALE {
        @Override
        public String getSprite(PokemonSprites sprites, boolean shiny) {
            return shiny ? sprites.getFrontShiny() : sprites.getFrontDefault();
        }
    },
    FEMALE {
        @Override
        public String getSprite(PokemonSprites sprites, boolean shiny) {
            return shiny ? sprites.getFrontShinyFemale() : sprites.getFrontFemale();
        }
    };

    public abstract String getSprite(PokemonSprites sprites, boolean shiny);
}
