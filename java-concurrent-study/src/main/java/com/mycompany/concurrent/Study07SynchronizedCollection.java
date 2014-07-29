package com.mycompany.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 複数スレッドでのList操作
 */
public class Study07SynchronizedCollection {
    
    public static void main(String[] args) throws InterruptedException {
        //final List<String> list = new ArrayList<String>();
        final List<String> list = Collections.synchronizedList(new ArrayList<String>());

        CountDownLatch latch = new CountDownLatch(10);
        for (int n = 0; n < 10; n++){
            Thread th = new Thread(() -> {
                for (int i = 0; i < 100; i++){
                    list.add("hello");
                }
                latch.countDown();
            });
            th.start();
        }


        latch.await();  //10スレッドでの処理終了待ち
        System.out.println(list.size());
        
    }
}
