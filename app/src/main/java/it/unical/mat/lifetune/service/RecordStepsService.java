package it.unical.mat.lifetune.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by beantoan on 1/7/18.
 */

public class RecordStepsService extends IntentService {
    private static final String TAG = RecordStepsService.class.getCanonicalName();

    public RecordStepsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");

        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Fitness.getRecordingClient#addOnCompleteListener: Successfully subscribed!");
                            } else {
                                FirebaseCrash.logcat(Log.ERROR, TAG, "Fitness.getRecordingClient#addOnCompleteListener:" + task.getException());
                                FirebaseCrash.report(task.getException());
                            }
                        });

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
