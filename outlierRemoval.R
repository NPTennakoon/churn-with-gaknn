#readfile<-function(path)
#{

library(ROCR)
library(foreign)

#Outliers_Removed<-read.arff('E:\\Project\\TestFiles\\churntrain2.arff')
#Outliers_Removed<-read.arff(path)
Outliers_Removed<-read.csv('Summarize.csv')
attach(Outliers_Removed)
y<-read.csv('ColumnsNames.csv')
attach(y)

sink('quantile.txt')
n<-ncol(y)-1
#n<-ncol(y)-2
print(n)
new<-nrow(Outliers_Removed)
print(new)
print(Outliers_Removed)
i=1
for(i in 1:n)
{
c<-noquote(colnames(y)[i])
print(c)
typecheck<-Outliers_Removed[,i]
if(is.numeric(typecheck))
{
mini<-min(Outliers_Removed[,c])
maxi<-max(Outliers_Removed[,c])
print(mini)
Q1<-quantile(Outliers_Removed[,c],0.25)
Q3<-quantile(Outliers_Removed[,c],0.75)
IQR<-Q3-Q1
lfence<-1.5*IQR
OF<-3
EVF<- 2 *OF
m_UpperOutlier<-Q3 + OF*IQR
m_LowerOutlier<-Q1 - OF*IQR
m_UpperExtremeValue<-Q3 + EVF*IQR
m_LowerExtremeValue<-Q1 - EVF*IQR


print(lfence)
#print(b)
	if(mini<m_LowerOutlier && mini<m_LowerExtremeValue)
	{
	print("hehehe")
	Outliers_Removed<-Outliers_Removed[!(Outliers_Removed[c]<m_LowerExtremeValue),]
	}
	else 
	{
		if (maxi>m_UpperOutlier && maxi>m_UpperExtremeValue)
		{
		print("huh uh")
		Outliers_Removed<-Outliers_Removed[!(Outliers_Removed[c]>m_UpperExtremeValue),]
		}
		else
		{
		print("hah aah")
		}
	}
}#end if is.numeric()
i=i+1
print("========")
}

print("++++++++++++++++")
print(Outliers_Removed)
new<-nrow(Outliers_Removed)
print(new)
write.arff(Outliers_Removed,"Outliers_Removed.arff")
#dev.off()

#}

