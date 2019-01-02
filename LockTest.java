
 /**************************************************************************
 * Copyright (c) 2016-2020 ZheJiang International E-Commerce Services Co.,Ltd. 
 * All rights reserved.
 * 
 * 名称：kafka
 * 版权说明：本软件属于浙江国贸云商企业服务有限公司所有，在未获得浙江国贸云商企业服务有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
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
