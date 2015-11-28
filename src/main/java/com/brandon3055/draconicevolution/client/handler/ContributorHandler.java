package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.brandonscore.common.handlers.FileHandler;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 5/11/2015.
 */
public class ContributorHandler {

	public static List<Contributor> contributors = new ArrayList<Contributor>();
	public static boolean successfulLoad = false;
	private static DLThread thread;

	public static void init(){

		thread = new DLThread();
		thread.start();

		FMLCommonHandler.instance().bus().register(new ContributorHandler());
	}

	private static void readFile(){
		File cFile = new File(FileHandler.configFolder, "/draconicevolution/contributors.json");

		if (!cFile.exists()){
			LogHelper.error("Could not find contributors file");
			return;
		}

		try
		{
			JsonReader reader = new JsonReader(new FileReader(cFile));
			reader.setLenient(true);

			reader.beginArray();

			while (reader.hasNext())
			{
				reader.beginObject();

				Contributor contributor = new Contributor();

				while (reader.hasNext())
				{
					String name = reader.nextName();

					if (name.equals("name")) contributor.name = reader.nextString();
					else if (name.equals("ign")) contributor.ign = reader.nextString();
					else if (name.equals("contribution")) contributor.contribution = reader.nextString();
					else if (name.equals("details")) contributor.details = reader.nextString();
					else if (name.equals("website")) contributor.website = reader.nextString();
					else if (name.equals("contributionLevel")) contributor.contributionLevel = reader.nextInt();
				}

				contributors.add(contributor);

				reader.endObject();
			}

			reader.endArray();

			reader.close();
			cFile.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event){
		if (thread == null) {
			FMLCommonHandler.instance().bus().unregister(this);
			return;
		}

		if (thread.isFinished()){
			thread = null;
			readFile();
			successfulLoad = true;
		}
		else if (thread.isFailed()){
			thread = null;
		}
	}

	public static class Contributor {
		public String name;
		public String ign;
		public String contribution;
		public String details;
		public String website;
		public int contributionLevel;

		public Contributor() {}

		@Override
		public String toString() {
			return "[Contributor: "+name+", Contribution: "+contribution+", Details: "+details+", Website: "+website+"]";
		}
	}

	public static class DLThread extends Thread {

		private boolean finished = false;
		private boolean failed = false;

		public DLThread(){
			super("DE Contributors DL Thread");
		}

		@Override
		public void run() {
			super.run();

			try
			{
				URL url = new URL("http://www.brandon3055.com/json/DEContributors.json");
				File cFile = new File(FileHandler.configFolder, "/draconicevolution/contributors.json");

				InputStream is = url.openStream();
				OutputStream os = new FileOutputStream(cFile);

				IOUtils.copy(is, os);

				is.close();
				os.close();
				finished = true;
			}
			catch (Exception e)
			{
				failed = true;
				e.printStackTrace();
			}

		}

		public boolean isFinished() {
			return finished;
		}

		public boolean isFailed() {
			return failed;
		}
	}
}
