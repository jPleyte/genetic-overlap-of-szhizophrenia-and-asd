package io.github.pleyte.gmis.intermediate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;

public class NetworkAnalysis {

	private static Logger log;

	static {
		InputStream stream = NetworkAnalysis.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(NetworkAnalysis.class.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	/**
	 * 
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		NetworkAnalysis analysis = new NetworkAnalysis();

		URL geneNetworkSifFile = NetworkAnalysis.class.getClassLoader().getResource("gene_network.sif");
		Graph<String, String> graph = NetworkLoader.loadSifGraph(geneNetworkSifFile.getFile());
		analysis.show(graph);
	}

	/**
	 * 
	 * @param graph
	 */
	private void show(Graph<String, String> graph) {
		log.info("Undirected graph has " + graph.getVertexCount() + " vertices and " + graph.getEdgeCount() + " edges");
		log.info("Average page rank score is " + getAveragePageRank(graph));
	}

	/**
	 * Return the average pageRank score for the large component(s)
	 * 
	 * @param graph
	 * @return
	 */
	private Double getAveragePageRank(Graph<String, String> graph) {
		PageRank<String, String> pageRank = new PageRank<>(graph,0.1);
		pageRank.evaluate();
		List<Double> pageRankVertexScore = new ArrayList<>();
		for(String node: graph.getVertices()) {
			log.fine(node + " with " + graph.getNeighborCount(node) + " neighbours has page rank " + pageRank.getVertexScore(node));
			if (graph.getNeighborCount(node) > 0) {
				pageRankVertexScore.add(pageRank.getVertexScore(node));
			}
		}

		return pageRankVertexScore.stream().mapToDouble(x -> x.doubleValue()).average().getAsDouble();
	}

}
