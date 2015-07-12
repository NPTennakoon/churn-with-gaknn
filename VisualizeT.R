visu<-read.csv('Plotting.csv')
attach(visu)
library(hexbin)
x<-ncol(visu)
print(x)
a<-colnames(visu)
png(filename="%d.png",bg="transparent",width=400,height=400)
#par(mfrow=c(2,2))
sink('test2.txt')
for(i in 1:x-1)
{

t<-visu[,i]
#print(t)
print(plot(hexbin(t,class),main=t),style="centroids")

i=i+1
}
#dev.off()