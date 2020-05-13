package com.example.flowlayout;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BY on 2020/04/22
 * 说明:
 */
public class MFlowLayout extends ViewGroup {
    //横纵间距
    private int horizontalSpacing = dp2px(16);
    private int verticalSpacing = dp2px(8);
    private List<List<View>> allLineView;
    private List<View> lineView;


    public MFlowLayout(Context context) {
        super(context);
    }

    public MFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        allLineView = new ArrayList<>();
        lineView = new ArrayList<>();
        int viewCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int viewGroupWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewGroupHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.e("MFlowLayout", " onMeasure 55 width:" + viewGroupWidth + " hright:" + viewGroupHeight);
        int viewGroupWidthSpecModel = MeasureSpec.getMode(widthMeasureSpec);
        int viewGroupHeightSpecModel = MeasureSpec.getMode(heightMeasureSpec);
        //要根据测量模式来显示控件的大小 需要知道测量的精确宽度和高度
        int childViewMaxWidth = 0;
        int childViewMaxHeight = 0;
        //每行测量的宽度
        int lineMeasureWidthTotal = 0;
        //每行测量的最大的高度
        int linMeasureHeight = 0;
        for (int i = 0; i < viewCount; i++) {
            View view = getChildAt(i);
            //获取子布局的参数
            LayoutParams childLP = view.getLayoutParams();
            //获取子布局widthMeasureSpec
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                    childLP.width);
            //获取子布局heighMeasureSpec
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                    childLP.height);
            view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            //获取子view的宽高
            int childMesauredWidth = view.getMeasuredWidth();
            int childMeasuredHeight = view.getMeasuredHeight();
            //获取到子view进行判断是否要换行  这样存在重复代码
//            if (lineMeasureWidthTotal + view.getWidth() < viewGroupWidth) {
            if (lineMeasureWidthTotal + childMesauredWidth + horizontalSpacing > viewGroupWidth) {
                //换行需要把上一整行的View保存起来
                allLineView.add(lineView);
                //需要把每一行最大高度加起来用于最后的控件高度控制
                childViewMaxHeight += linMeasureHeight;
                //获取所有行的最大宽度
                childViewMaxWidth = Math.max(childViewMaxWidth, lineMeasureWidthTotal);
                linMeasureHeight = 0;
                lineMeasureWidthTotal = 0;
                lineView = new ArrayList<>();
            }
            lineMeasureWidthTotal += childMesauredWidth + horizontalSpacing;
            //保存最大View的高度
            linMeasureHeight = Math.max(linMeasureHeight, childMeasuredHeight + verticalSpacing);
            lineView.add(view);
            //判断最后一个view所占的位置是否比之前的都要大
            if (i == viewCount - 1) {
                allLineView.add(lineView);
                childViewMaxHeight += linMeasureHeight;
                //获取所有行的最大宽度
                childViewMaxWidth = Math.max(childViewMaxWidth, lineMeasureWidthTotal);
            }
        }
        int realWidth = (viewGroupWidthSpecModel == MeasureSpec.EXACTLY) ? viewGroupWidth : childViewMaxWidth;
        int realHeight = (viewGroupHeightSpecModel == MeasureSpec.EXACTLY) ? viewGroupHeight : childViewMaxHeight;
        //设置控件的宽高
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //开始绘制
        int viewLeftPoint = 0;
        int viewTopPoint = 0;
        int viewmaxHeight = 0;
        for (List<View> line : allLineView) {
            viewLeftPoint = 0;
            viewmaxHeight = 0;
            for (View view : line) {
                view.layout(viewLeftPoint, viewTopPoint, viewLeftPoint + view.getMeasuredWidth(), viewTopPoint + view.getMeasuredHeight());
                viewLeftPoint += view.getMeasuredWidth() + horizontalSpacing;
                viewmaxHeight = Math.max(viewmaxHeight, view.getMeasuredHeight() + verticalSpacing);
            }
            viewTopPoint += viewmaxHeight;
        }
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
