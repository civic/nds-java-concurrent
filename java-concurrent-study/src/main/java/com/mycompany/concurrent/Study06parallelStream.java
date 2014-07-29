
package com.mycompany.concurrent;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * parallelStreamによる並行処理。
 * @author tsasaki
 */
public class Study06parallelStream {
    
    public static void main(String[] args) {
        
        for (int n = 10; n <= 10_000_000; n*= 10){
            List<Integer> list = IntStream.range(1, n+1).boxed().collect(Collectors.toList());


            long start = System.currentTimeMillis();
            BigDecimal result = list.parallelStream()
                    .map(num -> BigDecimal.valueOf(num).pow(2))     // convert BigDecimal: n*2
                    .reduce((a, b)->{return a.add(b);})             // sum
                    .orElse(BigDecimal.ZERO);

            long end = System.currentTimeMillis();

            System.out.printf("calc %8d:  %22s (%6dmsec)\n", n, result.toString(), end-start);

            /*

            parallelStream
calc       10:                     385 (     8msec)
calc      100:                  338350 (     0msec)
calc     1000:               333833500 (     2msec)
calc    10000:            333383335000 (    11msec)
calc   100000:         333338333350000 (    34msec)
calc  1000000:      333333833333500000 (    97msec)
calc 10000000:   333333383333335000000 (  1657msec)

            stream
calc       10:                     385 (     3msec)
calc      100:                  338350 (     1msec)
calc     1000:               333833500 (     5msec)
calc    10000:            333383335000 (     9msec)
calc   100000:         333338333350000 (    34msec)
calc  1000000:      333333833333500000 (   105msec)
calc 10000000:   333333383333335000000 (  3200msec)
            */

            int sum = IntStream.range(1, 1000).parallel()
                    .sum();
            System.out.println(sum);
        }
    }
}
