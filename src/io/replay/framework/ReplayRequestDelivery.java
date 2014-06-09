package io.replay.framework;

import java.util.concurrent.Executor;

import android.os.Handler;

public class ReplayRequestDelivery {

	private final Executor mResponsePoster;
	
	/**
	 * Post the request in a separate thread.
	 * @param handler The Handler on which requests will be executed, it needs to be <code> new Handler(Looper.getMainLooper()) </code>.
	 */
	public ReplayRequestDelivery(final Handler handler) {
		mResponsePoster = new Executor() {
			@Override
			public void execute(Runnable runnable) {
				handler.post(runnable);
			}
		};
	}
	
	/**
	 * Callback when the request a successfully posted.
	 * @param request The request that was posted.
	 */
	public void successPost(ReplayRequest request) {
		mResponsePoster.execute(new DeliveryRunnable(request));
	}
	
	/**
	 * Runnable deal with requests when they are successfully posted.
	 * @author richard
	 *
	 */
	private class DeliveryRunnable implements Runnable {
		private final ReplayRequest mRequest;
		
		public DeliveryRunnable(ReplayRequest request) {
			mRequest = request;
		}
		
		@Override
		public void run() {
			mRequest.finish();
		}
		
	}
}
