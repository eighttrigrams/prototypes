#include "stdafx.h"
#include <vector>
#include  <iostream>
#include <iomanip>
#include <string>
#include <fstream>
using namespace std;
using std::vector;

#include ".\lexikon.h"
#include ".\grammatik.h"


void npAusgabe(NP* np,const vector <string> &bezugsSatz)
{
			string test="";
			for (unsigned int j=np->anfangsposition;j<(np->endposition+1);j++) 
			{
				if ((j==(np->anfangsposition+1))&&((np->endposition-np->anfangsposition)>1)) test=test+" (";
				else if ((j==(np->endposition))&&((np->endposition-np->anfangsposition)>1)) test=test+") ";
				else test=test+" ";
			
				test=test+bezugsSatz[j]; //wörter zusammensetzen	

			}
			cout<<test;
			//np->ausgabe();cout<<endl;
			
			for (unsigned int j=0;j<np->komplemente.size();j++)
			{
				if ((j<np->komplemente.size())&&(j!=0)) cout<<"||";
				cout<<" [";
				npAusgabe(np->komplemente[j],bezugsSatz);
				cout<<"] ";
				
			}    
}


vector <string> tokenize(string &eingabeSatz)
{
  vector <string> v;
  string puffer;

	for (unsigned int i=0;i<(eingabeSatz.size());i++){
		if (eingabeSatz[i]==' '){			
			v.push_back(puffer);
			puffer.erase();
		}
		else 
		puffer+=eingabeSatz[i];
	}v.push_back(puffer);

  return v; 
}
void ausgabeListe(const vector <Wort*> ausgabeListe){
	for (unsigned int i=0;i<ausgabeListe.size();i++){
		Wort *w;
		w=(Wort*) ausgabeListe[i]; w->ausgabe();
		//völlig verwirrendes Phänomen, ich brauche hier kein casting auf die eigentlichen typen vornehmen, und
		//trotzdem wird immer die richtige ausgaberoutine angewählt, jedoch gibt es einen fehler wenn ich das casting auf wort* weglasse
	}
}
Lexikon lexikon("nomen.txt","verben.txt","determinierer.txt");
vector <string> satz; 


void lexToNP(char typ,const Wort* wort,NP* &np,int start,int ende)
{//überführt die lexikalischen eigenschaften eines wortes auf eine entsprechende np
	if (typ=='n'){
		Nomen *nom=(Nomen*) wort;
        NPeigenschaft eigenschaft;

		eigenschaft.kasus=nom->kasus;
		eigenschaft.genus=nom->genus;
		eigenschaft.numerus=nom->numerus;

		np->eigenschaft.push_back(eigenschaft);

        np->anfangsposition=start;
		np->endposition=ende;
	}
}



void sucheNP(int index,const vector <string> &einSatz,vector <NP*> &uebergabe){
	NP* gefundeneNP;
	vector <NP*> holen;
	
	vector <Wort*> listeEins;
	vector <Wort*> listeZwei;
    
    listeEins=lexikon.lookup(einSatz[index]);
	
	if (listeEins.empty()) return; //sofortiger abbruch falls wort nicht vorhanden oder verb

	if (listeEins[0]->typ=='n') {//______________________________________________________________________________________
        gefundeneNP=new NP;
		for (unsigned int i=0;i<listeEins.size();i++) lexToNP('n',listeEins[i],gefundeneNP,index,index);	//eigenschaften überführen	
		index++; 
		if (index!=einSatz.size())
		{	
			holen.clear(); //liste freimachen damit die uebergebenen transportiert werden können
			sucheNP(index,einSatz,holen);
			for (unsigned int i=0;i<holen.size();i++)
				gefundeneNP->komplemente.push_back(holen[i]);
		}
        uebergabe.push_back(gefundeneNP);
	}//__________________________________________________________________________________________________________________
	
	if (listeEins[0]->typ=='d') {
								 
								 //jetzt beginnt die suche, bis wohin geht diese np
								 for (unsigned int i=index+1;i<einSatz.size();i++)
								 {//für jedes im satz folgende glied wird geprüft, ob es einen vorhergehenden determinierer 
									 //vervolständigen kann
									 int voll=0; int folge=0;
									 listeZwei.clear();
									 listeZwei=lexikon.lookup(einSatz[i]);
									 if (listeZwei.empty()) continue; //überprüfen----------------
									 // if (listeZwei[0]->typ=='v') break;
									 if (listeZwei[0]->typ=='n') 
									 {			

                                         //hier sollte später die komplette vergleichsoperation hin
										 //mehrere ergebnisse sollten möglich sein beispielsweise die kinder schlagen die kinder
										 gefundeneNP=new NP;
										 for (unsigned int eins=0;eins<listeEins.size();eins++)					 
											 for (unsigned int zwei=0;zwei<listeZwei.size();zwei++)
											 {			 
												 Determinierer *det;
												 det=(Determinierer*) listeEins[eins];
												 Nomen *nom;
												 nom=(Nomen*) listeZwei[zwei];
																				 							 
												 if ((det->numerus==nom->numerus)&& //bei komplette übereinstimmung der merkmale zwischen
													 (det->kasus==nom->kasus)&&     //einem nomen und einem determinierer ist eine mögliche
													 (det->genus==nom->genus))      //np gefunden worden
												 {
													 lexToNP('n',listeZwei[zwei],gefundeneNP,index,i);
													 
                                                     voll=1; folge=i+1; //es wurde eine passende ergänzung gefunden i sagt wo
												 }                       			 
											 }
											
										  
										 if (voll) 
										 {   
											 holen.clear();
											 if (folge!=einSatz.size())
											 {
											 sucheNP(folge,einSatz,holen);
											 for (unsigned int i=0;i<holen.size();i++)
				                               gefundeneNP->komplemente.push_back(holen[i]);
											 }
											 uebergabe.push_back(gefundeneNP);
										 }
									 }
								 }

								 
	}
    //wenn ich hier bin heisst das wahrscheinlich, es wurde etwas gefunden
	//also stelle ich eine neue liste für komplemente zur verfügung und rufe die funktion rekrusiv auf
	//vector <NP*> komplemente;




 return;
}


void purge(const vector <string> &einSatz,vector <NP*> &np,unsigned int &index)
{
    unsigned int a=0;
	a=np[index]->endposition-np[index]->anfangsposition;
	
	if (a>1)//ist der aktuelle np zu purgbar
	{
		vector <Wort*> liste;
        liste=lexikon.lookup(einSatz[(np[index]->endposition)-1]);
		//_________________________________________________________________________________________________________________________________
		
		if ((!liste.empty())&&(liste[0]->typ=='d')) //wichtig doppel&& sonst führt er die zweite bed aus ,was bei leerer liste z absturz führt
		{
			//hier muss später per unifizierung genauer
		    //geprüft werden
            np.erase(np.begin()+index); 
			index=index-1; 
			return; //wichtig den index zu reduzieren, da ein element entfernt wurde
            
		}
		//___________________________________________________________________________________________________________________________________
	}
   
	//gibt es komplemente zu purgen
	
    for (unsigned int i=0;i<np[index]->komplemente.size();i++){
		purge(einSatz,np[index]->komplemente,i);
	}


}

int indepth(vector <NP*> np,unsigned int pos,unsigned int bis){//sucht ob es sich um einen komplementrand handelt
	                                         //durchsucht bis jetzt immer nur das erste komplement
	                                      
	if (np[pos]->endposition==bis) return 1;
    if (!np[pos]->komplemente.empty()) 
		if(indepth(np[pos]->komplemente,0,bis)) return 1;
    return 0;
}

void purge2(unsigned int bis,vector <NP*> &np,unsigned int index)
{
//es wird nach übereinstimmungen im rechten rand gesucht sowohl auf globaler wie auf komplementebene
	for (unsigned int i=index;i>0;i--){
	  if (indepth(np,i-1,bis))
	  {
		  np.erase(np.begin()+index); 
		  return; //auftrag erledigt
	  }
	 // if (i==0) return; //nicht mehr dekrementieren an pos 0
	}
}






int _tmain(int argc, _TCHAR* argv[])
{
    string t("der hund schlaegt die tollen kinder");
	cout<<t<<endl<<endl;
	satz=tokenize(t);
    //die kinder aergernden kinder der kinder
    //die kinder liebende frau des mannes des mannes
    //die kinder schlagen den hans der tollen kinder
    //die kinder des hundes schlagen den tollen hund der tollen kinder
    //die kinder der kinder pflegenden frau
    //der hund der tollen kinder
	//der hund schlaegt die tollen kinder-------------fehlerder hund der spielenden kinder
	vector <NP*> alleNP;
    string test="";


	for (unsigned int i=0;i<(satz.size());i++) {
    //das besondere: scuhe von jeder satzposition aus
		sucheNP(i,satz,alleNP);		
	}
	cout<<"folgende NPs koennen sich als wertvoll erweisen:"<<endl<<endl;
	for (unsigned int i=0;i<alleNP.size();i++) {
		cout<<alleNP[i]->anfangsposition<<" "<<alleNP[i]->endposition<<":";
		npAusgabe(alleNP[i],satz); cout<<endl;}
    cout<<endl<<endl;
	
    
    for (unsigned int i=0;i<alleNP.size();i++)
	{  
       purge(satz,alleNP,i); 
	}

	cout<<"alle adjazenten dets beseitigt:"<<endl<<endl;
	for (unsigned int i=0;i<alleNP.size();i++) {
		cout<<alleNP[i]->anfangsposition<<" "<<alleNP[i]->endposition<<":";
		npAusgabe(alleNP[i],satz); cout<<endl;}
    cout<<endl<<endl;



	cout<<"alle doppelten raender loeschen:"<<endl;
	for (unsigned int i=(alleNP.size()-1);i>0;i--)
	{  
		purge2(alleNP[i]->endposition,alleNP,i); 
	}

	cout<<"uebrig bleibt:"<<endl<<endl;
	
	for (unsigned int i=0;i<alleNP.size();i++) {
		cout<<alleNP[i]->anfangsposition<<" "<<alleNP[i]->endposition<<":";
		npAusgabe(alleNP[i],satz); cout<<endl;}
    cout<<endl<<endl;


    cout<<endl;
	system("PAUSE");
	return 0;
}



