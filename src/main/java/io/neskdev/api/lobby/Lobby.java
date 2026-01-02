package io.neskdev.api.lobby;

public interface Lobby {

    String getName();

    int getOnlinePlayers();

    int getMaxPlayers();

    boolean isOnline();

}
