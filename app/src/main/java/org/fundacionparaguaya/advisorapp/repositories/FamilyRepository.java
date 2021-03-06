package org.fundacionparaguaya.advisorapp.repositories;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import org.fundacionparaguaya.advisorapp.data.local.FamilyDao;
import org.fundacionparaguaya.advisorapp.data.remote.FamilyService;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.FamilyIr;
import org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation.IrMapper;
import org.fundacionparaguaya.advisorapp.models.Family;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

import static java.lang.String.format;

/**
 * The utility for the storage of families and their members.
 */
public class FamilyRepository {
    private static final String TAG = "FamilyRepository";

    private final FamilyDao familyDao;
    private final FamilyService familyService;

    @Inject
    public FamilyRepository(FamilyDao familyDao,
                            FamilyService familyService) {
        this.familyDao = familyDao;
        this.familyService = familyService;
    }

    //region Family
    public LiveData<List<Family>> getFamilies() {
        return familyDao.queryFamilies();
    }

    /**
     * Gets the families synchronously.
     */
    public List<Family> getFamiliesNow() {
        return familyDao.queryFamiliesNow();
    }

    public LiveData<Family> getFamily(int id) {
        return familyDao.queryFamily(id);
    }

    /**
     * Gets a family synchronously.
     */
    public Family getFamilyNow(int id) {
        return familyDao.queryFamilyNow(id);
    }

    public void saveFamily(Family family) {
        long rows = familyDao.updateFamily(family);
        if (rows == 0) { // no row was updated
            int id = (int) familyDao.insertFamily(family);
            family.setId(id);
        }
    }

    /**
     * Synchronizes the local families with the remote database.
     * @return Whether the sync was successful.
     */
    boolean sync() {
        boolean successful;
        successful = pushFamilies();
        if (successful) {
            successful = pullFamilies();
        }
        return successful;
    }

    void clean() {
        familyDao.deleteAll();
    }

    private boolean pushFamilies() {
        List<Family> pending = familyDao.queryPendingFamiliesNow();
        boolean success = true;

        // attempt to push each of the pending families
        for (Family family : pending) {
            try {
                Response<FamilyIr> response = familyService
                        .postFamily(IrMapper.mapFamily(family))
                        .execute();

                if (response.isSuccessful() || response.body() != null) {
                    // overwrite the pending family with the family from remote db
                    Family remoteFamily = IrMapper.mapFamily(response.body());
                    remoteFamily.setId(family.getId());
                    saveFamily(remoteFamily);
                } else {
                    Log.w(TAG, format("pushFamilies: Could not push family with id %d! %s", family.getId(), response.errorBody().string()));
                    success = false;
                }
            } catch (IOException e) {
                Log.e(TAG, format("pushFamilies: Could not push family with id %d!", family.getId()), e);
                success = false;
            }
        }
        return success;
    }

    private boolean pullFamilies() {
        try {
            Response<List<FamilyIr>> response =
                    familyService.getFamilies().execute();

            if (!response.isSuccessful() || response.body() == null) {
                Log.w(TAG, format("pullFamilies: Could not pull families! %s", response.errorBody().string()));
                return false;
            }

            List<Family> families = IrMapper.mapFamilies(response.body());
            for (Family family : families) {
                Family old = familyDao.queryRemoteFamilyNow(family.getRemoteId());
                if (old != null) {
                    family.setId(old.getId());
                }
                saveFamily(family);
            }
        } catch (IOException e) {
            Log.e(TAG, "pullFamilies: Could not pull families!", e);
            return false;
        }
        return true;
    }
    //endregion
}
