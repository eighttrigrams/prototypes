#pragma once
#include ".\adressfeld.h"


class IndexListEl{
public:
  IndexListEl *next;
  int index;
  IndexListEl(void){next=NULL;}
};

class PfadListEl{
public:
  IndexListEl *start;
  IndexListEl *iterator;

  PfadListEl *vorg;
  PfadListEl *next;
  
  PfadListEl(void);
  ~PfadListEl(void);
  bool gefunden;
};





class PfadList
{
public:
	AdressFeld *afeld;
	  
	int i_anfang(void){return zielindex;}
	int i_ende(void){return zielindex2;}
    bool gefunden;   //true,wenn eine strasse zur bearbeitung bleibt
	bool eineinziger;//true,wenn eine strasse mit nur einem eintrag
	

    
	bool inc_iterator(void){GoNext(ende);
                            if (ende->iterator==ende->start) return false;
                            else                             return true;
    } //erhöht it, gibt false zurück, wenn start erreicht
	int get_index(void){return ende->iterator->index;} // gibt den index an der akt iteratorpos zurück

	
	void Refresh(CString pruefstring);
	PfadList(AdressFeld *feld);
	~PfadList(void){delete basis;}

private:
	void GoNext(PfadListEl *element){element->iterator=element->iterator->next;}
	void Schliesse(PfadListEl *element){element->iterator->next=element->start;}
	void SetStart(PfadListEl *element){element->iterator=element->start;}
	bool EndeErreicht(PfadListEl *element){if  (element->iterator==element->start)
		                                   return true; else return false;}
    PfadListEl *Erstelle(PfadListEl *akt,int pruefindex,char pruefchar);
    void WerteAus(void);
    PfadListEl *basis;  
    PfadListEl *aktuell;  
    PfadListEl *ende;
	int zielindex;
	int zielindex2;
   
};



