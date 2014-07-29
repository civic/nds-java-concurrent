package com.mycompany.concurrent;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timerを使った遅延実行
 */
public class Study03Timer {
    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask tickTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("tick: " + new Date());
            }
        };
        timer.schedule(tickTask, 1000, 500);    //1000ms後から500ms間隔で実行

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
            }
        }, 5000);   //5000ms後にtaskキャンセル

        /*
        tick: Fri Aug 01 00:09:47 JST 2014
        tick: Fri Aug 01 00:09:47 JST 2014
        tick: Fri Aug 01 00:09:48 JST 2014
        tick: Fri Aug 01 00:09:48 JST 2014
        tick: Fri Aug 01 00:09:49 JST 2014
        tick: Fri Aug 01 00:09:49 JST 2014
        tick: Fri Aug 01 00:09:50 JST 2014
        tick: Fri Aug 01 00:09:50 JST 2014
        */
    }
    
}
