package com.chenjianhong.floatingwindow.library;

import android.Manifest;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 陈剑虹 on 16-9-11.
 * 悬浮窗口
 */
public class FloatingWindow implements View.OnTouchListener, View.OnClickListener {
    private String TAG = FloatingWindow.class.getSimpleName();

    private static final int DEFAULT_Y = 100;
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 400;

    private final int WINDOW_VIEW_ID;

    private final WindowManager mWindowManager;
    private Context mContext;
    private View mWindowView;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private TextView mTvClose;
    private TextView mTvTitle;
    private ImageView mIvScale;
    private LinearLayout mLlTitle;
    private FrameLayout mFlContent;

    private boolean isShow = false;


    public FloatingWindow(Context context) {
        mContext = context;
        WINDOW_VIEW_ID = this.hashCode();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initView();
        initEvent();
    }

    private float downScaleX = 0;
    private float downScaleY = 0;
    private float lastWindowWidth = 0;
    private float lastWindowHeight = 0;

    private void initEvent() {
        mWindowView.setOnTouchListener(this);
        mWindowView.setOnClickListener(this);
        mTvClose.setOnClickListener(this);
        //窗口缩放
        mIvScale.setOnTouchListener(new View.OnTouchListener() {
            int count = 0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downScaleX = rawX;
                        downScaleY = rawY;
                        lastWindowWidth = mWindowLayoutParams.width;
                        lastWindowHeight = mWindowLayoutParams.height;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        count++;
                        float movedX = rawX - downScaleX;
                        float movedY = rawY - downScaleY;
                        if (count > 3) {
                            setSize((int) (lastWindowWidth + movedX), (int) (lastWindowHeight + movedY));
                            if (mSizeChangedListener != null) {
                                mSizeChangedListener.onSizeChanged(mWindowLayoutParams.width, mWindowLayoutParams.height);
                            }
                            count = 0;
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });
    }

    private void initView() {
        if (mWindowView == null) {
            mWindowView = LayoutInflater.from(mContext).inflate(R.layout.view_floating_window, null);
            mTvClose = (TextView) mWindowView.findViewById(R.id.tv_float_close);
            mTvTitle = (TextView) mWindowView.findViewById(R.id.tv_float_title);
            mLlTitle = (LinearLayout) mWindowView.findViewById(R.id.ll_float_title);
            mFlContent = (FrameLayout) mWindowView.findViewById(R.id.fl_float_content);
            mIvScale = (ImageView) mWindowView.findViewById(R.id.iv_float_scale);

            mWindowView.setId(WINDOW_VIEW_ID);
            mWindowLayoutParams = new WindowManager.LayoutParams();
            mWindowLayoutParams.width = DEFAULT_WIDTH;
            mWindowLayoutParams.height = DEFAULT_HEIGHT;

            mWindowLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            else
                mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mWindowLayoutParams.format = PixelFormat.RGBA_8888;
            mWindowLayoutParams.y = DEFAULT_Y;
            mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        }
    }

    /**
     * 设置标题栏背景
     *
     * @param res
     */
    public void setTitleBackground(int res) {
        mLlTitle.setBackgroundResource(res);
    }

    /**
     * 设置标题栏背景色
     *
     * @param color
     */
    public void setTitleBackgroundColor(int color) {
        mLlTitle.setBackgroundColor(color);
    }

    /**
     * 设置标题栏背景
     *
     * @param drawable
     */
    public void setTitleBackground(Drawable drawable) {
        mLlTitle.setBackground(drawable);
    }

    /**
     * 设置窗口内容视图
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        mFlContent.removeAllViews();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        mFlContent.addView(contentView, params);
    }

    /**
     * 设置默认内容布局背景
     *
     * @param res
     */
    public void setContentBackground(int res) {
        mFlContent.setBackgroundResource(res);
    }

    /**
     * 设置默认内容布局背景色
     *
     * @param color
     */
    public void setContentBackgroundColor(int color) {
        mFlContent.setBackgroundColor(color);
    }

    /**
     * 设置默认内容布局背景
     *
     * @param drawable
     */
    public void setContentBackground(Drawable drawable) {
        mFlContent.setBackground(drawable);
    }

    /**
     * 设置窗口尺寸
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mWindowLayoutParams.width = width;
        mWindowLayoutParams.height = height;
        mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
    }

    /**
     * 移动窗口到指定位置
     *
     * @param x
     * @param y
     */
    public void moveToPosition(int x, int y) {
        if (isShow) {
            mWindowLayoutParams.x = x;
            mWindowLayoutParams.y = y;
            mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 显示窗口
     */
    public void show() {
        int alertWindowPermission = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.SYSTEM_ALERT_WINDOW);
        if (alertWindowPermission == PermissionChecker.PERMISSION_GRANTED) {
            mWindowManager.addView(mWindowView, mWindowLayoutParams);
            isShow = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(mContext)) {
                    mWindowManager.addView(mWindowView, mWindowLayoutParams);
                    isShow = true;
                } else {
                    Toast.makeText(mContext, "没有显示悬浮窗权限,请设置允许显示悬浮窗!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "没有显示悬浮窗权限,请设置允许显示悬浮窗!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 隐藏窗口
     */
    public void hide() {
        if (isShowing())
            mWindowManager.removeView(mWindowView);
        isShow = false;
    }

    public boolean isShowing() {
        return isShow;
    }

    private float lastX = 0;
    private float lastY = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = motionEvent.getRawX();
                lastY = motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = motionEvent.getRawX();
                float curY = motionEvent.getRawY();

                float movedX = lastX - curX;
                float movedY = curY - lastY;

                mWindowLayoutParams.x += movedX;
                mWindowLayoutParams.y += movedY;
                mWindowManager.updateViewLayout(mWindowView, mWindowLayoutParams);

                lastX = curX;
                lastY = curY;

                break;
            case MotionEvent.ACTION_UP:
                lastX = motionEvent.getRawX();
                lastY = motionEvent.getRawY();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_float_close) {
            hide();
        }
    }

    private SizeChangedListener mSizeChangedListener;

    /**
     * 设置尺寸变化监听器
     *
     * @param listener
     */
    public void setOnSizeChangedListener(SizeChangedListener listener) {
        if (listener != null)
            mSizeChangedListener = listener;
    }

    interface SizeChangedListener {
        void onSizeChanged(int width, int height);
    }
}
