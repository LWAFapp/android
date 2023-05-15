package com.Zakovskiy.lwaf.models.enums;

public enum CommentReaction {
    DISLIKE(0),
    LIKE(1);

    public Integer type;

    CommentReaction(Integer type) {
        this.type = type;
    }
}
