package com.android.launcher3.icons.clock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import com.android.launcher3.model.data.ItemInfoWithIcon;

import java.util.TimeZone;

class AutoUpdateClock extends Drawable implements Runnable {
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Bitmap mBitmap;
    private ClockLayers mLayers;

    AutoUpdateClock(ItemInfoWithIcon info, ClockLayers layers) {
        mBitmap = info.bitmap.icon;
        mLayers = layers;
    }

    private void rescheduleUpdate() {
        long millisInSecond = 1000L;
        unscheduleSelf(this);
        long uptimeMillis = SystemClock.uptimeMillis();
        scheduleSelf(this, uptimeMillis - uptimeMillis % millisInSecond + millisInSecond);
    }

    // Used only by Google Clock
    void updateLayers(ClockLayers layers) {
        mLayers = layers;
        if (mLayers != null) {
            mLayers.mDrawable.setBounds(getBounds());
        }
        invalidateSelf();
    }

    void setTimeZone(TimeZone timeZone) {
        if (mLayers != null) {
            mLayers.setTimeZone(timeZone);
            invalidateSelf();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        if (mLayers == null) {
            canvas.drawBitmap(mBitmap, null, rect, mPaint);
            return;
        }
        int count = canvas.save();
        canvas.drawBitmap(mLayers.bitmap, null, rect, mPaint);
        mLayers.updateAngles();
        canvas.scale(mLayers.scale, mLayers.scale,
                rect.exactCenterX() + mLayers.offset,
                rect.exactCenterY() + mLayers.offset);
        canvas.clipPath(mLayers.mDrawable.getIconMask());
        mLayers.mDrawable.getForeground().draw(canvas);
        canvas.restoreToCount(count);
        rescheduleUpdate();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        if (mLayers != null) {
            mLayers.mDrawable.setBounds(bounds);
        }
    }

    @Override
    public void run() {
        if (mLayers.updateAngles()) {
            invalidateSelf();
        } else {
            rescheduleUpdate();
        }
    }
}
