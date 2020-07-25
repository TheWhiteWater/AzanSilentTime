package nz.co.redice.demoservice.viewmodel;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.models.Azan;

public class HomeScreenViewModel extends ViewModel {

    @Inject Repository mRepository;
    private final SavedStateHandle savedStateHandle;

    @ViewModelInject
    public HomeScreenViewModel(Repository repository,
                               @Assisted SavedStateHandle savedStateHandle) {
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
    }

//    public LiveData<List<Azan>> getAzanTimesForCurrentDay() {
//        return mRepository.getTimeTableForCurrentDay();
//    }
//
//    public LiveData<List<Azan>> getAnnualCalendar() {
//        return mRepository.getAnnualTables();
//    }

}
