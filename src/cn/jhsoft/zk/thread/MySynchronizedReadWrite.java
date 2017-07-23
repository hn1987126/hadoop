package cn.jhsoft.zk.thread;

/**
 * Created by chen on 2017/7/9.
 */
public class MySynchronizedReadWrite {

    public static void main(String[] args) {
        final MySynchronizedReadWrite test = new MySynchronizedReadWrite();

        new Thread(){
            @Override
            public void run() {
                test.get(Thread.currentThread());
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                test.get(Thread.currentThread());
            }
        }.start();

    }


    public synchronized void get(Thread thread){
        for (int i = 0; i < 5; i++) {
            if (i%4 == 0){
                System.out.println(thread.getName() + "读操作");
            }else{
                System.out.println(thread.getName() + "写操作");
            }
        }
        System.out.println(thread.getName() + "读写操作完毕");
    }

}
