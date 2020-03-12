# schizophrenia-gene-modules
a network diffusion approach to inferring  gene modules involved in schizophrenia

## Files

* SFARI-Gene_genes_03-04-2020release_03-06-2020export.csv
	- Genes and scores exported from the Search Results Simons Foundation Autism Research Initiative (SFARI)
* table1.paper1_autism_563_gene_module.xlsx and table2.paper1_autism_11535_SFARI_genes_ranked.xlsx
	- Data from the paper Network Diffusion-Based Prioritization of Autism Risk Genes Identifies Significantly Connected Gene Modules
* data/scores_schizophrenia_1.ods
	- Data from the paper "Polygenic Risk Score, Genome-wide Association, and Gene Set Analyses of Cognitive Domain Deficits in Schizophrenia"
* data/scores_schizophrenia_2.ods
	- Data from the paper "Integrated Post-GWAS Analysis Sheds New Light on the Disease Mechanisms of Schizophrenia"
* clustered_gene_network.sif
	- All the genes from the clusters of size greater than 1 were fed back in to Reactome FI to create a new (smaller) network.
	- In order to make the network connected, linker genes are included. 
	 	


## Setup
download hierarchichal hotnet
https://github.com/raphael-group/hierarchical-hotnet.git

fix the 
create a python virtual environment

run PerformAnalysis

## References

1. Network Diffusion-Based Prioritization of Autism Risk Genes Identifies Significantly Connected Gene Modules

2. Polygenic Risk Score, Genome-wide Association, and Gene Set Analyses of Cognitive Domain Deficits in Schizophrenia

3. Integrated Post-GWAS Analysis Sheds New Light on the Disease Mechanisms of Schizophrenia

4. Human Gene Module: lists 913 genes implicated in autism, with annotations and links to published papers.
or Gene Scoring Module?
From https://www.sfari.org/resource/sfari-gene/ 
	https://gene.sfari.org/database/gene-scoring/
		- SFARI-Gene_genes_03-04-2020release_03-06-2020export.csv
	https://gene.sfari.org/database/human-gene/
		- SFARI-Gene_genes_03-04-2020release_03-06-2020export.csv

5. ASD and schizophrenia show distinct developmental profiles in common genetic overlap with population-based social communication difficulties		