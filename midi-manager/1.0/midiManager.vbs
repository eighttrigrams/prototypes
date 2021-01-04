rem --- per tastendruck für einen efx-kanal soll auch gleichzeitig ein bestimmter channel in live angezeigt werden
rem --- hier wird geregelt, welche midi-cc number dafür an live fesendet werden soll, wenn ein jeweiliger efx knopf 
rem --- gedrückt wird, die festgelegten nummern sind 0-23, wird der wert midiCCInstrument(3) deklariert, soll ein sprung ausbleiben
efxTriggerBcrLinks         = Array(0,0,0,0)
efxTriggerBcrLive          = Array(0,0,0,0)
sendTriggerBcrLive         = Array(112,113,114,115)
recTriggerBcrLive          = Array(119,120,121,122)
rem --- für jedes Instrument kann festgelegt werden, ob sein Aufruf gleichzeitig den Aufruf des entsprechende EfxKanals nach sich zieht
instrumentLinkedToEfx   = Array(1,1,0,0)

rem --- den behringers wird bei der anmeldung im system eine nummer mitgeteilt, falls dies in der falschen reihenfolge
rem --- stattfindet kann man die Links- und Rechts-Kanalnummern einfach vertauschen

bcrLinks                =  14
bcrRechts               =  16
bcrLive                 =   2

rem --- im folgenden finden sich die codierungen der toggle events für kanalwechsel/sprungbefehle, genau diese werte werden auch
rem --- immer an live weitergereicht

midiCCInstrument        = Array(96,97,98,99) 
midiCCefx               = Array(104,105,106,107)

midiCCoverwrite         = 101
midiCCstop              = 102
midiCCplay              = 103

midiCCactSendsLinks     = 100
midiCCactSendsRechts    = 108
midiCCswitchMixerBank   = 109

rem - folgende toggles werden erst vom skript erzeugt, je nach
rem - andfornderung wird dann ein bestimmter kanal in live getriggert 

midiCCTriggerMaster      = 116





Set mox = WScript.CreateObject("MIDIOX.MOXScript.1", "OnTrigger_")
mox.LoadProfile "d:\home\daniel\desk\input.ini" 

rem --- die midi control-change befehle werden zusammen mit den midi channels in einem byte zusammen codiert, hier also
rem --- die entprechenden zuordnungen

midiChannel = Array(0,0,0,0,0,0,0,0,0,184,185,186,187,188,189,190,191)
midiNotes   = Array(0,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159)
midiBankwechsel         = 192 
rem --- auf diesem kanal befinden sich die sendEfx: 9
rem --- auf diesem kanal befinden sich die normalen Efx: 10 
rem --- auf diesem kanal befinden sich die Instrument:e 11 
rem --- 
rem ---  mixer Bank 1 13
rem --- mixer Bank 2 14
rem --- mixer Bank 3  15
rem --- hier sind alle mute, solos, channel selects und steuerungsbuttons untergebracht 16

rem --- hier nun noch der befehl für einen bankwechsel



rem --- variablendeklarationen

dim mixer(384)
dim synth(128)
dim sendEfx(128)
dim efx(128)




dim Pads(1024)
addMachine=0
addEngine=0
addPad=-1
browserMode=0
dim backup(8)




rem --- dient der eventuell notwendigen wiederherstellung sowohl der Apad oder BpadVariablen, falls 
rem --- das neue Sample nicht ausgewählt werden sollte


rem --- bestimmt, ob die kanäle 9-16 oder 17-24 auf dem rechten behringer angezeigt werden 0 = 9-16, 1 = 17-24 
activeMixerBank = 0

rem --- ist diese variable auf 1 gesetzt, heisst das, daß der send-Modus aktiviert ist, was bedeutet, daß ein weiteres drücken des
rem --- activateSend-Knopfes ein weiterschalten der sendEfx-Page bewrikt
sendIsActiveLinks  = 0
sendIsActiveRechts = 0

rem --- codierungen für die angwählten items (instrument, efx, sendEfx)
whichEfx = 0
whichSendEfx = 0
whichInstrument = 0

recInstrument = -1

midiYoke=0
AactivePad = -1
BactivePad = -1
AactiveEngine=0
BactiveEngine=0
browserMode=0
backupPosition=0
shift=0

rem --- main loop ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
mox.fireMidiInput = 1
mox.DivertMidiInput = 0

mox.OutputMidiMsg bcrRechts, midiBankwechsel, whichEfx, 0
mox.OutputMidiMsg bcrLinks, midiBankwechsel, whichEfx, 0



presets=Array(0,127,64,64,64,0,127,0)
for i = 0 to 1023
	Pads(i) = presets(i mod 8)
	mox.OutputMidiMsg bcrLinks, channel11, (i mod 8)+72 , presets(i mod 8)
next


dim file, fso, ts
Set fso = CreateObject("Scripting.FileSystemObject")


Do While mox.ShouldExitScript = 0
Loop

mox.fireMidiInput = 0
rem --- main loop --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
rem --- main loop --------------------------------------------------------------------------------------------------------------------------------------------------------------------------


rem fso.close




Sub OnTrigger_MidiInput( timestamp, port, chan, dat1, dat2) 
	
	
	select case chan	
	
	case 146
		rem --- auf dem keyboard wurden noten gedrückt die signaliesieren, das ein pad bearbeitet werden soll
		rem --- gucken ob sampler aktiviert ist
		if midiYoke <> 9 then
			mox.OutputMidiMsg 4 + midiYoke, midiNotes(11), dat1 ,dat2
				
				
				
			rem --- soll die note bloß getriggert, oder auch zur bearbeitung ausgewählt werden?
			if shift = 1 then
				rem --- nur einmal und zwar bei an weil es sonst probleme mit den notes vom shift gibt
				if dat2 <> 0 then
					addPad = (dat1-36) * 8 + AddEngine
					for i = 0 to 7
					 	mox.OutputMidiMsg bcrLinks, midiChannel(11), i + 80, Pads ( addPad + i )
					next
					mox.OutputMidiMsg 4 + midiYoke, midiNotes(12), dat1 - 36 + 60, 127
					mox.OutputMidiMsg 4 + midiYoke, midiNotes(12), dat1 - 36 + 60, 0
				end if
			end if
		end if
		
			
		
		
		rem if (dat1 >= 36) and (dat1 <= 51) then
		rem 	mox.OutputMidimsg 4 + midiYoke, midiNotes(11), dat1, dat2
		
		
		
	
	case midiNotes(9)

		rem --- der zoom drumcomputer sendet auf diesem kanal die note on befehle und merkwürdigerweise
		rem --- auf dem kanal 136 die note off befehle, nur die note offs werden meist zum triggern von aktionen
		rem --- benutzt, sonst würden diese ja doppele getriggert.
		
		rem --- bei diesem pad jedoch wird der note on modus dafür benutz um in den shift modus zu gehen,
		rem --- das heisst während er gehalten wird, können noten andere events als wozu sie gedacht sind, ausführen
		if dat1 = 24 then
			shift = 1
		end if
	
	case 136
			
			rem --- shift für das auswählen von samples zur bearbeitung
			if dat1 = 24 then 
				shift = 0
			end if
			rem --- undo
			if dat1 = 34 then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 51, 127
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 51, 0
			end if
			rem --- commit
			if dat1 = 35 then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 49, 127
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 49, 0
			end if
			rem --- record
			if dat1 = 36 then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 57, 127
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 57, 0
			end if
			
			
			
			
			rem --- datei schreiben -------------------------------------------------------------------------
			if dat1 = 40 then
				readLinking()
			end if
			if  dat1 = 38 then
				writeStates()
			end if
			rem --- datei lesen -----------------------------------------------------------------------------
			if dat1 = 37 then 
				readStates()			
			end if
			if  dat1 = 20 and  dat2 = 64 then
				if addPad <> -1 then
					if browserMode = 0 then
						backupPosition = addPad
						browserMode = 1
						for i = 0 to 7 
							backup(i) = Pads(addPad + i)
						next
						
					end if
					Pads(addPad + 0 ) = 0
					Pads(AddPad + 1 ) = 127 
					rem Pads(addPad + 5 ) = 0
					Pads(addPad + 6 ) = 127
					Pads(addPad + 7 ) = 0
					
					for i = 0 to 7 
						mox.outputMidiMsg bcrLinks, midiChannel(11), 80 + i, Pads(addPad + i)
					next
					mox.outputMidiMsg 4 + midiYoke, midiNotes(13), 68, 127
					mox.outputMidiMsg 4 + midiYoke, 140, 68, 64
				end if
			end if
			if  dat1 = 21 and  dat2 = 64 then
				if addPad <> -1 then
					if browserMode = 0 then
						backupPosition = addPad
						browserMode = 1
						for i = 0 to 7 
							backup(i) = Pads(addPad + i)
						next
						
					end if
					Pads(addPad + 0 ) = 0
					Pads(AddPad + 1 ) = 127
					rem Pads(addPad + 5 ) = 0
					Pads(addPad + 6 ) = 127
					Pads(addPad + 7 ) = 0
					
					for i = 0 to 7 
						mox.outputMidiMsg bcrLinks, midiChannel(11), 80 + i, Pads(addPad + i)
					next
					mox.outputMidiMsg 4 + midiYoke, midiNotes(13), 69, 127
					mox.outputMidiMsg 4 + midiYoke, 140, 69, 64
				end if
			end if
			if  dat1 = 22 and dat2 = 64 then
				if addPad <> -1 then
					if browserMode = 1 then
						mox.outputMidiMsg 4 + midiYoke, midiNotes(13), 84, 127
						mox.outputMidiMsg 4 + midiYoke, 140, 84, 64
						
						for i = 0 to 7
							Pads(backupPosition + i) = backup(i)
							mox.outputMidiMsg bcrLinks, midiChannel(11), 80 + i, Pads(backupPosition + i)
						next
												
						addPad = backupPosition						
						browserMode = 0
					end if
				end if
			end if
			if ( dat1 = 23 ) then
			
				if browserMode = 1 then
					browserMode = 0
				end if
			
				
				mox.outputMidiMsg 4 + midiYoke, midiNotes(13), 83, 127
				mox.outputMidiMsg 4 + midiYoke, 140, 83, 64
			end if
			if ( dat1 = 12 ) then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 12, 127
				mox.outputMidiMsg 4 + midiYoke, 139, 12, 64
			end if
			if ( dat1 = 13 ) then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 14, 127
				mox.outputMidiMsg 4 + midiYoke, 139, 14, 64
			end if
			if ( dat1 = 14 ) then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 15, 127
				mox.outputMidiMsg 4 + midiYoke, 139, 15, 64
			end if
			if ( dat1 = 15 ) then
				mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 17, 127
				mox.outputMidiMsg 4 + midiYoke, 139, 17, 64
			end if
			
			
			if  dat1 >= 16 and dat1 <= 19 then
				if browsermode = 0 then
					mox.outputMidiMsg 4 + midiYoke, midiNotes(12), 76 + dat1 - 16, 127
					mox.outputMidiMsg 4 + midiYoke, 139, 76 + dat1 - 16, 64
				
					addEngine = (dat1-16) * 128 + addMachine 
					addPad=-1
				else
					wscript.echo "sample arretieren"
				end if
			end if
	
	
	case midiChannel(9)
	rem --- send kanäle
			
		sendEfx( dat1     ) = dat2  
	
	case midiChannel(10)
	rem --- efx kanäle
		efx( dat1         ) = dat2
	
	case midiChannel(11)
		rem -- entweder ein guru-channel
		if dat1 >= 80 and dat1 <= 87 and addPad <> -1 then
				zuordnung = Array(78,79,35,36,33,40,41,42)
				mox.outputMidiMsg 4 + midiYoke, midiChannel(12), zuordnung(dat1-80) , dat2
				
				Pads (addPad+dat1-80) = dat2
		else
		rem --- oder ein predator channel 
			synth( dat1       ) = dat2
		end if
		
	case midiChannel(13)
	rem --- mixer Bank 1
		mixer( dat1       ) = dat2
		
	case midiChannel(14)
	rem --- mixer Bank 2
		mixer( dat1 + 128 ) = dat2
		
	case midiChannel(15)
	rem --- mixer Bank 3
		mixer( dat1 + 256 ) = dat2
	
	
	
	case midiChannel(16) 
		rem --- alle toggle off events ausschließen --------------------------------------------------------------------------
		if dat2 > 66 then
		if browsermode = 0 then
			addPad = -1
			
		
			If dat1 < 24 Then 
			rem ------------------------------------------------------------------- program change channels 1-24
				
					mox.OutputMidiMsg bcrLinks, midiBankwechsel,  dat1, 0
					rewriteMixer bcrLinks	
					sendIsActiveLinks  = 0
					sendIsActiveRechts = 0
					addPad = -1
				
			End If
			
			
			
			If dat1 >= midiCCInstrument(0) and dat1 <= midiCCInstrument(3) then
				
					whichInstrument = dat1 - midiCCInstrument(0)
					rem if whichInstrument < 2 then
					goRec (whichInstrument)
					rem end if
					midiYoke = 9
					addPad = -1
				
					rewriteInstrument( whichInstrument)	
					sendIsActiveLinks  = 0
					sendIsActiveRechts = 0
				
					if dat1 = midiCCInstrument(2) then 
						mox.outputMidiMsg 4 , midiNotes(12), 76 , 127
						mox.outputMidiMsg 4 , 139, 76 , 64
						addMachine = 0
						addEngine = 0
						addPad = -1
						midiYoke = 0
					end if
					if dat1 = midiCCInstrument(3) then
						mox.outputMidiMsg 5 , midiNotes(12), 76 , 127
						mox.outputMidiMsg 5 , 139, 76 , 64
						addMachine = 512
						addEngine = 0
						addPad = -1
						midiYoke = 1
					end if
					
					rem --- soll ein efxKanal zusätzlich aufgerufen werden?
					if instrumentLinkedToEfx(whichInstrument) = 1 then
						whichEfx = whichInstrument
						rem --- muss auch für spätere zugriffe gesetz werden, deswegen der umweg über die variable
						rewriteEfx(whichEfx)
					end if
					
				
			end if	
			
			
			
			If dat1 >= midiCCEfx(0) and dat1 <= midiCCEfx(3) then
			
				
					whichEfx = dat1 - midiCCEfx(0)
					rewriteEfx(whichEfx)
					sendIsActiveLinks  = 0
					sendIsActiveRechts = 0
					addPad = -1 								
					
					rem sprung in behringer
					if efxTriggerBcrLinks(whichEfx) <> -1 then
						mox.OutputMidiMsg bcrLinks, midibankwechsel, efxTriggerBcrLinks(whichEfx), 0
						
						if efxTriggerBcrLinks(whichEfx) < 24 then
							rewriteMixer bcrLinks
						end if
						if efxTriggerBcrLinks(whichEfx) = 24 or efxTriggerBcrLinks(whichEfx) = 25 then
							rewriteInstrument whichEfx
						end if
					end if
					
					
					rem sprunf auf live
					if efxTriggerBcrLive(whichEfx) <> -1 then
						mox.outputMidiMsg bcrLive, midiChannel(16), efxTriggerBcrLive(whichEfx), 127
						mox.outputMidiMsg bcrLive, midiChannel(16), efxTriggerBcrLive(whichEfx), 0
					end if
										
					whichInstrument = whichEfx
					
			End If
			
			if dat1 = midiCCswitchMixerBank then
				activeMixerBank = (activeMixerBank + 1) mod 2
	
				if (sendIsActiveLinks = 1) then 
					mox.OutputMidiMsg bcrRechts, midiBankwechsel, 5 + (activeMixerBank * 10) + whichSendEfx, 0
					rewriteMixer bcrRechts
				else 
					if (sendIsActiveRechts = 1) then
						mox.OutputMidiMsg bcrRechts, midiBankwechsel, 4 + (activeMixerBank * 10) , 0
						rewriteMixer bcrRechts
					else
						rewriteEfx(whichEfx)
					end if
				end if		
			end if
			
			
			if ( dat1 = midiCCactSendsLinks ) then
				if (sendIsActiveLinks = 1) then
					whichSendEfx = ((whichSendEfx + 1) mod 3 )
				end if
				sendIsActiveLinks  = 1
				sendIsActiveRechts = 0
				rewriteSends bcrLinks, 31,bcrRechts , 5, whichSendEfx
			end if	
			
			if ( dat1 = midiCCactSendsRechts ) then
				if ( sendIsActiveRechts = 1) then
					whichSendEfx = ((whichSendEfx + 1) mod 3 )
				end if
				sendIsActiveLinks  = 0
				sendIsActiveRechts = 1
				rewriteSends bcrRechts, 4,bcrLinks , 28, whichSendEfx 
			end if	

			
		else
			
			if addMachine = 512 then
				mox.OutputMidiMsg bcrLive, midiChannel(16), midiCCinstrument(3), 127
				mox.OutputMidiMsg bcrLive, midiChannel(16), midiCCinstrument(3), 0
			else
				mox.OutputMidiMsg bcrLive, midiChannel(16), midiCCinstrument(2), 127
				mox.OutputMidiMsg bcrLive, midiChannel(16), midiCCinstrument(2), 0
			end if
			wscript.echo "sample wahl arretieren"
		end if
		end if
	rem --- kanal 16 ende
    end select	
   
End Sub 

Sub rewriteMixer( geraet ) 
	if (geraet = bcrLinks) then
		for i = 0 to 55 
			mox.OutputMidiMsg geraet, midiChannel(13) ,  i, mixer( i )
		next
	else
		for i = 0 to 55 
			mox.OutputMidiMsg geraet, midiChannel(14) + activeMixerBank ,  i, mixer( (activeMixerBank * 128) + 128 + i )
		next
	end if	
End Sub


Sub RewriteEfx( number)
	mox.OutputMidiMsg bcrRechts, midiBankwechsel, (activeMixerBank * 10) + number, 0
	rewriteMixer bcrRechts 
	
	for i = 0 to 31
		mox.OutputMidiMsg bcrRechts, midiChannel(10), (number * 32) + i , efx((number * 32) + i)
	next	
end sub

Sub rewriteSends(geraetHaupt , pageHaupt , geraetNeben , pageNeben, pageActive)
	mox.outputMidiMsg bcrLive, midiChannel(16), sendTriggerBcrLive(0) + pageActive , 127
	mox.outputMidiMsg bcrLive, midiChannel(16), sendTriggerBcrLive(0) + pageActive ,   0
	
	mox.outPutMidiMsg geraetHaupt, midiBankWechsel, pageHaupt, 0
	mox.outPutMidiMsg geraetNeben, midiBankWechsel, pageNeben + pageActive, 0
	
	rewriteMixer bcrLinks
	rewriteMixer bcrRechts
	
	for i = 0 to 127 
		mox.OutputMidiMsg geraetNeben, midiChannel(9), i, sendEfx(i)
	next	
end sub

Sub rewriteInstrument (number)
	mox.OutputMidiMsg bcrLinks, MidiBankwechsel, number + 24, 0
	for i = 0 to 23
		mox.OutputMidiMsg bcrLinks, midiChannel(11), (number * 32) + i, synth( (number * 32) + i)
	next
	rewriteMixer bcrLinks
end sub				

Sub goRec (number)
	if number <> recInstrument then
		if number <> -1 then
		mox.OutputMidiMsg bcrLive, midiChannel(16), recTriggerBcrLive(0) + recInstrument, 127
		mox.OutputMidiMsg bcrLive, midiChannel(16), recTriggerBcrLive(0) + recInstrument,   0
		end if
		
		recInstrument = number
		mox.OutputMidiMsg bcrLive, midiChannel(16), recTriggerBcrLive(0) + recInstrument, 127
		mox.OutputMidiMsg bcrLive, midiChannel(16), recTriggerBcrLive(0) + recInstrument,   0
	end if
end sub

sub readLinking()
	set File = fso.getFile("d:\home\daniel\desk\linking.txt")
	set ts = File.OpenAsTextStream(1,-2)
		
	efxTriggerBcrLinks(0) = CInt( ts.readline() )	
	efxTriggerBcrLinks(1) = CInt( ts.readline() )	
	efxTriggerBcrLinks(2) = CInt( ts.readline() )	
	efxTriggerBcrLinks(3) = CInt( ts.readline() )	
	
	efxTriggerBcrLive(0)  = CInt( ts.readline() )	
	efxTriggerBcrLive(1)  = CInt( ts.readline() )	
	efxTriggerBcrLive(2)  = CInt( ts.readline() )	
	efxTriggerBcrLive(3)  = CInt( ts.readline() )	
	
	wscript.echo "linking info gelesen"
	
	set file = nothing
	set ts = nothing
end sub

sub writeStates()
	set File = fso.OpenTextFile("d:\home\daniel\desk\guru_states.txt", 2, false)
	for i = 0 to 1023 
		file.write pads(i) & vbCrLf
	next
	wscript.echo "geschrieben"
	set file = nothing
end sub

sub readStates()
	set File = fso.getFile("d:\home\daniel\desk\guru_states.txt")
	set ts = File.OpenAsTextStream(1,-2)
	
	for i = 0 to 1023 
		pads(i) = CInt( ts.readline() )
	next
	wscript.echo "gelesen"
	addPad = -1
	set file = nothing
	set ts = nothing
end sub








