visu<-read.csv('Plotting.csv')
attach(visu)

x<-ncol(visu)
png(filename="O%d.png",bg="transparent",width=300,height=300)

for(i in 1:(x-1))
{
t<-visu[,i]
print(boxplot(t,las=2,main=colnames(visu)[i]))
i=i+1
}
dev.off()