package ly.img.pesdk_instagram_ui.app.instagram_ui.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import ly.img.pesdk_instagram_ui.app.R;

/**
 * Created by niklasbachmann on 06.12.17.
 */

public class BrushUndoButton extends Button implements View.OnClickListener {

    private OnClickListener onClickListener = null;

    private boolean isUndoStep = false;

    private OnUndoStateChangeListener onUndoStateChangeListener = null;

    public BrushUndoButton(Context context) {
        this(context, null);
    }

    public BrushUndoButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrushUndoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnUndoStateChangeListener(OnUndoStateChangeListener onUndoStateChangeListener) {
        this.onUndoStateChangeListener = onUndoStateChangeListener;
    }

    public void setUndoStep(boolean undoStep) {
        this.isUndoStep = undoStep;

        if (undoStep) {

            setTextColor(0xFFFFFFFF);
        } else {

            setTextColor(0x33FFFFFF);
        }

        if (onUndoStateChangeListener != null) {
            onUndoStateChangeListener.onUndoStateChange(undoStep);
        }
    }

    public interface OnUndoStateChangeListener {
        void onUndoStateChange(boolean undoStep);
    }

}
