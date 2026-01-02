package io.neskdev.api.lobby;

import java.util.List;
import java.util.UUID;

public interface LobbyManager {

    Lobby getLobby(String name);

    List<Lobby> getLobbies();

    Lobby getBestLobby();

    void sendPlayerToLobby(UUID playerUniqueId, Lobby lobby);

}
