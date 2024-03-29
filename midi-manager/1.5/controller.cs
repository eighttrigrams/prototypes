
using System;
using System.Collections.Generic;
using System.Text;

namespace midiManager
{
	// by controller
	// delegiert die einkommenden midi daten
	// an virtuelle behringer-instanzen
	class Controller
	{

		private List<BCR2000> bcrList = new List<BCR2000>();

		// konstruktor
		public Controller(
				MIDIOXLib.MoxScriptClass m,
				Device h)
		{
			mox = m;
			host = h;
		}

		public void addController(string dateiName)
		{
			BCR2000 bcr = new BCR2000(
					new Device(mox,0),
					host);
			ConfigControl.BcrConfig(bcr,dateiName);
			bcrList.Add(bcr);
		}

		private MIDIOXLib.MoxScriptClass mox;
		private Device host;

		// sendefx steuerung hilfsvariablen
		int sendIsActiveLinks=0;
		int sendIsActiveRechts=0;
		int activeSend=0;


		// eingehende midi daten verarbeiten
		public void delegat(
				int source,
				int chan,
				int midiCC,
				int valueOf)
		{
			// kanalwechsel ///////////////////////////////////////// 
			if (chan==191)
			{
			 	// toggle offs nicht beachten
				if (valueOf==0) return;    
				
				// sends kanal aktiviert
				// alle bcrs durchsuchen
				for (int i=0;i<bcrList.Count;i++)
				{
					if (midiCC==bcrList[i].toggleChangeMixer)
					{
						bcrList[i].changeMixer();
						return;
					}
					if (midiCC==bcrList[i].toggleFullMixer)
					{
						if (bcrList[i].sendIsActive==1) 
							activeSend=(activeSend+1)%
								bcrList[i].sendsCrossAmt;

						bcrList[i].writeFullMixer();
						bcrList[
							bcrList[i].sendsCrossRef].changeChannel(
										bcrList[i].sendsCrossTri+activeSend);
						return;
					}
					if (bcrList[i].inputDeviceID==source)
					if (midiCC<56)
						midiCC=bcrList[i].transform(midiCC);
				}
				
				
				for (int i=0;i<bcrList.Count;i++)
				{
				
					bcrList[i].changeChannel(midiCC);
				}
				return;
				
			}



			// normale controller daten //////////////////////////////
			int kommtVonEinemGeraet=-1;
			for (int i=0;i<bcrList.Count;i++)
			{
				if (source == bcrList[i].inputDeviceID)
				{
					// jetzt wird �berpr�ft, ob die daten von einem validen channel,
					// also entweder aus einer mixer oder einer ins section kommen,
					// andere channels k�nnen dann etwa zur instrumentensteuerung 
					// reserviert sein

					if (chan==bcrList[i].mixSectionCh)
					{
						kommtVonEinemGeraet=1;
						bcrList[i].changeValueMixer(midiCC,valueOf);
					}	
					if (chan==bcrList[i].insSectionCh)
					{
						bcrList[i].changeValue(midiCC,valueOf);
						kommtVonEinemGeraet=1;
					}
				}
				
			}
			
			if (kommtVonEinemGeraet==-1)  // kommt also vom host
			{
				for (int i=0;i<bcrList.Count;i++)
				{
					//TODO �berpr�fung �ber host source
					
					//if (chan>=bcrList[i].mixSectionCh)
						bcrList[i].setValueMixer(chan,midiCC,valueOf);
					//else
						bcrList[i].setValue(chan,midiCC,valueOf);
				}	
			}


		}

	}
}
