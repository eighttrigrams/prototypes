using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
namespace midiManager 
{
	class Command
	{
		private Device outp;

		private int inCH;
		private int outCH;
		private int inCC;
		private int outCC;

		public Command(int oCH,int oCC,int iCH, int iCC,Device h)
		{
			inCH = iCH;
			inCC = iCC;
			outCH = oCH;
			outCC = oCC;
			outp=h;
		}
		
		// testet ein midi 3-byte, auf entsprechungen, und mappt
		// es gegebenenfalls um
		public void fire(int ch,int cc,int v)
		{
			// wenn es sich um midiCC oder note-on handelt, dann
			// wird einfach gepr�ft, ob ein mapping erfolgen soll
			if ((cc==inCC) && (ch==inCH))
				outp.fire(outCH,outCC,v);
			
			// doppelt gepr�ft wird, wenn es sich um noten
			// werte handelt, denn dann m�ssen auch note-offs 
			// behandelt werden 
			if (inCH<160)
				if ((cc==inCC) && (ch==inCH-16))
					outp.fire(outCH-16,outCC,v);
		}
	}


	class InstrumentProtocolDevice
	{
		public int deviceID;
		public int trigger;
		public int recordButton;
		public Device device;
		public int notesInputChannel;
		public int notesOutputChannel;
		public int notesSeperator;

		private List<Command> commands;
		private List<Command> shiftCommands;

		public void execute(int ch,int cc,int v)
		{
			for (int i=0;i<commands.Count;i++)
				commands[i].fire(ch,cc,v);	
		}
		public void shiftExecute(int ch,int cc,int v)
		{
			for (int i=0;i<shiftCommands.Count;i++)
				shiftCommands[i].fire(ch,cc,v);
		}
		

		public InstrumentProtocolDevice(Device d,string dateiName)
		{
			if (dateiName=="") return;

			commands = new List<Command>();
			shiftCommands = new List<Command>();
			device = d;
			int zeile=0;
			int deviceCount = 0;
			int recordCount = 0;
			int commandCount = 0;
			int notesCount = 0;
			StreamReader reader = null;

			try 
			{
				Console.Write("    reading file: ");
				Console.WriteLine(dateiName);

				reader = 
					File.OpenText(dateiName);
				string line;
				line = reader.ReadLine();
				
				while (line!=null)
				{
					string[] stringList = null;
					int[]       intList = null;
					stringList=line.Split(':');
					
					zeile++;
					line = reader.ReadLine();          // vorher inkrement
					if (stringList.Length<2)           continue; // und weiter
					if (stringList[0].StartsWith("#")) continue; // und weiter

					// parameter in int liste konvertieren
					intList = new int[stringList.Length];
					for (int i=1;i<stringList.Length;i++)
						intList[i] = Convert.ToInt16(stringList[i]);
					

					// ------------
					if (stringList[0]=="notes")
					{
						if (stringList.Length<4)
						throw(
						new ParameterException(
							zeile));

						notesInputChannel = intList[1] -1 + 144;
						notesOutputChannel = intList[2]-1 + 144;
						notesSeperator = intList[3];

						if ((intList[1]<1)
							||(intList[1]>16)
							||(intList[2]<1)
							||(intList[2]>16))
						throw(
						new ParameterException(
							zeile,
							"format must be \"notes:(1..16):(1..16):(n)\""));
						
						notesCount++;
					}

					// ------------
					if (stringList[0]=="device")
					{
						if (stringList.Length<3)
						throw(
						new ParameterException(zeile));
										
						deviceID        = intList[1];
						device.deviceID = deviceID;
						trigger         = intList[2];

						if ((trigger<0)
							||(trigger>127))
						throw(
						new ParameterException(
							zeile,
							"format must be \"device:(n):(n):(0..127)\""));
						deviceCount++;
					}

					// ---------
					if (stringList[0]=="record")
					{
						if (stringList.Length<2)
						throw(
						new ParameterException(zeile));

						recordButton = intList[1];

						if ((recordButton<0)
							||(recordButton>127))
						throw(
						new ParameterException(
							zeile,
							"format must be \"record:(0..127)\""));
						
						recordCount++;
					}
					
					
					// -----------
					if (stringList[0]=="command")
					{
						if (stringList.Length<6)
						throw(
						new ParameterException(zeile));
						
						if ((intList[1]<1)
							||(intList[1]>16)
							||(intList[3]<1)
							||(intList[3]>16)
							||(intList[2]<0)
							||(intList[2]>127)
							||(intList[4]<0)
							||(intList[4]>127))
						throw(
						new ParameterException(
							zeile,
							"format must be \"command:(1..16):(0..127):(1..16):(1..127)"));
					
						int addition = 0;
						if (intList[5] == 0)
							addition = 144;
						if (intList[5] == 1)
							addition = 176;
						if (intList[5] == 2)
							addition = 224;


						commands.Add(
						new Command(
							intList[1]-1+addition,
							intList[2],
							intList[3]-1+addition,
							intList[4],
							d));	
						commandCount++;
					}
					if (stringList[0]=="shiftCommand")
					{
						if (stringList.Length<6)
						throw(
						new ParameterException(zeile));
						
						if ((intList[1]<1)
							||(intList[1]>16)
							||(intList[3]<1)
							||(intList[3]>16)
							||(intList[2]<0)
							||(intList[2]>127)
							||(intList[4]<0)
							||(intList[4]>127))
						throw(
						new ParameterException(
							zeile,
							"format must be \"shiftCommand:(1..16):(0..127):(1..16):(1..127)"));
					
						int addition = 0;
						if (intList[5] == 0)
							addition = 144;
						if (intList[5] == 1)
							addition = 176;
						if (intList[5] == 2)
							addition = 224;


						shiftCommands.Add(
						new Command(
							intList[1]-1+addition,
							intList[2],
							intList[3]-1+addition,
							intList[4],
							d));	
						commandCount++;
					}
				}
				reader.Close();
			}
			
			
			
			
			
			
			// ausnahmebehandlung //////////////////////////////////////////////////
			catch (FileNotFoundException f)
			{
				Console.WriteLine("    error: the specified file could not be found");
			}
			catch (FormatException fE)
			{
				Console.Write("    error:  wrong parameter format in line");
				Console.WriteLine(zeile);
			}
			catch (ParameterException p)
			{
				Console.WriteLine("    error: wrong parameter count");
			}
			catch (Exception exc)
			{
				Console.WriteLine("    sorry: an unknown error has occured");
			}
			finally
			{
				Console.WriteLine("    statistics");
				if (deviceCount==0)
				{
					Console.WriteLine("    error: no device tag could be found");
				}
				if (recordCount==0)
				{
					Console.WriteLine("    error: no record tag could be found");
				}
				if (notesCount==0)
				{
					Console.WriteLine("    error: no notes tag could be found");
				}
				Console.Write("        activated by midiCC: ");
				Console.WriteLine(trigger);
				Console.Write("        recieving midiNotes from Channel ");
				Console.WriteLine(notesInputChannel);
				Console.Write("        sending midiNotes to Channel ");
			    Console.Write(notesOutputChannel);
				Console.Write(" on midiYokeOutPort ");
				Console.WriteLine(device.deviceID);	

				Console.Write("        commands:");
				Console.WriteLine(commandCount);
				Console.WriteLine(" ");

			}
		}

	}

	class ParameterException: Exception
	{
		public int line;

		public ParameterException(int l)
		{
			Console.Write("    error in line: ");
			Console.Write(l);
			Console.WriteLine(" -> less parameters than expected");
		}
		public ParameterException(int l,string s)
		{
			Console.Write("    line ");
			Console.Write(l);
			Console.Write(": -- error -> ");
			Console.WriteLine(s);
		}
	}




}
