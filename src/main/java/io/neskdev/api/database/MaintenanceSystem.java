package io.neskdev.api.database;

import java.util.UUID;

public interface MaintenanceSystem {

    void isMaintenance();
    void setMaintenance(boolean status);
    void addAllowedPlayer(UUID uuid);
    void removeAllowedPlayer(UUID uuid);
    boolean isAllowed(UUID uuid);

}
