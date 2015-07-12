library(foreign)
a<-read.arff('churntrain2.arff')

mosthighlycorrelated <- function(mydataframe,numtoreport){
	# find the correlations
	cormatrix <- cor(mydataframe)
	# set the correlations on the diagonal or lower triangle to zero,
	# so they will not be reported as the highest ones:
	diag(cormatrix) <- 0
	cormatrix[lower.tri(cormatrix)] <- 0
	# flatten the matrix into a dataframe for easy sorting
	fm <- as.data.frame(as.table(cormatrix))
	# assign human-friendly names
	names(fm) <- c("First.Attribute", "Second.Attribute","Correlation")
	# sort and print the top n correlations
	head(fm[order(abs(fm$Correlation),decreasing=T),],n=numtoreport)
}

sink('CorrelationData.txt')
p<-mosthighlycorrelated(a[,unlist(lapply(a, is.numeric))], 10)
print(p)

####ref: http://little-book-of-r-for-multivariate-analysis.readthedocs.org/en/latest/src/multivariateanalysis.html