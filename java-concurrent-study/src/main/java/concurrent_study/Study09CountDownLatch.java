
package concurrent_study;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CountDownLatchでの同期。
 * 3つのスレッドがそれぞれ終了時にcountDownし、0になるのを待つ。
 */
public class Study09CountDownLatch {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++){
            Thread th = new Thread(()->{
                try {
                    System.out.println("wait start:" + Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("wait end:  " + Thread.currentThread().getName());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Study09CountDownLatch.class.getName()).log(Level.SEVERE, null, ex);
                }
                latch.countDown();
            });
            th.start();
        }

        latch.await(2, TimeUnit.SECONDS);  //最大2秒wait
        if (latch.getCount() == 0){
            System.out.println("Finish");
        } else {
            System.out.println("Timeout");
        }


    }
    
}
