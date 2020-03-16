package io.github.pleyte.gmis.intermediate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class SimpleHotNetExample {

	public static void main(String[] args) {
		SimpleHotNetExample sh = new SimpleHotNetExample();
		Graph<String, String> graph = sh.createGraph();
		sh.showPageRanks(graph);
		sh.visualise(graph);

	}

	private void visualise(Graph<String, String> graph) {
		Layout<String, String> layout = new KKLayout<>(graph);
		((KKLayout) layout).setAdjustForGravity(true);
		((KKLayout) layout).setDisconnectedDistanceMultiplier(40);
		((KKLayout) layout).setExchangeVertices(true);
		((KKLayout) layout).setLengthFactor(.5);

		VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(640, 480));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
		vv.getRenderContext().setVertexFillPaintTransformer(getVertexTransformer());

		JFrame jf = new JFrame();

		JPanel container = new JPanel();

		JScrollPane scrPane = new JScrollPane(container);

		jf.add(scrPane);
		container.add(vv);

		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}

	private Transformer<String, Paint> getVertexTransformer() {
		return new Transformer<String, Paint>() {

			@Override
			public Paint transform(String gene) {
				return new Color(155, 193, 224);
			}
		};
	}

	private void showPageRanks(Graph<String, String> graph) {
		PageRank<String, String> pageRank = new PageRank<>(graph, 0.1);
		pageRank.evaluate();
		List<Double> pageRankVertexScore = new ArrayList<>();
		for (String node : graph.getVertices()) {
			System.out.println(node + " with " + graph.getNeighborCount(node) + " neighbours has page rank " + pageRank.getVertexScore(node));
			if (graph.getNeighborCount(node) > 0) {
				pageRankVertexScore.add(pageRank.getVertexScore(node));
			}
		}

		System.out.println("Average page rank=" + pageRankVertexScore.stream().mapToDouble(x -> x.doubleValue()).average().getAsDouble());
	}

	private Graph<String, String> createGraph() {
		Graph<String, String> graph = new UndirectedSparseGraph<>();
		int edgeNumber = 1;
		graph.addEdge(String.valueOf(edgeNumber++), "a", "b");
		graph.addEdge(String.valueOf(edgeNumber++), "a", "c");
		graph.addEdge(String.valueOf(edgeNumber++), "b", "d");
		graph.addEdge(String.valueOf(edgeNumber++), "c", "f");
		graph.addEdge(String.valueOf(edgeNumber++), "c", "e");
		graph.addEdge(String.valueOf(edgeNumber++), "d", "e");
		graph.addEdge(String.valueOf(edgeNumber++), "e", "f");
		graph.addEdge(String.valueOf(edgeNumber++), "e", "g");
		graph.addEdge(String.valueOf(edgeNumber++), "d", "g");
		return graph;
	}

}
