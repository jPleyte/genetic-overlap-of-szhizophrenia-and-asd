package io.github.pleyte.gmis.result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.github.pleyte.gmis.NetworkAnalysis;
import io.github.pleyte.gmis.bean.NetworkScore;

/**
 * This class handles loading the results output files from HotNet
 * 
 * @author pleyte
 *
 */
public class ResultsLoader {
	private static Logger log;

	static {
		InputStream stream = ResultsLoader.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(ResultsLoader.class.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String FILE_CLUSTERS_SCORES_1 = "hotnet_network/results/clusters_network_1_scores_1.tsv";
	public static final String FILE_CLUSTERS_SCORES_2 = "hotnet_network/results/clusters_network_1_scores_2.tsv";
	public static final String FILE_CLUSTERS_SCORES_3 = "hotnet_network/results/clusters_network_1_scores_3.tsv";

	private NetworkScore networkScores1;
	private NetworkScore networkScores2;
	private NetworkScore networkScores3;

	public NetworkScore getNetworkScores1() throws IOException {
		if (networkScores1 == null) {
			networkScores1 = getNetworkScores(FILE_CLUSTERS_SCORES_1);
		}
		return networkScores1;
	}

	public NetworkScore getNetworkScores2() throws IOException {
		if (networkScores2 == null) {
			networkScores2 = getNetworkScores(FILE_CLUSTERS_SCORES_2);
		}
		return networkScores2;
	}

	public NetworkScore getNetworkScores3() throws IOException {
		if (networkScores3 == null) {
			networkScores3 = getNetworkScores(FILE_CLUSTERS_SCORES_3);
		}
		return networkScores3;
	}


	/**
	 * Load one of the HotNet cluster result files. The first few lines begin with a
	 * "#" and have some statistic values. The rest of the file has one cluster per
	 * line with each gene separated by a tab.
	 * 
	 * @param clustersScoresFileName
	 * @return
	 * @throws IOException
	 */
	private NetworkScore getNetworkScores(String clustersScoresFileName) throws IOException {
		log.fine("Loading network scores file " + clustersScoresFileName);
		URL clustersScoresFile = NetworkAnalysis.class.getClassLoader().getResource(clustersScoresFileName);

		if (clustersScoresFile == null) {
			throw new IOException("Unable to locate input file: " + clustersScoresFileName);
		}
		File f = new File(clustersScoresFile.getFile());
		if (!f.exists()) {
			throw new IOException("Unable to locate input file: " + f);
		}
		NetworkScore networkScore = new NetworkScore();
		try (Reader in = new FileReader(clustersScoresFile.getFile()); BufferedReader br = new BufferedReader(in)) {

			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					networkScore.addCluster(line.split("\t"));
				}
			}
		}

		return networkScore;
	}
}
