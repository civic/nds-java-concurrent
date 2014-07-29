Java 並行処理 今昔物語
================================

こちらのキーワード集を参考に、Javaにおける並行・並列処理の進化をまとめてみる。

[Java並行・並列・非同期処理チートシート - Qiita](http://qiita.com/yohhoy/items/bc119324d2b69570597b)


## concurrent関係 歴史年表..

| Version | Class / Package / Syntax    | Year |
|:-------:|:---------------------------:|:----:|
|  1.0    | Thread / synchronized       | '96  |
|  1.2    | Synchronized Collection     | '98  |
|         | ThreadLocal                 |      |
|  1.3    | Timer                       | '00  |
|   5     | ConcurrentHashMap ...       | '04  |
|         | Semaphore, Lock ...         |      |
|         | java.util.atomic ...        |      |
|   6     | CopyOnWriteArrayList ...    | '06  |
|   7     | Fork/Join ...               | '11  |
|   8     | parallelStream              | '14  |

## 並行処理について
### Thread

Java 1.0から。Runnableインターフェースを実行するThread。最も低レベルな実装。

Threadの生成、開始、排他制御、同期は自分でやらなければならない。

#### Threadの生成と実行

```java
Thread th = new Thread(new Runnable(){
    public void run(){
        //このスレッドでの処理
    }
});
th.start();     //スレッドの実行開始
```
- スレッドでの途中経過・実行結果を取得する方法は？
- 複数のスレッドで同じ変数を参照・更新する場合は？
- スケジュール的な実行は？

1.5以降ならExecutorsでOK。

#### synchronized 排他制御

```java
public synchronized void add(){ //メソッド全体
    //...
}

Object lock  = new Object();
synchronized(lock){     //ロックオブジェクトとブロック
    // ...
}
```
- コスト高
- ブロック構文に縛られる

### java.util.Timer

Java 1.3から。バックグラウンドでの遅延実行。単一のスレッドが割り当てられ、指定時間後または一定間隔にタスクを実行する。

javascriptのsetTimeout, setInterval的な。

単一スレッドで実行するのでタスクの実行に時間がかかると、スレッドを専有し、後続のタスクの実行に影響する。

```java
Timer timer = new Timer();
timer.schedule(new TimerTask(){
  public void run(){
      // ...
  }
}, 1000, 5000); //1秒後に開始、5秒間隔
```

1.5以降ならExecutorsでOK。

### java.util.concurrent.Executors

Java5から。別スレッドでのタスク実行をExecutorServiceに登録して実行。ExecutorServiceはシングルスレッドだったり固定数のスレッドプールだったりできる。

Futureを使うことで、別スレッドでのタスク実行結果を受け取るのが楽になる。ブロッキング、タイムアウト

```java
ExecutorsService es = Executors.newSingleThreadExecutor();
Future<Integer> future = es.submit(new Callable<>(){
    // ...
    return ret;
});

Integer ret = futore.get(5, TimeUnit.SECONDS); //タスクの終了までブロック(最大5秒)
```

ExecutorsServiceの実装を取り替えることで、タスク実行スレッドを変化させることができる。

```java
ExecutorsService es = Executors.newFixedThreadPool(3);  //3スレッドでタスク消化
```

### Fork/Join

Java7から。Executorsよりも、細粒度のタスクを実行する場合にExecutorsよりも高速。

ForkJoinPoolに、RecursiveTask(返り値あり)またはRecursiveAction(返り値なし)を登録して並列に実行する。

I/O処理中心のタスクよりも、CPU処理中心のタスクを実行する場合に効果的。

- 再帰的アルゴリズムとの適合性
- コア数増加によるロック競合の減少


```java

compute(){
    if (作業.サイズ < しきい値){
        return doWork(作業);
    } else {
        f1 = fork(分割した作業の前半);
        f2 = fork(分割した作業の後半);
        2つのfork処理がjoinするまで待機
    }
}
```

### Java8 parallelStream

Java8から。streamを並列処理する。内部的にFork/Joinを使っている。

```java
int total = IntStream.range(1, 1000)
              .parallel()
              .sum();
```
streamでの汎用的な処理を、簡単に並列化できる。

## スレッドセーフのために

### Synchronized Collection

Java1.2から。 複数のスレッドで安全にデータを操作しなければならない。
ArrayList, HashMapなどは複数のスレッドから触ると意図しないデータ操作になりかねない。

例： ArrayListにaddする処理は、内部的に配列の末尾インデックス＋１に要素を設定し、
要素数を増やすという処理が行われるが、複数のスレッドからaddされた場合に、要素数、インデックスの更新がアトミックに行われない。

```java
List<String> syncList = Collections.synchronizedList(new ArrayList<>());

Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());

```

Collections.synchronized〜は同期化したコレクションにしてくれる。Vectorを使うのと比べて、Listの実装は任意のものが使用できる。

- Vector: スレッドセーフ。遅い
- ArrayList: スレッド庵セーフ。速い
- Collections.synchronizedList: スレッドセーフ。遅い。リストの実装は任意

ただし、これはCollection内部の操作が同期化されるものなので、自分でgetしてaddするような2つの操作は同期化されないので
自分で行う必要がある。

### ConcurrentHashMap

Java5から。java.util.concurrent.ConcurrentHashMapは、スレッドセーフでありながら、
Collections.synchronizedMapよりも高い更新平行性をサポートする。

Collections.synchronizedMapや、Hashtableは、イテーレーション中に別スレッドで更新操作が行われると、
ConcurrentModificationExceptionを発生する。そのためイテレーション操作中はsynchronizedで排他制御する必要があった。

ConcurrentHashMapは、ある時点でのイテレーションを開始するので、そのような問題は発生しない。

getしてputのような値を参照して置き換えるような操作は、computeメソッドでアトミックに実行することができる。

### AtomicInteger

Java5から。プリミティブ型においても、複数スレッドで更新操作を行った場合は`i++`のような
一見アトミックにみえるインクリメント操作であっても意図しない結果になる可能性がある。

AtomicInteger、AtomicLongのようなクラスはスレッドセーフでロックフリーな変数として使用できる。

```java
AtomicInteger i = new AtomicInteger(1);
i.incrementAndGet();
```

### シンクロナイザ

java5から。マルチスレッドでの同期方法をサポートするためのユーティリティ。

いろいろある。Semaphore, CountDownLatch, CyclicBarrier, Phaser, Exchanger...


```java
CountDownLatch latch = new CountDownLatch(3);
for (int n = 0; n < 3; n++){
    Thread th = new Thread(() -> {
        // ... なんらかの処理
        latch.countDown();
    });
    th.start();
}
latch.await(2, TimeUnit.SECONDS);  //latchが0になるのを最大2秒wait

```
