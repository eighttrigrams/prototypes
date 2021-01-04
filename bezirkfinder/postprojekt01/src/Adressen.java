import java.io.*;


public class Adressen {
	
	
	String zeile="";
	String zeilenPuffer="";
	
	public DatenSatz addList[]=new DatenSatz[100];
	int anzahlAdressen=0;
	
	
	// arrays für die speicherung der indizes der infrage kommenden ziele
	// jeweils für die einzelnene schritte
	public Integer zieleStrasse[][]=new Integer[40][100]; 
	public Integer zieleHnr[]=new Integer[100]; 
	public Integer zielePlz[]=new Integer[100]; 
	
	Integer anzahlZieleStrasse[]=new Integer[40];
	Integer anzahlZieleHnr;
	Integer anzahlZielePlz;
    
	
	
	
	
	
	Adressen(){
	int aa=0;
	for (aa=0;aa<40;aa++){anzahlZieleStrasse[aa]=0;}
	anzahlZieleHnr=0;
	anzahlZielePlz=0;
	
		
    try{
	BufferedReader bread = new BufferedReader(new FileReader("d:\\table.txt"));
	if ((zeilenPuffer=bread.readLine())==null); //erste zeile abfangen
	
	int a1=0;
	int aussen=0;
	kompletteSchleife:
	for(;;)
	{	
	addList[aussen]=new DatenSatz();
	int innen=0;
	for (;;) 
	{
		if ((zeilenPuffer=bread.readLine())==null) break kompletteSchleife;
		else //----------------------------------------------------------------
		{
		if (innen!=9)
		{	
		  zeile="";
		  zeile=Helferlein.befreieZeile(zeilenPuffer);
		  zeile.toLowerCase();
		  try{a1=Integer.valueOf(zeile).intValue();}
		  catch(NumberFormatException n){a1=0;}
		  switch (innen)
		  {
		  case 1:  addList[aussen].index=a1; break;
		  case 2:  addList[aussen].plz=zeile.intern(); break;
		  case 3:  addList[aussen].strasse=zeile.intern();  break;
		  case 4:  addList[aussen].hnrModus=zeile.intern();  break;
		  case 5:  addList[aussen].hnVon=a1; break;
	      case 6:  addList[aussen].hnBis=a1; break;
		  case 7:  addList[aussen].welle=a1; break;
		  case 8:  addList[aussen].modul=a1; break;
		  
		  }
		} else break; 
	    }    //------------------------------------------------------------------
		innen++;
	}
	aussen++;
	}
	anzahlAdressen=aussen;	
    bread.close();
    }
    catch(FileNotFoundException e){System.out.println("Datei nicht gefunden");}
	catch(IOException b){System.out.println("IO Fehler");}
	zeigeDatenSaetze();
	}
	
	
	void schraenkeEinStrasse(char z)
	{
		anzahlZieleStrasse[1]=0;
		for (int i=0;i<anzahlAdressen;i++)
		{
			if (z==addList[i].strasse.charAt(0))
			{
				zieleStrasse[1][anzahlZieleStrasse[1]]=addList[i].index;
				anzahlZieleStrasse[1]++;
			}
		}
	}
	void schraenkeEinStrasse(int ebene,char z)
	{
		int hilfsIndex=0;
		anzahlZieleStrasse[ebene]=0;
		for (int i=0;i<anzahlZieleStrasse[ebene-1];i++)
		{
			hilfsIndex=zieleStrasse[ebene-1][i];
			if (addList[hilfsIndex].strasse.length()>=ebene)
			if (z==addList[hilfsIndex].strasse.charAt(ebene-1))
			{
				zieleStrasse[ebene][anzahlZieleStrasse[ebene]]=addList[hilfsIndex].index;
				anzahlZieleStrasse[ebene]++;
			}
		}
	}
	
	int schraenkeEinPlz(int ebene,String zahl)
	{   
		anzahlZielePlz=0;
		int istGleich;
		for (int i=0;i<anzahlZieleStrasse[ebene];i++){
			istGleich=1;
			for (int j=(5-zahl.length()),k=0;j<5;j++,k++)
				if (addList[zieleStrasse[ebene][i]].plz.charAt(j)!=zahl.charAt(k)) istGleich=0;
			if (istGleich==1)
			{
				zielePlz[anzahlZielePlz]=zieleStrasse[ebene][i];
				anzahlZielePlz++;
			}		
		}
		
		
		if (anzahlZielePlz==0) return 0;
		if (anzahlZielePlz==1) return 1;
		
		String referenz[]={"","","","","","","","","","","","","",""};
		String test="";
		
		int verschiedenePlz=1;
		referenz[0]=addList[zielePlz[0]].plz;  //eine plz (eigentl zwei) gibt es nach der prüfung ja in jedem falle
		referenz[0]=referenz[0].intern();
		System.out.println("neue plz gefunden: "+referenz[0]);
		
		for (int j=1;j<anzahlZielePlz;j++){	  //nur wenn es mehr als eine gibt gehts weiter
			test=addList[zielePlz[j]].plz; 
			test=test.intern();
			for (int f=0;f<12;f++)
			{
				if (referenz[f].equals(test)) break; //raus ausser inneren wenn vorhanden		
					System.out.println("neue plz gefunden: "+test);
					referenz[f+1]=test;
					verschiedenePlz++;		
			}			
		}
		
		
		
		System.out.println(verschiedenePlz+" verschiedene PLZs gibt es");
		return verschiedenePlz;
	}
	
	
		
	int schraenkeEinHnr(int ebene, int nummer)
	{
		int hilfsIndex=0;
		anzahlZieleHnr=0;
		int weitermachen=0; //klapper alle fälle ab oder stoppt wenn fertig
		for (int i=0;i<anzahlZieleStrasse[ebene];i++)
		{
			hilfsIndex=zieleStrasse[ebene][i];	    
			if ((addList[hilfsIndex].hnrModus.charAt(0)=='e')&&(nummer==addList[hilfsIndex].hnVon)){
			zieleHnr[anzahlZieleHnr]=hilfsIndex;
			anzahlZieleHnr++; 
			}
		}   
		if (anzahlZieleHnr==0) weitermachen=1;
		if (weitermachen==1){
			weitermachen=0;
			for (int i=0;i<anzahlZieleStrasse[ebene];i++)
			{
				hilfsIndex=zieleStrasse[ebene][i];	    
				if ((addList[hilfsIndex].hnrModus.charAt(0)=='u')
						&&(nummer>=addList[hilfsIndex].hnVon)
					    &&(nummer<=addList[hilfsIndex].hnBis)
					    &&(nummer % 2==1)){
				zieleHnr[anzahlZieleHnr]=hilfsIndex;
				anzahlZieleHnr++; 
				}
			}   
		}
		if (anzahlZieleHnr==0) weitermachen=1;
		if (weitermachen==1){
			weitermachen=0;
			for (int i=0;i<anzahlZieleStrasse[ebene];i++)
			{
				hilfsIndex=zieleStrasse[ebene][i];	    
				if ((addList[hilfsIndex].hnrModus.charAt(0)=='g')
						&&(nummer>=addList[hilfsIndex].hnVon)
					    &&(nummer<=addList[hilfsIndex].hnBis)
					    &&(nummer % 2==0)){
				zieleHnr[anzahlZieleHnr]=hilfsIndex;
				anzahlZieleHnr++; 
				}
			}   
		}
		if ((anzahlZieleHnr==0)&&(nummer!=0)) weitermachen=1;
		if (weitermachen==1){
			for (int i=0;i<anzahlZieleStrasse[ebene];i++)
			{
				hilfsIndex=zieleStrasse[ebene][i];	    
				if ((addList[hilfsIndex].hnrModus.charAt(0)=='x')
				||(addList[hilfsIndex].hnrModus.charAt(0)=='-')){
				zieleHnr[anzahlZieleHnr]=hilfsIndex;
				anzahlZieleHnr++; 
				}
			}   
		}
		return anzahlZieleHnr;
	}
	boolean alles(int ebene){
		if (anzahlZieleStrasse[ebene]==0) return false;
		for (int i=0;i<anzahlZieleStrasse[ebene];i++)
			if ((addList[zieleStrasse[ebene][i]].hnrModus.charAt(0)!='-')
				&& (addList[zieleStrasse[ebene][i]].hnrModus.charAt(0)!='x')) return false;
		System.out.println("---allesTrue-----");
		return true;
	}
	
	boolean allesX(){
		if (anzahlZieleHnr==0) return false;
		for (int i=0;i<anzahlZieleHnr;i++)
			if ((addList[zieleHnr[i]].hnrModus.charAt(0)!='x')
				&& (addList[zieleHnr[i]].hnrModus.charAt(0)!='-'))return false;
		System.out.println("---Xtrue-----");
		return true;
	}
	
	
	void zeigeZielePlz()
	{
		for (int i=0;i<anzahlZielePlz;i++)
		{
			System.out.println("gefunden: "+" "+addList[zielePlz[i]].plz+" "+
					zielePlz[i]+" "+addList[zielePlz[i]].strasse+" W:"+
					addList[zielePlz[i]].welle+" M:"+addList[zielePlz[i]].modul);
		}
		System.out.println("-----------------");
	}
	
	void zeigeZieleHnr()
	{
		if (anzahlZieleHnr>0)
		for (int i=0;i<anzahlZieleHnr;i++)
		{    
			 System.out.println("DatenSatz "+(i+1)+" von "+anzahlZieleHnr);
			 System.out.println("plz: "+addList[zieleHnr[i]].plz);
			 System.out.println("Modus: "+addList[zieleHnr[i]].hnrModus);
		     System.out.println("Modul: "+addList[zieleHnr[i]].modul);	
	         System.out.println("Welle: "+addList[zieleHnr[i]].welle);
	         System.out.println("hnVon: "+addList[zieleHnr[i]].hnVon);
	         System.out.println("hnBis: "+addList[zieleHnr[i]].hnBis);
	         System.out.println("Index: "+addList[zieleHnr[i]].index);
	         System.out.println("---------------------------");
		}
	}
	
	void zeigeDatenSaetze(){
		for (int i=0;i<anzahlAdressen;i++)
		{
			System.out.println("Index: "+addList[i].index);
			System.out.println("PLZ: "+addList[i].plz);
			System.out.println("Strasse: "+addList[i].strasse);
			System.out.println("Modus: "+addList[i].hnrModus);
			System.out.println("HausNummer von: "+addList[i].hnVon);
			System.out.println("HausNummer von: "+addList[i].hnBis);
			System.out.println("Welle: "+addList[i].welle);
			System.out.println("Modul: "+addList[i].modul);
			System.out.println("----------------");
		}}
	
	void zeigeZieleStrasse(int ebene)
	{
		if (ebene==0) {System.out.println("trefferliste enthält alle token"); return;}
		for (int i=0;i<anzahlZieleStrasse[ebene];i++)
		{
			System.out.println("gefunden: "+" "+addList[zieleStrasse[ebene][i]].plz+" "+
					zieleStrasse[ebene][i]+" "+addList[zieleStrasse[ebene][i]].strasse+" W:"+
					addList[zieleStrasse[ebene][i]].welle+" M:"+addList[zieleStrasse[ebene][i]].modul);
		}
		System.out.println("-----------------");
	}
	
	boolean strasseGefunden(int ebene)  
	{   boolean ergebnis=true;
		
		{   String referenz="";
		    if (anzahlZieleStrasse[ebene]==0) return false; 
		    //kann ja sein, dass auf der ebene nichts vorhanden ist
		    //dann ist folglich auch die strasse nicht mahr bestimmt
 			for (int j=0;j<anzahlZieleStrasse[ebene];j++)
			{
				if (referenz.equals("")) //wenn noh kein vergleich vorhanden ist
				{
				referenz=addList[zieleStrasse[ebene][j]].strasse;
				referenz=referenz.intern();
				}
				else
				{ //wenn auch nur ein string abweicht ist das ergebnis natürlich false
				if (!(referenz==addList[zieleStrasse[ebene][j]].strasse))
					ergebnis=false;
				}		
			}
			return ergebnis;
		}
	}
	
	
	
}
