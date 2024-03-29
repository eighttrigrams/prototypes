using System;
using System.Collections;
using System.IO;
using System.Text;
using System.ComponentModel;

namespace midiManager
{

//class Mi{
//	public int[] notes        = new int[17] { 0, 144, 145, 146, 147, 
//			                                  148, 149, 150, 151, 
//											  152, 153, 154, 155, 
//											  156, 157, 158, 159 };
//	public int NK(int i){
//		return (int)notes[i];}
//}


class Program
{static void Main(string[] args)
{
	Midi myMidi = new Midi();
	myMidi.start();
}}

enum KANAL { C9 = 184, C10, C11, C12, C13, C14, C15, C16 }

class  Midi
{


// midi erstes datenbyte

public int   midiBankwechsel      = 192;  







// devices und steuerungsvariablen
private     MIDIOXLib.MoxScriptClass mox = new MIDIOXLib.MoxScriptClass();
private     CGuru gu;
private     CGuru guruA;
private     CGuru guruB;
public int  liDEV;
private     DEV leDEV;
private     DEV riDev;

public int  A_1_2 = 0;                     
public int  B_1_2 = 0;                   
public int  A_1_port = 0;
public int  B_1_port = 0;
private int lastOne = 0;
private int activeSendEfx = 0;
private int sendIsActiveLinks = 0;
private int sendIsActiveRechts = 0;
public int  recInstrument = -1;       // traeger des record focus     
public int  shiftPads = 0;            //alternative pad funktion auswahl;  
public int  shiftScenes = 0;          //alternative scenes anwahl

// KANAL 16 - CTRL befehle _________________________________________

public int[] CTRLswitchINS            = new int[4] { 96, 97, 98, 99 }; 
public int[] CTRLswitchEFX            = new int[4]{ 104, 105, 106, 107 };    
public int   CTRLswitchSENDLinks      = 100;                   
public int   CTRLswitchSENDRechts     = 108;                  
public int   CTRLswitchBank           = 109;                      
public int[] CTRLswitchEFXout         = new int[4] { 7, 15, 7, 116 };  
public int[] CTRLswitchSENDs          = new int[4] { 112, 113, 114, 115 }; 
public int[] CTRLswitchRecord         = new int[4] { 119, 120, 121, 121 };           
public int   CTRLswitchMASTER         = 116;     






public void toggleOFF(int device, int chan, int data)
{
	mox.OutputMidiMsg(device, chan, data, 127);
	mox.OutputMidiMsg(device, chan, data, 0);
}



public int get(StreamReader re)
{
	return Convert.ToInt16(re.ReadLine().Split(':')[1]);
}

public void readConfigFile()
{
	try
	{
		StreamReader re = File.OpenText(".\\devices.txt");
		leDEV.init(           get(re)); 
		riDev.init(           get(re) );
		liDEV               = get(re);
		CTRLswitchEFXout[0] = get(re);
		CTRLswitchEFXout[1] = get(re);
		CTRLswitchEFXout[2] = get(re);
		CTRLswitchEFXout[3] = get(re);
		A_1_port            = get(re);
		B_1_port            = get(re);
		re.Close();	
		Console.WriteLine("gelesen");
	}
	catch (Exception exc)
	{   
		Console.WriteLine("fehler");
	}
}



public void start()
{
	mox.MidiInput += new MIDIOXLib._IMoxScriptEvents_MidiInputEventHandler(this.midiIn);
	leDEV = new DEV(mox);
	riDev = new DEV(mox);
	readConfigFile();
	guruA = new CGuru(mox, B_1_port);
	guruB = new CGuru(mox, B_1_port+1);
	gu = guruA;
	
	
	
	leDEV.addPAGE(24, (int)KANAL.C11, 0);    // INSTRUMENT1
	leDEV.addPAGE(25, (int)KANAL.C11, 32);   // INSTRUMENT2
	leDEV.addPAGE(26, (int)KANAL.C11, 64);   // INSTRUMENT3
	leDEV.addPAGE(27, (int)KANAL.C11, 96);   // INSTRUMENT4
	leDEV.addPAGE(28, (int)KANAL.C9, 0);     // SENDEFX1
	leDEV.addPAGE(29, (int)KANAL.C9, 32);    // SENDEFX2
	leDEV.addPAGE(30, (int)KANAL.C9, 64);    // SENDEFX3
	leDEV.addPAGE(31, (int)KANAL.C13, 32);   // SENDS MIXER1
	leDEV.addMIXER(   (int)KANAL.C13);       //   
 	riDev.addPAGE(0,  (int)KANAL.C10, 0);    // EFX1
	riDev.addPAGE(1,  (int)KANAL.C10, 32);   // EFX2
	riDev.addPAGE(2,  (int)KANAL.C10, 64);   // EFX3
	riDev.addPAGE(3,  (int)KANAL.C10, 96);   // EFX4
	riDev.addPAGE(4,  (int)KANAL.C14, 32);   // SENDS MIXER2
	riDev.addPAGE(4,  (int)KANAL.C15, 32);   // SENDS MIXER3
	riDev.addPAGE(5,  (int)KANAL.C9, 0);     // SENDEFX1
	riDev.addPAGE(6,  (int)KANAL.C9, 32);    // SENDEFX2
	riDev.addPAGE(7,  (int)KANAL.C9, 64);    // SENDEFX3
	riDev.addMIXER(   (int)KANAL.C14);
	riDev.addMIXER(   (int)KANAL.C15);

	//main----------------
	mox.FireMidiInput=1;
	mox.DivertMidiInput=0;
	Console.ReadLine();
	mox.FireMidiInput=0;
	//main----------------
}


private void writeRecordFocus(int number)
{
	if (number != recInstrument) 
	{
		if (number != -1) toggleOFF(liDEV, (int)KANAL.C16, CTRLswitchRecord[0] + recInstrument);
			recInstrument = number; 
		toggleOFF(liDEV,(int)KANAL.C16,CTRLswitchRecord[0]+ recInstrument);                 
	}   
}


private void midiIn(int ts, int port, int chan, int dat, int wert)
{
	switch (chan)
	{
	case 152:
		// ein ZOOM-Rythm-track drumcomputer wird als steuerung der
		// guru-maschinen missbraucht. dieser besitzt eine 1 oktaven
		// tastatur. auf seinem bass kanal sendet er mit 152 auf kanal
		// 9 seine noten, 152 sind somit die note-on events, 136 die
		// note offs. nun wird hier die zuweisung der notentasten
		// an bestimmte funktionen der guru maschinen gebunden, die
		// implementation obliegt der guru-klasse selbst.
		//
		if ((dat >=12) && (dat<=19))	gu.selectPattern(dat-12);
		if ((dat >=24) && (dat<=31))    gu.selectPattern(dat-24);

		//if ((dat >=20) && (dat<=23))    gu.selectEngine(dat-20);
		//if ((dat >=32) && (dat<=35))    gu.selectEngine(dat-32);
		//if (dat == 24) gu.patternView();  
		//if (dat == 26) gu.graphEditView(); 
		//if (dat == 28) gu.padEditView(); 
		//if (dat == 29) gu.scenesView(); 
		
		if (dat == 22) gu.undoCommand();
		if (dat == 23) gu.commitCommand(); 
		if (dat == 21) gu.recordCommand(); 
	
		if (dat == 32) gu.browserUp();
		if (dat == 33) gu.browserDown();
		if (dat == 34) gu.browserCancel();
		if (dat == 35) gu.browserCommit();

	break;
	case 144:
		//note vom keyboard f�r jeweils einen der beiden synthies
		mox.OutputMidiMsg(A_1_port + A_1_2, 144, dat, wert);
	break;
	
	case 159: 
		// der akai drumpad-controller wird sowohl zur steuerung
		// als auch zum triggern von drumpads und scenes benutzt
		// 
		//
		if (dat < 84)
			mox.OutputMidiMsg(B_1_port + B_1_2, 154, dat, wert);
		// if (shiftPads == 1) toggleOFF(B_1_port + B_1_2, midiNotes[12], dat - 36 + 60);     
		if ((dat >= 60) && (dat <=63 ))
				toggleOFF(B_1_port + B_1_2, 144, dat);
		if ((dat >= 72) && (dat <=75 ))
				toggleOFF(B_1_port + B_1_2, 144, dat);
		if ((dat >= 84) && (dat <=87 )) 
				gu.selectEngine(dat-84);
		if (dat == 96) 
				gu.patternView();
		if (dat == 97)
				gu.graphEditView();
		if (dat == 98)
				gu.padEditView();
		if (dat == 99)
				gu.scenesView();
	break;
	
	case 175: 
		//kommt von der akai - aftertouch
		//mox.OutputMidiMsg(liDEV, 4 + A_1_2, midiNotes[11], wert);
	break;
	
	case (int)KANAL.C9: 
		leDEV.setPAGEParam(4 + (dat / 32), dat, wert);
		riDev.setPAGEParam(6 + (dat / 32), dat, wert);
	break;
	
	case (int)KANAL.C10: 
		riDev.setPAGEParam(0+(dat/32),dat,wert); 
	break;
	
	case (int)KANAL.C11: 
		leDEV.setPAGEParam(0+(dat/32),dat,wert); 
	break;
	
	case (int)KANAL.C13: 
		
		//mixer bank1
		if (dat < 32) leDEV.setMIXERParam(0, dat, wert);
			else      leDEV.setPAGEParam(7, dat, wert); 
	break; 
	
	case (int)KANAL.C14: 
		//mixer bank2
		if (dat<32) riDev.setMIXERParam(0, dat, wert); 
			else    riDev.setPAGEParam(4,dat,wert);
	break; 
	
	case (int)KANAL.C15: 
		//mixer bank3
		if (dat<32) riDev.setMIXERParam(1, dat, wert); 
			else    riDev.setPAGEParam(5,dat,wert);
	break;
	
	case 191: 
		//midiKanal 16
		if (wert == 0)//nur auf die off events vom toggleOff reagieren
		{
			if (dat == CTRLswitchBank)
			{
				riDev.switchMIXERBank();
				riDev.callPAGE(lastOne);
			}
			if (dat < 24) 
			{
				leDEV.callMIXER(dat);
				sendIsActiveLinks=0;
				sendIsActiveRechts=0;
			}
			if (dat == CTRLswitchSENDLinks) 
			{
				if (sendIsActiveLinks == 1) 
				{
					activeSendEfx = (activeSendEfx + 1) % 3;
				}
				sendIsActiveLinks=1;
				sendIsActiveRechts=0;
				leDEV.callPAGE(7);
				lastOne = 6 + activeSendEfx;
				riDev.callPAGE(lastOne);
				toggleOFF(liDEV,(int)KANAL.C16,CTRLswitchSENDs[0] + activeSendEfx);
			}
			if (dat == CTRLswitchSENDRechts)
			{
				if (sendIsActiveRechts == 1) 
				{
					activeSendEfx = (activeSendEfx + 1) % 3;
				}
				sendIsActiveLinks=0;
				sendIsActiveRechts=1;
				leDEV.callPAGE(4 + activeSendEfx);
				lastOne = 4 + riDev.MIXERBank;
				riDev.callPAGE(lastOne);

				toggleOFF(liDEV, (int)KANAL.C16, CTRLswitchSENDs[0] + activeSendEfx);
			}		
			if ((dat >= CTRLswitchINS[0]) && (dat <= CTRLswitchINS[3])) 
			{	
				// die Kn�pfe A1-B4 der linken behringer sind dazu gedacht,
				// die instrument pages 25-28 auf der linken seite anzuspringen,
				// sowie die imstrumente im host aufzurufem.
				
				int i=dat - CTRLswitchINS[0];   
				writeRecordFocus(i);
				leDEV.callPAGE(i);
				sendIsActiveLinks=0;
				sendIsActiveRechts=0;

				// sobald ein geraet ausgew�hlt wird muss f�r die instrumenten
				// gruppe A und B der jeweils jetzt aktuelle midiYoke port f�r
				// kommunikation bereitstehen, bei den gurus muss sicher
				// gestellt sein das auf der jeweils richtigen maschine 
				// gearbeitet wird

				if (dat == CTRLswitchINS[0])   A_1_2 = 0;      
				if (dat == CTRLswitchINS[1])   A_1_2 = 1;
				if (dat == CTRLswitchINS[2]) { B_1_2 = 0;   gu = guruA; }
				if (dat == CTRLswitchINS[3]) { B_1_2 = 1;   gu = guruB; }}

				
			if ((dat >= CTRLswitchEFX[0]) && (dat <= CTRLswitchEFX[3])){ 
				lastOne = dat - CTRLswitchEFX[0];
				riDev.callPAGE(lastOne);
				sendIsActiveLinks=0;
				sendIsActiveRechts=0;

				if (CTRLswitchEFXout[lastOne] != -1) 
				toggleOFF(liDEV, (int)KANAL.C16, CTRLswitchEFXout[lastOne]);
			}
		}
	break;
	default:
	break;
}}}}
