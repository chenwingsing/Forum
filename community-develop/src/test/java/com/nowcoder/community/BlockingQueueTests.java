package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//生产者 消费者
public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);//队列最多10个数
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();

    }
}

class Producer implements Runnable {
    private BlockingQueue<Integer> queue;
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            for(int i = 0; i < 100; i++) {
                Thread.sleep(20);//20 ms 服务器处理时间是随机的，这里模拟了固定的时间
                queue.put(i);//put是阻塞方法
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size());//生产完后队列中有多少数
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;
    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            while(true) {
                Thread.sleep(new Random().nextInt(1000));//消费者时间不固定，模拟了真实业务，在0~1000随机
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费: " + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
