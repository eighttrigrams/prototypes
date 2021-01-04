#pragma once

#include ".\grammatik.h"  //braucht in der definition zugriff auf grammatik weil die ziellisten von der form wort sind



class Lexikon
{
public:
	vector <Wort*> lookup(string vergleich); //vergleicht einen gegebenen string mit allen im Lexikon vorhandenen einträgen und
											 //schreibt alle matches in die zielliste und gibt diese zielliste dann zurück
	Lexikon(char *dateiNameNomen,char *dateiNameVerben,char *dateiNameDeterminierer);
	                                         //schriebt alle lexikoneinträge in die wortliste
	~Lexikon(void);                          //entfernt die wortliste ordnungsgemäß aus dem speicher
    void wortformLesen(Wort *w,string &eingabe);
private:
	
	vector <Wort*> wortListe;
	vector <Wort*> zielListe;
};

