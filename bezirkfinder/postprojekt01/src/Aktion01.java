
import java.awt.*;
import java.awt.event.*;
import java.applet.*;


public class Aktion01 extends Applet
                      implements KeyListener
{	
	Adressen dat=new Adressen();
	
	Panel statusfeld=new Panel();
	Panel eingabe=new Panel();
	Panel ausgabe=new Panel();

	TextField plz =new TextField(5);
	TextField strasse =new TextField(40);
	TextField hnr =new TextField(4);
	
	TextField modul=new TextField(1);
	TextField welle=new TextField(1);
	
	TextField messages=new TextField(60);
	
	char c;
    String vergleichStrasse;
    String vergleichPlz;
    String vergleichHnr;
    int    vHnr;
   
    int indexPlz;   
    int indexStrasse;//die ebene, auf der wir uns bei vergleichStrasse bewegen werden
    int indexHnr;
	
    int plzInsgesamt=0;
    int hnrInsgesamt=0;
    
    int verschiedenePlz=0;
    
    
	public void init ()
	{
		
		setSize(600,400);
		
		statusfeld.setBounds(0,0,getWidth(),200);
		eingabe.setBounds(0,160,getWidth(),200);
		ausgabe.setBounds(0,240,getWidth(),200);
		
		add(statusfeld);
		add(eingabe);
		add(ausgabe);
		
		statusfeld.add(messages);
		messages.setEditable(false);
		messages.setText("eingabe erwartet...");
		
		plz.addKeyListener(this);
		strasse.addKeyListener(this);
		hnr.addKeyListener(this);
		
		eingabe.add(plz);
		eingabe.add(strasse);
		eingabe.add(hnr);
		
		ausgabe.add(welle);
		ausgabe.add(modul);
		welle.setEditable(false);
		modul.setEditable(false);
	
		resetEingaben();
			
	}
	
	public void keyPressed(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){
		
		c=e.getKeyChar();	
		if (c=='\u0008'){         
//      -----------------------back-------------------------------------	    
//			-----------------------PLZ back---------------------------------
			if (e.getSource()==plz){
				e.setKeyChar('\u0008');
				if (vergleichPlz.length()>0)
			    {   indexPlz-=1;
			        vergleichPlz=vergleichPlz.substring(0,vergleichPlz.length()-1);
				    messages.setText(vergleichPlz);
				    plzInsgesamt=weitereBearbeitungPlz(dat.schraenkeEinPlz(indexStrasse,vergleichPlz));
				    modul.setText("");
					welle.setText("");
				    if (plzInsgesamt==1){
					    messages.setText("DatenSatz eindeutig bestimmt, viel Spass!");
					    modul.setText(""+dat.addList[dat.zielePlz[0]].modul);
					    welle.setText(""+dat.addList[dat.zielePlz[0]].welle);
					    plzInsgesamt=0;  //hat seinen zweck erfüllt
				    }
				    if ((dat.allesX())&&verschiedenePlz==1&&plzInsgesamt!=0){//jetzt muss aber noch der index gefunden werden
						  int k; 
						  for (k=0;k<dat.anzahlZieleHnr;k++)
							{ 
							  if (dat.addList[dat.zielePlz[0]].plz==dat.addList[dat.zieleHnr[k]].plz) //nur die passenden vergl 
								if ((dat.addList[dat.zieleHnr[k]].hnrModus=="x")
									|| (dat.addList[dat.zieleHnr[k]].hnrModus=="-"))break;
							}			  
						    messages.setText("DatenSatz wurde eindeutig bestimmt  cpcpcp!");
							modul.setText(""+dat.addList[dat.zieleHnr[k]].modul);
							welle.setText(""+dat.addList[dat.zieleHnr[k]].welle);
							plzInsgesamt=0;  //hat seinen zweck erfüllt
						    }
			    }
			}  
//          -----------------------STRASSE back---------------------------------
			if (e.getSource()==strasse){
				e.setKeyChar('\u0008');
				if (vergleichStrasse.length()>0){
			        indexStrasse-=1;
					vergleichStrasse=vergleichStrasse.substring(0,vergleichStrasse.length()-1);
					messages.setText(vergleichStrasse);
					//wichtig das es 3 und nicht nur 2 fälle gibt, keine ahnung mehr warum					
					if (indexStrasse==0) //falls nicht mehr eingegeben ist
					{
					messages.setText("eingabe erwartet...");
					System.out.println("keine besonderen Ergebnisse!");
					System.out.println("-----------------");
					}
					if (indexStrasse==1) //wenn nur ein buchstabe eingegeben ist
					{
					dat.schraenkeEinStrasse(vergleichStrasse.charAt(0));
					dat.zeigeZieleStrasse(1);
					}
					if (indexStrasse>1)  //für mehr als einen buchstaben
					{
					dat.schraenkeEinStrasse(indexStrasse,vergleichStrasse.charAt(indexStrasse-1));
					dat.zeigeZieleStrasse(indexStrasse);
					}	
					weitereBearbeitung(dat.strasseGefunden(indexStrasse));
					plz.setEditable(false);
					if ((dat.alles(indexStrasse))&&(dat.anzahlZieleStrasse[indexStrasse]!=1)){//mehr als eine, aber alles gl typ
					      messages.setText("nur noch plz bitte!");
						  hnr.setEditable(false);
						  plz.setEditable(true);
					}	
			    }
			}
//          -----------------------HNR back---------------------------------			
			if (e.getSource()==hnr){
				e.setKeyChar('\u0008');
				if (vergleichHnr.length()>0)
			    {   indexHnr-=1;
			    plz.setEditable(false); resetPlz();
			    vergleichHnr=vergleichHnr.substring(0,vergleichHnr.length()-1);
			    try{vHnr=Integer.parseInt(vergleichHnr);} catch(NumberFormatException n){vHnr=0;}//wichtig zurücksetzen
				messages.setText(""+vHnr);
				hnrInsgesamt=weitereBearbeitungHnr(dat.schraenkeEinHnr(indexStrasse,vHnr));	
				if (dat.allesX()) plz.setEditable(true);
				if ((hnrInsgesamt>1)&&(plzInsgesamt==1)){
					messages.setText("DatenSatz wurde eindeutig bestimmt!");
					modul.setText(""+dat.addList[dat.zieleHnr[0]].modul);
					welle.setText(""+dat.addList[dat.zieleHnr[0]].welle);
				}
			    }
			}   
		}					   
//      -----------------------back-------------------------------------
		else if (c=='\u001B')  {e.setKeyChar('\u0008'); resetEingaben();} //escape gedrückt
//      -----------------------neuer Buchstabe--------------------------		
		else{                     
			messages.setText("lese von der Tastatur...");
//      -----------------------PLZ neues c---------------------------------
		if (e.getSource()== plz){
			if ((!(c>=48 && c<=57))||(indexPlz==5)){e.setKeyChar('\u0008'); resetPlz(); }
//	        ----out of bounds oder keine zahl eingegeben-------------------------------			
			else{ 
			  indexPlz++;
		      vergleichPlz=vergleichPlz+c;
			  messages.setText(vergleichPlz);
			  plzInsgesamt=weitereBearbeitungPlz(dat.schraenkeEinPlz(indexStrasse,vergleichPlz));
			  modul.setText("");
			  welle.setText("");
			  if (plzInsgesamt==1){
					messages.setText("DatenSatz wurde eindeutig bestimmt!");
					modul.setText(""+dat.addList[dat.zielePlz[0]].modul);
					welle.setText(""+dat.addList[dat.zielePlz[0]].welle);
					plzInsgesamt=0;   //hat seinen zweck erfüllt
				    }
			  if ((dat.allesX())&&verschiedenePlz==1&&plzInsgesamt!=0){//jetzt muss aber noch der index gefunden werden
				  int k; 
				  for (k=0;k<dat.anzahlZieleHnr;k++)
					{   
					    if (dat.addList[dat.zielePlz[0]].plz==dat.addList[dat.zieleHnr[k]].plz) //nur die passenden vergl 
						if ((dat.addList[dat.zieleHnr[k]].hnrModus=="x")
							|| (dat.addList[dat.zieleHnr[k]].hnrModus=="-"))break;
					}			  
				    messages.setText("DatenSatz wurde eindeutig bestimmt  cpcpcp!");
					modul.setText(""+dat.addList[dat.zieleHnr[k]].modul);
					welle.setText(""+dat.addList[dat.zieleHnr[k]].welle);
					plzInsgesamt=0;  //hat seinen zweck erfüllt
				    }
			}						
		}
//      -----------------------STRASSE neues c---------------------------------
		if (e.getSource()== strasse){
			if (indexStrasse==39) {e.setKeyChar('\u0008'); resetEingaben();} //out of bounds
			else{
			  vergleichStrasse=vergleichStrasse+c;
			  indexStrasse++;
			  messages.setText(vergleichStrasse);		  
			  if (indexStrasse==1)
		   	  {
			  dat.schraenkeEinStrasse(c);
			  dat.zeigeZieleStrasse(indexStrasse);
			  }
			  else
			  {
			  dat.schraenkeEinStrasse(indexStrasse,c);
			  dat.zeigeZieleStrasse(indexStrasse);
			  }	
			  weitereBearbeitung(dat.strasseGefunden(indexStrasse));
			  plz.setEditable(false);
			  if ((dat.alles(indexStrasse))&&(dat.anzahlZieleStrasse[indexStrasse]!=1)){//mehr als eine, aber alles gl typ
					messages.setText("strasse gefunden....nur noch plz bitte!");
					hnr.setEditable(false);
					plz.setEditable(true);
			  }
			}
		}
//      -----------------------HNR neues c---------------------------------
		if (e.getSource()== hnr){
			if ((!(c>=48 && c<=57))||(indexHnr==4)){e.setKeyChar('\u0008'); resetHnr();resetPlz();plz.setEditable(false);}
//          ----out of bounds oder keine zahl eingegeben-------------------------------			
			else{ 
			  indexHnr++;
			  plz.setEditable(false);resetPlz();
			  vergleichHnr=vergleichHnr+c;
			  try{vHnr=Integer.parseInt(vergleichHnr);}catch(NumberFormatException n){}
			  messages.setText(""+vHnr);
			}		
			hnrInsgesamt=weitereBearbeitungHnr(dat.schraenkeEinHnr(indexStrasse,vHnr));
			if (dat.allesX()) plz.setEditable(true);
			if ((hnrInsgesamt>1)&&(plzInsgesamt==1)){
				messages.setText("DatenSatz wurde eindeutig bestimmt!");
				modul.setText(""+dat.addList[dat.zieleHnr[0]].modul);
				welle.setText(""+dat.addList[dat.zieleHnr[0]].welle);
			}
		}
		}
//      -----------------------neuer Buchstabe--------------------------	
	}
	
	int weitereBearbeitungPlz(int entscheidung)
	{   //als argument wird die anzahl verschiedener plzs übergeben!!!
		verschiedenePlz=entscheidung; //und direkt rausgeschickt
		if (entscheidung==1){
			messages.setText("Plz bestimmt. Bitte weiter durch HNR einschränken!");  
		}
		if (entscheidung==0){
			messages.setText("PLZ/HNR-Kombi bei "+dat.addList[dat.zieleStrasse[indexStrasse][0]].strasse+" nicht vorhanden!");
		}
		if (entscheidung>1){
			messages.setText("geben Sie mehr Ziffern für die PLZ ein! (enter für neu)");
		}
		dat.zeigeZielePlz();
		return dat.anzahlZielePlz; //raus gehen die plzs insgesamt
	}
	
	
	
	int weitereBearbeitungHnr(int entscheidung)
	{  //rein kommt die anzahl der passenden datensaetze
		if (entscheidung==1){
			if (!(verschiedenePlz>1)){
			messages.setText("DatenSatz wurde eindeutig bestimmt!");
			modul.setText(""+dat.addList[dat.zieleHnr[0]].modul);
			welle.setText(""+dat.addList[dat.zieleHnr[0]].welle);
	        }else{
	        	messages.setText("Schränken Sie weiter per PLZ ein!");
	        	//plz.setEditable(true);
	        }
		}
		if (entscheidung==0){
			modul.setText("");
			welle.setText("");
		}
		if (entscheidung>1){
			messages.setText("Schränken Sie weiter per PLZ ein!");
			modul.setText("");
			welle.setText("");
		}
		dat.zeigeZieleHnr();
		return entscheidung; //und geht auch wieder raus
	}
	
	void weitereBearbeitung(boolean b)
	{   //übergeben wird ob die strasse eindeutig bestimmt ist oder nicht
		if (b){
		messages.setText("TAB drücken und HNR eingeben bitte...      gef.: "+
				dat.addList[dat.zieleStrasse[indexStrasse][0]].strasse);
		plz.setEditable(true);resetPlz();
		hnr.setEditable(true);resetHnr();
		if (dat.anzahlZieleStrasse[indexStrasse]==1)
		{
			messages.setText("DatenSatz wurde eindeutig bestimmt!");
			welle.setText(""+dat.addList[dat.zieleStrasse[indexStrasse][0]].welle);
			modul.setText(""+dat.addList[dat.zieleStrasse[indexStrasse][0]].modul);
		}
		}else{
		plz.setEditable(false);resetPlz();
		hnr.setEditable(false);resetHnr();
		}
		
	}

	
	
	void resetHnr()
	{
		modul.setText("");
		welle.setText("");
		indexHnr=0; 
		vHnr=0; 
		vergleichHnr="";
		hnr.setText("");
	}
	void resetPlz()
	{   
		modul.setText("");
		welle.setText("");
		indexPlz=0;  
		vergleichPlz="";
		plz.setText("");
	}
	
	void resetEingaben()
	{
	  plz.setEditable(false);
	  hnr.setEditable(false);
	  vergleichPlz="";      indexPlz=0;
	  vergleichStrasse="";  indexStrasse=0;
	  vergleichHnr="";      indexHnr=0;
	  vHnr=0;
	  modul.setText("");
	  welle.setText("");
	  strasse.setText("");
	  hnr.setText("");
	  plz.setText("");
	  messages.setText("eingabe erwartet...");
	}
}
