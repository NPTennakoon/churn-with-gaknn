library(ROCR)
library(foreign)

#fd<-read.csv('Plotting.csv')
fd<-read.arff('churntrain2.arff')
attach(fd)

sink('stats.txt')
print(summary(fd))
