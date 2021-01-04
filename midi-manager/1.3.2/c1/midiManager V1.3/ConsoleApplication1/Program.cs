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

        enum midiChannel { C9 = 184, C10, C11, C12, C13, C14, C15, C16 }
        
        class  Midi


        {

            

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
            public int midiYokeOffset = 0;
            public int midiYakeOffset = 0;


            public int bcrLive;

            private int lastOne = 0;
            private int activeSendEfx = 0;
            private int sendIsActiveLinks = 0;
            private int sendIsActiveRechts = 0;


            //------------- aufrufe --------------------------------------------------------------------------

            public int[] midiCCInstrument = new int[4] { 96, 97, 98, 99 };   //midiCC, die das InstrumentEvent auslösen
            public int[] midiCCefx = new int[4]{ 104, 105, 106, 107 };          //midiCC, die das EfxEvent auslösen
            public int midiCCSendLinks = 100;                   //midiCC, von links aufgerufenes SendEvent
            public int midiCCSendRechts = 108;                  //midiCC, von rechts aufgerufenes SendEvent
            public int midiCCSwitch = 109;                      //midiCC, von rechts zum wechseln der kanalbänke für rechts
            
            //------------- felder ---------------------------------------------------------------------------

            
  

            //-------------- steuerungsvariablen -------------------------------------------------------------

            
            
           
            public int recInstrument = -1;                     //welcher instrument kanal trägt gerade den record focus     
            public int shiftPads = 0;                             //alternative pad funktion auswahl;  
            public int shiftScenes = 0;                           //alternative scenes anwahl

            private CBehringer behringerLinks;
            private CBehringer behringerRechts;

            private MIDIOXLib.MoxScriptClass mox = new MIDIOXLib.MoxScriptClass();
            
            private CGuru guru;
            private CGuru guruA;
            private CGuru guruB;
            
            
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

                    int a;

                    a=Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    behringerLinks.init(a);
                    guruA.init(a);
                    guruB.init(a);
                    behringerRechts.init(Convert.ToInt16(reader.ReadLine().Split(':')[1]));
                    bcrLive         = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[0] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[1] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[2] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    triggerCCefx[3] = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    midiYakeOffset = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    midiYokeOffset = Convert.ToInt16(reader.ReadLine().Split(':')[1]);
                    
                    reader.Close();
                    Console.WriteLine("gelesen");
                }
                catch (Exception exc)
                {   Console.WriteLine("fehler");}
            }

           

            public void start()
            {
                mox.MidiInput += new MIDIOXLib._IMoxScriptEvents_MidiInputEventHandler(this.midiIn);
                guruA = new CGuru(mox, 4, "d:\\home\\daniel\\desk\\guruA.txt");
                guruB = new CGuru(mox, 5, "d:\\home\\daniel\\desk\\guruB.txt");

                behringerLinks = new CBehringer(mox);
                
                behringerLinks.addBank(24, (int)midiChannel.C11, 0);
                behringerLinks.addBank(25, (int)midiChannel.C11, 32);
                behringerLinks.addBank(26, (int)midiChannel.C11, 64);
                behringerLinks.addBank(27, (int)midiChannel.C11, 96);
                behringerLinks.addBank(28, (int)midiChannel.C9, 0);
                behringerLinks.addBank(29, (int)midiChannel.C9, 32);
                behringerLinks.addBank(30, (int)midiChannel.C9, 64);
                behringerLinks.addBank(31, (int)midiChannel.C13, 32); //sends mixer1

                behringerLinks.addMixer((int)midiChannel.C13);

                behringerRechts = new CBehringer(mox);

                behringerRechts.addBank(0, (int)midiChannel.C10, 0);
                behringerRechts.addBank(1, (int)midiChannel.C10, 32);
                behringerRechts.addBank(2, (int)midiChannel.C10, 64);
                behringerRechts.addBank(3, (int)midiChannel.C10, 96);

                behringerRechts.addBank(4, (int)midiChannel.C14, 32); //sends mixer2
                behringerRechts.addBank(4, (int)midiChannel.C15, 32); //sends mixer3

                behringerRechts.addBank(5, (int)midiChannel.C9, 0); //sends mixer3
                behringerRechts.addBank(6, (int)midiChannel.C9, 32); //sends mixer3
                behringerRechts.addBank(7, (int)midiChannel.C9, 64); //sends mixer3

                behringerRechts.addMixer((int)midiChannel.C14);
                behringerRechts.addMixer((int)midiChannel.C15);


                readConfigFile();

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
                    if (number != -1) toggleOFF(bcrLive, (int)midiChannel.C16, recTriggerBcrLive[0] + recInstrument); //erster schritt, vorhandenen focus lösen, falls vorhanden
                    recInstrument = number; //und neuen Focus setzen
                    toggleOFF(bcrLive,(int)midiChannel.C16,recTriggerBcrLive[0]+ recInstrument);                 
                }   
             }
             
            
            private void midiIn(int ts, int port, int chan, int dat, int wert)
            {
            switch (chan)
            {
                case 152: if (dat == 24) shiftPads = 1; break;// note ons von der zoom

                case 136:
                   //hier sind automatisch nur die note offs von der zoom

                    if (dat == 21)
                    {
                        shiftPads = 0;//shiftPads funktion    
                        shiftScenes = ((shiftScenes + 1) % 9) * 8; //shiftpads offset
                    }
                    if (dat == 22) guru.undoCommand();//undo
                    if (dat == 23) guru.commitCommand(); //commit
                    if (dat == 20) guru.recordCommand(); //recrd
                    if (dat == 12) guru.patternView();  //pattern view
                    if (dat == 13) guru.graphEditView(); //graph edit
                    if (dat == 14) guru.padEditView(); //pad edit
                    if (dat == 15) guru.scenesView(); //scenes view
                    if (dat == 32) readConfigFile();
                    if (dat == 37) guru.load(); //pad einstellungen laden
                    if (dat == 38) guru.save(); //pad einstelungen schreiben
                    if ((dat >= 16) && (dat <= 19)) guru.switchMachine(dat - 16);
               break;

               case 144:
                    //note vom keyboard für jeweils einen der beiden synthies
                    mox.OutputMidiMsg(midiYakeOffset + midiYake, 144, dat, wert);
               break;

               case 159: //kommt von der akai - noten
                    mox.OutputMidiMsg(midiYokeOffset + midiYoke, midiNotes[11], dat, wert);
                    if (shiftPads == 1) toggleOFF(midiYokeOffset + midiYoke, midiNotes[12], dat - 36 + 60); //auch pattern auswählen    
                    if (dat >= 60) toggleOFF(midiYokeOffset + midiYoke, midiNotes[15], dat + shiftScenes);
               break;
               case 175: //kommt von der akai - aftertouch
                    //mox.OutputMidiMsg(bcrLive, 4 + midiYoke, midiNotes[11], wert);
               break;

               case (int)midiChannel.C9: behringerLinks.setBankParam(4 + (dat / 32), dat % 32, wert);
                                         behringerRechts.setBankParam(6 + (dat / 32), dat % 32, wert);
                   break;
   
               case (int)midiChannel.C10: behringerRechts.setBankParam(0+(dat/32),dat%32,wert); break;

               case (int)midiChannel.C11: behringerLinks.setBankParam(0+(dat/32),dat % 32,wert); break;

               case (int)midiChannel.C13: //mixer bank1
                   if (dat < 32) behringerLinks.setMixerParam(0, dat, wert);
                   else behringerLinks.setBankParam(7, (dat % 32), wert); 
                   break; 
               case (int)midiChannel.C14: //mixer bank2
                   if (dat<32) behringerRechts.setMixerParam(0, dat, wert); 
                   else behringerRechts.setBankParam(4,dat%32,wert);
                   break; 
               case (int)midiChannel.C15: //mixer bank3
                   if (dat<32) behringerRechts.setMixerParam(1, dat, wert); 
                   else behringerRechts.setBankParam(5,dat%32,wert);
                   break;

                   
                    
                          
               case 191: //midiKanal 16
                    if (wert == 0)//nur auf die off events vom toggleOff reagieren
                    {
                        if (dat == midiCCSwitch)
                        {
                            behringerRechts.switchBank();
                            behringerRechts.callBank(lastOne);
                        }
                        if (dat < 24) 
                        {
                            behringerLinks.callMixer(dat);
                            sendIsActiveLinks=0;
                            sendIsActiveRechts=0;
                            
                        }

                        if (dat == midiCCSendLinks) 
                        {
                            if (sendIsActiveLinks == 1) 
                            {
                                activeSendEfx = (activeSendEfx + 1) % 3;
                            }
                            sendIsActiveLinks=1;
                            sendIsActiveRechts=0;
                            behringerLinks.callBank(7);
                            lastOne = 6 + activeSendEfx;
                            behringerRechts.callBank(lastOne);
                            
                            toggleOFF(bcrLive,(int)midiChannel.C16,triggerCCsendEfx[0] + activeSendEfx);
                        }

                        if (dat == midiCCSendRechts)
                        {
                            if (sendIsActiveRechts == 1) 
                            {
                                activeSendEfx = (activeSendEfx + 1) % 3;
                            }
                            sendIsActiveLinks=0;
                            sendIsActiveRechts=1;
                            behringerLinks.callBank(4 + activeSendEfx);
                            lastOne = 4 + behringerRechts.mixerBank;
                            behringerRechts.callBank(lastOne);

                            toggleOFF(bcrLive, (int)midiChannel.C16, triggerCCsendEfx[0] + activeSendEfx);
                        }

                        if ((dat >= midiCCInstrument[0]) && (dat <= midiCCInstrument[3])) //------------------instrumente--------------
                        {
                            int whichOne=dat - midiCCInstrument[0];   
                            writeRecordFocus(whichOne);

                            behringerLinks.callBank(whichOne);
                            sendIsActiveLinks=0;
                            sendIsActiveRechts=0;
                            
                            if (dat == midiCCInstrument[0])   midiYake = 0;      
                            if (dat == midiCCInstrument[1])   midiYake = 1;
                            if (dat == midiCCInstrument[2]) { midiYoke = 0; guru = guruA; }
                            if (dat == midiCCInstrument[3]) { midiYoke = 1; guru = guruB; }
                            
                        }
 
                        if ((dat >= midiCCefx[0]) && (dat <= midiCCefx[3])){ //------------------effekte------------------------
                            lastOne = dat - midiCCefx[0];
                         
                            
                            behringerRechts.callBank(lastOne);
                            sendIsActiveLinks=0;
                            sendIsActiveRechts=0;
                            
                            if (triggerCCefx[lastOne] != -1) 
                                toggleOFF(bcrLive, (int)midiChannel.C16, triggerCCefx[lastOne]);
                        }
                    }
                    break;

                default:

                    break;
            }
            }
        }
}
