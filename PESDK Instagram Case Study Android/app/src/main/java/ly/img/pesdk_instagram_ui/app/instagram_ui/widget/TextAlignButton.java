package ly.img.pesdk_instagram_ui.app.instagram_ui.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import ly.img.pesdk_instagram_ui.app.R;
/**
 * Created by niklasbachmann on 06.12.17.
 */

public class TextAlignButton extends ImageButton implements View.OnClickListener {

    private Paint.Align textAlignState = Paint.Align.CENTER;
    private OnClickListener onClickListener = null;

    private OnAlignStateChangeListener onAlignStateChangeListener = null;

    public TextAlignButton(Context context) {
        this(context, null);
    }

    public TextAlignButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextAlignButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // Toggle text align state
        switch (textAlignState) {
            case CENTER: setTextAlignState(Paint.Align.LEFT); break;
            case LEFT: setTextAlignState(Paint.Align.RIGHT); break;
            case RIGHT: setTextAlignState(Paint.Align.CENTER); break;
            default: throw new RuntimeException("Unsupported align mode.");
        }

        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnAlignStateChangeListener(OnAlignStateChangeListener onAlignStateChangeListener) {
        this.onAlignStateChangeListener = onAlignStateChangeListener;
    }

    public void setTextAlignState(Paint.Align textAlignState) {
        this.textAlignState = textAlignState;

        // Update UI state
        switch (textAlignState) {
            case CENTER:
                setImageResource(R.drawable.iui_alignment_center);
                break;
            case LEFT:
                setImageResource(R.drawable.iui_alignment_left);
                break;
            case RIGHT:
                setImageResource(R.drawable.iui_alignment_right);
                break;
            default:
                throw new RuntimeException("Unsupported align mode.");
        }

        if (onAlignStateChangeListener != null) {
            onAlignStateChangeListener.onAlignStateChange(textAlignState);
        }
    }

    public Paint.Align getTextAlignState() {
        return textAlignState;
    }

    public interface OnAlignStateChangeListener {
        void onAlignStateChange (Paint.Align textAlignState);
    }

}
