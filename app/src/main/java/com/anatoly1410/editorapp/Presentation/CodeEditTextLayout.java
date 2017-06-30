package com.anatoly1410.editorapp.Presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.Data.VisualStylesManager;

/**
 * Created by 1 on 07.12.2016.
 */
/*Component that contains CEditText and other code edit instruments*/
public class CodeEditTextLayout extends RelativeLayout {
    /*References to inner components*/
    private CEditText mInnerEditText;
    private HorizontalScrollView mHScrollView;
    private CScrollView mInnerScrollView;
    private int mVerticalLineIndent = 60;
    private Paint mLinePaint;
    private MainActivity parentActivity;
    public void setVerticalLineIndent(int indent) {
        mVerticalLineIndent = indent;
    }
    /*Constructor:
    * - sets references to inner components
    * - sets basic listeners*/
    public CodeEditTextLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
        parentActivity = (MainActivity)context;
        setWillNotDraw(false);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.code_edit_text, this);

        mInnerEditText = (CEditText) findViewById(R.id.mainEdit);
        mInnerScrollView = (CScrollView) findViewById(R.id.codeScrollView);
        mHScrollView = (HorizontalScrollView) findViewById(R.id.hCodeScrollView);

        mInnerScrollView.setBoundCEditText(mInnerEditText);


        mHScrollView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                mHScrollView.requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        mLinePaint = parentActivity.visualStylesManager.getCScrollViewLinePaint();
        mVerticalLineIndent = parentActivity.visualStylesManager.getLeftIndent();
    }
    /*Draws vertical left-side line*/
    private void drawVerticalLine(Canvas canvas, int height)
    {
        canvas.drawLine(mVerticalLineIndent,0,mVerticalLineIndent,height,mLinePaint);
    }
    /*Draw method*/
    @Override
    public void onDraw(Canvas canvas) {
        drawVerticalLine(canvas, getMeasuredHeight());
        super.onDraw(canvas);
    }

}
