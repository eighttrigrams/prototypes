#pragma once
#include <vector>

struct NPeigenschaft{
	int numerus;
	int kasus;
	int genus;
};

class NP
{
public:
    vector <NP*> komplemente;
    vector <NPeigenschaft> eigenschaft;

	unsigned int anfangsposition;
	unsigned int endposition;
    //sind die werte 0, sind dies nicht unbedingt die initialwerte, vielmehr kann es vorkommen, das
	//dies den status unbekannter wert repräsentiert
	NP(){anfangsposition=0;endposition=0;}
	~NP(){}

	void ausgabe();
private:
};


class Wort
{
public:

	//friend void Lexikon::lookup(string vergleich);
	char typ;
	Wort(){}
	~Wort(){}
	virtual void erkenneEintrag(char eingabe[100]){}
	virtual void ausgabe(){}
//protected:
	string wortform;
};

class Verb: public Wort
{
public:
	void merkmaleAuslesen(string &eingabe);
	Verb(){typ='v';}
	~Verb(){}
	void ausgabe();
private:
	int numerus;
	int person;

	int argumente;
	int argument1;
	int argument2;

};

class Nomen: public Wort
{
public:
	void merkmaleAuslesen(string &eingabe);
	Nomen(){typ='n';}
	~Nomen(){}
	void ausgabe();

    int numerus;
	int kasus;
    int genus;
private:
	
};

class Determinierer: public Wort
{
public:
	void merkmaleAuslesen(string &eingabe);
	Determinierer(){typ='d';}
	~Determinierer(){}
	void ausgabe();
    int numerus;
	int kasus;
	int genus;

private:
	
	
};

