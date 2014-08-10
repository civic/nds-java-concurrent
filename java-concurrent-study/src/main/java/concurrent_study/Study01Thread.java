
package concurrent_study;

/**
 * Threadの利用例
 * 2つのスレッドで異なるタイミングでカウンターを出力
 */
public class Study01Thread {
    public static void main(String[] args) throws Exception{

        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++){
                    System.out.printf("A : %d\n", i*300);

                    try { Thread.sleep(300); } catch (InterruptedException ex) {}
                }
            }
        });

        Thread th2 = new Thread(() -> {
            for (int i = 0; i < 10; i++){
                System.out.printf("           B : %d\n", i*200);
                
                try { Thread.sleep(200); } catch (InterruptedException ex) {}
            }
        });

        th1.start();
        th2.start();
        /* 
        output:
        ----------------------
            A : 0
                       B : 0
                       B : 200
            A : 300
                       B : 400
            A : 600
                       B : 600
                       B : 800
            A : 900
                       B : 1000
            A : 1200
                       B : 1200
                       B : 1400
            A : 1500
                       B : 1600
            A : 1800
                       B : 1800
            A : 2100
            A : 2400
            A : 2700
        */
    }
}
