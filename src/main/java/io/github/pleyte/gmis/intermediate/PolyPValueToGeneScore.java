package io.github.pleyte.gmis.intermediate;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import io.github.pleyte.gmis.PerformAnalysis;

/**
 * This class takes the data extracted from Table 3 of "Polygenic Risk Score,
 * Genome-wide Association, and Gene Set Analyses of Cognitive Domain Deficits
 * in Schizophrenia" (https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6252137/) and
 * writes it out as a scores file suitable for use with Hierarchical HotNet.
 * 
 * The input file is a csv file with a gene and a p-value. Each gene may be
 * listed more than once. The p-values are converted to z-scores and they
 * highest z-score for each pvalue is selected.
 * 
 * @author pleyte
 *
 */
public class PolyPValueToGeneScore {
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

	public static void main(String[] args) throws IOException {
		PolyPValueToGeneScore ppvToZ = new PolyPValueToGeneScore();
		Map<String, Double> geneScore = ppvToZ.load("/Users/pleyte/git/gene-modules-in-schizophreina/data/PolygenicRiskScore_files/table3_data.csv");
		ppvToZ.save(geneScore, "/Users/pleyte/git/gene-modules-in-schizophreina/data/PolygenicRiskScore_files/scores_2.tsv");
	}

	/**
	 * Write the converted p-scores out to tab delimited file
	 * 
	 * @param outFile
	 * @throws IOException
	 */
	private void save(Map<String, Double> geneScore, String outFile) throws IOException {
		try (Writer out = new FileWriter(outFile)) {
			CSVPrinter printer = CSVFormat.TDF.print(out);
			for (Entry<String, Double> entry : geneScore.entrySet()) {
				printer.printRecord(entry.getKey(), entry.getValue());
			}
		}

		log.info("Wrote gene scores to " + outFile);
	}

	/**
	 * return -log10(value)
	 * 
	 * @param pValue
	 * @return
	 */
	private Double getScore(String pValue) {
		return -1.0 * Math.log10(Double.valueOf(pValue));
	}

	/**
	 * Read comma separate list of genes and p-values. Convert p-values to a score
	 * by applying -log10(p-value).
	 * 
	 * @param inFile
	 * @throws IOException
	 */
	private Map<String, Double> load(String inFile) throws IOException {
		Map<String, Double> geneScore = new HashMap<>();

		try (Reader in = new FileReader(inFile)) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

			for (CSVRecord record : records) {
				String gene = record.get("gene");
				String pValue = record.get("p_value");
				Double score = getScore(pValue);

				if (gene.contains(" ")) {
					throw new IOException("Encountered space in gene named " + gene);
				}

				if (geneScore.get(gene) == null) {
					log.info("Accepting default for " + gene + ": pValue=" + pValue + ", score=" + score);
					geneScore.put(gene, score);
				} else if (score > geneScore.get(gene)) {
					log.info("Update score for " + gene + ": pValue=" + pValue + ", old score=" + geneScore.get(gene) + ", new score=" + score);
					geneScore.put(gene, Double.valueOf(score));
				}
			}
		}

		return geneScore;
	}

}
