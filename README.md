Java 並行処理 今昔物語
================================

こちらのキーワード集を参考に、Javaにおける並行・並列処理の進化をまとめてみる。

[Java並行・並列・非同期処理チートシート - Qiita](http://qiita.com/yohhoy/items/bc119324d2b69570597b)


## concurrent関係 歴史年表

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


## Thread

Java 1.0から。Runnableインターフェースを実行するThread。最も低レベルな実装。

Threadの生成、開始、排他制御、同期は自分でやらなければならない。

### Threadの生成と実行

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

### synchronized 排他制御

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

## java.util.Timer

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

## java.util.concurrent.Executors

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

## Fork/Join

Java7から。Executorsよりも、細かく大量の多数を実行する場合に高速。
