package ly.img.pesdk_instagram_ui.app.instagram_ui.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import ly.img.pesdk_instagram_ui.app.R;

import ly.img.android.sdk.utils.Trace;

/**
 * Created by niklasbachmann on 06.12.17.
 */

public class TextFillButton extends ImageButton implements View.OnClickListener {

    public boolean isTextFillClicked = false;

    private OnClickListener onClickListener = null;

    private OnTextFillStateChangeListener onTextFillStateChangeListener = null;

    public TextFillButton(Context context) {
        this(context, null);
    }

    public TextFillButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFillButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setTextFillState(!isTextFillClicked);
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnTextFillStateChangeListener(OnTextFillStateChangeListener onTextFillStateChangeListener) {
        this.onTextFillStateChangeListener = onTextFillStateChangeListener;
    }

    public void setTextFillState(boolean isTextFillClicked) {
        this.isTextFillClicked = isTextFillClicked;

        setImageResource(
          isTextFillClicked
            ? R.drawable.iui_fill_selected
            : R.drawable.iui_fill
        );

        if (onTextFillStateChangeListener != null) {
            onTextFillStateChangeListener.onTextFillStateChange(isTextFillClicked);
        }
    }

    public boolean getTextFillState() {
        return isTextFillClicked;
    }


    public interface OnTextFillStateChangeListener {
        void onTextFillStateChange (boolean isTextFillClicked);
    }

}
