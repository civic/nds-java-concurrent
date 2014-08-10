
package concurrent_study;

/**
 * synchronizedの利用例。
 * synchronizedを使って、2つのスレッドからカウンターnumを安全にインクリメント。
 */
public class Study02Synchronized {
    private static int num = 0;
    public static void main(String[] args) throws Exception{
        int LOOP = 50000;
        Object lock = new Object();
        Thread th1 = new Thread(()->{
            for (int i = 0; i < LOOP; i++){
                synchronized(lock){
                    num++;
                }
            }
        });
        Thread th2 = new Thread(() -> {
            for (int i = 0; i < LOOP; i++){
                synchronized(lock){
                    num++;
                }
            }
        });
        th1.start();
        th2.start();

        th1.join();
        th2.join();

        System.out.printf("num=%d\n", num);     //100000?
    }
}
