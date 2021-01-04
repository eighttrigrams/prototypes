using System;
using System.Collections;
using System.Text;
///hahhahha//kashdfkjhsadkjfhasdjk
namespace midiManager
{

class MIXER 
{
	private int[] words = new int[32];
	private int channel;

	public MIXER(int c) {channel = c; }

	public void call(DEV be)
	{
		for (int i = 0; i != 32; i++) be.mox.OutputMidiMsg(be.geraet, channel, i, words[i]);
	}

	public void set(int dat1, int dat2)
	{
		if (dat1 < 32) words[dat1] = dat2;
	}
}


class PAGE 
{
	private int[] words = new int[32];
	private int page;
	private int offset;
	private int channel;

	public PAGE(int p,int c,int o)
	{
		channel = c;
		page = p;
		offset = o;
	}

	public void call(DEV be)
	{
		be.mox.OutputMidiMsg(be.geraet, 192, page+(be.MIXERBank*10), 0);
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





class DEV
{
	public int geraet;   //hierüber wird das geraet angesprochen 
	public MIDIOXLib.MoxScriptClass mox;////////////friend
	public int MIXERBank = 0;                         //0 = rechts werden kanäle 9-16, bei 1 kanäle 17-24

	public ArrayList MIXERs=new ArrayList();
	public ArrayList PAGEs = new ArrayList();   

	public DEV(MIDIOXLib.MoxScriptClass m)
	{
		mox = m;
	}

	public void init(int g)
	{
		geraet = g;
	}

	public void addMIXER(int ch)
	{
		MIXER neuerMixer = new MIXER(ch);
		MIXERs.Add(neuerMixer);
	}

	public void addPAGE(int p,int c,int o)
	{
		PAGE neuePAGE = new PAGE(p,c,o);
		PAGEs.Add(neuePAGE);
	}

	public void setPAGEParam(int n, int dat1, int dat2)
	{
		if (n < PAGEs.Count)
		{
			((PAGE)PAGEs[n]).set(dat1 % 32, dat2);
		}
	}

	public void setMIXERParam(int n,int dat1, int dat2)
	{
		if (n < MIXERs.Count)
		{
			((MIXER)MIXERs[n]).set(dat1,dat2);
		}
	}

	public void callPAGE(int n)
	{
		if (n < PAGEs.Count)
		{
			((PAGE)PAGEs[n]).call(this);
			((MIXER)MIXERs[MIXERBank]).call(this);
		}
	}

	public void callMIXER(int jump)
	{
		mox.OutputMidiMsg(geraet, 192, jump, 0);
		((MIXER)MIXERs[MIXERBank]).call(this);
	}

	public void switchMIXERBank()
	{
		MIXERBank = (MIXERBank + 1) % MIXERs.Count;
	}


}
}
