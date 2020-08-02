package nz.co.redice.demoservice.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.SavedStateHandle;

import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.PermissionHelper;
import nz.co.redice.demoservice.utils.PreferencesHelper;

public class AutoLocationViewModel extends AndroidViewModel {


    private final SavedStateHandle savedStateHandle;
    private Repository mRepository;
    private PreferencesHelper mPreferencesHelper;
    private PermissionHelper mPermissionHelper;


    @ViewModelInject
    public AutoLocationViewModel(@NonNull Application application,
                         Repository repository,
                         PreferencesHelper preferencesHelper,PermissionHelper permissionHelper,
                         @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mPreferencesHelper = preferencesHelper;
        mPermissionHelper = permissionHelper;




    }
}
