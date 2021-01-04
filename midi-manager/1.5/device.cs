using System;
using System.Collections;
using System.Text;

namespace midiManager
{
	class Device
	{
		private MIDIOXLib.MoxScriptClass mox;
		public int deviceID;

		public Device(MIDIOXLib.MoxScriptClass m,int d)
		{
			mox=m;
			deviceID=d;
		}
		
		public void toggleOff(
				int channel, 
				int midiCC)
		{
			mox.OutputMidiMsg(deviceID, channel, midiCC, 127);
			mox.OutputMidiMsg(deviceID, channel, midiCC, 0);
		}


		public void fire(
			   int channel,
		   	   int midiCC,
			   int valueOf)
	 	{
			mox.OutputMidiMsg(deviceID,channel,midiCC,valueOf);
		}		

	}

}
