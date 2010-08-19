package net.sourceforge.mfl.filebrowser;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import net.sourceforge.mfl.BaseThread;
import net.sourceforge.mfl.Logs;

public class FileBrowser extends List implements CommandListener
{
	private String directoryName = "mfl/";

	private CommandListener parentCommandListener;

	private Command parentSelectedCommand;

	private MIDlet parent;

	private boolean initialized = false;

	private String selectedDirectory = null;

	public FileBrowser(MIDlet parent, String title, CommandListener listener,
			Command selectedCommand)
	{
		super(title, List.TEXT_WRAP_ON);
		this.parent = parent;

		addCommand(selectedCommand);
		setSelectCommand(selectedCommand);
		setCommandListener(this);
		parentCommandListener = listener;
		parentSelectedCommand = selectedCommand;
	}

	public String getSelectedDirectory()
	{
		return selectedDirectory;
	}

	public void init(String directory)
	{
		if (!initialized)
		{
			initialized = true;
			Enumeration roots = FileSystemRegistry.listRoots();
			while (roots.hasMoreElements())
			{
				String root = (String) roots.nextElement();
				append(root + directoryName, null);
			}

			for (int i = 0; i < size(); i++)
			{
				if (getString(i).equals(directory))
				{
					setSelectedIndex(i, true);
					selectedDirectory = getString(getSelectedIndex());
					return;
				}
			}
		}
	}

	public void commandAction(final Command command,
			final Displayable displayable)
	{
		Logs.MFL.debug(getClass(), "Filebrowser command: " + command);

		if (command == parentSelectedCommand)
		{

			final String candidate = getString(getSelectedIndex());

			new BaseThread()
			{
				public void runSafe()
				{
					boolean error = false;

					if (!candidate.equals(selectedDirectory))
					{
						FileConnection fileConnection = null;
						// create directory if it does not exists
						try
						{
							Logs.MFL.debug(getClass(), "Trying to open "
									+ candidate);
							fileConnection = (FileConnection) Connector
									.open("file:///" + candidate);
							Logs.MFL.debug(getClass(), candidate + " opened.");
						} catch (IOException e)
						{
							error = true;
							showMessage("Error while opening directory '"
									+ candidate + "': " + e.getMessage());
						} catch (SecurityException e)
						{
							error = true;
							showMessage("Access denied to '" + candidate
									+ "'. Please select another directory.");
						}

						if (fileConnection != null)
						{
							try
							{
								if (!fileConnection.exists())
								{
									fileConnection.mkdir();
								}
							} catch (IOException e)
							{
								error = true;
								showMessage("Error while creating directory '"
										+ candidate + "': " + e.getMessage());
							} catch (SecurityException e)
							{
								error = true;
								showMessage("Access denied to '" + candidate
										+ "'. Please select another directory.");
							}
						}
					}

					if (!error)
					{
						selectedDirectory = candidate;
						Logs.MFL.debug(getClass(),
								"Filebrowser finished. Selected directory:"
										+ selectedDirectory);
						parentCommandListener.commandAction(command,
								displayable);
					}
					else
					{
						Logs.MFL.debug(getClass(),
								"Filebrowser finished with error.");
					}
				}
			}.start();
		}
	}

	private void showMessage(String message)
	{
		Alert alert = new Alert("Error", message, null, AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		Display.getDisplay(parent).setCurrent(alert);
	}
}
