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
import org.apache.commons.lang3.StringUtils;



/**
 * Read the SFARI (https://www.sfari.org/) database and write out a gene-score
 * tab delimited file for use with Hierarchical HotNet.
 * 
 * @author pleyte
 *
 */
public class SfariToGeneScore {

	private static Logger log;

	static {
		InputStream stream = SfariToGeneScore.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			log = Logger.getLogger(SfariToGeneScore.class.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		SfariToGeneScore sfariToGs = new SfariToGeneScore();
		Map<String, Integer> geneScoreMap = sfariToGs.load("/Users/pleyte/git/gene-modules-in-schizophreina/data/SFARI/SFARI-Gene_genes_03-04-2020release_03-06-2020export.csv");
		sfariToGs.save(geneScoreMap, "/Users/pleyte/git/gene-modules-in-schizophreina/data/SFARI/scores_3.tsv");
	}

	private void save(Map<String, Integer> geneScoreMap, String outFile) throws IOException {
		try (Writer out = new FileWriter(outFile)) {
			CSVPrinter printer = CSVFormat.TDF.print(out);
			for (Entry<String, Integer> entry : geneScoreMap.entrySet()) {
				printer.printRecord(entry.getKey(), entry.getValue());
			}
		}

		log.info("Wrote gene scores to " + outFile);
	}

	/**
	 * Load the SFARI database
	 * 
	 * @param string
	 * @throws Exception
	 */
	private Map<String, Integer> load(String inFile) throws Exception {
		Map<String, Integer> geneScoreMap = new HashMap<>();

		try (Reader in = new FileReader(inFile)) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
			geneScoreMap = new HashMap<>();

			for (CSVRecord record : records) {
				String gene = record.get("gene-symbol");
				String geneScore = record.get("gene-score");
				boolean isSyndromic = "1".equals(record.get("syndromic"));

				if (geneScore == null && !isSyndromic) {
					throw new Exception("No score or syndromic indicator for " + gene);
				}

				int score = getScore(geneScore, isSyndromic);

				if (geneScoreMap.get(gene) == null) {
					log.info(gene + ": geneScore=" + geneScore + ", syndromic=" + isSyndromic + ", finalScore=" + score);
					geneScoreMap.put(gene, score);
				} else {
					throw new Exception("Found duplicate gene");
				}
			}
		}

		return geneScoreMap;
	}

	/**
	 * Map the SFARI score and syndromic indicator to a numeric value/heat score. S
	 * 100 1S 90 1 80 2S 70 2 60 3S 50 3 40 4S 30 4 20 - 1
	 * 
	 * @param geneScoreMap
	 * @param isSyndromic
	 * @return
	 * @throws Exception
	 */
	private int getScore(String geneScore, boolean isSyndromic) throws Exception {
		if (StringUtils.isBlank(geneScore) && isSyndromic) {
			return 100;
		} else if ("1".equals(geneScore)) {
			return isSyndromic ? 90 : 80;
		} else if ("2".equals(geneScore)) {
			return isSyndromic ? 70 : 60;
		} else if ("3".equals(geneScore)) {
			return isSyndromic ? 50 : 40;
		} else {
			throw new Exception("Unrecognized score: " + geneScore + ", syndromic=" + isSyndromic);
		}
	}
}

