package nz.co.redice.demoservice.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import nz.co.redice.demoservice.BR;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.models.Azan;
import nz.co.redice.demoservice.utils.Converters;
import nz.co.redice.demoservice.utils.PrefHelper;
import nz.co.redice.demoservice.view.presentation.TimeTable;

public class HomeScreenViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    public TimeTable mTimeTable;
    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;


    @ViewModelInject
    public HomeScreenViewModel(Repository repository,
                               @Assisted SavedStateHandle savedStateHandle) {
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mTimeTable = new TimeTable();
    }

    public LiveData<Azan> getTimesForSelectedDate(Long date) {
        return mRepository.getTimesForSelectedDate(date);

    }



}
