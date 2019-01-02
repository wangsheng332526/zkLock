
package com.test.lock;

import java.util.Date;

/**
  * TODO 请在此处添加注释
  * @author <a href="mailto:"wangsheng"@zjiec.com”>"wangsheng"</a>
  * @version 2019年1月1日  下午1:52:03  
  * @since 2.0
  */
public class UserServiceImpl implements UserService{
	public void sayName(String userName){
		System.out.println(new Date()+","+userName+"自我介绍....");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
