package io.github.pleyte.gmis.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkScore {

	private List<Set<String>> clusters = new ArrayList<>();

	public void addCluster(String[] genes) {
		Set<String> cluster = new HashSet<>();
		for (String gene : genes) {
			cluster.add(gene);
		}

		clusters.add(cluster);
	}

	public List<Set<String>> getClusters() {
		return clusters;
	}

}
