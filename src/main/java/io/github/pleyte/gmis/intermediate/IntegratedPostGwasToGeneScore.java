package io.github.pleyte.gmis.intermediate;

import java.io.FileNotFoundException;
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
import org.apache.commons.lang3.StringUtils;

import io.github.pleyte.gmis.PerformAnalysis;

/**
 * Tthis class loads gene scores from three tables exported from the PDF paper (Integrated Post-GWAS Analysis Sheds New
 * Light on the Disease Mechanisms of Schizophrenia) and saves all the gene scores to the "scores_1.tsv" file.
 * 
 * @author pleyte
 *
 */
public class IntegratedPostGwasToGeneScore {
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
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		IntegratedPostGwasToGeneScore ipToGs = new IntegratedPostGwasToGeneScore();

		Map<String, Double> putativeGeneScores = ipToGs
				.loadGenesWithPutativeSupport("data/IntegratedPost-GWASAnalysis_files/Table1.csv");
		Map<String, Double> literatureGeneScores = ipToGs
				.loadGenesWithLiteratureSupport("data/IntegratedPost-GWASAnalysis_files/tabula-TableS2.csv");
		Map<String, Double> regulatoryGeneScores = ipToGs
				.loadGenesWithRegulatorySupport("data/IntegratedPost-GWASAnalysis_files/tabula-TableS4.csv");

		Map<String, Double> geneScoreConsolidated = consolidateGeneScores(putativeGeneScores, literatureGeneScores, regulatoryGeneScores);
		ipToGs.save(geneScoreConsolidated, "data/IntegratedPost-GWASAnalysis_files/scores_1.tsv");

	}

	/**
	 * Load the gene scores for the 132 putative genes in Table 1
	 * 
	 * @param table1CsvFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Map<String, Double> loadGenesWithPutativeSupport(String table1CsvFile) throws FileNotFoundException, IOException {
		Map<String, Double> geneScore = new HashMap<String, Double>();
		try (Reader in = new FileReader(table1CsvFile)) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
				String gene = record.get("Gene symbol");
				String score = record.get("Score");

				if (StringUtils.isBlank(score)) {
					throw new IOException("Table1: Encountered blank score for record number " + record.getRecordNumber());
				} else if (geneScore.get(gene) != null && !geneScore.get(gene).equals(Double.valueOf(score))) {
					throw new IOException("Table1: Encountered duplicate gene " + gene + " with differing score in input file at record number " + record.getRecordNumber());
				} else {
					geneScore.put(gene, Double.valueOf(score));
				}
			}
		}

		return geneScore;
	}

	/**
	 * Save the gene score map to file
	 * 
	 * @param geneScoreConsolidated
	 * @param string
	 * @throws IOException
	 */
	private void save(Map<String, Double> geneScoreConsolidated, String geneScoreFile) throws IOException {
		try (Writer out = new FileWriter(geneScoreFile)) {
			CSVPrinter printer = CSVFormat.TDF.print(out);
			for (Entry<String, Double> entry : geneScoreConsolidated.entrySet()) {
				printer.printRecord(entry.getKey(), entry.getValue());
			}
		}

		log.info("Wrote gene scores to " + geneScoreFile);
	}

	/**
	 * Extract gene scores from the CSV version of the Table S4 from the paper
	 * 
	 * @param tableS4CsvFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Map<String, Double> loadGenesWithRegulatorySupport(String tableS4CsvFile) throws FileNotFoundException, IOException {
		Map<String, Double> geneScore = new HashMap<String, Double>();
		try (Reader in = new FileReader(tableS4CsvFile)) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
				String gene = record.get("Gene symbol");
				String score = record.get("Score");

				if (StringUtils.isBlank(gene)) {
					// the rows with missing gene can be ignored
					continue;
				} else if (StringUtils.isBlank(score)) {
					throw new IOException("S4: Encountered blank score for record number " + record.getRecordNumber());
				} else if (geneScore.get(gene) != null && !geneScore.get(gene).equals(Double.valueOf(score))) {
					throw new IOException("S4: Encountered duplicate gene " + gene + " with differing score in input file at record number " + record.getRecordNumber());
				} else {
					geneScore.put(gene, Double.valueOf(score));
				}
			}
		}

		return geneScore;
	}

	/**
	 * Trim a trailing '*' from the gene name
	 * 
	 * @param gene
	 * @return
	 */
	private String trim(String gene) {
		if (gene.endsWith("*")) {
			return gene.substring(0, gene.length() - 1);
		} else {
			return gene;
		}
	}

	/**
	 * Extract gene scores from the CSV version of the Table S2 from the paper
	 * 
	 * @param string
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private Map<String, Double> loadGenesWithLiteratureSupport(String tableS2CsvFile) throws FileNotFoundException, IOException {
		Map<String, Double> geneScore = new HashMap<String, Double>();
		try (Reader in = new FileReader(tableS2CsvFile)) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
			for (CSVRecord record : records) {
				String gene = record.get("Gene symbol1");
				String score = record.get("Score");

				if (StringUtils.isBlank(gene)) {
					throw new IOException("S2: Encountered blank gene on for record number " + record.getRecordNumber());
				} else if (StringUtils.isBlank(score)) {
					throw new IOException("S2: Encountered blank score for " + gene + " record number " + record.getRecordNumber());
				} else if (geneScore.get(gene) != null) {
					throw new IOException("S2: Encountered duplicate gene in input file at record number " + record.getRecordNumber());
				} else {
					//					if (!trim(gene).equals(gene)) {
					//						log.severe("jDebug: trimming " + gene + " to " + trim(gene));
					//					}
					geneScore.put(trim(gene), Double.valueOf(score));
				}
			}
		}

		return geneScore;
	}

	/**
	 * Merge two gene score maps
	 * 
	 * @param literatureGeneScores
	 * @param regulatoryGeneScores
	 * @param regulatoryGeneScores2
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Double> consolidateGeneScores(Map<String, Double> putativeGeneScores,
			Map<String, Double> literatureGeneScores, Map<String, Double> regulatoryGeneScores) throws Exception {

		// Create a new map with all the genes from the first gene set
		Map<String, Double> geneSet = new HashMap<>(putativeGeneScores);

		// Add genes from the second gene set
		for (Entry<String, Double> entry : regulatoryGeneScores.entrySet()) {
			if (geneSet.containsKey(entry.getKey()) && Double.compare(geneSet.get(entry.getKey()), entry.getValue()) != 0) {
				log.info("Regulatory set encountered conflictict with " + entry.getKey() + ": " + geneSet.get(entry.getKey()) + " vs " + entry.getValue());
				geneSet.put(entry.getKey(), Math.max(geneSet.get(entry.getKey()), entry.getValue()));
			} else {
				geneSet.put(entry.getKey(), entry.getValue());
			}
		}

		// Add genes from the third gene set
		for (Entry<String, Double> entry : literatureGeneScores.entrySet()) {
			if (geneSet.containsKey(entry.getKey()) && Double.compare(geneSet.get(entry.getKey()), entry.getValue()) != 0) {
				log.info("Literature set encountered conflictict with " + entry.getKey() + ": " + geneSet.get(entry.getKey()) + " vs " + entry.getValue());
				geneSet.put(entry.getKey(), Math.max(geneSet.get(entry.getKey()), entry.getValue()));
			} else {
				geneSet.put(entry.getKey(), entry.getValue());
			}
		}

		return geneSet;
	}

}
