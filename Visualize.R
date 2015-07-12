visu<-read.csv('Plotting.csv')
attach(visu)
library(hexbin)
x<-ncol(visu)
png(filename="%d.png",bg="transparent",width=500,height=500)
par(mfrow=c(2,2))
for(i in 1:x)
{
t<-visu[,i]
print(plot(t,class,main=colnames(visu)[i]),style="centroids")
i=i+1
}
dev.off()
