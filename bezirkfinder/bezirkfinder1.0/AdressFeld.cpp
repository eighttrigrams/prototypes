#include "StdAfx.h"
#include ".\adressfeld.h"


BezirkFeld::BezirkFeld(void){
   CString t;
   groesse=0;
   if (datei.Open("d:\\_desktop\\bezirke.txt",CFile::modeRead)){
	   t="";
	   while (datei.ReadString(t)>0) groesse++;
	   groesse=groesse/3;
	   zuordnung=new Zuordnung[groesse];
	   datei.SeekToBegin();
	   for (int i=0;i<groesse;i++){
		   datei.ReadString(t); zuordnung[i].bezirk=t;
		   datei.ReadString(t); zuordnung[i].welle=t;
		   datei.ReadString(t); zuordnung[i].modul=t;
	   }
   }datei.Close();
}

AdressFeld::AdressFeld(void)
{   CString t;
	groesse=0;   
	if (datei.Open("d:\\_desktop\\adressenliste2.txt",CFile::modeRead)){
		t="";
		while (datei.ReadString(t)>0) groesse++;
		groesse=groesse/6;
        eintrag=new Eintrag[groesse];
        datei.SeekToBegin();
		for (int i=0;i<groesse;i++){
            datei.ReadString(t); eintrag[i].strasse=t;
		    datei.ReadString(t); eintrag[i].plz=t;
			datei.ReadString(t); eintrag[i].hnvon=t;
			datei.ReadString(t); eintrag[i].hnbis=t;
			datei.ReadString(t); eintrag[i].modus=t;
			datei.ReadString(t); eintrag[i].bezirk=t;
		}
	}datei.Close();
}

