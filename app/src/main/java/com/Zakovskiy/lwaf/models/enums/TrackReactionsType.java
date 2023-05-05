package com.Zakovskiy.lwaf.models.enums;

public enum TrackReactionsType {
    UNDEFINED(0),
    LIKE(1),
    DISLIKE(2),
    SUPER_LIKE(3);

    private Integer type;

    TrackReactionsType(Integer type) {
        this.type = type;
    }

    public static TrackReactionsType fromInt(int value) {
        for (TrackReactionsType reaction : TrackReactionsType.values()) {
            if (reaction.type == value) {
                return reaction;
            }
        }
        return null;
    }
}
