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

void NP::ausgabe(){
  char* numerusChar[3]={"leer","sg","pl"};
  char* kasusChar[5]={"kein Kasus","NOM","GEN","DAT","AKK"};
  char* genusChar[3]={"neutrum","maskulin","feminin"};
  

  cout<<"Moegliche Eigenschaften:"<<endl;
	  for (unsigned int i=0;i<eigenschaft.size();i++){
          cout<<setw(5)<<left<<numerusChar[eigenschaft[i].numerus];
          cout<<setw(5)<<left<<kasusChar[eigenschaft[i].kasus];
          cout<<setw(8)<<left<<genusChar[eigenschaft[i].genus]<<endl;
	  }
}