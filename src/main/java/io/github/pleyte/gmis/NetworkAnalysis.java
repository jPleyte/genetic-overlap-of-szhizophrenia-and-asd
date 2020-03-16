package io.github.pleyte.gmis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import io.github.pleyte.gmis.intermediate.NetworkLoader;

/**
 * This class provides information about a network; including vertex and edge count, and Page Rank 
 * 
 * @author pleyte
 *
 */
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
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void show(Graph<String, String> graph) throws FileNotFoundException, IOException {
		String directedOrUndirected = null;
		if (graph instanceof UndirectedGraph) {
			directedOrUndirected = "Undirected";
		} else {
			directedOrUndirected = "Directed";
		}
		log.info(directedOrUndirected + " graph has " + graph.getVertexCount() + " vertices and " + graph.getEdgeCount() + " edges");
		log.info("Average page rank score is " + getAveragePageRank(graph));

		Set<String> schizophreniaGeneSet1 = loadGeneSetFromScoreFile("hotnet_network/scores_1.tsv");
		Set<String> unlinkedSchizophreniaGeneSet1 = getUnlinkedGenes(graph, schizophreniaGeneSet1);
		log.info(unlinkedSchizophreniaGeneSet1.size() + " of the genes in the first schizophrenia gene set are not in the final network.");
		log.fine("Unlinked sch1=" + quoteAndCommaSeparate(unlinkedSchizophreniaGeneSet1));

		Set<String> schizophreniaGeneSet2 = loadGeneSetFromScoreFile("hotnet_network/scores_2.tsv");
		Set<String> unlinkedSchizophreniaGeneSet2 = getUnlinkedGenes(graph, schizophreniaGeneSet2);
		log.info(unlinkedSchizophreniaGeneSet2.size() + " of the genes in the second schizophrenia gene set are not in the final network.");
		log.fine("Unlinked sch2=" + quoteAndCommaSeparate(unlinkedSchizophreniaGeneSet2));

		Set<String> schizophreniaGeneSet3 = loadGeneSetFromScoreFile("hotnet_network/scores_3.tsv");
		Set<String> unlinkedSchizophreniaGeneSet3 = getUnlinkedGenes(graph, schizophreniaGeneSet3);
		log.info(unlinkedSchizophreniaGeneSet3.size() + " of the genes in the SFARI gene set are not in the final network.");
		log.fine("Unlinked SFARI=" + quoteAndCommaSeparate(unlinkedSchizophreniaGeneSet3));

		log.info("There are " + getLinkerGeneCount(graph, schizophreniaGeneSet1, schizophreniaGeneSet2, schizophreniaGeneSet3).size() + " linker genes in the network which are not in any of the gene sets");
	}

	/**
	 * Convert a list of elements to a quoted comma separated list (ie 'a','b','c')
	 * 
	 * @param unlinkedSchizophreniaGeneSet1
	 * @return
	 */
	private String quoteAndCommaSeparate(Set<String> unlinkedSchizophreniaGeneSet1) {
		return String.join(", ", unlinkedSchizophreniaGeneSet1.stream().map(name -> ("'" + name + "'")).collect(Collectors.toSet()));
	}

	/**
	 * Load the genes from a gene score file
	 * 
	 * @param geneScoreFile
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private Set<String> loadGeneSetFromScoreFile(String geneScoreFile) throws FileNotFoundException, IOException {
		Set<String> geneSet = new HashSet<>();
		try (Reader in = new FileReader(this.getClass().getClassLoader().getResource(geneScoreFile).getFile())) {
			Iterable<CSVRecord> records = CSVFormat.TDF.parse(in);
			for (CSVRecord record : records) {
				assert (record.size() == 2);
				geneSet.add(record.get(0));
			}
		}

		return geneSet;
	}


	/**
	 * Return a list of genes which are in the graph but are not in any of the gene
	 * score files
	 * 
	 * @param graph
	 * @param schizophrenia1Genes
	 * @param schizophrenia2Genes
	 * @param sfariGenes
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SafeVarargs
	private Set<String> getLinkerGeneCount(Graph<String, String> graph, Set<String>... geneSets) throws FileNotFoundException, IOException {
		Set<String> genes = new HashSet<>(graph.getVertices());

		for (Set<String> geneSet : geneSets) {
			genes.removeAll(geneSet);
		}

		return genes;
	}

	/**
	 * Return a list of the genes which are in the scores file but are not in the
	 * graph
	 * 
	 * @param graph
	 * @param hotNetScoreFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Set<String> getUnlinkedGenes(Graph<String, String> graph, Set<String> geneSet) throws FileNotFoundException, IOException {
		Set<String> unlinkedGenes = new HashSet<>();
		for (String gene : geneSet) {
			if (!graph.containsVertex(gene)) {
				unlinkedGenes.add(gene);
			}
		}
		return unlinkedGenes;
	}

	/**
	 * Return the average pageRank score for the component(s), excluding any
	 * unconnected vertices
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
