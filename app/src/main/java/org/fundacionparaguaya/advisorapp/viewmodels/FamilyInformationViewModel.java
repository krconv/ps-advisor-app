package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;

import java.util.concurrent.ExecutionException;


public class FamilyInformationViewModel extends ViewModel {

    private FamilyRepository mFamilyRepository;
    private LiveData<Family> currentFamily;


    public  FamilyInformationViewModel(FamilyRepository familyRepository){
        mFamilyRepository = familyRepository;
    }

    /**
     * Sets the current family for this view model and returns the LiveData representation
     * @param id family id for this view model
     * @return current family selected
     */
    public LiveData<Family> setFamily(int id){
        currentFamily = mFamilyRepository.getFamily(id);
        return currentFamily;
    }



    /**Gets the current family that's been set by setFamily**/
    public LiveData<Family> getCurrentFamily()
    {
        if(currentFamily == null)
        {
            throw new IllegalStateException("setFamily must be called in ViewModel before getCurrentFamily");
        }
        else return currentFamily;
    }
}
