package io.github.pleyte.gmis.chain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ParameterCheck implements Command {
	private static Logger log = Logger.getLogger(ParameterCheck.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		log.fine(ParameterCheck.class.getSimpleName() + ".execute");

		verifyHotNetInstallation(context);

		verifyPythonEnvironment(context);

		return CONTINUE_PROCESSING;
	}

	/**
	 * Make sure python is installed
	 * 
	 * @param context
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void verifyPythonEnvironment(Context context) throws IOException, InterruptedException {
		URL testScript = ParameterCheck.class.getClassLoader().getResource("test.sh");
		if (testScript == null) {
			log.severe("Unable to locate test script");
		}

		log.fine("Found resource: " + testScript.getFile());

		List<String> testCommand = new ArrayList<>();
		testCommand.add("bash");
		testCommand.add(testScript.getFile());

		ProcessBuilder build = new ProcessBuilder();
		build.directory(new File("/tmp"));
		build.command(testCommand);
		Process process = build.start();

		try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				log.info(line);
			}
		}

//		process.waitFor(); 

	}

	/**
	 * Make sure Hierarchichal HotNet is installed
	 * 
	 * @param context
	 * @throws Exception
	 */
	private void verifyHotNetInstallation(Context context) throws Exception {

		File hotnetDirectory = (File) context.get("hotnet");
		if (!hotnetDirectory.isDirectory()) {
			log.severe("HotNet directory does not exist: " + hotnetDirectory);
			throw new Exception("HotNet directory does not exist: " + hotnetDirectory);
		} else {
			log.fine("Found HotNet directory: " + hotnetDirectory);
		}
	}

}
