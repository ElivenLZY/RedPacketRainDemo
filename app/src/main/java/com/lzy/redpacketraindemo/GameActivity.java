package com.lzy.redpacketraindemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.lzy.redpacketraindemo.pojo.ActiveDayConfigModel;
import com.lzy.redpacketraindemo.util.CollectionUtils;
import com.lzy.redpacketraindemo.util.DataHelper;
import com.lzy.redpacketraindemo.widget.ActiveDayRedPacket;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {

    @BindView(R.id.txt_reward_count)
    TextView txtRewardCount;
    @BindView(R.id.txt_game_time)
    TextView txtGameTime;
    @BindView(R.id.layout_game)
    ActiveDayRedPacket layoutGame;

    private int duration;

    private ActiveDayConfigModel data;

    private Timer gameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        gameTimer = new Timer();
        initData();
        initView();
    }

    private void initData() {
        data = DataHelper.getData();
        duration = data.Duration;
    }

    private void initView() {
        if (!CollectionUtils.isEmpty(data.Units)) {
            startDownTime();
        } else {
            finish();
        }
    }

    private void startDownTime() {
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!layoutGame.isPause()) {
                            Log.d("gameTimer", duration + "");
                            txtGameTime.setText(duration + "");
                            --duration;
                            if (duration < 0) {
                                gameTimer.cancel();
                                showGameOverDialog();
                            }
                        }
                    }
                });
            }
        }, 0, 1000);

        layoutGame.setActiveDayLisenter(new ActiveDayRedPacket.ActiveDayLisenter() {
            @Override
            public void onViewClick(ActiveDayConfigModel.Unit unit) {
                txtRewardCount.setText(layoutGame.getResultData().size() + "");
            }
        });
        layoutGame.setDataAndStart(data.Units);
    }

    private void showGameOverDialog() {
        layoutGame.stopGame();
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("游戏时间到啦~\n共抓获小喵" + layoutGame.getResultData().size() + "个\n")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        layoutGame.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutGame.resumeGame();
    }

    @Override
    protected void onDestroy() {
        if (gameTimer != null) gameTimer.cancel();
        layoutGame.stopGame();
        super.onDestroy();

    }

}
