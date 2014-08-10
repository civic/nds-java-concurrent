package concurrent_study;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Fork/Joinで細粒度のタスクを実行。
 * １〜10,000,000の要素をすべて2乗してBigDecimal化し合計する。forkJoinで並行的に計算する。
 */
public class Study05ForkJoin {
    
    private static class TotalTask extends RecursiveTask<BigDecimal>{
        private static final int THRESHOLD = 100;
        private final List<Integer> list;

        /**
         * 指定された配列の要素nの n^2の合計を算出するタスク
         */
        public TotalTask(List<Integer> list) {
            this.list = list;
        }

        @Override
        protected BigDecimal compute() {
            if (list.size() < THRESHOLD){
                //リストのサイズがTHRESHOLD未満
                BigDecimal total = BigDecimal.ZERO;
                for (Integer n : list){
                    total = total.add(BigDecimal.valueOf(n).pow(2));    // total += n^2
                }
                return total;
            } else {
                //THRESHOLD以上なら半分にタスク分割
                int index = this.list.size() / 2;
                TotalTask left = new TotalTask(list.subList(0, index));
                left.fork();
                TotalTask right = new TotalTask(list.subList(index, list.size()));
                right.fork();
                return right.invoke().add(left.invoke());
            }
        }
    }


    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        System.out.printf("parallesism=%d, poolsize=%d\n", pool.getParallelism(), pool.getPoolSize());

        for (int n = 10; n <= 10_000_000; n*= 10){
            List<Integer> list = IntStream.range(1, n+1).boxed().collect(Collectors.toList());

            long start = System.currentTimeMillis();
            BigDecimal result = pool.invoke(new TotalTask(list));
            long end = System.currentTimeMillis();

            System.out.printf("calc %8d:  %22s (%6dmsec)\n", n, result.toString(), end-start);
        }

        /*

parallesism=8, poolsize=0
calc       10:                     385 (     4msec)
calc      100:                  338350 (     3msec)
calc     1000:               333833500 (    12msec)
calc    10000:            333383335000 (    22msec)
calc   100000:         333338333350000 (    64msec)
calc  1000000:      333333833333500000 (   198msec)
calc 10000000:   333333383333335000000 (  4051msec)

        */
    }

}
