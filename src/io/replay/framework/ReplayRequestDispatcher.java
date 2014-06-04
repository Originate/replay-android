package io.replay.framework;

import java.util.concurrent.BlockingQueue;

import android.os.Process;

public class ReplayRequestDispatcher extends Thread {
	
	private volatile boolean mQuit = false;
	private volatile int dispatchInterval = 0;
	private volatile boolean dispatching = false;

	private final BlockingQueue<ReplayRequest> mQueue;
	private final ReplayAPIManager mManager;
	private final ReplayRequestDelivery mDelivery;
	
	public ReplayRequestDispatcher(BlockingQueue<ReplayRequest> queue, 
			ReplayAPIManager manager, ReplayRequestDelivery delivery) {
		mQueue = queue;
		mManager = manager;
		mDelivery = delivery;
	}
	
	public void quit() {
		mQuit = true;
		interrupt();
	}
	
	public void setDispatchInterval(int interval) {
		dispatchInterval = interval;
	}
	
	public void dipatchNow() {
		dispatching = true;
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		
		ReplayRequest request;
		
		int waited = 0;
		while (true) {
			// check if the queue is empty for this dispatch, wait if empty
			synchronized (mQueue) {
	        	if (dispatching && mQueue.isEmpty()) {
	        		dispatching = false;
	        	}
			}

        	// sleep for a interval time
        	if (!dispatching && dispatchInterval >= 0) {
    			try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// We may have been interrupted because it was time to quit.
					if (mQuit) {
	                    return;
	                }
	                continue;
				}
    			waited += 100;
    			
        		if (waited < dispatchInterval * 1000) {
        			// get back to wait again
        			continue;
        		} else {
        			// time to dispatch 
        			waited = 0;
        			dispatching = true;
        		}
        	}
        	
            try {
            	request = mQueue.poll();
            	
            	if (null == request) {
            		// wait for the next interval if no request to dispatch
            		continue;
            	}
            	
	            boolean success = mManager.doPost(request);
	            
	            if (success) {
	            	mDelivery.successPost(request);
	            }
			} catch (Exception e) {
				ReplayIO.errorLog(e.getMessage());
			}
		}
	}
}
