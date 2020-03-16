# Using Network Diffusion to Identify the Genetic Overlap of Schizophrenia and ASD

In this project gene scores from two schizophrenia studies and one ASD study are compared in an effort to find genetic overlap among these two highly heritable diseases.   
  
A list of genes from each study is compiled and used to generate a network using (ReactomeFIVIz)[https://reactome.org/tools/reactome-fiviz#Overview] an exported from (Cystoscape)[https://cytoscape.org/] in SIF format. 

Network diffusion is applied to the network and gene scores using Hierarchical HotNet (3). 

## Files

* ``src/main/java`` Java source files
* ``src/main/resources/gene_network.sif`` - Gene network created from three studies
* ``src/main/resources/clustered_gene_network.sif`` - Gene network created from top genes after network diffusion
* ``src/main/resources/hotnet_network`` - Files input to and output from Hierarchical HotNet
* ``R/eda.Rmd`` - Some processing steps which are performed in R
* ``data/`` - Multiple artifacts   
	 	

## Setup

run PerformAnalysis

## License
See `LICENSE.txt` for license information.

## References

1. HotNet: 
	a. F. Vandin, E. Upfal, and B.J. Raphael. (2011) Algorithms for Detecting Significantly Mutated Pathways in Cancer. Journal of Computational Biology. 18(3):507-22
	b. F. Vandin, P. Clay, E. Upfal, and B. J. Raphael. Discovery of Mutated Subnetworks Associated with Clinical Data in Cancer. In Proc. Pacific Symposium on Biocomputing (PSB), 2012.

2. HotNet2: M.D.M. Leiserson*, F. Vandin*, H.T. Wu, J.R. Dobson, J.V. Eldridge, J.L. Thomas, A. Papoutsaki, Y. Kim, B. Niu, M. McLellan, M.S. Lawrence, A.G. Perez, D. Tamborero, Y. Cheng, G.A. Ryslik, N. Lopez-Bigas, G. Getz, L. Ding, and B.J. Raphael. (2014) Pan-Cancer Network Analysis Identifies Combinations of Rare Somatic Mutations across Pathways and Protein Complexes. Nature Genetics 47, 106–114 (2015).

3. Hierarchical HotNet: M.A. Reyna, M.D.M. Leiserson, B.J. Raphael. Hierarchical HotNet: identifying hierarchies of altered subnetworks. ECCB/Bioinformatics 34(17):i972-980, 2018.

4. Integrated Post-GWAS Analysis Sheds New Light on the Disease Mechanisms of Schizophrenia: Lin JR, Cai Y, Zhang Q, Zhang W, Nogales-Cadenas R, Zhang ZD. Integrated Post-GWAS Analysis Sheds New Light on the Disease Mechanisms of Schizophrenia. Genetics. 2016;204(4):1587–1600. doi:10.1534/genetics.116.187195

5. Polygenic risk score, genome-wide association, and gene set analyses of cognitive domain deficits in schizophrenia: Nakahara, S., Medland, S., Turner, J. A., Calhoun, V. D., Lim, K. O., Mueller, B. A., Bustillo, J. R., O'Leary, D. S., Vaidya, J. G., McEwen, S., Voyvodic, J., Belger, A., Mathalon, D. H., Ford, J. M., Guffanti, G., Macciardi, F., Potkin, S. G., & van Erp, T. (2018). Polygenic risk score, genome-wide association, and gene set analyses of cognitive domain deficits in schizophrenia. Schizophrenia research, 201, 393–399. https://doi.org/10.1016/j.schres.2018.05.041

6. Tarjan R.E. (1983) An improved algorithm for hierarchical clustering using strong components. Inform. Process. Lett., 17, 37–41.

7. ASD and schizophrenia show distinct developmental profiles in common genetic overlap with population-based social communication difficulties: St Pourcain, B., Robinson, E., Anttila, V. et al. ASD and schizophrenia show distinct developmental profiles in common genetic overlap with population-based social communication difficulties. Mol Psychiatry 23, 263–270 (2018). https://doi.org/10.1038/mp.2016.198

8. Network Diffusion-Based Prioritization of Autism Risk Genes Identifies Significantly Connected Gene Modules: Mosca E, Bersanelli M, Gnocchi M, et al. Network Diffusion-Based Prioritization of Autism Risk Genes Identifies Significantly Connected Gene Modules. Front Genet. 2017;8:129. Published 2017 Sep 25. doi:10.3389/fgene.2017.00129

