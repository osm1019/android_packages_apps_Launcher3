package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;

import com.android.launcher3.dagger.ApplicationContext;
import com.android.launcher3.lineage.trust.AppLockHelper;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Utility class to filter out components from various lists
 */
public class AppFilter {

    private final Set<ComponentName> mFilteredComponents;
    private final Context mContext;

    @Inject
    public AppFilter(@ApplicationContext Context context) {
        mContext = context;
        mFilteredComponents = Arrays.stream(
                context.getResources().getStringArray(R.array.filtered_components))
                .map(ComponentName::unflattenFromString)
                .collect(Collectors.toSet());
    }

    public boolean shouldShowApp(ComponentName app) {
        if (app != null) {
            try {
                if (AppLockHelper.getInstance(mContext).isPackageHidden(app.getPackageName())) {
                    return false;
                }
            } catch (Throwable ignored) {
            }
        }
        return !mFilteredComponents.contains(app);
    }
}
