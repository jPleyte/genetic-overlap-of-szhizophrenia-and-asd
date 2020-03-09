package io.github.pleyte.gmis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.pleyte.gmis.chain.AnalysisChain;
import io.github.pleyte.gmis.chain.AnalysisContext;

/**
 * This class contains the main method which launches the processing pipeline.
 * 
 * @author pleyte
 *
 */
public class PerformAnalysis {

	private static Logger log;
	private Context context;

	static {
		InputStream stream = PerformAnalysis.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(PerformAnalysis.class.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		log.fine("main");
		PerformAnalysis analysis = new PerformAnalysis();
		analysis.parseCommandLineParameters(args);
		analysis.execute();
	}

	private void execute() throws Exception {
		Command analysisChain = new AnalysisChain();
		analysisChain.execute(context);
	}


	/**
	 * Parse and pre-process command line parameters
	 * 
	 * @param args
	 */
	private void parseCommandLineParameters(String[] args) {
		Options options = new Options();
		CommandLine cmd = null;
		try {
			options.addOption(Option.builder("h")
					.argName("hierarchical-hotnet-dir")
					.desc("Hierarchichal HotNet installation directory")
					.hasArg()
					.longOpt("hotnet")
					.required()
					.build());

			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(PerformAnalysis.class.getName(), null, options, "Foot");
			System.exit(-1);
		}

		context = new AnalysisContext();
		context.put("hotnet", new File(cmd.getOptionValue("hotnet")));
	}

}
