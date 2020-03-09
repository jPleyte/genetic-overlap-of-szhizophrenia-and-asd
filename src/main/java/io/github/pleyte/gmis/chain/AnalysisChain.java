package io.github.pleyte.gmis.chain;

import org.apache.commons.chain.impl.ChainBase;

public class AnalysisChain extends ChainBase {
	public AnalysisChain() {
		super();
		addCommand(new ParameterCheck());
	}
}
