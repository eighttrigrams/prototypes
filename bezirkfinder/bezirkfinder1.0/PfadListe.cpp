#include "StdAfx.h"
#include ".\pfadliste.h"


void PfadList::WerteAus(){
   //auswertung ist wirklich nur das letzte element eindeutig?
   eineinziger=false;
   gefunden=false; bool serie;
   PfadListEl *auswertung;
   if (aktuell->gefunden==true)
   {  serie=true;
      gefunden=true;
	  auswertung=aktuell->vorg;
      while (auswertung!=basis){
		  if (auswertung->gefunden==false) serie=false;
		  if ((auswertung->gefunden==true)&&
			  (serie==false)) gefunden=false;
		  auswertung=auswertung->vorg;
	  } 
   } else gefunden=false;
   //auswertung welche eintraege gibt es dann zur strasse?
   if (gefunden==true) {
	   zielindex=ende->start->index;
       CString vergleich;
	   vergleich=afeld->eintrag[zielindex].strasse;
	   int j;j=zielindex+1;zielindex2=zielindex;
	   while (j<afeld->groesse){
		   if (afeld->eintrag[j].strasse==vergleich) zielindex2=j;
		   else break; j++;
	       }
	   if (zielindex==zielindex2) eineinziger=true;
   } 
}

PfadListEl *PfadList::Erstelle(
			  PfadListEl *akt,
			  int pruefindex,
			  char pruefchar){				  
  PfadListEl *tmp;
  tmp=new PfadListEl;
  int anzahl=0;
  do{// falls prüfstelle nicht vorhanden fängt das die nächste zeile ab
	  if (pruefindex<afeld->eintrag[akt->iterator->index].strasse.GetLength())
	  if (afeld->eintrag[akt->iterator->index].strasse.GetAt(pruefindex)
		 ==pruefchar){
	   //--------------eintrag in die neue liste-----------
	   anzahl++;
      
	   if (tmp->start==NULL) {tmp->start=new IndexListEl;
	                          SetStart(tmp);
							  tmp->iterator->index=akt->iterator->index;
	                         }
	   else                  {
                              tmp->iterator->next=new IndexListEl;
							  GoNext(tmp);
							  tmp->iterator->index=akt->iterator->index;
	                         }

	   //--------------eintrag in die neue liste-----------
	   }
	   GoNext(akt);
  }
  while (!EndeErreicht(akt));
  if (anzahl==0) return NULL;
  Schliesse(tmp);
  SetStart(tmp);
 
 
		
  if (anzahl==1) tmp->gefunden=true;
  return tmp;

}

void PfadList::Refresh(CString pruefstring){
	aktuell=basis; 
	//vor dem neuerstellen die vorherige liste loeschen
	while (ende!=basis){
	   ende=ende->vorg;
	   delete ende->next;
    }



	if (pruefstring.GetLength()!=0)
	for (int i=0;i<pruefstring.GetLength();i++){
     PfadListEl *tmp;
	 if (tmp=Erstelle(aktuell,i,pruefstring.GetAt(i))){  //achtung:zuweis, kein vgl
	   tmp->vorg=aktuell;
	   aktuell->next=tmp;	   
	   aktuell=aktuell->next;
	   ende=aktuell;
	 } else {ende->gefunden=false;// grund: sei der letzte
	         break;          }    // eintrag als gefunden definiert
	                              // es könnten jetzt zeichen kommen
                                  // die die suche ungültig machen
                                  // bei der auswertung aber würde festgestellt
                                  // der letzte eintrag sei true
   }WerteAus();
}



PfadList::PfadList(AdressFeld *feld)
{afeld=feld;
 zielindex=0;
 zielindex2=0;
 gefunden=false;
 basis=new PfadListEl;
 aktuell=basis;
 ende=basis;

 basis->start=new IndexListEl;
 SetStart(basis);
 basis->start->index=0;

 CString temp;
 temp=afeld->eintrag[0].strasse;

 int i=1;
 while (i<afeld->groesse){
	 if (!(temp==afeld->eintrag[i].strasse)){
		 temp=afeld->eintrag[i].strasse;
		 basis->iterator->next=new IndexListEl;
		 GoNext(basis);
		 basis->iterator->index=i;
	     }
     i++;
 }
 Schliesse(basis);
 SetStart(basis);
}


PfadListEl::~PfadListEl(){
  IndexListEl *t;
  iterator=start;
  iterator=iterator->next;
  while (!(iterator==start))
	   {
          t=iterator;
		  iterator=iterator->next;;
		  delete t;
	   }  
  delete start;
}



PfadListEl::PfadListEl(void)
{   gefunden=false;
	start=NULL;
	iterator=NULL;
	vorg=NULL;
	next=NULL;
}





