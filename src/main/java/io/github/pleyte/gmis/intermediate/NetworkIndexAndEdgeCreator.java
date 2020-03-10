package io.github.pleyte.gmis.intermediate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import edu.uci.ics.jung.graph.Graph;
import io.github.pleyte.gmis.NetworkAnalysis;
import io.github.pleyte.gmis.PerformAnalysis;

/**
 * This class generates the HotNet network index and edge list tab delimited
 * files
 * 
 * @author pleyte
 *
 */
public class NetworkIndexAndEdgeCreator {
	private static Logger log;

	static {
		InputStream stream = PerformAnalysis.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(PerformAnalysis.class.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		NetworkIndexAndEdgeCreator networkIndexAndEdgeCreator = new NetworkIndexAndEdgeCreator();

		URL geneNetworkSifFile = NetworkAnalysis.class.getClassLoader().getResource("gene_network.sif");

		Graph<String, String> graph = NetworkLoader.loadSifGraph(geneNetworkSifFile.getFile());
		NetworkLoader.removeLoneVertices(graph);

		Map<String, Integer> geneIndexMap = networkIndexAndEdgeCreator.saveGeneIndex(graph, "network_1_index_gene.tsv");
		networkIndexAndEdgeCreator.saveEdgeList(graph, geneIndexMap, "network_1_edge_list.tsv");
	}

	/**
	 * Create the network gene index. Each line contains a number and a gene name
	 * separated by a tab.
	 * 
	 * @param graph
	 * @param string
	 * @throws IOException
	 */
	private Map<String, Integer> saveGeneIndex(Graph<String, String> graph, String geneIndexFileName) throws IOException {
		Map<String, Integer> geneIndexMap = new HashMap<>();
		int index = 1;
		try (Writer out = new FileWriter(geneIndexFileName)) {
			CSVPrinter printer = CSVFormat.TDF.print(out);
			for (String vertex : graph.getVertices()) {
				geneIndexMap.put(vertex, index);
				printer.printRecord(index, vertex);
				++index;
			}
		}

		log.info("Wrote " + geneIndexMap.size() + " genes to index file " + geneIndexFileName);
		return geneIndexMap;
	}

	/**
	 * Create the network edge list. Even if the network is undirected this method
	 * prints vertices in both directions (a to b and b to a) because HotNet will
	 * ignore the redundant information if it is not needed, and it can use the
	 * directionality if we tell it to.
	 * 
	 * @param graph
	 * @param string
	 * @throws IOException
	 */
	private void saveEdgeList(Graph<String, String> graph, Map<String, Integer> geneIndexMap, String geneEdgeFile) throws IOException {
		int countVertexPairs = 0;

		try (Writer out = new FileWriter(geneEdgeFile)) {
			CSVPrinter printer = CSVFormat.TDF.print(out);
			for (String sourceVertex : graph.getVertices()) {
				for (String destinationVertex : graph.getNeighbors(sourceVertex)) {
					if (destinationVertex.equals(sourceVertex)) {
						log.severe("Ignoring self loop of " + destinationVertex);
						continue;
					}
					printer.printRecord(geneIndexMap.get(sourceVertex), geneIndexMap.get(destinationVertex));
					++countVertexPairs;
				}
			}
		}

		log.info("Wrote " + countVertexPairs + " edges to " + geneEdgeFile);
	}


}
