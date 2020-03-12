package io.github.pleyte.gmis.intermediate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.transformation.DirectionTransformer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLReader;
import io.github.pleyte.gmis.NetworkAnalysis;
import io.github.pleyte.gmis.PerformAnalysis;

public class NetworkLoader {

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
	 * Load an unidirected graph from tab delimited SIF file
	 * 
	 * @param sifFileName
	 * @return
	 * @throws Exception
	 */
	public static Graph<String, String> loadSifGraph(String sifFileName) throws IOException {
		Graph<String, String> graph = new UndirectedSparseGraph<>();

		try (Reader in = new FileReader(sifFileName)) {
			Iterable<CSVRecord> records = CSVFormat.TDF.parse(in);
			int edgeNumber = 0;
			for (CSVRecord record : records) {

				// Individual nodes are placed at the top of the file; skip that section
				//				if (record.size() < 3) {
				//					continue;
				if (record.size() == 1) {
					graph.addVertex(record.get(0));
				} else if (record.size() != 3) {
					throw new IOException("SIF record number " + record.getRecordNumber() + " has an unexpected number of fields: " + record.size());
				} else if (!"-".equals(record.get(1)) && !" ".equals(record.get(1)) && !"".equals(record.get(1))) {
					// It seems the middle value can be "", " ", or "-".
					throw new IOException("SIF record number " + record.getRecordNumber() + " should have a dash in the middle instead of: " + record.get(1));
				} else {
					graph.addEdge(String.valueOf(edgeNumber), record.get(0), record.get(2));
				}

				++edgeNumber;
			}
		}

		return graph;
	}

	/**
	 * Load a graph from a GraphML file
	 * 
	 * @param graphMlFileName
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Graph<String, String> loadGraphMl(String graphMlFileName) throws ParserConfigurationException, SAXException, IOException {
		GraphMLReader<Graph<String, String>, String, String> gmlr = new GraphMLReader<>(null, new EdgeFactory());

		try (Reader reader = new BufferedReader(new InputStreamReader(NetworkAnalysis.class.getClassLoader().getResourceAsStream(graphMlFileName)))) {
			// The graphml file defines a directed graph
			Graph<String, String> graph = new DirectedSparseGraph<>();
			gmlr.load(reader, graph);

			// convert to undirected and return
			return DirectionTransformer.toUndirected(graph, new GraphFactory(), new EdgeFactory(), true);
		}
	}

	/**
	 * static class that defines edge names because Cytsocape doesn't provide edge
	 * ids
	 * 
	 * @author pleyte
	 *
	 */
	static class EdgeFactory implements Factory<String> {
		int i = 0;

		@Override
		public String create() {
			return String.valueOf(i++);
		}
	}

	/**
	 * 
	 * @author pleyte
	 *
	 */
	static class GraphFactory implements Factory<UndirectedGraph<String, String>> {

		@Override
		public UndirectedGraph<String, String> create() {
			return new UndirectedSparseGraph<>();
		}

	}

	public static Graph<String, String> removeLoneVertices(Graph<String, String> graph) throws Exception {
		for(String node: graph.getVertices()) {
			if(graph.getVertexCount()==0) {
				graph.removeVertex(node);
				log.info("Removing solitary node" + node);
			} else if (graph.getVertexCount() == 1 && graph.getVertices().toArray()[0].equals(node)) {
				throw new Exception("Self edges are not expected");
			}
		}

		return graph;
	}

}
