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



void Lexikon::wortformLesen(Wort *w,string &eingabe){
  int i=0;
  for (;(eingabe[i]!=' ');i++){
      w->wortform+=eingabe[i];
  }
  eingabe=eingabe.substr(i+1,eingabe.size());
}

Lexikon::Lexikon(char *dateiNameNomen,
				 char *dateiNameVerben,
				 char *dateiNameDeterminierer)
{	char puffer[100];

	ifstream dateiNomen;
	ifstream dateiVerben;
	ifstream dateiDeterminierer;
	dateiNomen.open(dateiNameNomen);
	dateiVerben.open(dateiNameVerben);
	dateiDeterminierer.open(dateiNameDeterminierer);
	if (!dateiNomen){
		cout<<"NomenDatei konnte nicht gefunden werden!"<<endl; return;}
	if (!dateiVerben){
		cout<<"VerbenDatei konnte nicht gefunden werden!"<<endl; return;}
	if (!dateiDeterminierer){
		cout<<"DeterminiererDatei konnte nicht gefunden werden!"<<endl; return;}

	while (!dateiNomen.eof()){
		dateiNomen.getline(puffer,100,'\n');
	    string s1(puffer);
		Nomen *n = new Nomen;
		wortformLesen((Wort*) n,s1);
		n->merkmaleAuslesen(s1);
		wortListe.push_back((Wort*) n);
	}
	while (!dateiVerben.eof()){
		dateiVerben.getline(puffer,100,'\n');
	    string s1(puffer);
		Verb *v = new Verb;
		wortformLesen((Wort*) v,s1);
		v->merkmaleAuslesen(s1);
		wortListe.push_back((Wort*) v);
	}
	while (!dateiDeterminierer.eof()){
		dateiDeterminierer.getline(puffer,100,'\n');
	    string s1(puffer);
		Determinierer *d = new Determinierer;
		wortformLesen((Wort*) d,s1);
		d->merkmaleAuslesen(s1);
		wortListe.push_back((Wort*) d);
	}

    

	dateiNomen.close();
	dateiVerben.close();
}






vector <Wort*> Lexikon::lookup(string vergleich){
	zielListe.clear();
	
	for (unsigned int i=0;i<wortListe.size();i++){
	if (vergleich==(wortListe[i]->wortform)) zielListe.push_back(wortListe[i]);
    }
	return zielListe;
}

Lexikon::~Lexikon(void)
{
	for (unsigned int i=0;i<wortListe.size();i++){
		delete wortListe[i];	
	}
}


void Verb::ausgabe(){
  char* numerusChar[3]={"infinitiv","singular","plural"};
  char* personChar[4]={"0.pers","1.pers","2.pers","3.pers"};
  char* kasusChar[5]={"kein Argument","NOM","GEN","DAT","AKK"};
  cout<<setw(10)<<right<<wortform.c_str()<<setw(9)<<left<<" - Verb ";
  cout<<setw(7)<<left<<personChar[person];
  cout<<setw(10)<<left<<numerusChar[numerus]<<" ";
  if (argumente==1) cout<<kasusChar[argument1]<<" ";
  cout<<endl;
}
void Nomen::ausgabe(){
  char* numerusChar[3]={"infinitiv","singular","plural"};
  char* kasusChar[5]={"kein Argument","NOM","GEN","DAT","AKK"};
  char* genusChar[3]={"neutrum","maskulin","feminin"};
  cout<<setw(10)<<right<<wortform.c_str()<<setw(9)<<left<<" - Nomen ";
  cout<<setw(7)<<left<<" ";
  cout<<setw(10)<<left<<numerusChar[numerus]<<" ";
  cout<<kasusChar[kasus]<<" ";
  cout<<genusChar[genus];
  cout<<endl;
}
void Determinierer::ausgabe(){
  char* numerusChar[3]={"infinitiv","singular","plural"};
  char* kasusChar[5]={"kein Argument","NOM","GEN","DAT","AKK"};
  char* genusChar[3]={"neutrum","maskulin","feminin"};
  cout<<setw(10)<<right<<wortform.c_str()<<setw(9)<<left<<" - Det ";
  cout<<setw(7)<<left<<" ";
  cout<<setw(10)<<left<<numerusChar[numerus]<<" ";
  cout<<kasusChar[kasus]<<" ";
  cout<<genusChar[genus];
  cout<<endl;
}



void Determinierer::merkmaleAuslesen(string &eingabe){
  numerus=eingabe[0]-48;
  kasus=eingabe[2]-48;
  genus=eingabe[4]-48;
  return;
}

void Nomen::merkmaleAuslesen(string &eingabe){
  numerus=eingabe[0]-48;
  kasus=eingabe[2]-48;
  genus=eingabe[4]-48;
  return;
}

void Verb::merkmaleAuslesen(string &eingabe){
  person=eingabe[0]-48;
  numerus=eingabe[2]-48;
  argumente=eingabe[4]-48;
  argument1=eingabe[6]-48;
  return;
}



