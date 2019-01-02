package com.test.lock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
  * 分布式锁
  * @author <a href="mailto:"wangsheng"@zjiec.com”>"wangsheng"</a>
  * @version 2019年1月2日  上午9:30:39  
  * @since 2.0
  */
public class CuratorLock {
	static String lock_path = "/curator_lock_path"; 
	static CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
								.sessionTimeoutMs(5000)
								.retryPolicy(new ExponentialBackoffRetry(1000, 3))
								.build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		final InterProcessMutex lock = new InterProcessMutex(client, lock_path);
		final CountDownLatch latch = new CountDownLatch(1);
		for(int i=0;i<20;i++){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						latch.await();
						lock.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.out.println("订单号:"+orderNo);
					try {
						lock.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}).start();
			
		}
		latch.countDown();
	}
}