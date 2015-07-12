library(colorspace)
library(ggplot2)

bars<-read.csv('Plotting.csv')
attach(bars)


x<-ncol(bars)
#print(x)
#a<-colnames(bars)

png(filename="B%d.png",bg="transparent",width=300,height=300)

#i=1
for(i in 1:x)
{

#print(noquote(colnames(bars)[i]))
na<-noquote(colnames(bars)[i])
t<-bars[,i]

#counts<-table(bars$class,bars$colnames(bars)[i])
counts<-table(bars$class,t)
barplot(counts, main=colnames(bars)[i], xlab="values", col=c("darkblue","red"),legend = rownames(counts))

#print(plot(hexbin(t,class),main=colnames(bars)[i]),style="centroids")

i=i+1
}
dev.off()