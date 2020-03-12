package io.github.pleyte.gmis.result;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ResultAnalysis {

	private static Logger log;

	static {
		InputStream stream = ResultsLoader.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(ResultAnalysis.class.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		ResultAnalysis analysis = new ResultAnalysis();

		ResultsLoader results = new ResultsLoader();
		analysis.showClusterInfo(results);

	}

	private void showClusterInfo(ResultsLoader results) throws IOException {
		List<String> allGenesList = new ArrayList<>();
		Set<String> allGenesSet = new HashSet<>();

		List<String> l1 = results.getNetworkScores1().getClusters().stream().flatMap(Set::stream).collect(Collectors.toList());
		List<String> l2 = results.getNetworkScores2().getClusters().stream().flatMap(Set::stream).collect(Collectors.toList());
		List<String> l3 = results.getNetworkScores3().getClusters().stream().flatMap(Set::stream).collect(Collectors.toList());

		allGenesList.addAll(l1);
		allGenesList.addAll(l2);
		allGenesList.addAll(l3);

		allGenesSet.addAll(l1);
		allGenesSet.addAll(l2);
		allGenesSet.addAll(l3);

		// Schizophrenia I
		log.info("Schizophrenia I results have " + results.getNetworkScores1().getClusters().size() + " clusters.");
		log.info("Schizophrenia I results have " + l1.size() + " genes");

		// Schizophrenia II
		log.info("Schizophrenia II results have " + results.getNetworkScores2().getClusters().size() + " clusters.");
		log.info("Schizophrenia II results have " + l2.size() + " genes");

		// Autism
		log.info("Autism results have " + results.getNetworkScores3().getClusters().size() + " clusters.");
		log.info("Autism results have " + l3.size() + " genes");

		log.info("List of all genes has " + allGenesList.size() + " elements");
		log.info("Set of all distinct genes has " + allGenesSet.size() + " elements");
	}

}
