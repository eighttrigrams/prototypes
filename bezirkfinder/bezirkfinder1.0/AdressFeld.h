#pragma once

struct Eintrag{
	CString strasse;
	CString plz;
	CString hnvon;
	CString hnbis;
	CString modus;
	CString bezirk;
};

class AdressFeld
{
public:
    int groesse;
    Eintrag *eintrag;
	AdressFeld(void);
	~AdressFeld(void){ delete eintrag;}
private:
	CStdioFile datei;
};

struct Zuordnung{
	CString bezirk;
	CString welle;
	CString modul;
};

class BezirkFeld
{
public:
	BezirkFeld(void);
	~BezirkFeld(void){ delete zuordnung;}
	int anzahl(){return groesse;}
	CString get_bezirk(int i){return zuordnung[i].bezirk;}
	CString get_modul(int i){return zuordnung[i].modul;}
	CString get_welle(int i){return zuordnung[i].welle;}
private:
	int groesse;
	CStdioFile datei;
	int i;
    Zuordnung *zuordnung;
};
