package com.example.camera;

import android.util.AttributeSet;
import android.view.*;
import android.content.Context;

public class AutoFitTextureView extends TextureView {
    public int radiowidth = 0;
    public int radioheight = 0;

    public AutoFitTextureView(Context context) {
        super(context,null);
    }

    public AutoFitTextureView(Context context, AttributeSet at) {
        super(context, at);
    }

    public AutoFitTextureView(Context context, AttributeSet at, int defstyle) {
        super(context, at, defstyle);
    }

    public void setAspectRadio(int w, int h)
    {
        if(w<0||h<0)
            throw new IllegalArgumentException("尺寸有误！");
        radiowidth=w;
        radioheight=h;
        requestLayout();
    }

    //OnMeasure
    protected void OnMeasure(int widthmeasurespec,int heightmeasurespec)
    {
        super.onMeasure(widthmeasurespec,heightmeasurespec);
        final int width= MeasureSpec.getSize(widthmeasurespec);
        final int height= MeasureSpec.getSize(heightmeasurespec);
        if(0==radiowidth || 0==radioheight)
        {
            setMeasuredDimension(width,height);
        }
        else
        {
            if(width<height*radiowidth/radioheight)
                setMeasuredDimension(width,width*radioheight/radiowidth);
            else
                setMeasuredDimension(height*radiowidth/radioheight,height);
        }
    }
}
