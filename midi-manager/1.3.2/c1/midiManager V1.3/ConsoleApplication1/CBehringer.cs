using System;
using System.Collections.Generic;
using System.Collections;
using System.Text;
///hahhahha//kashdfkjhsadkjfhasdjk
namespace ConsoleApplication1
{

    class CMixer 
    {
        private int[] words = new int[32];
        private int channel;

        public CMixer(int c) {channel = c; }

        public void call(CBehringer be)
        {
            for (int i = 0; i != 32; i++) be.mox.OutputMidiMsg(be.geraet, channel, i, words[i]);
        }

        public void set(int dat1, int dat2)
        {
            if (dat1 < 32) words[dat1] = dat2;
        }
    }
    class CBank
    {
        private int[] words = new int[32];
        private int page;
        private int offset;
        private int channel;

        public CBank(int p,int c,int o)
        {
            channel = c;
            page = p;
            offset = o;
        }

        public void call(CBehringer be)
        {
            be.mox.OutputMidiMsg(be.geraet, 192, page+(be.mixerBank*10), 0);
            for (int i = 0; i != 32; i++)
            {
                be.mox.OutputMidiMsg(be.geraet, channel, i+offset, words[i]);
            }
        }

        public void set(int dat1, int dat2)
        {
            if (dat1 < 32)
            {
                words[dat1] = dat2;
            }
        }


    }





    class CBehringer
    {
        public int geraet;   //hierüber wird das geraet angesprochen 
        public MIDIOXLib.MoxScriptClass mox;////////////friend
        public int mixerBank = 0;                         //0 = rechts werden kanäle 9-16, bei 1 kanäle 17-24
              
        public ArrayList mixer=new ArrayList();
        public ArrayList baenke = new ArrayList();   

        public CBehringer(MIDIOXLib.MoxScriptClass m)
        {
            mox = m;
        }
        public void init(int g)
        {
            geraet = g;
        }
        public void addMixer(int ch)
        {
            CMixer neuerMixer = new CMixer(ch);
            mixer.Add(neuerMixer);
        }
        public void addBank(int p,int c,int o)
        {
            CBank neueBank = new CBank(p,c,o);
            baenke.Add(neueBank);
        }
        public void setBankParam(int n, int dat1, int dat2)
        {
            if (n < baenke.Count)
            {
                ((CBank)baenke[n]).set(dat1, dat2);
            }
        }
        public void setMixerParam(int n,int dat1, int dat2)
        {
            if (n < mixer.Count)
            {
                ((CMixer)mixer[n]).set(dat1,dat2);
            }
        }
        public void callBank(int n)
        {
            if (n < baenke.Count)
            {
                ((CBank)baenke[n]).call(this);
                ((CMixer)mixer[mixerBank]).call(this);
            }
        }
        public void callMixer(int jump)
        {
            mox.OutputMidiMsg(geraet, 192, jump, 0);
            ((CMixer)mixer[mixerBank]).call(this);
        }
        public void switchBank()
        {
            mixerBank = (mixerBank + 1) % mixer.Count;
        }

        



    }
}
