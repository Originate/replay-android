package io.replay.framework;

import java.util.concurrent.Executor;

import android.os.Handler;

public class ReplayRequestDelivery {

	private final Executor mResponsePoster;
	
	public ReplayRequestDelivery(final Handler handler) {
		mResponsePoster = new Executor() {
			@Override
			public void execute(Runnable runnable) {
				handler.post(runnable);
			}
		};
	}
	
	public void successPost(ReplayRequest request) {
		mResponsePoster.execute(new DeliveryRunnable(request));
	}
	
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
