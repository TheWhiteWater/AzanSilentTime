package nz.co.redice.demoservice.viewmodel;

import android.app.Application;
import android.util.Log;

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

public class HomeScreenViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    public LiveData<EntryModel> model = new MutableLiveData<>();
    private Repository mRepository;
    private ServiceHelper mServiceHelper;
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
        return mRepository.getSelectedDate(date);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mServiceHelper.doUnbindService(getApplication());
    }

    public LiveData<Integer> getDatabaseSize() {
        return mRepository.getDatabaseSize();
    }

    public void fillUpDatabase() {
        mRepository.requestStandardAnnualCalendar(mPreferencesHelper.getLatitude(), mPreferencesHelper.getLongitude());
    }

    public LiveData<EntryModel> updateEntry(EntryModel model) {
        mRepository.updateSelectedEntry(model);
        return mRepository.getSelectedDate(model.getDate());
    }
}
