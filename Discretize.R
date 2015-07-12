#readfiled <- function(path1)
#{

library(foreign)
library(infotheo)

#s<-read.arff(path1)
#s<-read.arff('E:\\Project\\TestFiles\\churntrain2.arff')
s<-read.csv('Summarize.csv')
attach(s)
Discritized<-discretize(s,"equalwidth",10)
numcol<-ncol(Discritized)
for(i in 1:numcol)
{
#t<-noquote(colnames(Discritized)[1])
#print(t)
#Discritized$t<-factor(Discritized$t)
Discritized[,i] <- as.factor(Discritized[,i])
i=i+1
}
#sink('errory.txt')
#print(t)
write.arff(Discritized,"Discritized.arff")

#dev.off()

#}

