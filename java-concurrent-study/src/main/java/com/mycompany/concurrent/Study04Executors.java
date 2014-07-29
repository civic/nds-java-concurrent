package com.mycompany.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Exectutorsを使った並行処理
 * @author t_sasaki
 */
public class Study04Executors {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService es = Executors.newFixedThreadPool(4);

        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            Future<Integer> future = es.submit(()->{        //new Callable<Integer>(){}
                System.out.println(Thread.currentThread().getName() + " 開始");
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 終了");
                return 1;
            });
            futures.add(future);
        }

        for (int i = 0; i < 4; i++){
            Future<Integer> future = futures.get(i);
            System.out.printf("     タスク(%d) 実行結果 %d\n", i, future.get(2, TimeUnit.SECONDS));
        }
        es.shutdown();

        /*
        Executors.newFixedThreadPool(2)の場合の実行結果

        pool-1-thread-2 開始
        pool-1-thread-1 開始
        pool-1-thread-2 終了
        pool-1-thread-1 終了
        pool-1-thread-2 開始
             タスク(0) 実行結果 1
             タスク(1) 実行結果 1
        pool-1-thread-1 開始
        pool-1-thread-2 終了
             タスク(2) 実行結果 1
        pool-1-thread-1 終了
             タスク(3) 実行結果 1

        */

        /*
        Executors.newFixedThreadPool(4)の場合の実行結果

        pool-1-thread-1 開始
        pool-1-thread-4 開始
        pool-1-thread-3 開始
        pool-1-thread-2 開始
        pool-1-thread-3 終了
        pool-1-thread-2 終了
        pool-1-thread-1 終了
        pool-1-thread-4 終了
             タスク(0) 実行結果 1
             タスク(1) 実行結果 1
             タスク(2) 実行結果 1
             タスク(3) 実行結果 1

        */
    }
    
}
