package cn.jhsoft.zk.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 * Created by chen on 2017/7/9.
 */
public class ThreadPoolWithRunable {


    public static void main(String[] args) {



        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName()+"在工作");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                    }
                }
            });
        }
        pool.shutdown();

    }

}
