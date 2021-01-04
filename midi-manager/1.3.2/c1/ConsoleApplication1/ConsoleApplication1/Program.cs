using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.ComponentModel;
using System.Data;

namespace ConsoleApplication1
{ 
    class Program
    {
        
        static void Main(string[] args)
        {
            
            Midi myMidi = new Midi();
            myMidi.start();
        }



        }
        class  Midi
        {




            public int[] midiChannel = new int[17] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 184, 185, 186, 187, 188, 189, 190, 191 };       //erstes MidiDatenByte - MidiChannel + Befehl CC
            public int[] midiNotes = new int[17] { 0, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159 };       //erstes MidiDatenByte - MidiChannel + Befehl NN
            public int midiBankwechsel = 192;                   //erstes MidiDatenByte - Bankwechselbefehl

            public int[] triggerCCefx = new int[4] { 7, 15, 7, 116 };  //midiCC wird beim efxEvent getriggert
            public int[] triggerCCsendEfx = new int[4] { 112, 113, 114, 115 }; //midiCC wird beim sendEvent getriggert
            

            //------------- sprungbefehle, nachdem bestimmte events ausgelöst wurden -------------------------

            
            public int[] recTriggerBcrLive = new int[4] { 119, 120, 121, 121 };  //midiCC wird beim Instrumentenwechsel getriggert         
            public int masterTriggerBcrLive = 116;               //midiCC wird zum aufruf des master getriggert

              
                                    //midiYoke Kanalnummer
            public int midiYoke = 0;                          //midiYoke Kanalnummer (für linken oder rechten guru)  
            public int midiYake = 0;                          //midiYoke Kanaknummer (für linken oder rechten synth)
            public int bcrLinks;                          //midiYoke Kanalnummer
            public int bcrRechts;                         //midiYoke Kanalnummer
            public int bcrLive; 

            //------------- aufrufe --------------------------------------------------------------------------

            public int[] midiCCInstrument = new int[4] { 96, 97, 98, 99 };   //midiCC, die das InstrumentEvent auslösen
            public int[] midiCCefx = new int[4]{ 104, 105, 106, 107 };          //midiCC, die das EfxEvent auslösen
            public int midiCCSendLinks = 100;                   //midiCC, von links aufgerufenes SendEvent
            public int midiCCSendRechts = 108;                  //midiCC, von rechts aufgerufenes SendEvent
            public int midiCCSwitch = 109;                      //midiCC, von rechts zum wechseln der kanalbänke für rechts
            
            //------------- felder ---------------------------------------------------------------------------

            
  

            //-------------- steuerungsvariablen -------------------------------------------------------------

            
            
           
            public int recInstrument = -1;                     //welcher instrument kanal trägt gerade den record focus     
            public int shift = 0;                             //alternative pad funktion auswahl;  


            public CBehringer behringer;
            public MIDIOXLib.MoxScriptClass mox = new MIDIOXLib.MoxScriptClass();
            public CGuru guru;
            public CGuru guruA;
            public CGuru guruB;
            
            
             // --

            //public MIDIOXLib.MoxScriptClass mox;
            public void toggleOFF(int device, int chan, int data)
            {
                mox.OutputMidiMsg(device, chan, data, 127);
                mox.OutputMidiMsg(device, chan, data, 0);
            }

            
            public void readConfigFile()
            {
                try
                { 
                    StreamReader reader = File.OpenText("d:\\home\\daniel\\desk\\devices.txt");
                    
                    bcrLinks        = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    bcrRechts       = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    bcrLive         = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[0] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[1] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[2] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[3] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);                 
                    reader.Close();
                    Console.WriteLine("gelesen");
                }
                catch (Exception exc)
                {   Console.WriteLine("fehler");}
            }

            public void init()
            {
                readConfigFile();
                guruA.init(bcrLinks);
                guruB.init(bcrLinks);
                behringer.init(bcrLinks,bcrRechts);
            }

            public void start()
            {
                mox.MidiInput += new MIDIOXLib._IMoxScriptEvents_MidiInputEventHandler(this.midiIn);
                guruA = new CGuru(mox, bcrLinks, 4, "d:\\home\\daniel\\desk\\guruA.txt");
                guruB = new CGuru(mox, bcrLinks, 5,"d:\\home\\daniel\\desk\\guruB.txt");
                behringer = new CBehringer(mox, bcrLinks, bcrRechts);
                 //dazu muss erst ein objekt initialisiert werden von behringer sonst crash


                init();
                guru = guruA;
                
                mox.FireMidiInput=1;
                mox.DivertMidiInput=0;
                string a = Console.ReadLine();
                mox.FireMidiInput=0;
            }

            
            private void writeRecordFocus(int number)
            {
                if (number != recInstrument) // nur änderung vornehmen falls WIRKLICH zu einem anderen kanal gewechselt wird
                {
                    if (number != -1) toggleOFF(bcrLive, midiChannel[16], recTriggerBcrLive[0] + recInstrument); //erster schritt, vorhandenen focus lösen, falls vorhanden
                    recInstrument = number; //und neuen Focus setzen
                    toggleOFF(bcrLive,midiChannel[16],recTriggerBcrLive[0]+ recInstrument);                 
                }   
             }
             
            
            private void midiIn(int ts, int port, int chan, int dat, int wert)
            {
            switch (chan)
            {
                case 152: // note ons von der zoom
                    if (dat == 24) {
                        shift = 1;
                    }
                    break;

                case 136:
                   //hier sind automatisch nur die note offs von der zoom

                        if (dat == 24) shift = 0;//shift funktion    
                        if (dat == 34) guru.undoCommand();//undo
                        if (dat == 35) guru.commitCommand(); //commit
                        if (dat == 36) guru.recordCommand(); //recrd
                        if (dat == 12) guru.patternView();  //pattern view
                        if (dat == 13) guru.graphEditView(); //graph edit
                        if (dat == 14) guru.padEditView(); //pad edit
                        if (dat == 15) guru.scenesView(); //scenes view
                        if (dat == 32) init();
                        if (dat == 37) guru.load(); //pad einstellungen laden
                        if (dat == 38) guru.save(); //pad einstelungen schreiben
                        if ((dat >= 16) && (dat <= 19)) guru.switchMachine(dat - 16);
               break;

               case 144:
                    //note vom keyboard für jeweils einen der beiden synthies
                    mox.OutputMidiMsg(2 + midiYake, 144, dat, wert);
               break;

               case 159: //kommt von der akai - noten
                    mox.OutputMidiMsg(4 + midiYoke, midiNotes[11], dat, wert);
                    if (shift == 1) toggleOFF(4 + midiYoke, midiNotes[12], dat - 36 + 60); //auch pattern auswählen    
                    if (dat >= 60
                        )  toggleOFF(4 + midiYoke, midiNotes[15], dat);
               break;
               case 175: //kommt von der akai - aftertouch
                    //mox.OutputMidiMsg(bcrLive, 4 + midiYoke, midiNotes[11], wert);
               break;

               case 184: behringer.setValue(184, dat, wert); break; //festgelegter kanal für die send effekte
               case 185: behringer.setValue(185, dat, wert); break; //festgelegter kanal für die effekte
               case 186: behringer.setValue(186, dat, wert); break; // festgelegter kanal für instrumente
               case 188: behringer.setValue(188, dat, wert); break; //mixer bank1
               case 189: behringer.setValue(189, dat, wert); break; //mixer bank2
               case 190: behringer.setValue(190, dat, wert); break; //mixer bank3
                          
               case 191: //midiKanal 16
                    if ((wert == 0) && (guru.browserMode ==0))//nur auf die off events vom toggleOff reagieren
                    {
                        if (dat == midiCCSwitch) behringer.switchBank();
                        if (dat < 24) behringer.writeMixer(dat);

                        if (dat == midiCCSendLinks) 
                        {
                            int call=behringer.writeSends(0,0);
                            toggleOFF(bcrLive, midiChannel[16], triggerCCsendEfx[0] + call);  
                        }

                        if (dat == midiCCSendRechts)
                        {
                            int call=behringer.writeSends(1,0); 
                            toggleOFF(bcrLive, midiChannel[16], triggerCCsendEfx[0] + call);
                        }

                        if ((dat >= midiCCInstrument[0]) && (dat <= midiCCInstrument[3])) //------------------instrumente--------------
                        {
                            int whichOne=dat - midiCCInstrument[0];   
                            writeRecordFocus(whichOne);
                            behringer.writeInstrument(whichOne);
                            if (dat == midiCCInstrument[0])   midiYake = 0;      
                            if (dat == midiCCInstrument[1])   midiYake = 1;
                            if (dat == midiCCInstrument[2]) { midiYoke = 0; guru = guruA; }
                            if (dat == midiCCInstrument[3]) { midiYoke = 1; guru = guruB; }
                            
                        }
 
                        if ((dat >= midiCCefx[0]) && (dat <= midiCCefx[3])){ //------------------effekte------------------------
                            int whichOne = dat - midiCCefx[0];
                            behringer.writeEfx(whichOne);
                            if (triggerCCefx[whichOne] != -1) 
                                toggleOFF(bcrLive, midiChannel[16], triggerCCefx[whichOne]);
                        }
                    }
                    break;

                default:

                    break;
            }
            }
        }
}
