package com.seemile.launcher.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.seemile.launcher.R;

public class CompatDialog extends Dialog {

    private CompatParams mParams;

    private int mCompatDialogLayout;

    protected Window mWindow;

    // 自定义
    private View mView;

    protected CompatDialog(Context context) {
        this(context, 0);
    }

    protected CompatDialog(Context context, int theme) {
        super(context, theme);

        mWindow = getWindow();

        final Context themeContext = getContext();
        TypedArray a = themeContext.obtainStyledAttributes(null,
                R.styleable.CompatDialog, R.attr.compatDialogStyle, 0);

        mCompatDialogLayout = a.getResourceId(R.styleable.CompatDialog_layout,
                R.layout.compat_dlg_layout);
        a.recycle();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        installContent();
    }

    public void installContent() {
        /* We use a custom title so never request a window title */
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        // 输入法弹出
        // if (mView == null) {
        // mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
        // WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // }
        mWindow.setContentView(mCompatDialogLayout);

        final CompatParams p = mParams;
        final Context context = getContext();
        Configuration configuration = getContext().getResources().getConfiguration();
        final boolean isPortrait = configuration != null ? configuration.orientation == Configuration.ORIENTATION_PORTRAIT : false;
        if (p != null) {
            if (p.mWindowWidthFullScreen || p.mWindowHeightFullScreen) {
                // 全屏显示
                WindowManager.LayoutParams lp = mWindow.getAttributes();
                lp.width = p.mWindowWidthFullScreen ? WindowManager.LayoutParams.MATCH_PARENT
                        : WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = p.mWindowHeightFullScreen ? WindowManager.LayoutParams.MATCH_PARENT
                        : WindowManager.LayoutParams.WRAP_CONTENT;
                mWindow.setAttributes(lp);
                mWindow.getDecorView().setPadding(0, 0, 0, 0);
            } else {
                WindowManager wm = (WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                WindowManager.LayoutParams lp = mWindow.getAttributes();

                lp.width = (int) (dm.widthPixels * (isPortrait ? 0.7f : 0.5f));
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mWindow.setAttributes(lp);
                mWindow.getDecorView().setPadding(0, 0, 0, 0);
            }
            if (p.mWindowGravity > 0) {
                mWindow.setGravity(p.mWindowGravity);
            }
        }
        setupView();
    }

    protected void setupView() {
        // ----------------- title panel --------------
        setupTitlePanel();

        // ----------------- content panel --------------
        setupContentPanel();

        // ----------------- button panel --------------
        setupButtonPanel();

    }

    protected void setupTitlePanel() {
        final CompatParams p = mParams;
        final Context context = getContext();
        if (p == null || context == null) {
            return;
        }
        ViewGroup titlePanel = (ViewGroup) mWindow
                .findViewById(R.id.compat_dlg_title_panel);
        if (titlePanel == null) {
            return;
        }
        boolean hasTitle = !TextUtils.isEmpty(p.mTitle);
        TextView titleView = (TextView) mWindow.findViewById(R.id.compat_dlg_title);
        if (titleView == null) {
            return;
        }
        if (hasTitle) {
            titleView.setText(p.mTitle);
            titleView.setCompoundDrawablesWithIntrinsicBounds(p.mIcon, null, null, null);
            titlePanel.setVisibility(View.VISIBLE);
        } else {
            titleView.setText("");
            titlePanel.setVisibility(View.GONE);
        }
    }

    protected void setupContentPanel() {
        final CompatParams p = mParams;
        final Context context = getContext();
        if (p == null || context == null) {
            return;
        }
        ViewGroup contentPanel = (ViewGroup) mWindow
                .findViewById(R.id.compat_dlg_content_panel);
        if (contentPanel == null) {
            return;
        }
        TextView msgView = (TextView) mWindow.findViewById(R.id.compat_dlg_message);

        boolean hasMsg = !TextUtils.isEmpty(p.mMessage);

        // 有自定义view时，content panel隐藏
        contentPanel.setVisibility(mView != null || !hasMsg ? View.GONE
                : View.VISIBLE);
        if (hasMsg) {
            if (msgView != null) {
                msgView.setText(p.mMessage);
            }
        }
    }

    protected void setupButtonPanel() {
        final CompatParams p = mParams;
        final Context context = getContext();
        if (p == null || context == null) {
            return;
        }
        ViewGroup buttonPanel = (ViewGroup) mWindow
                .findViewById(R.id.compat_dlg_btn_panel);

        if (buttonPanel == null) {
            return;
        }
        final View positiveBtn = mWindow.findViewById(R.id.compat_dlg_positive_btn);
        boolean hasPositive = !TextUtils.isEmpty(p.mPositiveButtonText);
        if (positiveBtn != null) {
            if (hasPositive) {
                if (positiveBtn instanceof TextView)
                    ((TextView) positiveBtn).setText(p.mPositiveButtonText);
                positiveBtn.setVisibility(View.VISIBLE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (p.mPositiveButtonListener != null) {
                            p.mPositiveButtonListener.onClick(CompatDialog.this,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                        // dismiss();
                    }
                });
            } else {
                positiveBtn.setVisibility(View.GONE);
            }
        }

        final View negativeBtn = mWindow.findViewById(R.id.compat_dlg_negative_btn);
        boolean hasNegative = !TextUtils.isEmpty(p.mNegativeButtonText);

        if (negativeBtn != null) {
            if (hasNegative) {
                if (negativeBtn instanceof TextView)
                    ((TextView) negativeBtn).setText(p.mNegativeButtonText);
                negativeBtn.setVisibility(View.VISIBLE);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (p.mNegativeButtonListener != null) {
                            p.mNegativeButtonListener.onClick(CompatDialog.this,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                        dismiss();
                    }
                });
            } else {
                negativeBtn.setVisibility(View.GONE);
            }
        }

        final View neutralBtn = mWindow.findViewById(R.id.compat_dlg_neutral_btn);
        boolean hasNeutral = !TextUtils.isEmpty(p.mNeutralButtonText);
        if (neutralBtn != null) {
            if (hasNeutral) {
                if (neutralBtn instanceof TextView)
                    ((TextView) neutralBtn).setText(p.mNeutralButtonText);
                neutralBtn.setVisibility(View.VISIBLE);
                neutralBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (p.mNeutralButtonListener != null) {
                            p.mNeutralButtonListener.onClick(CompatDialog.this,
                                    DialogInterface.BUTTON_NEUTRAL);
                        }
                        dismiss();
                    }
                });
            } else {
                neutralBtn.setVisibility(View.GONE);
            }
        }

        if (!hasPositive && !hasNegative && !hasNeutral) {
            buttonPanel.setVisibility(View.GONE);
        } else {
            buttonPanel.setVisibility(View.VISIBLE);
        }

    }


    protected void setParams(CompatParams p) {
        mParams = p;
        mView = p.mView;
    }

    public View getCustomView() {
        final CompatParams p = mParams;
        return p != null ? p.mView : null;
    }

    public static class Builder {
        protected CompatParams P;

        public Builder(Context context) {
            P = new CompatParams(context);
        }

        public Builder(Context context, int theme) {
            P = new CompatParams(context, theme);
        }

        public Builder setTitle(int titleId) {
            return setTitle(P.mContext.getText(titleId));
        }

        public Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }

        public Builder setMessage(int messageId) {
            return setMessage(P.mContext.getText(messageId));
        }

        public Builder setMessage(CharSequence message) {
            P.mMessage = message;
            return this;
        }

        public Builder setIcon(int resId) {
            return setIcon(P.mContext.getResources().getDrawable(resId));
        }

        public Builder setIcon(Drawable icon) {
            P.mIcon = icon;
            return this;
        }

        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            return setPositiveButton(P.mContext.getText(textId), listener);
        }

        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            P.mPositiveButtonText = text;
            P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            return setNegativeButton(P.mContext.getText(textId), listener);
        }

        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            P.mNegativeButtonText = text;
            P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, final OnClickListener listener) {
            return setNeutralButton(P.mContext.getText(textId), listener);
        }

        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            P.mNeutralButtonText = text;
            P.mNeutralButtonListener = listener;
            return this;
        }


        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setCustomView(int layoutResId) {
            return setCustomView(LayoutInflater.from(P.mContext).inflate(layoutResId,
                    null));
        }

        public Builder setCustomView(View view) {
            P.mView = view;
            return this;
        }


        public Builder setWindowGravity(int gravity) {
            P.mWindowGravity = gravity;
            return this;
        }

        public Builder setWindowFullScreen(boolean widthFullScreen) {
            setWindowFullScreen(widthFullScreen, false);
            return this;
        }

        public Builder setWindowFullScreen(boolean widthFullScreen,
                                           boolean heightFullScreen) {
            P.mWindowWidthFullScreen = widthFullScreen;
            P.mWindowHeightFullScreen = heightFullScreen;
            return this;
        }

        public CompatDialog create() {
            final CompatDialog dialog = P.mTheme > 0 ? new CompatDialog(P.mContext,
                    P.mTheme) : new CompatDialog(P.mContext);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            dialog.setParams(P);
            return dialog;
        }

        public CompatDialog show() {
            final CompatDialog dialog = create();
            dialog.show();
            return dialog;
        }

        public Builder setTheme(int theme) {
            P.mTheme = theme;
            return this;
        }

    }

    private static class CompatParams {

        public int mTheme;

        public Context mContext;

        public CharSequence mTitle;

        public CharSequence mMessage;

        public Drawable mIcon;

        public View mView;

        public CharSequence mNeutralButtonText;
        public OnClickListener mNeutralButtonListener;

        public CharSequence mPositiveButtonText;
        public OnClickListener mPositiveButtonListener;
        public CharSequence mNegativeButtonText;
        public OnClickListener mNegativeButtonListener;

        public boolean mCancelable;

        public OnCancelListener mOnCancelListener;

        public OnDismissListener mOnDismissListener;

        public int mWindowGravity;

        public boolean mWindowWidthFullScreen;
        public boolean mWindowHeightFullScreen;

        public CompatParams(Context context) {
            mContext = context;
            mCancelable = true;
        }

        public CompatParams(Context context, int theme) {
            mContext = context;
            mTheme = theme;
            mCancelable = true;
        }

    }

}
