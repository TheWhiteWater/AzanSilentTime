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
import nz.co.redice.demoservice.utils.Converters;
import nz.co.redice.demoservice.utils.PreferencesHelper;
import nz.co.redice.demoservice.utils.ServiceHelper;
import nz.co.redice.demoservice.view.presentation.TimeTable;

public class HomeScreenViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private Repository mRepository;
    private ServiceHelper mServiceHelper;
    private MutableLiveData<TimeTable> timeTable = new MutableLiveData<>();
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
        Log.d("App", "HomeScreenViewModel: database size" + mRepository.getDatabaseSize().getValue());
    }


    public LiveData<TimeTable> getTimesForSelectedDate(Long date) {
        if (mRepository.getDatabaseSize().getValue() != null) {
            EntryModel selected = mRepository.getTimesForSelectedDate(date).getValue();
            TimeTable result = new TimeTable();
            result.setDate(Converters.convertLongValueOfLocalDateIntoString(selected.getDate(), mPreferencesHelper.getLocalDateFormatPattern()));
            result.setFajr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getFajr(), mPreferencesHelper.getLocalTimeFormatPattern()));
            result.setAsr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getAsr(), mPreferencesHelper.getLocalTimeFormatPattern()));
            result.setDhuhr(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getDhuhr(), mPreferencesHelper.getLocalTimeFormatPattern()));
            result.setIsha(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getIsha(), mPreferencesHelper.getLocalTimeFormatPattern()));
            result.setMaghrib(Converters.convertLongValueOfLocalDateTimeIntoString(selected.getMaghrib(), mPreferencesHelper.getLocalTimeFormatPattern()));
            timeTable.setValue(result);
        } else {
            Log.d("App", "getTimesForSelectedDate: empty database " );
        }
        return timeTable;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mServiceHelper.doUnbindService(getApplication());
    }
}
