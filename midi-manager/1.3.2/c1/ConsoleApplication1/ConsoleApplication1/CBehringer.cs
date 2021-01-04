using System;
using System.Collections.Generic;
using System.Text;
///hahhahha//kashdfkjhsadkjfhasdjk
namespace ConsoleApplication1
{
    
    

        class CBehringer
        {
            class CMixer
            {
                public int[] words = new int[128];
                public int channel;
                public int geraet;
                public int pageOffset;
                public MIDIOXLib.MoxScriptClass mox;

                public CMixer(MIDIOXLib.MoxScriptClass m, int g, int c, int offset) { mox = m; geraet = g; channel = c; pageOffset = offset; }

                public void write()//wo genau das auszugeben ist steht bis zum aufruf nicht fest
                {
                    for (int i = 0; i != 32; i++) mox.OutputMidiMsg(geraet, channel, i, words[i]);   
                }
            }


            class CBank
            {
                public int[] words = new int[32];
                public int physicalPage;
                private int offset;
                private int channel;

                public CBank(int p, int c, int o)
                {
                    offset = o;
                    channel = c;
                    physicalPage = p;
                }
                public void write(CMixer m)
                {
                    m.mox.OutputMidiMsg(m.geraet, 192, m.pageOffset + physicalPage, 0);
                    for (int i = 0; i != 32; i++)
                    {
                        m.mox.OutputMidiMsg(m.geraet, channel, offset + i, words[i]);
                    }
                    m.write();
                }
            }


            public void writeMixer(int kanal)
            {//dient einzig und allein von aussen herbeigeführten mixeranfragen
                mixer[0].mox.OutputMidiMsg(mixer[0].geraet, 192, kanal, 0);
                mixer[0].write();
                sendIsActiveLinks = 0;
                sendIsActiveRechts = 0;
            }


            private int[] midiChannel = new int[17] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 184, 185, 186, 187, 188, 189, 190, 191 };       //erstes MidiDatenByte - MidiChannel + Befehl CC
            private int midiBankwechsel = 192;

            private int whichEfx = 0;                          //trägt nummer des aktiven efx
            private int whichSendEfx = 0;                      //trägt nummer des aktiven sendEfx

            private int mixerBank = 0;                         //0 = rechts werden kanäle 9-16, bei 1 kanäle 17-24 
            private int sendIsActiveLinks = 0;                 //programm befindet sich im sendModus (aufruf von links)
            private int sendIsActiveRechts = 0;                //programm befindet sich im sendModus (aufruf von rechts)

            //public MIDIOXLib.MoxScriptClass mox;

            private CBank[] efx = new CBank[4];
            private CBank[] synth = new CBank[2];
            private CMixer[] mixer = new CMixer[3];
            private CBank[] sendEfx = new CBank[3];
            private CBank[] sends = new CBank[3];

            public CBehringer(MIDIOXLib.MoxScriptClass m, int l, int r)
            {
                mixer[0]   = new CMixer(m, l, midiChannel[13], 0);
                mixer[1]   = new CMixer(m, r, midiChannel[14], 0);
                mixer[2]   = new CMixer(m, r, midiChannel[15], 10);
                efx[0]     = new CBank(0, midiChannel[10], 0);
                efx[1]     = new CBank(1, midiChannel[10], 32);
                efx[2]     = new CBank(2, midiChannel[10], 64);
                efx[3]     = new CBank(3, midiChannel[10], 96);
                synth[0]   = new CBank(24, midiChannel[11], 0);
                synth[1]   = new CBank(25, midiChannel[11], 32);
                sendEfx[0] = new CBank(5,midiChannel[9],0);
                sendEfx[1] = new CBank(6, midiChannel[9], 32);
                sendEfx[2] = new CBank(7,midiChannel[9],64);
                sends[0]   = new CBank(31, midiChannel[13], 32);
                sends[1]   = new CBank(4, midiChannel[14], 32);
                sends[2]   = new CBank(4, midiChannel[15], 32);

            }

            public void setValue(int chan, int data, int word)
            //bekommt einen midi-datenPaket und ordnet es in die entsprechenden felder ein
            {
                switch (chan)
                {
                    case 184: if (data < 96) sendEfx[data / 32].words[data % 32] = word;  break;
                    case 185: efx[data / 32].words[data % 32] = word; break;
                    case 186: if (data < 64) synth[data / 32].words[data % 32] = word; break;
                    case 188: 
                        if (data < 32)
                            mixer[0].words[data] = word;
                        else
                            sends[0].words[data % 32] = word;
                        break;
                    case 189:
                        if (data < 32)
                            mixer[1].words[data] = word;
                        else
                            sends[1].words[data % 32] = word;
                        break;
                    case 190: 
                        if (data<32)
                            mixer[2].words[data] = word;
                        else
                            sends[2].words[data % 32] = word;
                        break;
                }
            }

            public void init(int l, int r)
            {
                mixer[0].geraet = l;
                mixer[1].geraet = r;
                mixer[2].geraet = r;
            }


            public void switchBank()
            {
                mixerBank = (mixerBank + 1) % 2;

                if (sendIsActiveLinks == 1) writeSends(0, 1);
                else
                    if (sendIsActiveRechts == 1) writeSends(1, 1);
                    else
                        writeEfx(whichEfx);
            }

            public void writeInstrument(int number)
            {
                if (number < synth.Length)
                    synth[number].write(mixer[0]);
                if (number == 2) writeMixer(26);
                if (number == 3) writeMixer(27);

            }

            public void writeEfx(int number)
            {

                whichEfx = number;
                sendIsActiveLinks = 0;
                sendIsActiveRechts = 0;
                efx[number].write(mixer[1 + mixerBank]);
            }

            public int writeSends(int geraet,int rewrite)
            {
                if (geraet == 0)
                {
                    if (sendIsActiveLinks == 1)
                    {
                        if (rewrite == 0)
                            whichSendEfx = (whichSendEfx + 1) % 3;

                    }
                    
                    sends[0].write(mixer[0]);
                    sendEfx[whichSendEfx].write(mixer[1 + mixerBank]);
                    sendIsActiveRechts = 0;
                    sendIsActiveLinks = 1;
                }
                else
                {
                    if (sendIsActiveRechts == 1)
                    {
                        if (rewrite == 0)
                            whichSendEfx = (whichSendEfx + 1) % 3;
                    }


                    sends[1 + mixerBank].write(mixer[1 + mixerBank]);

                    int back = sendEfx[whichSendEfx].physicalPage;

                    sendEfx[whichSendEfx].physicalPage = 28 + whichSendEfx;
                    sendEfx[whichSendEfx].write(mixer[0]);
                    sendEfx[whichSendEfx].physicalPage = back;

                    
                    sendIsActiveLinks = 0;
                    sendIsActiveRechts = 1;


                }
                             return whichSendEfx;
            }
        }


    
}
