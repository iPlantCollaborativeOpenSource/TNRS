library(ape)
library(rjson)	
library(RCurl) 

tnrs.api<-'http://tnrs.iplantc.org/tnrsm-svc'

#Tree topology from Ackerly, D. 2009. Conservatism and diversification of plant functional traits: Evolutionary rates versus phylogenetic signal. PNAS 106:19699--19706.
lobelioids.string<-'((((((Lobelia_kauaensis,Lobelia_villosa),Lobelia_gloria-montis),(Trematolobelia_kauaiensis,Trematolobelia_macrostachys)),((Lobelia_hypoleuca,Lobelia_yuccoides),Lobelia_niihauensis)),((Brighamia_insignis,Brighamia_rockii),(Delissea_rhytidosperma,Delissea_subcordata))),((((Cyanea_pilosa,Cyanea_acuminata),Cyanea_hirtella),(Cyanea_coriacea,Cyanea_leptostegia)),(((Clermontia_kakeana,Clermontia_parviflora),Clermontia_arborescens),Clermontia_fauriei)));'

#Transform the newick sting into an ape phylo object
tree<-read.tree(text=lobelioids.string)

#Obtain the taxa names
old.names<-tree$tip.label

#Change the underscore characters into blank spaces
old.names<-gsub('_',' ',old.names)

#Transporms the vector into a string
old.names<-paste(old.names,collapse=',')

#The string needs to be URL-encoded
old.names<-curlEscape(old.names)

#Send a request to the TNRS service
url<-paste(tnrs.api,'/matchNames?retrieve=best&names=',old.names,sep='')
tnrs.json<-getURL(url) 

#The response needs to be converted from JSON
tnrs.results<-fromJSON(tnrs.json)

#The corrected names are extracted from the response
names<-sapply(tnrs.results[[1]], function(x) c(x$nameSubmitted,x$acceptedName))
names<-as.data.frame(t(names),stringsAsFactors=FALSE)

#If TNRS did not return any accepted name (no match, or name is already accepted), the submitted name is retained
names[names[,2]=="",2]<-names[names[,2]=="",1] 

#The old taxa names are replaced with the corrected taxa names
tree$tip.label<-names[,2]

plot(tree)
