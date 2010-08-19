package net.sourceforge.mfl;

import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import net.sourceforge.mfl.exceptions.FileSecurityException;
import net.sourceforge.mfl.filebrowser.FileBrowser;
import net.sourceforge.mfl.log.ConsoleAppender;
import net.sourceforge.mfl.log.FileAppender;
import net.sourceforge.mfl.log.LogSystem;
import net.sourceforge.mfl.log.LoggerConf;
import net.sourceforge.mfl.youtube.Format;
import net.sourceforge.mfl.youtube.SearchResult;
import net.sourceforge.mfl.youtube.VideoPage;
import net.sourceforge.mfl.youtube.YoutubeBrowser;


public class MFL extends MIDlet implements CommandListener
{

	private static final Command CMD_SEARCH = new Command("Search", Command.BACK, 1);

	private static final Command CMD_SELECT_FORMAT = new Command("Ok", Command.OK, 1);

	private static final Command CMD_SELECT_RESULT = new Command("Save file", Command.OK, 1);

	private static final Command CMD_VIEW_IN_BROWSER = new Command("View in browser", Command.ITEM, 3);

	private static final Command CMD_OPEN = new Command("Open", Command.ITEM, 3);

	private static final Command CMD_BACK_TO_SEARCH = new Command("Back", Command.CANCEL, 1);

	private static final Command CMD_NEW_SEARCH = new Command("New search", Command.OK, 1);

	private static final Command CMD_CANCEL_DOWNLOAD = new Command("Cancel download", Command.CANCEL, 1);

	private static final Command CMD_BACK_TO_RESULTS = new Command("Back", Command.CANCEL, 1);

	private static final Command CMD_SHOW_DOWNLOAD_TASKS = new Command("Tasks", Command.ITEM, 2);

	private static final Command CMD_SHOW_DIR_PREF = new Command("Prefs", Command.ITEM, 3);

	private static final Command DIR_SELECTED = new Command("Select", Command.OK, 1);

	private TextBox query;

	private List searchResults;

	private Vector results;

	private VideoPage selectedVideoPage;

	private YoutubeBrowser yb;

	private boolean initialized = false;

	private List formatList;

	private List downloadTasksList;

	private BaseThread listImageLoader;

	private Format preselectedFormat = Format.FLV_FLASH_LITE;

	private Displayable loadingImage = new LoadingScreen();

	private FileBrowser fileBrowser = new FileBrowser(this, "Download directory", this, DIR_SELECTED);

	private String targetDirectory = null;

	private Command followUpCommand;

	private Vector downloadTasks = new Vector();

	public MFL()
	{

		yb = new YoutubeBrowser(this);

		query = new TextBox("Search Youtube", "", 128, 0);
		query.addCommand(CMD_SEARCH);
		query.addCommand(CMD_SHOW_DOWNLOAD_TASKS);
		query.addCommand(CMD_SHOW_DIR_PREF);
		query.setCommandListener(this);

		searchResults = new List("Searchresults", List.TEXT_WRAP_ON);
		searchResults.addCommand(CMD_BACK_TO_SEARCH);
		searchResults.addCommand(CMD_SELECT_RESULT);
		searchResults.addCommand(CMD_VIEW_IN_BROWSER);
		searchResults.setSelectCommand(CMD_SELECT_RESULT);
		searchResults.setCommandListener(this);

		formatList = new List("Choose format...", List.TEXT_WRAP_ON);
		formatList.addCommand(CMD_SELECT_FORMAT);
		formatList.addCommand(CMD_BACK_TO_RESULTS);
		formatList.setCommandListener(this);

		downloadTasksList = new List("Download tasks", List.TEXT_WRAP_ON);
		downloadTasksList.addCommand(CMD_NEW_SEARCH);
		downloadTasksList.addCommand(CMD_CANCEL_DOWNLOAD);
		downloadTasksList.addCommand(CMD_OPEN);
		downloadTasksList.setCommandListener(this);

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException
	{
		LogSystem.closeAppenders();
	}

	protected void pauseApp()
	{
	}

	private void loadPreferences() throws RecordStoreException
	{
		Logs.MFL.info(this.getClass(), "Load preferences.");
		RecordStore recordStore = RecordStore.openRecordStore("mfl", true);
		targetDirectory = new String(recordStore.getRecord(1));
	}

	private void savePreferences()
	{
		Logs.MFL.info(this.getClass(), "Save preferences.");

		try
		{
			RecordStore recordStore = RecordStore.openRecordStore("mfl", true);
			byte[] targetDir = targetDirectory.getBytes();
			if (recordStore.getNextRecordID() == 1)
			{
				recordStore.addRecord(targetDir, 0, targetDir.length);
			}
			else
			{
				recordStore.setRecord(1, targetDir, 0, targetDir.length);
			}

		}
		catch (RecordStoreFullException e)
		{
			Logs.MFL.error(getClass(), "An error occured while saving record store.", e);
		}
		catch (RecordStoreException e)
		{
			Logs.MFL.error(getClass(), "An error occured while saving record store.", e);
		}
		catch (RuntimeException e)
		{
			Logs.MFL.error(getClass(), "An error occured while saving record store.", e);
		}
	}

	protected void startApp() throws MIDletStateChangeException
	{
		System.out.println("Test");
		if (!initialized)
		{
			Logs.MFL.info(this.getClass(), "Application start.");

			boolean firstStart = false;
			try
			{
				loadPreferences();
				firstStart = targetDirectory == null;
			}
			catch (RecordStoreException e)
			{
				Logs.MFL.debug(getClass(), "RecordstoreException ignored - interpret as first start.");
				firstStart = true;
			}

			initialized = true;

			if (!firstStart)
			{
				Display.getDisplay(this).setCurrent(query);
			}
			else
			{
				Alert firstStartMessage =
					new Alert("Welcome to MFL",
						"Seems like this is the first start of MFL. Please select a download directory.", null,
						AlertType.INFO);
				firstStartMessage.setTimeout(Alert.FOREVER);

				fileBrowser.init(null);
				Display.getDisplay(this).setCurrent(firstStartMessage, fileBrowser);
			}
		}
	}

	public void commandAction(Command command, Displayable disp)
	{
		final MFL thisMFL = this;

		if (listImageLoader != null)
		{
			listImageLoader.interrupt();
		}

		if (command == CMD_BACK_TO_SEARCH || command == CMD_NEW_SEARCH)
		{
			Display.getDisplay(this).setCurrent(query);
		}
		else if (command == CMD_BACK_TO_RESULTS)
		{
			Display.getDisplay(this).setCurrent(searchResults);
		}
		else if (command == CMD_SHOW_DIR_PREF)
		{
			followUpCommand = CMD_BACK_TO_SEARCH;
			fileBrowser.init(targetDirectory);
			Display.getDisplay(this).setCurrent(fileBrowser);
		}
		else if (command == DIR_SELECTED)
		{
			targetDirectory = fileBrowser.getSelectedDirectory();
			Logs.MFL.debug(getClass(), "Directory selected:" + targetDirectory);

			savePreferences();

			commandAction(followUpCommand != null ? followUpCommand : CMD_BACK_TO_SEARCH, disp);
		}
		else if (command == CMD_VIEW_IN_BROWSER)
		{
			SearchResult result = (SearchResult) results.elementAt(searchResults.getSelectedIndex());
			String url = yb.getVideoPageUrl(result.getId());
			try
			{
				this.platformRequest(url);
			}
			catch (ConnectionNotFoundException e)
			{
				Logs.MFL.error(this.getClass(), "An error occured during platform request.", e);
			}
		}
		else if (command == CMD_OPEN)
		{
			int taskToOpen = downloadTasksList.getSelectedIndex();
			if (taskToOpen >= 0)
			{
				DownloadTask task = (DownloadTask) downloadTasks.elementAt(taskToOpen);
				String filename = task.getTargetFilename();
				try
				{
					Logs.MFL.info(this.getClass(), "Trying to open " + filename);
					platformRequest("file:/" + filename);
				}
				catch (ConnectionNotFoundException e)
				{
					Logs.MFL.error(this.getClass(), "An error occured while opening " + filename, e);
				}
			}
		}
		else if (command == CMD_SELECT_RESULT)
		{
			Display.getDisplay(this).setCurrent(loadingImage);

			new BaseThread()
			{
				public void runSafe()
				{

					try
					{
						SearchResult result = (SearchResult) results.elementAt(searchResults.getSelectedIndex());
						selectedVideoPage = yb.getVideoPage(result.getId(), null);

						formatList.deleteAll();

						Format[] formats = selectedVideoPage.getAvailableFormats();

						int preselectedIndex = 0;
						for (int i = 0; i < formats.length; i++)
						{
							formatList.append(formats[i].getDescription(), null);

							if (formats[i] == preselectedFormat)
							{
								preselectedIndex = i;
							}
						}

						if (formatList.size() > 0)
						{
							formatList.setSelectedIndex(preselectedIndex, true);
						}

						Display.getDisplay(thisMFL).setCurrent(formatList);
					}
					catch (Exception e)
					{
						Logs.MFL.error(this.getClass(), "An error occured while loading youtube formats.", e);
					}
				}
			}.start();
		}
		else if (command == CMD_SELECT_FORMAT)
		{
			final SearchResult result = (SearchResult) results.elementAt(searchResults.getSelectedIndex());

			final DownloadTask task = new DownloadTask(downloadTasksList, result);

			// choose selected format
			final Format format = (Format) selectedVideoPage.getAvailableFormats()[formatList.getSelectedIndex()];
			final String filename = targetDirectory + createFilename(result.getTitle()) + "." + format.getExtension();
			task.setTargetFilename(filename);
			downloadTasks.addElement(task);
			Display.getDisplay(thisMFL).setCurrent(downloadTasksList);

			Thread downloadThread = new BaseThread()
			{
				public void runSafe()
				{
					try
					{

						try
						{
							yb.saveVideo(filename, selectedVideoPage.getId(), selectedVideoPage.getSignature(), format, task);
						}
						catch (FileSecurityException e)
						{
							task.setError(true);
							Logs.MFL.error(this.getClass(),
								"A security exception occured while trying to open or write the file '" + filename + "'", e);

							Logs.MFL.debug(this.getClass(), "Build alert");
							Alert a =
								new Alert("Security error", "There was a security error while trying to save the video to "
									+ targetDirectory + ". Please select another target directory and try again.", null,
									AlertType.ERROR);
							a.setTimeout(Alert.FOREVER);

							followUpCommand = CMD_SELECT_FORMAT;
							fileBrowser.init(targetDirectory);
							Display.getDisplay(thisMFL).setCurrent(a, fileBrowser);
						}
					}
					catch (Exception e)
					{
						task.setError(true);
						task.updateList();
						Logs.MFL.error(this.getClass(), "An error occured while downloading youtube video.", e);
					}

				}

			};
			downloadThread.start();
			task.setDownloadThread(downloadThread);
		}
		else if (command == CMD_SEARCH && query.getString().equals("logmfl"))
		{
			LogSystem.addLoggerConf(new LoggerConf(new FileAppender().init()));
		}
		else if (command == CMD_SEARCH && query.getString().equals("logcon"))
		{
			LogSystem.addLoggerConf(new LoggerConf(new ConsoleAppender().init()));
		}
		else if (command == CMD_SEARCH)
		{
			Display.getDisplay(thisMFL).setCurrent(loadingImage);

			new BaseThread()
			{
				public void runSafe()
				{
					try
					{
						searchResults.deleteAll();
						results = yb.getSearchResults(query.getString(), null);

						String[] imageUrls = new String[results.size()];
						for (int i = 0; i < results.size(); i++)
						{
							SearchResult result = (SearchResult) results.elementAt(i);
							searchResults.append(result.getTitle() + " (" + result.getDuration() + ")", null);
							imageUrls[i] = result.getImageUrl();
						}

						Display.getDisplay(thisMFL).setCurrent(searchResults);

						listImageLoader = new AsynchronousListImageLoader(searchResults, imageUrls);
						listImageLoader.start();
					}
					catch (Exception e)
					{
						Logs.MFL.error(this.getClass(), "An error occured while loading youtube search results.", e);
					}
				}
			}.start();
		}
		else if (command == CMD_SHOW_DOWNLOAD_TASKS)
		{
			Display.getDisplay(thisMFL).setCurrent(downloadTasksList);
		}
		else if (command == CMD_CANCEL_DOWNLOAD)
		{
			int taskToCancel = downloadTasksList.getSelectedIndex();
			Logs.MFL.info(getClass(), "Cancelling task with index " + taskToCancel);
			if (taskToCancel >= 0)
			{
				DownloadTask task = (DownloadTask) downloadTasks.elementAt(taskToCancel);
				task.getDownloadThread().interrupt();
			}
			Display.getDisplay(thisMFL).setCurrent(downloadTasksList);
		}
	}

	private String createFilename(String title)
	{
		StringBuffer filename = new StringBuffer();
		for (int i = 0; i < title.length(); i++)
		{
			char c = title.charAt(i);
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			{
				filename.append(c);
			}
			else if (c == ' ')
			{
				filename.append('_');
			}
		}
		return filename.toString();
	}

}
