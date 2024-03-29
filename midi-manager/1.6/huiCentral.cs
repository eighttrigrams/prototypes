using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace midiManager
{
	class HuiCentral
	{
	
		private MIDIOXLib.MoxScriptClass mox;
		private Device huiHost;
		private int currentMixer = 0;

		public HuiCentral(
			MIDIOXLib.MoxScriptClass m,
			Device d)
		{
			mox=m;
			huiHost=d;
		}

		public void fire(int byte1,int byte2,int byte3)
		{
			if (byte3>0)
				huiHost.toggleOff(byte1,byte2);
		}

		public void changeMixer(int newMixer)
		{
			int run = currentMixer - newMixer;
			if (run == 0) return;
			
			if (run < 0) 
			{
				run=-run;
				for (int i=0;i<run;i++)
				{
					huiHost.toggleOff(144,47);
				}
				currentMixer=newMixer;
			}
			else
			{
				for (int i=0;i<run;i++)
				{
					huiHost.toggleOff(144,46);
				}
				currentMixer=newMixer;
			}
		}
	}
}
