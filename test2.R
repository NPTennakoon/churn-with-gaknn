fd<-read.csv('output.csv')
attach(fd)
library(ROCR)

pred <- prediction(Predicted,Actual)
pref1<-performance(pred,'tpr','fpr')

## precision/recall curve (x-axis: recall, y-axis: precision)
pref2<- performance(pred, 'prec', 'rec')

## sensitivity/specificity curve (x-axis: specificity,y-axis: sensitivity)
pref3<- performance(pred, 'sens', 'spec')

##lift chart (x-axis: Rate of positive predictions, y-axis:lift value)
pref4<- performance(pred, 'lift', 'rpp')

png("modelaccuracy.png",bg="transparent",width=500,height=500)
par(mfrow=c(2,2))
print(plot(pref1,main="ROC Curve"))
print(plot(pref2,main="Precision- Recall Curve"))
print(plot(pref3,main="Sensitivity- Specificity Curve"))
print(plot(pref4,main="Lift Curve"))

auc.tmp <- performance(pred,'auc');
auc <- as.numeric(auc.tmp@y.values)
auc <- paste( "AUC: ", sprintf("%1.3f", auc) );

lifttmp <- performance(pred,"lift");
lift <- slot(lifttmp,"y.values")[[1]]

sink('auc.txt')
print(auc)
cat("Lift: ",lift)

preci<-performance(pred,'prec')
#preclass<-preci@x.values[[1]]
pre<-preci@y.values[[1]]
#print(preclass)
#print(pre)
cat("\nPrecision: ",pre)

rec<-performance(pred,'tpr')
rec<-rec@y.values[[1]]
#print(rec)
cat("\nRecall: ",rec)

dev.off()
