package nz.co.redice.demoservice.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.utils.ServiceHelper;
import nz.co.redice.demoservice.view.presentation.ReadableTimings;

public class HomeScreenViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private Repository mRepository;
    private ServiceHelper mServiceHelper;
    private MutableLiveData<ReadableTimings> mReadableData = new MutableLiveData<>();

    private PreferencesHelper mPreferencesHelper;


    @ViewModelInject
    public HomeScreenViewModel(@NonNull Application application,
                               Repository repository, ServiceHelper serviceHelper,
                               PreferencesHelper preferencesHelper,
                               @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mServiceHelper = serviceHelper;
        mServiceHelper.startService(application);
        mServiceHelper.doBindService(application);
        mPreferencesHelper = preferencesHelper;
    }


    public LiveData<EntryModel> getTimesForSelectedDate(Long date) {
        return mRepository.getTimesForSelectedDate(date);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mServiceHelper.doUnbindService(getApplication());
    }
}
