package ca.etsmtl.applets.etsmobile.util;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by gnut3ll4 on 26/03/16.
 */
public class Synchronizer<T> {

    private Dao<T, ?> dao;

    public Synchronizer(Dao<T, ?> dao) {
        this.dao = dao;
    }

    public void synchronize(List<T> remoteObjects) {

        if(remoteObjects == null) {
            return;
        }

        try {
            // Deletes entries in DB that don't exist on API
            List<T> localObjects = dao.queryForAll();
            for (T localObject : localObjects) {
                if (!remoteObjects.contains(localObject)) {
                    dao.delete(localObject);

                }
            }

            // Adds new API entries on DB or updates existing ones
            for (T remoteObject : remoteObjects) {
                dao.createOrUpdate(remoteObject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}