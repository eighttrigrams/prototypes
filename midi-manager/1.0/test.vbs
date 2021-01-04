rem --- per tastendruck für einen efx-kanal soll auch gleichzeitig ein bestimmter channel in live angezeigt werden
rem --- hier wird geregelt, welche midi-cc number dafür an live fesendet werden soll, wenn ein jeweiliger efx knopf 
rem --- gedrückt wird, die festgelegten nummern sind 0-23, wird der wert midiCCInstrument4 deklariert, soll ein sprung ausbleiben
dim efxJumpTo(4)

efxJumpTo(0)            =  14
efxJumpTo(1)            =  15
efxJumpTo(2)            =  -1
efxJumpTo(3)            =  -1


rem --- für jedes Instrument kann festgelegt werden, ob sein Aufruf gleichzeitig den Aufruf des entsprechende EfxKanals nach sich zieht
dim instrumentLinkedToEfx(4)

instrumentLinkedToEfx(0) =  1
instrumentLinkedToEfx(1) =  1
instrumentLinkedToEfx(2) =  0
instrumentLinkedToEfx(3) =  0


rem --- den behringers wird bei der anmeldung im system eine nummer mitgeteilt, falls dies in der falschen reihenfolge
rem --- stattfindet kann man die Links- und Rechts-Kanalnummern einfach vertauschen

bcrLinks                =  11
bcrRechts               =  12
bcrLive                 =   2

rem --- im folgenden finden sich die codierungen der toggle events für kanalwechsel/sprungbefehle, genau diese werte werden auch
rem --- immer an live weitergereicht

midiCCInstrument1 =  96
midiCCInstrument2 =  97
midiCCInstrument3 =  98
midiCCInstrument4 =  99

midiCCoverwrite   = 101
midiCCstop        = 102
midiCCplay        = 103

midiCCEfx1        = 104
midiCCEfx2        = 105
midiCCEfx3        = 106
midiCCEfx4        = 107

midiCCactSendsLinks     = 100
midiCCactSendsRechts    = 108
midiCCswitchMixerBank   = 109

rem - folgende toggles werden erst vom skript erzeugt, je nach
rem - andfornderung wird dann ein bestimmter kanal in live getriggert 

midiCCjumpToSendEfx1    = 112
midiCCjumpToSendEfx2    = 113
midiCCjumpToSendEfx3    = 114
midiCCjumpToSendEfx4    = 115

midiCCJumpToMaster      = 116

midiCCrecInstrument1    = 119
midiCCrecInstrument2    = 120
midiCCrecInstrument3    = 121 
midiCCrecInstrument3    = 122




Set mox = WScript.CreateObject("MIDIOX.MOXScript.1", "OnTrigger_")
mox.LoadProfile "d:\home\daniel\desk\input.ini" 

rem --- die midi control-change befehle werden zusammen mit den midi channels in einem byte zusammen codiert, hier also
rem --- die entprechenden zuordnungen

rem --- auf diesem kanal befinden sich die sendEfx:
midiChannel09           = 184
rem --- auf diesem kanal befinden sich die normalen Efx:
midiChannel10           = 185
rem --- auf diesem kanal befinden sich die Instrument:e
midiChannel11           = 186
rem --- 
midiChannel12           = 187
rem ---  mixer Bank 1
midiChannel13           = 188
rem --- mixer Bank 2
midiChannel14           = 189
rem --- mixer Bank 3 
midiChannel15           = 190
rem --- hier sind alle mute, solos, channel selects und steuerungsbuttons untergebracht
midiChannel16           = 191
rem --- hier nun noch der befehl für einen bankwechsel
midiBankwechsel         = 192 


rem --- variablendeklarationen

dim mixer(384)
dim synth(128)
dim sendEfx(128)
dim efx(128)

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



rem --- main loop
mox.fireMidiInput = 1
mox.DivertMidiInput = 0

mox.OutputMidiMsg bcrRechts, midiBankwechsel, whichEfx, 0
mox.OutputMidiMsg bcrLinks, midiBankwechsel, whichEfx, 0

Do While mox.ShouldExitScript = 0
Loop

mox.fireMidiInput = 0
rem --- main loop









Sub OnTrigger_MidiInput( timestamp, port, chan, dat1, dat2) 
	
	
	select case chan	
	
	case midiChannel09
	rem --- send kanäle
		sendEfx( dat1     ) = dat2  
	
	case midiChannel10
	rem --- efx kanäle
		efx( dat1         ) = dat2
	
	case midiChannel11
	rem --- instrument kanal
		synth( dat1       ) = dat2
		
	case midiChannel13
	rem --- mixer Bank 1
		mixer( dat1       ) = dat2
		
	case midiChannel14
	rem --- mixer Bank 2
		mixer( dat1 + 128 ) = dat2
		
	case midiChannel15
	rem --- mixer Bank 3
		mixer( dat1 + 256 ) = dat2
	
	
	
	case midiChannel16 
		rem --- alle toggle off events ausschließen --------------------------------------------------------------------------
		if dat2 > 66 then
		
			If dat1 < 24 Then 
			rem ------------------------------------------------------------------- program change channels 1-24
				mox.OutputMidiMsg bcrLinks, midiBankwechsel,  dat1, 0
				rewriteMixer bcrLinks	
				sendIsActiveLinks  = 0
				sendIsActiveRechts = 0
			End If
			
			
			If dat1 >= midiCCInstrument1 and dat1 <= midiCCInstrument4 then
				whichInstrument = dat1 - midiCCInstrument1
				goRec (whichInstrument)
				rewriteInstrument( whichInstrument)	
				sendIsActiveLinks  = 0
				sendIsActiveRechts = 0
				
				rem --- soll ein efxKanal zusätzlich aufgerufen werden?
				if instrumentLinkedToEfx(whichInstrument) = 1 then
					whichEfx = whichInstrument
					rem --- muss auch für spätere zugriffe gesetz werden, deswegen der umweg über die variable
					rewriteEfx(whichEfx)
				end if
			end if	
			
			
			
			If dat1 >= midiCCEfx1 and dat1 <= midiCCEfx4 then
				whichEfx = dat1 - midiCCEfx1
				rewriteEfx(whichEfx)
				sendIsActiveLinks  = 0
				sendIsActiveRechts = 0
				
				
				rem --- die folgenden zeilen bestimmen, ob an den aufruf eines efxChannels auch der Aufrif eines 
				rem --- Kanals in Live gekoppelt werden soll
				if efxJumpTo(dat1 - midiCCEfx1) <> -1 then
					mox.outputMidiMsg bcrLive, midiChannel16, efxJumpTo(dat1 - midiCCEfx1), 127
					mox.outputMidiMsg bcrLive, midiChannel16, efxJumpTo(dat1 - midiCCEfx1), 0
				end if			
				
				rem soll auch ein instrument aufgerufen werden?
				if instrumentLinkedToEfx( whichEfx) = 1 then
					whichInstrument = whichEfx
					rem --- muss auch für spätere zugriffe gesetz werden, deswegen der umweg über die variable
					rewriteInstrument( whichInstrument)
				end if
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
		end if
		
    end select	
   
End Sub 

Sub rewriteMixer( geraet ) 
	if (geraet = bcrLinks) then
		for i = 0 to 55 
			mox.OutputMidiMsg geraet, midiChannel13 ,  i, mixer( i )
		next
	else
		for i = 0 to 55 
			mox.OutputMidiMsg geraet, midiChannel14 + activeMixerBank ,  i, mixer( (activeMixerBank * 128) + 128 + i )
		next
	end if	
End Sub


Sub RewriteEfx( number)
	mox.OutputMidiMsg bcrRechts, midiBankwechsel, (activeMixerBank * 10) + number, 0
	rewriteMixer bcrRechts 
	
	for i = 0 to 31
		mox.OutputMidiMsg bcrRechts, midiChannel10, (number * 32) + i , efx((number * 32) + i)
	next	
end sub

Sub rewriteSends(geraetHaupt , pageHaupt , geraetNeben , pageNeben, pageActive)
	mox.outputMidiMsg bcrLive, midiChannel16, midiCCjumpToSendEfx1 + pageActive , 127
	mox.outputMidiMsg bcrLive, midiChannel16, midiCCjumpToSendEfx1 + pageActive ,   0
	
	mox.outPutMidiMsg geraetHaupt, midiBankWechsel, pageHaupt, 0
	mox.outPutMidiMsg geraetNeben, midiBankWechsel, pageNeben + pageActive, 0
	
	rewriteMixer bcrLinks
	rewriteMixer bcrRechts
	
	for i = 0 to 127 
		mox.OutputMidiMsg geraetNeben, midiChannel09, i, sendEfx(i)
	next	
end sub

Sub rewriteInstrument (number)
	mox.OutputMidiMsg bcrLinks, MidiBankwechsel, number + 24, 0
	for i = 0 to 23
		mox.OutputMidiMsg bcrLinks, midiChannel11, (number * 32) + i, synth( (number * 32) + i)
	next
	rewriteMixer bcrLinks
end sub				

Sub goRec (number)
	if number <> recInstrument then
		if number <> -1 then
		mox.OutputMidiMsg bcrLive, midiChannel16, midiCCrecInstrument1 + recInstrument, 127
		mox.OutputMidiMsg bcrLive, midiChannel16, midiCCrecInstrument1 + recInstrument,   0
		end if
		
		recInstrument = number
		mox.OutputMidiMsg bcrLive, midiChannel16, midiCCrecInstrument1 + recInstrument, 127
		mox.OutputMidiMsg bcrLive, midiChannel16, midiCCrecInstrument1 + recInstrument,   0
	end if
end sub


