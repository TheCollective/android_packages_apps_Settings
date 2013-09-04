/*
 * Copyright (C) 2013 BeanStalk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.os.ServiceManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.app.INotificationManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class MiscSettings extends SettingsPreferenceFragment
                        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_HALO_STATE = "halo_state";
    private static final String KEY_HALO_ENABLED = "halo_enabled"; 
    private static final String KEY_HALO_HIDE = "halo_hide";
    private static final String KEY_HALO_REVERSED = "halo_reversed";

    private ContentResolver mContentResolver;
    private ListPreference mHaloState;
    private CheckBoxPreference mHaloEnabled; 
    private CheckBoxPreference mHaloHide;
    private CheckBoxPreference mHaloReversed;
    private INotificationManager mNotificationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.halo);

        PreferenceScreen prefSet = getPreferenceScreen();

	mNotificationManager = INotificationManager.Stub.asInterface(
                ServiceManager.getService(Context.NOTIFICATION_SERVICE));

        mHaloEnabled = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_ENABLED);
        mHaloEnabled.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_ENABLED, 0) == 1);

        mHaloState = (ListPreference) prefSet.findPreference(KEY_HALO_STATE);
        mHaloState.setValue(String.valueOf((isHaloPolicyBlack() ? "1" : "0")));
        mHaloState.setOnPreferenceChangeListener(this);

        mHaloHide = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_HIDE);
        mHaloHide.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_HIDE, 0) == 1);

        mHaloReversed = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_REVERSED);
        mHaloReversed.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_REVERSED, 1) == 1);
    }

    private boolean isHaloPolicyBlack() {
        try {
            return mNotificationManager.isHaloPolicyBlack();
        } catch (android.os.RemoteException ex) {
                // System dead
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mHaloState) {
            boolean state = Integer.valueOf((String) objValue) == 1;
            try {
                mNotificationManager.setHaloPolicyBlack(state);
            } catch (android.os.RemoteException ex) {
                // System dead
            }
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mHaloEnabled) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_ENABLED, mHaloEnabled.isChecked()
                    ? 1 : 0);
	} else if (preference == mHaloHide) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_HIDE,
                    mHaloHide.isChecked() ? 1 : 0);  
        } else if (preference == mHaloReversed) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_REVERSED,
                    mHaloReversed.isChecked() ? 1 : 0);
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}

