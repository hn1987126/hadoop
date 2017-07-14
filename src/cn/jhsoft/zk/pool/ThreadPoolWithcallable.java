package cn.jhsoft.zk.pool;

import java.net.Socket;
import java.util.concurrent.*;

/**
 * 固定线程数量的线程池
 * Created by chen on 2017/7/9.
 */
public class ThreadPoolWithcallable {

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 14; i++) {
            Future<String> submit = pool.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return Thread.currentThread().getName();
                }
            });
            System.out.println(submit.get());
        }
        pool.shutdown();
    }

}
