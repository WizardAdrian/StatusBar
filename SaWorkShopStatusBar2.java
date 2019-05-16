package com.didi.amm.shop.maintenance.sa;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.didi.amm.shop.maintenance.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adrian
 */
public class SaWorkShopStatusBar2 extends View {

    private Paint mPaint;
    private TextPaint mPaintText;
    private Path[] mPaths;
    private PorterDuffXfermode mPorterDuffXfermode;
    private int mViewWidth = 0;

    private int mNumberOfStatus = 0;//状态的个数
    private int mWidthOfTagWithoutArrow;//单个标签除去箭头的宽度
    private int mWidthOfTagArrow;//单个标签的箭头的宽度
    private int mHeightOfTag;//单个标签的高度
    private int mHeightOfTagArrow;//单个标签箭头的高度
    private List<float[]> mStatusBackgroundPoints = new ArrayList<>();//每个标签的背景的坐标，drawPath时使用
    private String[] mStatusTexts;

    private int[] mColorsStatusDoing;
    private int[] mColorsStatusDone;
    private int[] mColorsStatusToDo;

    private int mCurrentStatus;

    private int mTextSize;

    public SaWorkShopStatusBar2(Context context) {
        super(context);
        init();
    }

    public SaWorkShopStatusBar2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public SaWorkShopStatusBar2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SaWorkShopStatusBar2);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SaWorkShopStatusBar2_swssb_TextSize) {
                final int defThemeTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
                mTextSize = a.getDimensionPixelSize(attr, defThemeTextSize);
            }
        }
        a.recycle();
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mPaintText = new TextPaint();
        mPaintText.setTextSize(mTextSize);
        mPaintText.setAntiAlias(true);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        mColorsStatusDoing = new int[]{Color.parseColor("#CCCCCC"), Color.parseColor("#999999")};
        mColorsStatusDone = new int[]{Color.parseColor("#205DDB"), Color.parseColor("#3890E7")};
        mColorsStatusToDo = new int[]{Color.WHITE, Color.parseColor("#F2F3F4")};
    }

    public void setNumberOfStatus(String[] statusTexts) {
        if (statusTexts != null) {
            mStatusTexts = statusTexts;
            mNumberOfStatus = mStatusTexts.length;
            mPaths = new Path[mNumberOfStatus];
            for (int i = 0; i < mPaths.length; i++) {
                mPaths[i] = new Path();
            }
        }
        mViewWidth = 0;
        calculatePoints();
        invalidate();
    }

    public int getNumberOfStatus() {
        return mNumberOfStatus;
    }
//
//    public void setCurrentStatus(int currentStatus) {
//        mCurrentStatus = currentStatus;
//        invalidate();
//    }


    public void setCurrentStage(String stageText) {
        if (TextUtils.isEmpty(stageText) || stageText.equals("init")) {
            mCurrentStatus = -1;
        } else if (stageText.equals("end")) {
            mCurrentStatus = mStatusTexts.length;
        } else {
            for (int i = 0; i < mStatusTexts.length; i++) {
                if (stageText.equals(mStatusTexts[i])) {
                    mCurrentStatus = i;
                    break;
                }
            }
        }
        invalidate();
    }

    /**
     * 计算箭头形状path需要经过的点位
     * *
     */
    private void calculatePoints() {
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0 && mNumberOfStatus != 0) {
                mWidthOfTagWithoutArrow = mViewWidth / mNumberOfStatus;
                mHeightOfTag = getMeasuredHeight();
                mWidthOfTagArrow = (int) (mWidthOfTagWithoutArrow / 2 * 0.2);
                mHeightOfTagArrow = mHeightOfTag / 2;
                mStatusBackgroundPoints.clear();
                float[] points;
                for (int i = 0; i < mNumberOfStatus; i++) {
                    if (i == 0) {
                        //Oops! 注意第一个状态不需要箭头尾部的形状
                        points = new float[]{
                                i * mWidthOfTagWithoutArrow, 0,
                                (i + 1) * mWidthOfTagWithoutArrow, 0,
                                (i + 1) * mWidthOfTagWithoutArrow + mWidthOfTagArrow, mHeightOfTagArrow,
                                (i + 1) * mWidthOfTagWithoutArrow, mHeightOfTag,
                                i * mWidthOfTagWithoutArrow, mHeightOfTag,
                                (i + 1) * mWidthOfTagWithoutArrow + mWidthOfTagArrow, mHeightOfTag,
                        };
                    } else {
                        points = new float[]{
                                i * mWidthOfTagWithoutArrow, 0,
                                (i + 1) * mWidthOfTagWithoutArrow, 0,
                                (i + 1) * mWidthOfTagWithoutArrow + mWidthOfTagArrow, mHeightOfTagArrow,
                                (i + 1) * mWidthOfTagWithoutArrow, mHeightOfTag,
                                i * mWidthOfTagWithoutArrow, mHeightOfTag,
                                i * mWidthOfTagWithoutArrow + mWidthOfTagArrow, mHeightOfTagArrow,
                                (i + 1) * mWidthOfTagWithoutArrow + mWidthOfTagArrow, mHeightOfTag,
                        };
                    }
                    mStatusBackgroundPoints.add(points);
                }
            }
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int widthResult = 100;
//        int heightResult = 100;
//
//        if (widthMode != MeasureSpec.AT_MOST) {
//            widthResult = widthSize;
//        }
//
//        if (heightMode != MeasureSpec.AT_MOST) {
//            heightResult = heightSize;
//        }
//
//        int resultSize = widthResult > heightResult
//                ? heightResult : widthResult;
//
//        setMeasuredDimension(resultSize, resultSize);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculatePoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = mStatusBackgroundPoints.size();
        for (int i = 0; i < size; i++) {
            float[] tag = mStatusBackgroundPoints.get(i);

            Path path = mPaths[i];
            if (i == 0) {
                path.moveTo(tag[0], tag[1]);
                path.lineTo(tag[2], tag[3]);
                path.lineTo(tag[4], tag[5]);
                path.lineTo(tag[6], tag[7]);
                path.lineTo(tag[8], tag[9]);
                path.close();

                draw(i, canvas, path, tag[0], tag[1], tag[10] * 2, tag[11]);
            } else {
                path.moveTo(tag[0], tag[1]);
                path.lineTo(tag[2], tag[3]);
                path.lineTo(tag[4], tag[5]);
                path.lineTo(tag[6], tag[7]);
                path.lineTo(tag[8], tag[9]);
                path.lineTo(tag[10], tag[11]);
                path.close();

                draw(i, canvas, path, tag[0], tag[1], tag[12] * 2, tag[13]);
            }

            mPaint.setXfermode(null);
            mPaint.setShader(null);
        }
    }

    private void draw(int index, Canvas canvas, Path path, float left, float top, float right, float bottom) {
        LinearGradient linearGradient;
        int[] colors;

        if (mCurrentStatus == -1) {
            colors = mColorsStatusDone;
            mPaintText.setColor(Color.WHITE);
        } else {
            if (mCurrentStatus == index) {
                colors = mColorsStatusDoing;
                mPaintText.setColor(Color.WHITE);
            } else if (mCurrentStatus > index) {
                colors = mColorsStatusDone;
                mPaintText.setColor(Color.WHITE);
            } else {
                colors = mColorsStatusToDo;
                mPaintText.setColor(Color.parseColor("#C5C5C6"));
            }
        }

        linearGradient = new LinearGradient(mWidthOfTagWithoutArrow * index, 0, mWidthOfTagWithoutArrow * (index + 1) + mWidthOfTagArrow, 0,
                colors,
                null,
                Shader.TileMode.CLAMP);

        int sc = canvas.saveLayer(left, top, right, bottom, mPaint, Canvas.ALL_SAVE_FLAG);

        canvas.drawPath(path, mPaint);

        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setShader(linearGradient);
        canvas.drawRect(left, top, right, bottom, mPaint);

        canvas.restoreToCount(sc);

        String text = mStatusTexts[index];
        float stringWidth = mPaintText.measureText(text);
        float startTextX = (mWidthOfTagWithoutArrow + mWidthOfTagArrow - stringWidth) / 2 + mWidthOfTagWithoutArrow * index;
        float baseY = mHeightOfTag / 2 - 2;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = baseY + fontTotalHeight / 2 + offY;
        canvas.drawText(text, startTextX, newY, mPaintText);
    }
}
