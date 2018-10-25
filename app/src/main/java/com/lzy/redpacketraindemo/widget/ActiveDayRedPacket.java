package com.lzy.redpacketraindemo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.redpacketraindemo.R;
import com.lzy.redpacketraindemo.pojo.ActiveDayConfigModel;
import com.lzy.redpacketraindemo.util.CollectionUtils;
import com.lzy.redpacketraindemo.util.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 虔诚猫日捉猫猫游戏的view   android:animateLayoutChanges="true"
 * Created by Administrator on 2018/1/3.
 */

public class ActiveDayRedPacket extends FrameLayout {

    private static final int DEAFAULT_WIDTH = 800;   //容器默认宽
    private static final int DEAFAULT_HEIGHT = 800;  //容器默认高

    private int width, height;//容器初始宽高

    private Context mContext;

    private int dataIndex = 0;//

    private ActiveDayConfigModel.Unit unit;
    //数据源
    private List<ActiveDayConfigModel.Unit> mUnits = new ArrayList<>();
    //游戏结果数据
    private List<ActiveDayConfigModel.Unit> resultList = new ArrayList<>();

    private LayoutParams itemParams;

    private ImageView enterView; //进场view

    private GifDrawable enterGifDrawable;

    private int itemWidth, itemHeight;

    //随机出现的猫图标
    private int[] rangIcons = new int[]{
            R.drawable.active_day_rang_1,
            R.drawable.active_day_rang_2,
            R.drawable.active_day_rang_3,
            R.drawable.active_day_rang_4
    };


    private boolean isPause = true;

    private LayoutInflater layoutInflater;

    public ActiveDayRedPacket(@NonNull Context context) {
        this(context, null);
    }

    public ActiveDayRedPacket(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveDayRedPacket(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMySize(DEAFAULT_WIDTH, widthMeasureSpec);
        height = getMySize(DEAFAULT_HEIGHT, heightMeasureSpec);
        itemWidth = width / 5;
        itemHeight = width / 5;
        itemParams = new LayoutParams(itemWidth, itemHeight);
        setMeasuredDimension(width, height);
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    public void setDataAndStart(List<ActiveDayConfigModel.Unit> Units) {
        mUnits.clear();
        mUnits.addAll(Units);
        resultList.clear();
        removeAllViews();
        startGame(0);
    }

    private void generateEnterView() {
        if (CollectionUtils.isEmpty(mUnits)) return;
        if (enterView != null) removeView(enterView);
        enterView = new ImageView(mContext);
        final int x = RandomUtils.getRandom(width - itemWidth);
        final int y = RandomUtils.getRandom(height - itemHeight);
        enterView.setX(x);
        enterView.setY(y);
        unit = mUnits.get(dataIndex % mUnits.size());
        try {
            enterGifDrawable = new GifDrawable(getResources(), rangIcons[unit.Type - 1]);
            enterView.setImageDrawable(enterGifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enterView.setOnClickListener(null);
                resultList.add(unit);
                if (mActiveDayLisenter != null) mActiveDayLisenter.onViewClick(unit);
                startGame(100);
                try {
                    generateExitView(unit, x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        addView(enterView, itemParams);
        ++dataIndex;
        startGame((int) (unit.Duration * 1000));
    }

    private void generateExitView(ActiveDayConfigModel.Unit unit, int x, int y) throws IOException {
        View exitView = getExitView();
        exitView.setX(x);
        exitView.setY(y + 50);
        addView(exitView, new LayoutParams(itemWidth, itemHeight + 50));
        PropertyValuesHolder disappearingAlpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        PropertyValuesHolder disappearingTranslationY = PropertyValuesHolder.ofFloat("translationY", exitView.getY(), exitView.getY() - 100);
        ObjectAnimator exitObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(exitView, disappearingAlpha, disappearingTranslationY);
        exitObjectAnimator.setDuration(600);
        exitObjectAnimator.start();
        exitObjectAnimator.addListener(new ExitAnimatorListenerAdapter(exitObjectAnimator, exitView));

    }

    private View getExitView() throws IOException {
        View exitView = layoutInflater.inflate(R.layout.content_active_day_game_item_exit_view, null);
        ImageView imageView = (ImageView) exitView.findViewById(R.id.img_content);
        TextView textView = (TextView) exitView.findViewById(R.id.txt_content);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));
        GifDrawable exitGifDrawable;
        String tsStr;
        if (unit.Result == 0) {//没中奖
            exitGifDrawable = new GifDrawable(getResources(), R.drawable.active_day_no_prize);
            tsStr = "空空如也~";
        } else {//中奖啦
            exitGifDrawable = new GifDrawable(getResources(), R.drawable.active_day_prize);
            tsStr = "中奖啦~";
        }
        imageView.setImageDrawable(exitGifDrawable);
        textView.setText(tsStr);
        return exitView;
    }

    class ExitAnimatorListenerAdapter extends AnimatorListenerAdapter {

        ObjectAnimator objectAnimator;
        View view;

        public ExitAnimatorListenerAdapter(ObjectAnimator objectAnimator, View view) {
            this.objectAnimator = objectAnimator;
            this.view = view;
        }

        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {
            //setLayoutTransition(null);//去除布局动画，必须加这句，不然在隐藏或移除view时会执行最后一次执行的动画
            if (objectAnimator != null) objectAnimator.cancel();
            objectAnimator = null;
            if (view != null) removeView(view);
            view = null;

        }
    }

    /**
     * 返回游戏结果数据
     *
     * @author lzy
     * create at 2018/1/10 11:04
     **/
    public List<ActiveDayConfigModel.Unit> getResultData() {
        return resultList;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isPause) {
                generateEnterView();
            }
        }
    };

    private void initTimer(int deleayTime) {
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1, deleayTime);
    }

    public void startGame(int deleayTime) {
        isPause = false;
        initTimer(deleayTime);
    }

    public void resumeGame() {
        if (!isPause) return;
        isPause = false;
        initTimer(0);
    }

    public void pauseGame() {
        if (isPause) return;
        isPause = true;
        handler.removeMessages(1);
    }

    public void stopGame() {
        isPause = true;
        handler.removeMessages(1);
        removeAllViews();
    }

    public boolean isPause() {
        return isPause;
    }

    @Override
    protected void onDetachedFromWindow() {
        stopGame();
        super.onDetachedFromWindow();
    }

    ActiveDayLisenter mActiveDayLisenter;

    public void setActiveDayLisenter(ActiveDayLisenter activeDayLisenter) {
        this.mActiveDayLisenter = activeDayLisenter;
    }

    public interface ActiveDayLisenter {
        void onViewClick(ActiveDayConfigModel.Unit unit);
    }


    /**
     * 添加布局动画（作用于子view）
     *
     * @author lzy
     * create at 2018/1/4 15:05
     **/
    private void initLayoutAnimation() {
        LayoutTransition mLayoutTransition = new LayoutTransition();

        //设置每个动画持续的时间
        mLayoutTransition.setStagger(LayoutTransition.APPEARING, 50);
//        mLayoutTransition.setStagger(LayoutTransition.DISAPPEARING, 800);
//        mLayoutTransition.setStagger(LayoutTransition.CHANGE_APPEARING, 50);
//        mLayoutTransition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 50);


        PropertyValuesHolder appearingScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1.0f);
        PropertyValuesHolder appearingScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1.0f);
        PropertyValuesHolder appearingAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        ObjectAnimator mAnimatorAppearing = ObjectAnimator.ofPropertyValuesHolder(this, appearingAlpha, appearingScaleX, appearingScaleY);
        //为LayoutTransition设置动画及动画类型
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, mAnimatorAppearing);

//        PropertyValuesHolder disappearingAlpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
////        PropertyValuesHolder disappearingRotationY = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 180.0f);
//        PropertyValuesHolder disappearingTranslationY = PropertyValuesHolder.ofFloat("translationY", imageView.getY(), imageView.getY() - 100);
//        ObjectAnimator mAnimatorDisappearing = ObjectAnimator.ofPropertyValuesHolder(this, disappearingAlpha,disappearingTranslationY);
//        //为LayoutTransition设置动画及动画类型
//        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mAnimatorDisappearing);

//        ObjectAnimator mAnimatorChangeDisappearing = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f);
//        //为LayoutTransition设置动画及动画类型
//        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mAnimatorChangeDisappearing);
//
//        ObjectAnimator mAnimatorChangeAppearing = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f);
//        //为LayoutTransition设置动画及动画类型
//        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, mAnimatorChangeAppearing);

        //为mImageViewGroup设置mLayoutTransition对象
//        this.setLayoutTransition(mLayoutTransition);

    }

}
