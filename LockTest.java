
package com.test.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
  * TODO 请在此处添加注释
  * @author <a href="mailto:"wangsheng"@zjiec.com”>"wangsheng"</a>
  * @version 2019年1月1日  下午1:51:36  
  * @since 2.0
  */
public class LockTest {
	public static void main(String[] args) {
		UserService u = new UserServiceImpl();
		UserService uu = (UserService) new LockWrapper(u).getProxy();
		
		ExecutorService es = Executors.newFixedThreadPool(3);
		for(int i=0;i<10;i++){
			final int s = i;
			Runnable r = new Runnable() {
				@Override
				public void run() {
					uu.sayName("AAA"+s);
				}
			};	
			es.submit(r);
		}
	}
}	
