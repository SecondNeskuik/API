package io.neskdev.api;

import lombok.Getter;
import lombok.Setter;
import io.neskdev.api.database.DatabaseManager;
import io.neskdev.api.rank.RankRepository;

public interface NeskAPI {

    RankRepository getRankRepository();

    DatabaseManager getDatabaseManager();


    static NeskAPI getProvider() {
        return Provider.getProvider();
    }

    static void setProvider(NeskAPI api) {
        Provider.setProvider(api);
    }

    class Provider {
        @Getter @Setter
        private static NeskAPI provider;
    }
}
