using System;
using System.Collections.Generic;
using System.Text;


namespace midiManager
{
	class InstrController
	{
		MIDIOXLib.MoxScriptClass mox;
		private Device host;
		private int shiftButtonNote;
		private int shiftButtonChannel;

		public InstrController(MIDIOXLib.MoxScriptClass m,Device h,int rowsCount)
		{
			mox = m;
			host = h;
			
			rows = new List <List <Instrument>> ();
			actuals = new List<Instrument>();
			
			for (int i=0;i<rowsCount;i++)
			{
				rows.Add(new List<Instrument>());
				actuals.Add(new Instrument(new Device(mox,0),""));  //TODO dummy?
			}			
			
		}

		public void setShift(int c,int n)
		{
			shiftButtonNote=n;
			shiftButtonChannel=c;
		}

		// m*n matrix von instrumenten jede row besteht aus einem set virtueller
		// instrumente, die sich ein set physikalischer controller teilen, die faktisch
		// als ein controller betrachtet werden
		List <List <Instrument>> rows;
		// das jeweils aktuelle instrument einer row findet man dann in
		List <Instrument> actuals;

		int recordFocus = -1;	
		int shiftPressed=  0;

		public void addInstrument(int row,string dateiName)
		{
			Instrument insert = new Instrument(new Device(mox,0),dateiName);
			
			rows[row].Add(insert);
			// mindestens ein instrument ist in der row vorhanden, also 
			// kann hier das erste gesetzt werden
			actuals[row]=rows[row][0];
		}


		
		public void delegat(
			int chan,
			int midiCC,
			int valueOf)
		{
			// rowB
			if (rows.Count==0) return;

			if (shiftPressed==0)
				for (int i=0;i<actuals.Count;i++)
				{
					if (actuals[i]!=null)
					if (chan==actuals[i].notesInputChannel)
					{
						// der seperator-check prüft, ob eine
						// simple note gespielt werden soll
						if (midiCC<actuals[i].notesSeperator) 
						{
							actuals[i].device.fire(
								actuals[i].notesOutputChannel,
								midiCC,
								valueOf);
						}
					}
				}
			
		
			// koordinatorisches
			// recordFocus setzen und aktuell
			// spielbare instrumente wechseln
			if (chan==191)
			{
				for (int j=0;j<rows.Count;j++)
				{
					if (rows[j].Count==0) continue;
					for (int i=0;i<rows[j].Count;i++)
						if (midiCC == rows[j][i].trigger)
						{
							actuals[j] = rows[j][i];
							if (recordFocus!=((10)*j+i))
							{
								recordFocus=10*j+i;
								host.toggleOff(191,rows[j][i].recordButton);
							}
						}
				}
				return; 	
			}
			
			if ((chan==shiftButtonChannel) 
				&& (midiCC==shiftButtonNote)
				&& (valueOf>0))
			{
				//Console.WriteLine("shift pressed");
				shiftPressed=1;
				return;
			}	
			if ((chan==shiftButtonChannel) 
				&& (midiCC==shiftButtonNote)
				&& (valueOf==0))
			{
				//Console.WriteLine("shift released");
				shiftPressed=0;
				return;
			}

			// KommandoHandler der Instrumente aufrufen
			for (int i=0;i<actuals.Count;i++)
				if (actuals[i]!=null)
					if (shiftPressed==1)
					{
						actuals[i].shiftExecute(chan,midiCC,valueOf);
					}
					else
					{
						actuals[i].execute(chan,midiCC,valueOf);
					}
			
		}
	}
}
