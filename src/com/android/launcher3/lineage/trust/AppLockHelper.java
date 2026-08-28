/*
 * Copyright (C) 2019 The LineageOS Project
 * Copyright (C) 2023 AlphaDroid
 * Copyright (C) 2023-2025 crDroid Android Project
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
package com.android.launcher3.lineage.trust;

import android.app.AxSandboxManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppLockHelper {

    private AxSandboxManager mAppLockManager;

    @Nullable
    private static AppLockHelper sSingleton;

    private AppLockHelper(@NonNull Context context) {
        mAppLockManager = context.getSystemService(AxSandboxManager.class);
    }

    public static synchronized AppLockHelper getInstance(@NonNull Context context) {
        if (sSingleton == null) {
            sSingleton = new AppLockHelper(context);
        }
        return sSingleton;
    }

    public void setShouldHideApp(@NonNull String packageName, boolean hide) {
        if (mAppLockManager == null) return;
        mAppLockManager.setPackageHiddenFromLauncher(packageName, hide);
    }

    public boolean isPackageHidden(@NonNull String packageName) {
        if (mAppLockManager == null) return false;
        return mAppLockManager.isPackageHiddenFromLauncher(packageName);
    }

    public void setShouldProtectApp(@NonNull String packageName, boolean protect) {
        if (mAppLockManager == null) return;
        if (protect) {
            mAppLockManager.addLockedApp(packageName);
        } else {
            mAppLockManager.removeLockedApp(packageName);
        }
    }

    public boolean isPackageProtected(@NonNull String packageName) {
        if (mAppLockManager == null) return false;
        return mAppLockManager.isAppLocked(packageName);
    }

    public int getHiddenPackagesCount() {
        if (mAppLockManager == null) return 0;
        return mAppLockManager.getHiddenFromLauncherPackages().size();
    }
}
