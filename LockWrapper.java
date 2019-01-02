
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.ZooDefs.Ids;

/**
  * TODO 请在此处添加注释
  * @author <a href="mailto:"wangsheng"@zjiec.com”>"wangsheng"</a>
  * @version 2019年1月1日  下午1:36:02  
  * @since 2.0
  */
public class LockWrapper {
	private static CountDownLatch latch = new CountDownLatch(1);
	private static Thread thread = Thread.currentThread(); 
	private Object obj;
	private Object result;
	
	public LockWrapper(Object obj){
		this.obj = obj;
	}
	
	public Object getProxy(){
		
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				ZooKeeper zk = connectServer();
				if(zk!=null){
					createNode(zk,method,args);
				}
				return result;
				
			}
		});
		
	}
	
	private ZooKeeper connectServer() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					System.out.println(".....ccc...." + event);
					if (event.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown(); // 唤醒当前正在执行的线程
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zk;
	}
	
	private void createNode(final ZooKeeper zk,Method method, Object[] args){
		zk.create("/lockDemo/lockId", "".getBytes(), Ids.OPEN_ACL_UNSAFE
				, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
					
					@Override
					public void processResult(int rc, String path, Object ctx, String name) {
						System.out.println("rc:"+rc+",path:"+path+",ctx:"+ctx+",real ptah name:"+name);
						if(rc==0){//创建成功
							System.out.println("创建成功,开始执行业务方法....");
							try {
								result = method.invoke(obj, args);
								zk.delete("/lockDemo/lockId", 0);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
							thread.interrupt();
						}else if(rc==-110){//已经存在节点
							System.out.println("创建失败,已经存在节点");
							watchNode(zk,method,args);
						}
					}
				}, "");
		
	}

	private void watchNode(final ZooKeeper zk,Method method, Object[] args) {
		try {
			System.out.println("开始监控节点");
			List<String> nodeList = zk.getChildren("/lockDemo", new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						System.out.println("发现节点有变化");
						createNode(zk,method,args);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
