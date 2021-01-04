using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace midiManager
{
	class MidiProtocolDevice
	{
		public int insSectionCh;
		public int mixSectionCh;
		public int sendsCrossRef;
		public int sendsCrossAmt;
		public int sendsCrossTri;
		public int sendIsActive = -1;
		public int inputDeviceID;
		// konstruktor
		public MidiProtocolDevice(Device d,Device h)
		{
			dev = d;
			host = h;
		}

		// aktuell angewaehlter kanal
		private int currentPage = -1;
		public int currentMixer = 0;	
		private int ID;
		
		public int toggleFullMixer;
		private int toggleMixerChange;
		private int toggleMixerFashion;
		private Device dev;
		private Device host;

		private List <Page>  pagesList = new List <Page>();
		private List <Mixer> mixerList = new List <Mixer>();

		// todo, transformation der zur channel-anwahl reservierten midi-knöpfe
		// auf kanal 16, damit jeder mixer auch im host seine entsprechenden 
		// channel-strips anwählen kann
		public int transform(int v)
		{
			return mixerList[currentMixer].activationAddition+v; 
		}


		// wenn es einen channel gibt der
		// das einkommende midiCC trifft
		// wird der aktuelle channel gesetzt
		public void changeChannel(int t)
		{
			int currentPageOld=currentPage;

			// seite suchen
			int found=-1;
			for (int i=0;i<pagesList.Count;i++)
			{
				if (pagesList[i].trigger==t)
				{	
					found=i;
					//sendIsActive=-1;
				}
				
			}
			// TODO wichtig für das return trigger problem
			if (found==-1) return;
			currentPage=found;

			// seite auch in live wechseln
			//if (currentPageOld!=currentPage)
			//{
			// wurde eine seite gefunden, ist die send page
			// auf diesem controller nicht mehr aktiv
			sendIsActive=-1;

			// die page für das entsprechende layout (relative,absolute)
			// auf dem controller setzen
			dev.toggleOff(192,pagesList[currentPage].mode);

			// im host den channel triggern
			host.toggleOff(191,pagesList[currentPage].trigger);
			// und die midiCCs senden
			writeAll();
			//}
		}

		public void writeFullMixer()
		{
			currentPage=-1;
			sendIsActive=1;
			dev.toggleOff(192,0);
			for (int i=0;i<128;i++)
			{
				dev.fire(mixSectionCh,
						i,
						mixerList[currentMixer].values[i]);
			}
		}

		public int changeMixer(int byte2)
		{
			if (toggleMixerFashion==0)
			{
				if (byte2==toggleMixerChange)
				{
					currentMixer = (currentMixer + 1) % mixerList.Count;
				}
				else return 0;
			}
			if (toggleMixerFashion==1)
			{
				if (byte2==toggleMixerChange)
				{
					int newMixer = currentMixer - 1;
					if (newMixer == -1)             return 0;
					currentMixer = newMixer;
				} else
				if (byte2==(toggleMixerChange+1))
				{
					int newMixer = currentMixer + 1;
					if (newMixer > (mixerList.Count-1)) return 0;
					currentMixer = newMixer;
				} else return 0;
			}
			if (toggleMixerFashion==2)
			{
				if ((byte2>=toggleMixerChange)
					&&(byte2<(toggleMixerChange+mixerList.Count)))
						currentMixer = byte2-toggleMixerChange;
				else return 0;
			}
			// bei erfolg ....
			if (currentPage==-1) writeFullMixer();
			else writeAll();
			return 1;
		}

		// schreibt eine absolute seite auf eine geraet
		// schreibt einen mixer auf ein geraet
		private void writeAll()
		{
			//if (pagesList[currentPage].mode==0)
			for (int i=0;i<pagesList[currentPage].numCC;i++)
				dev.fire(
					insSectionCh,
					i,
					pagesList[currentPage].values[i]);
			for (int i=0;i<24;i++)
				dev.fire(
					mixSectionCh,
					i,
					mixerList[currentMixer].values[i]);
		}
		
		// kommt vom geraet selbst
		public void changeValue(int cc,int v)
		{
			host.fire(
				pagesList[currentPage].channel,
				cc+pagesList[currentPage].startCC,
				v);
		}


		// kommt vom host
		public void setValue(int ch,int cc,int val)
		{
			for (int i=0;i<pagesList.Count;i++)
				if (pagesList[i].sort(ch,cc,val))
				{
					if (i==currentPage)	
						dev.fire(insSectionCh,cc-pagesList[i].getStart(),val);
				}

		}

		// kommt vom host
		public void setValueMixer(
				int ch,
				int cc,
				int val)
		{
			for (int i=0;i<mixerList.Count;i++)
				if (mixerList[i].sort(ch,cc,val))
				{
					if (i==currentMixer)
					{
						dev.fire(mixSectionCh,cc,val);
					}
				}
		}

		// kommt vom geraet selbst
		public void changeValueMixer(
				int cc,
				int v)
		{
			host.fire(mixerList[currentMixer].channel,cc,v);
		}


		public void addPage(
				int mode,
				int startCC,
				int numCC,
				int channel,
				int trigger)
		{
			pagesList.Add(new Page(
						mode,
						startCC,
						numCC,
						channel,
						trigger));
		}

		public void addMixer(
				int ch,      // reservierte Midi-Kanal 
				int aA)      // activationAddition für Channel-
			                 // Wechsel-Befehle
		{
			mixerList.Add(new Mixer(
						ch,
						aA));
		}

		// das hier ist einfach dazu da, damit zb per config-file
		// der controller mit daten gefüttert wird
		public void setData(
			int iDI,	int dI,		int I,		
			int tCM,	int tCF,    int mS,	
			int iS,		int sCR,	int sCA,
			int sCT,    int tFM)
		{
			inputDeviceID       = iDI;
			dev.deviceID        = dI;
			ID                  = I;
			toggleMixerChange   = tCM;
			toggleMixerFashion = tCF;
		    mixSectionCh        = mS;
			insSectionCh        = iS;
			sendsCrossRef       = sCR;
			sendsCrossAmt       = sCA;
			sendsCrossTri       = sCT;
			toggleFullMixer     = tFM;
		}

	}
		
}

