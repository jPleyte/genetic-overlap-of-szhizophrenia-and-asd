package io.github.pleyte.gmis.visualisation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import io.github.pleyte.gmis.intermediate.NetworkLoader;
import io.github.pleyte.gmis.result.ResultsLoader;

public class GeneNetworkVisualisation {
	public static final String FILE_NETWORK_SCORED_AND_LINKED_GENES = "gene_network.sif";
	public static final String FILE_NETWORK_CLUSTERED_AND_LINKED_GENES = "clustered_gene_network.sif";

	public static void main(String[] args) throws IOException {
		GeneNetworkVisualisation networkVisualisation = new GeneNetworkVisualisation();

		ResultsLoader results = new ResultsLoader();

		// Show the big graph with all 1199 scored and linker genes
		//		networkVisualisation.allVertices();

		// Show the graph made up of only the clusters of size > 1, and their linker
		// genes.
		networkVisualisation.clusteredGeneNetwork(results);
	}

	/**
	 * Load the graph that we started with that contains the 1199 scored and linker
	 * genes.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Graph loadLargeScoredAndLinkedGraph() throws IOException {
		URL geneNetworkSifFile = GeneNetworkVisualisation.class.getClassLoader().getResource(FILE_NETWORK_SCORED_AND_LINKED_GENES);
		Graph<String, String> graph = NetworkLoader.loadSifGraph(geneNetworkSifFile.getFile());
		return graph;
	}

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
		// 3 Layout<String, String> layout = new ISOMLayout<>(graph);
		// 2 Layout<String, String> layout = new CircleLayout<>(graph);
		//		1 Layout<String, String> layout = new FRLayout<>(graph);
		// not as good as 1 Layout<String, String> layout = new FRLayout2<>(graph);
		Layout<String, String> layout = new KKLayout<>(graph);
		((KKLayout) layout).setAdjustForGravity(true);
		((KKLayout) layout).setDisconnectedDistanceMultiplier(100);
		((KKLayout) layout).setExchangeVertices(true);
		((KKLayout) layout).setLengthFactor(1.2);

		VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(1280, 800));

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
		vv.getRenderContext().setVertexFillPaintTransformer(getVertexTransformer(results));
		render(vv);
	}

	private Transformer<String, Paint> getVertexTransformer(ResultsLoader results) {
		return new Transformer<String, Paint>() {
			private final Color[] palette = { Color.GRAY, Color.GREEN, Color.BLUE, Color.RED, Color.MAGENTA };

			@Override
			public Paint transform(String gene) {
				try {
					boolean isNetwork1 = results.getNetworkScores1().getClusters().stream().flatMap(Set::stream).anyMatch(gene::equals);
					boolean isNetwork2 = results.getNetworkScores2().getClusters().stream().flatMap(Set::stream).anyMatch(gene::equals);
					boolean isNetwork3 = results.getNetworkScores3().getClusters().stream().flatMap(Set::stream).anyMatch(gene::equals);

					if ((isNetwork1 ? 1 : 0) + (isNetwork2 ? 1 : 0) + (isNetwork3 ? 1 : 0) > 1) {
						return palette[4];
					} else if (isNetwork1) {
						return palette[1];
					} else if (isNetwork2) {
						return palette[2];
					} else if (isNetwork3) {
						return palette[3];
					} else {
						// linker gene
						return palette[0];
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Color.PINK;
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

		VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(1024, 768));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
		render(vv);
	}

	private void render(VisualizationImageServer<String, String> vv) {
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.getContentPane().add(vv);
		jf.pack();
		jf.setVisible(true);
	}
}
