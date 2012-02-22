/**
 * 
 */
package com.sohu.wuhan;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Bundle;
import android.os.Message;

import com.sohu.wuhan.Constant.Error;

/**
 * @author Leon
 * 
 */

public class AsyncTask extends Thread {
	private DirectUrl durl = null;
	private boolean bStop = false;
	private Lock lock = new ReentrantLock(false);
	private Condition condition = lock.newCondition();

	private LinkedList<Task> taskList = new LinkedList<Task>();

	public AsyncTask(DirectUrl __durl) {
		durl = __durl;
	}

	@Override
	public void run() {
		while (!bStop) {
			try {
				while (!bStop) {
					lock.lock();
					try {
						if (0 == taskList.size())
							condition.await();

						if (bStop)
							break;

						Task task = taskList.removeFirst();
						if (null != task) {
							durl.error = Error.OK;
							String ss = durl.call_url(task.getTarget(),
									task.getMethod(), task.getContent());
							if (bStop)
								break;

							Message msg = new Message();
							Bundle data = new Bundle();
							data.putSerializable("error", durl.error);
							data.putString("result", ss);
							msg.setData(data);
							task.getHandler().sendMessage(msg);
						}
					} finally {
						lock.unlock();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* Only Used in internal call */
	protected void postTask(Task __task) {
		lock.lock();
		try {
			taskList.addLast(__task);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/* Only take effective on Asynchronous call */
	public void cancel() {
		bStop = true;
	}

	public boolean isRunning() {
		return (false == bStop);
	}
}
