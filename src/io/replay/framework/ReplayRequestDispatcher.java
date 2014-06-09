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

	/**
	 * The dispatcher that controls the processing of requests in the queue.
	 * <br>
	 * There are three different approach for different value of interval. If dispatch interval is negative,
	 * the dispatcher will wait for a dispatching signal, once got, process all requests in queue. If zero,
	 * the dispatcher will wait for requests to be add to the queue, process immediately once added. If positive
	 * value is set, the dispatcher will wait that amount of seconds and produce a dispatching signal to process
	 * all the request in queue. 
	 */
	public ReplayRequestDispatcher(BlockingQueue<ReplayRequest> queue, 
			ReplayAPIManager manager, ReplayRequestDelivery delivery) {
		mQueue = queue;
		mManager = manager;
		mDelivery = delivery;
	}
	
	/**
	 * Stop the dispatcher by interrupt the thread.
	 */
	public void quit() {
		mQuit = true;
		interrupt();
	}
	
	/**
	 * Update the dispatcher interval.
	 * @param interval The dispatch interval in seconds.
	 */
	public void setDispatchInterval(int interval) {
		dispatchInterval = interval;
	}
	
	/**
	 * Set the dispatching signal to true.
	 */
	public void dipatchNow() {
		dispatching = true;
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		
		ReplayRequest request;
		
		int waited = 0;
		while (true) {
			// interval < 0, wait for manual dispatch
			if (dispatchInterval < 0) {
				if (!dispatching) {
					// wait for a while and check for dispatching signal again
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// We may have been interrupted because it was time to quit.
						if (mQuit) {
		                    return;
		                }
		                continue;
					}
				} else {
	            	request = mQueue.poll();
	            	
	            	if (null == request) {
	            		// wait for the next manual dispatch
	            		dispatching = false;
	            		continue;
	            	} else {
	            		executeRequest(request);
	            	}
				}
			}
			// dispatch immediately when there's request in the queue
			else if (dispatchInterval == 0) {
    			try {
					request = mQueue.take();
					
					executeRequest(request);
				} catch (InterruptedException e) {
					if (mQuit) {
						return;
					}
					continue;
				}
        	}
			// dispatchInterval > 0, wait for interval time
			else {
				if (!dispatching) {
					// sleep 100 milliseconds a time
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
	        			continue;
	        			// will be dispatching in the next loop
	        		}
				} else {
					request = mQueue.poll();
		        	
					// wait for the next interval if no request to dispatch
		        	if (null == request) {
		        		// stop dispatching for this interval
		        		dispatching = false;
		        		continue;
		        	} else {
		        		executeRequest(request);
		        	}
				}
			}
		}
	}
	
	private void executeRequest(ReplayRequest request) {
		try {
			boolean success = mManager.doPost(request);
            
            if (success) {
            	mDelivery.successPost(request);
            }
		} catch (Exception e) {
			ReplayIO.errorLog(e.getMessage());
		}
	}
}
