
using System;
using System.Collections.Generic;
using System.Text;

namespace midiManager
{
	// by controller
	// delegiert die einkommenden midi daten
	// an virtuelle behringer-instanzen
	class MidiCentral
	{

		private List<MidiProtocolDevice> bcrList = new List<MidiProtocolDevice>();

		// konstruktor
		public MidiCentral(
				MIDIOXLib.MoxScriptClass m,
				Device h)
		{
			mox = m;
			host = h;
		}

		public void addController(string dateiName)
		{
			MidiProtocolDevice bcr = new MidiProtocolDevice(
					new Device(mox,0),
					host);
			ConfigControl.BcrConfig(bcr,dateiName);
			bcrList.Add(bcr);
		}

		private MIDIOXLib.MoxScriptClass mox;
		private Device host;

		// sendefx steuerung hilfsvariablen
		private int sendIsActiveLinks=0;
		private int sendIsActiveRechts=0;
		private int activeSend=0;


		public void processHost(
				int byte1,
				int byte2,
				int byte3)
		{
			for (int i=0;i<bcrList.Count;i++)
			{			
				bcrList[i].setValueMixer(byte1,byte2,byte3);
				bcrList[i].setValue(byte1,byte2,byte3);
			}	
		}


		public int processChannel(
				int source,
				int byte1,
				int byte2,
				int byte3)
		{
			if (byte1!=191) return -1;
			if (byte3==0)  return -1;
			
			// sends kanal aktiviert
			// alle bcrs durchsuchen
			for (int i=0;i<bcrList.Count;i++)
			{
				// eventuell den mixer wechseln
				if (bcrList[i].changeMixer(byte2)==1) return bcrList[i].currentMixer;
					
				if (byte2==bcrList[i].toggleFullMixer)
				{
					if (bcrList[i].sendIsActive==1) 
						activeSend=(activeSend+1)%
							bcrList[i].sendsCrossAmt;

					bcrList[i].writeFullMixer();
					bcrList[
						bcrList[i].sendsCrossRef].changeChannel(
									bcrList[i].sendsCrossTri+activeSend);
					return -1;
				}
				if (bcrList[i].inputDeviceID==source)
				if (byte2<56)
					byte2=bcrList[i].transform(byte2);
			}
			for (int i=0;i<bcrList.Count;i++)
				bcrList[i].changeChannel(byte2);
			return -1;
		}


		// eingehende midi daten verarbeiten
		public void processMidi(
				int source,
				int byte1,
				int byte2,
				int byte3)
		{
			for (int i=0;i<bcrList.Count;i++)
			{
				if (source == bcrList[i].inputDeviceID)
				{
					// jetzt wird überprüft, ob die daten von einem validen channel,
					// also entweder aus einer mixer oder einer ins section kommen,
					// andere channels können dann etwa zur instrumentensteuerung 
					// reserviert sein

					if (byte1==bcrList[i].mixSectionCh)
						bcrList[i].changeValueMixer(byte2,byte3);
					if (byte1==bcrList[i].insSectionCh)
						bcrList[i].changeValue(byte2,byte3);
				}	
			}
		}
	}
}
