#readfile<-function(path)
#{

library(ROCR)
library(foreign)

#Outliers_Replaced<-read.arff('E:\\Project\\TestFiles\\churntrain2.arff')
#Outliers_Replaced<-read.arff(path)
Outliers_Replaced<-read.csv('Summarize.csv')
attach(Outliers_Replaced)
y<-read.csv('ColumnsNames.csv')
attach(y)

sink('quantiles.txt')
n<-ncol(y)-1
#n<-ncol(y)-2
print(n)
new<-nrow(Outliers_Replaced)
print(new)
print(Outliers_Replaced)
i=1
for(i in 1:n)
{
c<-noquote(colnames(y)[i])
print(c)
typecheck<-Outliers_Removed[,i]
if(is.numeric(typecheck))
{
mini<-min(Outliers_Replaced[,c])
maxi<-max(Outliers_Replaced[,c])
print(mini)
Q1<-quantile(Outliers_Replaced[,c],0.25)
Q3<-quantile(Outliers_Replaced[,c],0.75)
IQR<-Q3-Q1
#lfence<-1.5*IQR
OF<-3
EVF<- 2 *OF
m_UpperOutlier<-Q3 + OF*IQR
m_LowerOutlier<-Q1 - OF*IQR
m_UpperExtremeValue<-Q3 + EVF*IQR
m_LowerExtremeValue<-Q1 - EVF*IQR

#print(lfence)
#print(b)
j=1
for(j in 1:new)
{
	if(mini<m_LowerOutlier && mini<m_LowerExtremeValue)
	{
	print("hehehe")
	Outliers_Replaced[j,i]<- m_LowerOutlier
	}
	else 
	{
		if (maxi>m_UpperOutlier && maxi>m_UpperExtremeValue)
		{
		print("huh uh")
		Outliers_Replaced[j,i]<- m_UpperOutlier
		}
		else
		{
		print("hah aah")
		}
	}
j=j+1
}
}#end if is.numeric()
i=i+1
print("========")
}

print("++++++++++++++++")
print(Outliers_Replaced)
new<-nrow(Outliers_Replaced)
print(new)
write.arff(Outliers_Replaced,"Outliers_Replaced.arff")
#dev.off()

#}