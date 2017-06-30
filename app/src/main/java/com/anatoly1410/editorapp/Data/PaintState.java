package com.anatoly1410.editorapp.Data;

import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by 1 on 18.05.2017.
 */

public class PaintState implements Serializable {
    public String color;
    public Paint.Style style;
    public Integer textSize;
    public Boolean antiAlias;

    public PaintState(){
        color = null;
        style = null;
        textSize = null;
        antiAlias = null;
    }
}
