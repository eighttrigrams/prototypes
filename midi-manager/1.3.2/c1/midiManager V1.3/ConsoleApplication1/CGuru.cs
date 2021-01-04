using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
//hallo
namespace ConsoleApplication1
{
    class CGuru
    {
        private int[] midiChannel = new int[17] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 184, 185, 186, 187, 188, 189, 190, 191 };       //erstes MidiDatenByte - MidiChannel + Befehl CC
        private int[] midiNotes = new int[17] { 0, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159 };       //erstes MidiDatenByte - MidiChannel + Befehl NN
        private int bcrLinks;
        private int port;
        private MIDIOXLib.MoxScriptClass mox;
        public int browserMode = 0;                       //ist ein browser im guru aktiv?
        private int addPad = -1;                            //teil des index im pads, -1 heißt auch benutzung geblockt
        private int addEngine = 0;                         //teil des index im pads
      
        private int[] pads = new int[512];            //zu den guru pads gehörende werte
        private int[] backup = new int[8];             //backup der bedienelemente bei guru browserzugriff
        private int[] presets = new int[8] { 0, 127, 64, 64, 64, 0, 127, 0 };            //grundeinstellungen der guru pads
        private string dateiName;



        public void load()
        {
            try
            {
                Console.WriteLine("lese.:..");
                StreamReader reader = File.OpenText(dateiName);
                int index = 0;
                string line;
                while (((line = reader.ReadLine()) != null) && (index != 512))
                {
                    pads[index] = Convert.ToInt16(line);
                    Console.WriteLine(pads[index] + " bei " + index);
                    index++;
                }
                Console.WriteLine(index+ " zeilen gelesen");
                reader.Close();
            }
            catch(Exception e)
            {
                Console.WriteLine("datei konnte nicht gelesen werden");
            }
        
        }
        public void save()
        {
            try
            {
                StreamWriter writer= new StreamWriter(dateiName);
                Console.WriteLine("schreibe");
                for (int index = 0; index != 512; index++)
                {
                    writer.WriteLine(pads[index]);
                }

                writer.Close();
            }
            catch (Exception e)
            {
                Console.WriteLine("datei konnte nicht geschrieben werden");
            }
        }


        public void toggleOFF(int device, int chan, int data)///???
        {
            mox.OutputMidiMsg(device, chan, data, 127);
            mox.OutputMidiMsg(device, chan, data, 0);
        }


        public CGuru(MIDIOXLib.MoxScriptClass m, int p,string name)
        //initialisiert alle parameter beim ersten aufruf
        {
            dateiName = name;
           
            mox = m;
            port = p;
            for (int i = 0; i != 512; i++)
            {
                pads[i] = presets[i % 8];
            }
        }
        public void init(int p)
        //für folgende aufrufe aus dem hauptprogramm gedacht, wenn der bcr-port nicht stimmt
        {
            bcrLinks = p;
        }
        public void patternView(){
            toggleOFF(port, midiNotes[12], 12);  //pattern view
        }
        public void graphEditView() { toggleOFF(port, midiNotes[12], 14); }//graph edit
        public void padEditView(){ toggleOFF(port, midiNotes[12], 15);  }//pad edit
        public void scenesView(){  toggleOFF(port, midiNotes[12], 18);         }//scenes view
        public void undoCommand(){ toggleOFF(port, midiNotes[12], 51);   }//undo
        public void commitCommand(){toggleOFF(port, midiNotes[12], 49);}//commit
        public void recordCommand(){toggleOFF(port, midiNotes[12], 57);}//recrd


        public void switchMachine(int number) 
        {
            if (browserMode == 0)
            {
                toggleOFF(port, midiNotes[12], 76 + number);
                addEngine= (number * 128);
                addPad = -1;

            }
        
        }
    }
}
