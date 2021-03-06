package io.github.pleyte.gmis.visualisation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.pdf.PDFGraphics2D;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import io.github.pleyte.gmis.NetworkAnalysis;
import io.github.pleyte.gmis.intermediate.NetworkLoader;
import io.github.pleyte.gmis.result.ResultsLoader;

public class GeneNetworkVisualisation {
	private static Logger log;

	private static final Color COLOUR_ORANGISH = new Color(237, 135, 21);
	private static final Color COLOUR_BLOOD = new Color(126, 0, 0);
	private static final Color COLOUR_AQUA = new Color(97, 153, 176);
	private static final Color[] PALETTE_TWO = { Color.lightGray, COLOUR_ORANGISH, COLOUR_BLOOD, COLOUR_AQUA };

	static {
		InputStream stream = GeneNetworkVisualisation.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(GeneNetworkVisualisation.class.getName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String FILE_NETWORK_SCORED_AND_LINKED_GENES = "gene_network.sif";
	public static final String FILE_NETWORK_CLUSTERED_AND_LINKED_GENES = "clustered_gene_network.sif";

	public static void main(String[] args) throws IOException {
		GeneNetworkVisualisation networkVisualisation = new GeneNetworkVisualisation();

		ResultsLoader results = new ResultsLoader();

		// Show the big graph with all 1199 scored and linker genes
		//		networkVisualisation.allVertices();

		// Show the graph made up of only the clusters of size > 1, and their linker
		// genes.
		Graph<String, String> graph = networkVisualisation.loadClusteredAndLinkedGraph();

		networkVisualisation.clusteredGeneNetwork(results);

		NetworkAnalysis na = new NetworkAnalysis();
		na.show(graph);
	}

	/**
	 * Load the graph that we started with that contains the 1199 scored and linker
	 * genes.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Graph<String, String> loadLargeScoredAndLinkedGraph() throws IOException {
		URL geneNetworkSifFile = GeneNetworkVisualisation.class.getClassLoader().getResource(FILE_NETWORK_SCORED_AND_LINKED_GENES);
		Graph<String, String> graph = NetworkLoader.loadSifGraph(geneNetworkSifFile.getFile());
		return graph;
	}

	/**
	 * The the graph which only has clustered genes and the linker genes neccessary to form a single component.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Graph<String, String> loadClusteredAndLinkedGraph() throws IOException {
		URL geneNetworkSifFile = GeneNetworkVisualisation.class.getClassLoader().getResource(FILE_NETWORK_CLUSTERED_AND_LINKED_GENES);
		Graph<String, String> graph = NetworkLoader.loadSifGraph(geneNetworkSifFile.getFile());
		return graph;
	}

	/**
	 * 
	 * @param results
	 * @throws IOException
	 */
	private void clusteredGeneNetwork(ResultsLoader results) throws IOException {
		Graph<String, String> graph = loadClusteredAndLinkedGraph();
		Layout<String, String> layout = new KKLayout<>(graph);
		((KKLayout) layout).setAdjustForGravity(true);
		((KKLayout) layout).setDisconnectedDistanceMultiplier(80);
		((KKLayout) layout).setExchangeVertices(true);
		((KKLayout) layout).setLengthFactor(.9);

		VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(1280, 1000));

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
		vv.getRenderContext().setVertexFillPaintTransformer(getVertexTransformer(results));
		render(vv);
	}


	private Transformer<String, Paint> getVertexTransformer(ResultsLoader results) {
		return new Transformer<String, Paint>() {

			@Override
			public Paint transform(String gene) {
				try {
					boolean isNetwork1 = results.getNetworkScores1().getClusters().stream().flatMap(Set::stream).anyMatch(gene::equals);
					boolean isNetwork2 = results.getNetworkScores2().getClusters().stream().flatMap(Set::stream).anyMatch(gene::equals);
					boolean isNetwork3 = results.getNetworkScores3().getClusters().stream().flatMap(Set::stream).anyMatch(gene::equals);

					// Which of the three sets overlap?
					//					if(isNetwork1 && isNetwork2 && isNetwork3) {
					//						log.severe("jDebug: All three overlap");
					//					} else if (isNetwork1 && isNetwork2) {
					//						log.severe("jDebug: one and two ");
					//					} else if (isNetwork1 && isNetwork3) {
					//						log.severe("jDebug: one and three");
					//					} else if (isNetwork2 && isNetwork3) {
					//						log.severe("jDebug: two and three");
					//					}
					if (isNetwork1 && isNetwork2 && isNetwork3) {
						// This scenario is not currently expected
						return Color.RED;
					} else if (isNetwork1 && isNetwork3) {
						return new GradientPaint(0, 0, PALETTE_TWO[1], 1, 2, PALETTE_TWO[3], true);
					} else if (isNetwork1 && isNetwork2) {
						return new GradientPaint(0, 0, PALETTE_TWO[1], 1, 2, PALETTE_TWO[2], true);
					} else if (isNetwork2 && isNetwork3) {
						return new GradientPaint(0, 0, PALETTE_TWO[2], 1, 2, PALETTE_TWO[3], true);
					} else if (isNetwork1) {
						return PALETTE_TWO[1];
					} else if (isNetwork2) {
						return PALETTE_TWO[2];
					} else if (isNetwork3) {
						return PALETTE_TWO[3];
					} else {
						// linker gene
						return PALETTE_TWO[0];
					}
				} catch (IOException e) {
					log.severe("Exception occurred during transform: " + e.getMessage());
					throw new RuntimeException("Exception while transforming vertex colour", e);
				}
			}
		};

	}



	/**
	 * Show the big graph with all 1199 scored and linker genes
	 * 
	 * @param graph
	 * @throws IOException
	 */
	private void allVertices() throws IOException {
		Graph<String, String> graph = loadLargeScoredAndLinkedGraph();
		Layout<String, String> layout = new ISOMLayout<>(graph);

		VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(1024, 640));
		vv.setBackground(Color.WHITE);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
		render(vv);
	}

	private void render(VisualizationImageServer<String, String> vv) {
		JFrame jf = new JFrame();
		jf.setBackground(Color.WHITE);

		JPanel container = new JPanel();
		container.setBackground(Color.WHITE);

		JScrollPane scrPane = new JScrollPane(container);
		scrPane.setBackground(Color.WHITE);

		jf.add(scrPane);
		container.add(vv);

		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);

	}

	/**
	 * Save image of graph as file. This isn't working.
	 * 
	 * @param vv
	 * @param fileName
	 * @throws IOException
	 */
	private void save(VisualizationImageServer<String, String> vv, String fileName) throws IOException {

		log.info("Writing graph to file " + fileName);

		VectorGraphics g = new PDFGraphics2D(new File("/tmp/Network.pdf"), vv);
		//		VectorGraphics g = new SVGGraphics2D(new File("/tmp/Network.svg"), vv);
	}

}
