visu<-read.csv('Plotting.csv')
attach(visu)
library(hexbin)
x<-ncol(visu)
print(x)
a<-colnames(visu)
png(filename="%d.png",bg="transparent",width=400,height=400)
#par(mfrow=c(2,2))
#sink('test2.txt')
for(i in 1:x)
{
#t<-noquote(colnames(visu)[i])
#print(t)
#print(plot(colnames.visu[[i]],class,style="centroids",main=colnames(visu)[i])
#print(plot(t,class,style="centroids"))
print(noquote(colnames(visu)[i]))
print(i)

t<-visu[,i]
#print(t)
print(plot(hexbin(t,class),main=colnames(visu)[i]),style="centroids")
#print(plot(visu[,i:class],class,main=colnames(visu)[i]))
i=i+1
}
dev.off()