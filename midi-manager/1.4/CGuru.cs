using System;
using System.Collections;
using System.Text;
using System.IO;
namespace midiManager 
{
class CGuru
{
     
//erstes MidiDatenByte - MidiChannel + Befehl NN
private int port;
private MIDIOXLib.MoxScriptClass mox;
public  int browserMode =  0;                           




public CGuru(MIDIOXLib.MoxScriptClass _mox, 
		               int           _port){
	                   mox    =       _mox;
	                   port   =      _port;}


public void selectPattern(int n){
	Console.WriteLine(n);
	mox.OutputMidiMsg(port, 154, 12+(n % 8), 127);
	mox.OutputMidiMsg(port, 154, 12+(n % 8), 0);}
public void selectEngine(int n){
	mox.OutputMidiMsg(port, 155, 76+(n % 4), 127);
	mox.OutputMidiMsg(port, 155, 76+(n % 4), 0);}
public void patternView(){
	mox.OutputMidiMsg(port, 155, 12, 127);
	mox.OutputMidiMsg(port, 155, 12, 0); }
public void graphEditView(){ 
	mox.OutputMidiMsg(port, 155, 14, 127);
	mox.OutputMidiMsg(port, 155, 14, 0); }
public void padEditView(){ 
	mox.OutputMidiMsg(port, 155, 15, 127);
	mox.OutputMidiMsg(port, 155, 15, 0); }
public void scenesView(){  
	mox.OutputMidiMsg(port, 155, 18, 127);
	mox.OutputMidiMsg(port, 155, 182, 0); }
public void undoCommand(){ 
	mox.OutputMidiMsg(port, 155, 51, 127);
	mox.OutputMidiMsg(port, 155, 51, 0); } 
public void commitCommand(){
	mox.OutputMidiMsg(port, 155, 49, 127);
	mox.OutputMidiMsg(port, 155, 49, 0);}
public void recordCommand(){
	mox.OutputMidiMsg(port, 155, 57, 127);
	mox.OutputMidiMsg(port, 155, 57, 0);}
public void browserCommit(){
	mox.OutputMidiMsg(port, 156, 83, 127);
	mox.OutputMidiMsg(port, 156, 83, 0);}
public void browserCancel(){
	mox.OutputMidiMsg(port, 156, 84, 127);
	mox.OutputMidiMsg(port, 156, 84, 0);}
public void browserUp(){
	mox.OutputMidiMsg(port, 156, 68, 127);
	mox.OutputMidiMsg(port, 156, 68, 0);}
public void browserDown(){
	mox.OutputMidiMsg(port, 156, 69, 127);
	mox.OutputMidiMsg(port, 156, 69, 0);}

}}
