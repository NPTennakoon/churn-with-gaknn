#readfilesum<-function(path)
#{

#	print(path)
#	i<-as.integer(num)	

	library(ROCR)
	#library(foreign)
	#library(gplots)

	fd<-read.csv('Summarize.csv')
	#fd<-read.arff('churntrain2.arff')
	#fd<-read.arff(path)
	attach(fd)

	sink('stats_summary.txt')
	n<- ncol(fd)
	i=1
	for(i in 1:n)
	{
	t<-fd[,i]
	#print(summary(colnames(fd)[i]))
	print(colnames(fd)[i])
	print(summary(t))
	}
#dev.off()
#}


