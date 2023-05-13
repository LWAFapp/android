package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

public enum FriendType {
    REMOVE_FRIEND(0, R.string.remove_from_friends, PacketDataKeys.FRIEND_DELETE_FRIENDSHIP),
    ACCEPT_REQUEST(1, R.string.accept_friend_request, PacketDataKeys.FRIEND_ACCEPT_FRIENDSHIP),
    CANCEL_REQUEST(2, R.string.cancel_friend_request, PacketDataKeys.FRIEND_DELETE_FRIENDSHIP),
    ADD_FRIEND(3, R.string.add_friend, PacketDataKeys.FRIEND_ADD_TO_FRIENDSHIP_LIST);

    public Integer type;
    public Integer title;
    public String packetType;

    FriendType(Integer type, Integer title, String packetType) {
        this.type = type;
        this.title = title;
        this.packetType = packetType;
    }
}
