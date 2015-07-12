#readfile<-function(path)
#{

library(ROCR)
library(foreign)
library(robustbase)

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

#calculate the MedCouple
MC<-mc(Outliers_Removed[,c])

	if(MC>0)
	{
		m_UpperOutlier<-Q3 + 1.5*exp(4*MC)*IQR
		m_LowerOutlier<-Q1 - 1.5*exp(-3.5*MC)*IQR
	}
	else if(MC<0)
		{
			m_UpperOutlier<-Q3 + 1.5*exp(3.5*MC)*IQR
			m_LowerOutlier<-Q1 - 1.5*exp(-4*MC)*IQR
		}
		else
		{
			m_UpperOutlier<-Q3 + 1.5*IQR
			m_LowerOutlier<-Q1 - 1.5*IQR
		}



#print(b)
	if(mini<m_LowerOutlier )
	{
	print("hehehe")
	Outliers_Removed<-Outliers_Removed[!(Outliers_Removed[c]<m_LowerOutlier),]
	}
	else 
	{
		if (maxi>m_UpperOutlier )
		{
		print("huh uh")
		Outliers_Removed<-Outliers_Removed[!(Outliers_Removed[c]>m_UpperOutlier),]
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