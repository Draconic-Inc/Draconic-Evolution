package com.brandon3055.draconicevolution.common.utills;

/**
 * Created by Brandon on 24/02/2015.
 */
public class UpdateChecker
{
	private final UpdateCheckThread thread;

	public UpdateChecker()
	{
		thread = new UpdateCheckThread();
		thread.start();
	}



	public class UpdateCheckThread extends Thread
	{
		private String version;
		private int snapshot;
		private String note;
		private boolean checkComplete = false;
		private boolean checkFaild = false;

		@Override
		public void run() {
			LogHelper.info("Thread Run");
			try
			{
				LogHelper.info("Thread going to sleep");
				Thread.sleep(4000L);
				LogHelper.info("Thread resuming");
			}
			catch (InterruptedException e)
			{
				checkFaild = true;
				e.printStackTrace();
			}
		}
	}

}
