package com.brandon3055.draconicevolution.common.utills;

import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

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

			JsonReader reader;
			try
			{

				URL versionURL = new URL("https://raw.githubusercontent.com/brandon3055/Draconic-Evolution/master/VERSION.json");

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((versionURL).openStream()));

				String cl;

				while ((cl = bufferedReader.readLine()) != null) {
					LogHelper.info(cl);
				}


//				File versionFile = new File("");
//
//				InputStream is = versionURL.openStream();
//				OutputStream os = new FileOutputStream(versionFile);
//				ByteStreams.copy(is, os);
//
//				reader = new JsonReader(new FileReader(versionFile));
//
//				reader.beginObject();
//				LogHelper.info(reader.nextString());
//
//				while (reader.hasNext())
//				{
//					String name = reader.nextName();
//
//					if (name.equals("Version"))
//					{
//						LogHelper.info(reader.nextString());
//					}
//					else if (name.equals("Snapshot"))
//					{
//						LogHelper.info(reader.nextInt());
//					}
//					else if (name.equals("MCVersion"))
//					{
//						LogHelper.info(reader.nextString());
//					}
//					else if (name.equals("ReleaseNote"))
//					{
//						LogHelper.info(reader.nextString());
//					}
//				}
//
//				reader.endObject();
//				reader.close();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
