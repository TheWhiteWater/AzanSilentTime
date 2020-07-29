package nz.co.redice.demoservice.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.models.Azan;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.utils.ServiceHelper;
import nz.co.redice.demoservice.view.presentation.TimeTable;

public class HomeScreenViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    public TimeTable mTimeTable;
    private Repository mRepository;
    private ServiceHelper mServiceHelper;



    @ViewModelInject
    public HomeScreenViewModel(@NonNull Application application,
                               Repository repository, ServiceHelper serviceHelper,
                               @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mTimeTable = new TimeTable();
        mServiceHelper = serviceHelper;
        mServiceHelper.startService(application);
        mServiceHelper.doBindService(application);
    }



    public LiveData<Azan> getTimesForSelectedDate(Long date) {
        return mRepository.getTimesForSelectedDate(date);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mServiceHelper.doUnbindService(getApplication());
    }
}
