Midi Manager Version 1.4
wder midi manager für behringer bcr2000 controller
grundlegende beschreibung der verteilung der midi ccs
beschreibung der behringer page-setups


O nomenklatur            80%
1 mixer sektion          90%
2 instrument sektion     90%
3 control sektion        0%
4 die midi kanäle        90%
5 die pages              90%
6 send                   0%
7 conpiling etc          50%  
8 anforderungen          99%
9 compiling 
10 midiyoke              70%
19 einbettung guru       0%





0:EINLEITUNG_____________________________________________________________________
TODO

beschreibung des problems, das die behringer immer alle möglichkeiten zu versprechen
schienen, aber das nie befriedigend war.

grundidee der teilung mixer instremente für den guten workflow bot die ernüchternde einsich
das immer wieder aktualisiert werden musste, aber dies nicht akzeptabel und frustrierend 
war.

dann entdeckung midioxmidiyoke
das die möglichkeit eröffnete den datenfluss zu manipulieren
die karten sind neu gemischt




PHYSIKALISCHE AUFTEILUNG____________________________________________________________________________

beschreibung der behringer page-setups

das physische layout besteht aus drei sektionen. 
	
	MIXER SEKTION	

	oben eine reihe (druck-knöpfe),
	die jeweils nochmal in vier bänken alternative belegungen aufweisen können. 
	darunter zwei reihen taster.

	CONTROL SEKTION

	auf der rechten seite befinden sich einige taster,
	die sich für steuerungswecke anbieten

	INSTRUMENTEN SEKTION

	das unter feld von controllern eignet sich zur steuerung
	von synth, efx, eqs etc

jeder behringer bcr2000 controller verfügt über 32 pages, die jeweils
unabhängig von allen anderen pages mit midi-ccs belegt werden können.
da jedoch im midi-standard nur eine begrenzte anzahl von midi-ccs vor-
gesehen ist
	
	16 kanäle zu 128 nummern

muss ein geeignetes belegungs-schema genutzt werden.

0:LOGISCHE AUFTEILUNG:__________________________________________________________

die aufteilung der behringer-controller in 3 sektionen erscheint nahe-
liegend, wenn man sowohl steuerung, instrumente-bedienung und mixer-einheit
auf den geräten gleichzeitig bedienen will. dennoch ist diese einteilung 
nur vordergründig so vollzogen, wie es das physikalische layout der maschine
nahelegt. gemeint ist folgendes:

die MIXER-Abteilung ist nur teilweise dem mixer aufgabenbereich gewidmet.
die ersten drei bänke zu je 8 encodern gehören zum mixer als volume, fine-
control und panorama steurungen; die darunter befindlichen taster sind mute
bzw solo buttons für die 8 CHANNELS. die 4 bank von encodern ist funktional
der instrumenten-sektion zugeordnet und dient dieser als vierte reihe für 
nicht so oft benutze funktionen. die ersten drei reihen der INS sektion bilden
sind die drei darunter liegenden reihen normaler encoder. die CTRL-sektion 
bilden im "grunde genommen" einerseits die vier bänke push knöpfe der push
encoder der oberen physikalischen sektion. diese haben keine getrennten funk
tionen, sondern tragen auf jeder bank identische cc-nummern, welche zur auswahl
der CHANNELS dienen, und auch funktionieren sollen, egal mit welcher enc-bank
man gerade arabeitet. das "im grunde genommen" deutet bereits an, das eine
ausnahme besteht. die 3 bank pusher trägt nun doch die cc-nummern der channel 
mutes und dient als quick-mute for convenience. zusätzlich zur CTRL-sektion
finden sich auf der rechts-unten-phys sektion jeweils 2*4 pusher wieder, die
zur ausführung diverser steuer-befehle wie etwa "stop"/"play" gedacht sind.


O:NOMENKLATUR:__________________________________________________________

es wird eine funktionale unterteilung vorgenommen

	KANAL               ein midi-kanal
	PAGE                eine abstrakte page, die einer physikalischen 
                        behringer bank entspricht behringer page
	#BANK                eine physikalische behringer bank
	CHANNEL             ein kanal im daw/host
	liDEV               ableton live
	leDEV		    der linke behringer controller               
	riDEV               der rechte behringer controller

MIXER SEKTION

	MIXER1              bezeichnet den mixer der CHANNELS 1-8   (leDEV)
	MIXER2              bezeichnet den mixer der CHANNELS 9-16  (riDEV)
	MIXER3              bezeichnet den mixer der CHANNELS 17-24 (riDEV)
	
	MIX 1.bank.enc      die erste encoder bank        (8 x channel "volume") 
	MIX 2.bank.enc      die zweite encoder bank       (8 x channel "finetune") 
	MIX 3.bank.enc      die dritte encoder bank       (8 x channel "panorama")

	MIX 1.push          die obere pusher-reihe        (8 x toggle on inv "mute")
	MIX 2.push          die untere pusher-reihe       (8 x toggle off "solo")

INSTRUMENT SEKTION

	INS 1.row           erste reihe                   (8 x diverse funktionen)
	INS 2.row           zweite reihe                  (8 x diverse funktionen)
	INS 3.row           dritte reihe                  (8 x diverse funktionen)
	INS 4.row           für nicht so oft genutzt funktionen der instrumente steht
                        die 4 bank der encoder der oberen sektion als erweiterung zur 
	                    verfügung                    (8 x diverse funkeionen)

CTRL SEKTION 

	CTRL select         um sowohl PAGES wie auch CHANNELS 
	                    zu selektieren. die pusher der bänke 
	                    1,2 und 4 mit den jeweils gleichen 
                            midi ccs (jedoch je nach MIXERX 
	                    verschieden)                   (8 x select "CHANNEL"/"PAGE")

	A1 A2               das layout der rechts unteren sektion
	A3 A4               der behringer maschine zum besseren verständnis 
                            der nachfolgenden benamsung
	B1 B2
	B3 B4

	CTRL A.1            je nach behringer DEV entweder zur auswahl           
	CTRL A.2            der instrumente oder der send-efx.
	CTRL A.3
	CTRL A.4

	CTRL B.1            zur anwahl der PAGE, die die SENDEFX des jeweiligen
	                    MIXERX auf einem DEV anzeigt (zeigt dann gleichzeitig
	                    auf dem anderen DEV eines von drei zur verfügenden 
	                    SENDEFX an)
	CTRL B.2            auf einem DEV mit mehr als einem MIXERX zum mixer-bank
                            wechsel, ansonsten frei belegbar
	CTRL B.3            stop 
	CTRL B.4            start


neben den drei sektionen vergebe ich noch folgende symbolische namen zur identifiezierung
einzelner PAGEs. amgemerkt werden muss schon hier, das die SENDEFXx auf jeweils pro mixer-
bank vorkommen müssen, da jeweils ein mixer bei anwahl der send funktion zusammen mit seinen
send-amounts für 3 send a 8 CHANNELS angezeigt wird, auf der anderen DEV dann jeweils eines
von 3 möglichen send geräten. (bei erneutem druck das nächste etc). 

	CHANNEL 1-24        jeweils das channel-strip eines CHANNEL anwählen
	EFX1                jeweils eine PAGE pro efx
	EFX2
	EFX3
	EFX4
	INSTRUMENT1         jeweils eine PAGE pro instrument
	INSTRUMENT2
	INSTRUMENT3
	INSTRUMENT4
	SENDEFX1            jeweils eine PAGE pro sendefx
	SENDEFX2
	SENDEFX3
	



I:MIXER SEKTION:_________________________________________________________
	
von mir vorgesehen sind maximal 2 mixer pro behringer einheit. ein mixer
ist auf mehreren pages vertreten und nimmt in anspruch:
die ersten drei bänke der push-encoder sowie alle tast funktionen
der push encoder sowie die zwei knopfreihen darunter
nimmt für sich jeweils einen kompletten midi-kanal in anspruch

die belegung der midi-ccs eines mixers wird dann
wie folgt festgelegt:

	PLACE	          TYPE        USED MIDI CCS          FUNCTION          

	MIX 1.bank.enc    enc         0-7	                 volume
	MIX 2.bank.enc    enc         64-71                  fine tune
	MIX 3.bank.enc    enc         8-15                   panorama

	MIX 1.but         but         16-23                  mute 
    MIX 2.but         but         56-63                  solo
        
die drucktaster der push-encoder müssen über den midi-steuerungs-kanal
16 belegt werden, je nachdem welche kanalsprünge sie bewirken sollen (
in live und auch auf den behringers). da ein mixer aus 8 kanälen besteht,
wird also einem mixer eines der folgendes vier layouts für die push-encoder
reihen 1,2,4 zuteil (pusher 3 wird als quick mute benutzt und enthält die
gleichen werte wie die oberen buttons, nur als toggle off:

 
		          TYPE        USED MIDI CCS          FUNCTION          

	mixer setup 1     but         0-7                    select channel 1-8
	mixer setup 2     but         8-15                   select channel 9-16
	mixer setup 3     but         16-23                  select channel 17-24
	mixer setup 4     but         24-31                  select channel 25-32

zusätlich gibt es einen teil an midi-ccs der zwar zu einem mixer gehört,
aber nicht auf jeder seite gesehen wird, stattdessen findet man diese für
sends genutzten midi-ccs auf send pages, und zwar in der instrument-section
wieder. die midi-kanalnummer ist also die gleiche wie für den mixer festgelegt.

	
	PLACE	          TYPE        USED MIDI CCS          FUNCTION          

	INS 1.row         enc         24-31                  send 1
    INS 2.row         enc         32-39                  send 2
    INS 3.row         enc         40-47                  send 3
	INS 4.row         enc         48-56                  send 4
B

	

II:INSTRUMENT SEKTION:_____________________________________________________

die grundidee für einen sparsamen umgang mit midi-cc resourcen ist 
folgender: jedes instrument bekommt 32 midi cc nummern zugeteilt. ein midi-kanal
teilt sich somit in 4/4, die dann INS x.setup heissen.

die zählung beginnt in der ersten reihe (INS 1.row, INS 2.row, INS 3.row), und
setzt sich dann auf bank 4 der push encoder in der oberen sektion fort, etwas 
schwerer zugänglich und den mixer dann verdeckend sind diese reihen für nich
so oft benutzte funtionen zur verfügung. diese heissen dann INS 4.row. die
setups sind also nun

	
	PLACE	          TYPE        USED MIDI CCS          FUNCTION          
	    
                         
        PAGE 1-8          enc         0-31                   INS 1.setup
        PAGE 9-16         enc         32-63                  INS 2.setup
        PAGE 17-24        enc         64-95                  INS 3.setup
        PAGE 25-32        enc         96-127                 INS 4.setup

die zuordnungen zu den physikalischen behringer pages laufen dann so, wie:
place bereits andeutet.                           
	 
ein einzelner midi-kanal für instrumente ist also zb auf den seiten 1,9,17,25
verteilt aufzufinden. ansersherum findet man immer wieder eine reihung 1-8
der midi-kanäle auf den behringer pages wieder 1-8 auf den pages 1-8, 1-8
auf den pages 9-16 usw.



III:CONTROL SECTION:___________________________________________________________

der KANAL 16 steht für controller cc bereit 
 
                    midi-cc         wählt CHANNEL an  wählt PAGE an 

leDEV

	CTRL A.1    xxx             INSTRUMENT1       25 auf leDEV
	CTRL A.2    xxx             INSTRUMENT2       26 auf leDEV           
	CTRL A.3    xxx             INSTRUMENT3       27 auf leDEV
	CTRL A.4    xxx             INSTRUMENT4       28 auf leDEV
	
	CTRL B.1    xxx             SENDS             32 auf leDEV, (1)5-(1)7 auf riDEV
	CTRL B.2    xxx             undefined
	
	            midi-cc         funktion
	
	CTRL B.3    xxx             STOP              im liDEV
	CTRL B.4    xxx             START             im liDEV

	
riDEV
	CTRL A.1    xxx             EFX1             (1)1 auf riDEV 
    CTRL A.2    xxx             EFX2             (1)2 auf riDEV
	CTRL A.3    xxx             EFX3             (1)3 auf riDEV
	CTRL A.4    xxx             EFX4             (1)4 auf riDEV

	CTRL B.1    xxx                              (1)8 auf riDEV, 29-31 auf leDEV
	CTRL B.2    xxx                              switch mixer auf riDEV

	            midi-cc         funktion

	CTRL B.3    xxx             STOP              im liDEV
	CTRL B.4    xxx             START             im liDEV
	





IV:DIE MIDI-KANÄLE:____________________________________________________________

die 16 zur verfügung stehenden midi-kanäle sind wie folgt verteilt

	                                                              ART DER VERTEILUNG (CCs)

	KANAL  1          cmp + eq für die CHANNELS 1,9,17,(25)       INS      
	KANAL  2          cmp + eq für die CHANNELS 2,10,18,(26)      INS
	KANAL  3          cmp + eq für die CHANNELS 3,11,19,(27)      INS
	KANAL  4          cmp + eq für die CHANNELS 4,12,20,(28)      INS
	KANAL  5          cmp + eq für die CHANNELS 5,13,21,(29)      INS
	KANAL  6          cmp + eq für die CHANNELS 6,14,22,(30)      INS
	KANAL  7          cmp + eq für die CHANNELS 7,15,23,(31)      INS
	KANAL  8          cmp + eq für die CHANNELS 8,16,24,(32)      INS
	KANAL  9          send efx                                    INS
	KANAL 10          efx                                         INS
	KANAL 11          instruments                                 INS
	KANAL 12          UNUSED
	KANAL 13          mixer 1                                     MIX 
	KANAL 14          mixer 2                                     MIX
	KANAL 15          mixer 3                                     MIX
	KANAL 16          controls                                

V:DIE PAGES:___________________________________________________________________

main konkretes setup sieht folgendermassen aus. ich habe 2 behringer-controller.
auf dem linken ist ein mixer, und zwar für die CHANNELS 1-8, alle 32 PAGES 
zeigen diesen mixer an.
der rechte behringer enthält zwei mixer, einen für die steuerung der CHANNELS 9-16
und einen zweiten für die steuerung der CHANNELS 17-24. jedes INS muss also auf
diesem behringer doppelt implementiert sein, einmal zusammen mit dem mixer 2, und 
einmal mit dem mixer 3. konkret sieht das so aus das eine trennung ab PAGE 10 besteht,
 
DEVICE1        
		mixer
	1  	MIXER1           CHANNEL  1         KANAL 1
	2   MIXER1           CHANNEL  2         KANAL 2
	3	MIXER1           CHANNEL  3         KANAL 3
	4   MIXER1           CHANNEL  4         KANAL 4
	5 	MIXER1           CHANNEL  5         KANAL 5
	6  	MIXER1           CHANNEL  6         KANAL 6
	7	MIXER1           CHANNEL  7         KANAL 7
	8	MIXER1           CHANNEL  8         KANAL 8
	9	MIXER1           CHANNEL  9         KANAL 1
	10  MIXER1           CHANNEL 10         KANAL 2
	11  MIXER1           CHANNEL 11         KANAL 3 
	12  MIXER1           CHANNEL 12         KANAL 4
	13  MIXER1           CHANNEL 13         KANAL 5
	14  MIXER1           CHANNEL 14         KANAL 6
	15  MIXER1           CHANNEL 15         KANAL 7
	16  MIXER1           CHANNEL 16         KANAL 8
	17  MIXER1           CHANNEL 17         KANAL 1
	18  MIXER1           CHANNEL 18         KANAL 2
	19  MIXER1           CHANNEL 19         KANAL 3
	20  MIXER1           CHANNEL 20         KANAL 4
	21  MIXER1           CHANNEL 21         KANAL 5
	22  MIXER1           CHANNEL 22         KANAL 6
	23  MIXER1           CHANNEL 23         KANAL 7
	24  MIXER1           CHANNEL 24         KANAL 8
	25  MIXER1           INSTRUMENT1        KANAL 11
	26  MIXER1           INSTRUMENT2        KANAL 11
	27	MIXER1           INSTRUMENT3        KANAL 11
	28	MIXER1           INSTRUMENT4        KANAL 11
	29	MIXER1           SENDEFX1           KANAL 9
	30	MIXER1           SENDEFX2           KANAL 9
	31	MIXER1           SENDEFX3           KANAL 9
	32	MIXER1           SENDS MIXER 1      KANAL 13 über mixer 1

DEVICE 2

	1       MIXER2           EFX1               KANAL 10
	2	MIXER2		 EFX2               KANAL 10
	3	MIXER2           EFX3               KANAL 10
	4	MIXER2           EFX4               KANAL 10
	5	MIXER2           SENDEFX1           KANAL  9
	6	MIXER2           SENDEFX2           KANAL  9
	7	MIXER2           SENDEFX3           KANAL  9
	8	MIXER2           SENDS MIXER 2      KANAL 14 über mixer 2 
	9	MIXER2
	10 	MIXER3
	11	MIXER3           EFX1               KANAL 10
	12	MIXER3           EFX2               KANAL 10
	13	MIXER3           EFX3               KANAL 10
	14	MIXER3           EFX4               KANAL 10
	15	MIXER3           SENDEFX1           KANAL  9
	16	MIXER3           SENDEFX2           KANAL  9
	17	MIXER3           SENDEFX3           KANAL  9
	18	MIXER3	         SENDS MIXER 3      KANAL 15 über mixer 3 
	19	MIXER3
	20	MIXER3



TODO
VIa:STEUERUNG die erste:_____________________________________________________________________

ok, so weit zu gut, gehen wir jetzt einmal vom best-case szenario aus und sehen 
ein voll funktionstüchtiges setup vor uns aufgebaut, alles ist tip top verbunden und
wir sehen nur noch die angeschalteten controller vor uns und ein ableton live auf einem
hoffentlich mindestens 22" display.

das linke DEV hortet einen mixer. mit den knöpfen MIX 1.push und MIX 2.push haben wir kontrolle
über mutes und solos der CHANNELS 1-8 in liDEV, 

im host wird durch betätigung also auf den
entsprechenden CHANNEL geschaltet. gleichzeitig wechselt der behringer auf PAGE 1-8, wo dann
genau die dem CHANNEL entsprechenden kontrollen in der instrument sektion zur verfügung stehen.
die mixer sektion wird automatisch auf den neuesten stand gebracht und es muss sich um nichts
gekümmert werden (TODO: das war auch de eigentliche anlass für das skript, vielleicht passt das
aber auch in die einleitung). in concreto könnte das so aussehen, ich betätige den ersten pusher.
im host wird auf den kickdrum kanal gewechselt und die entsprechenden instrumente(vst) angezeigt.
diese kann ich nun (etwa eq und compressor) direkt manipulieren, gleichzeitig habe ich aber immer
zugriff auf die mixer-funktionalität aller CHANNELS 1-8 im griff und im blick.

der zweite ist etwas schwiriger da sich hier zwei mixer drauf befinden 


TODO
VIb:STEUERUNG die zweite: send:____________________________________________________________

ein bischen knifllig war das ganze projekt ja insgesamt schon, ich kann mich nicht
erinnern wieviel zeit ich allein vor midiox gebraucht habe, eine befriedigende 
nutzung der controller zu entwickeln, so das die funktionalitäten nicht nur gut gemeint,
sondern vor allem auch gut integriert waren, das heisst sich seemless in einen workflow einfügen
konnten und nicht brachlagen aufgrund mangelnder accessability. deswegen bin ich besonders stolz 
auf das send-konzept.

an dieser stelle möchte ich es nur in seiner anwendung schildern und die technischen
details ausser acht lassen.



VII:COMPILING_RUNTIME_SYSTEM_INSTALL_MIDIOX_MIDIYOKE_SETUP:___________________


VIII:ANFORDERUNGEN:_______________________________________________

	1.        .NET-framework runtime 2.0   wird benötigt zu zugriff auf com schnittstelle
	2.	      midiox                       stellt die com-api zur verfügung
	3.        midiyoke                     plugin, das virtuelle midi devices zur verfü
                                           gung stellt
	4.        ableton live
	5.        mono compiler                einfacher als virtual studio, ausserdem
                                           anti-microsoft
IX:COMPILING:_____________________________________________________

für den fall das irgendetwas mit der exe datei nicht stimmt, kann das project neu
compiliert werden. dazu setzt man am besten eine mono shell auf: 

	verknüpfung zu cmd.exe mit dem switch /k "pfad zur .bat datei"
        .bat datei mit mono ungebungsvariablen erzeugen, damit die mono-dateien 
	in jedem verzeichnis verwendbar werden.

benötigte dateien alle müssen in einem verzeichnis sein

	program.cs                             hauptprogramm
	cbehringer.cs                          speicherung der midi-ccs
    cguru.cs                               zur ansteuerung der gurus
	devices.txt                            konfigurationsdatei 
	input.ini                              midiyoke einstellungen
	interop.MIDIOXLib.dll                  api ist in midi ox-enthalten, rüber
	                                       kopieren nötig

in der shell kann das ganze dann kompiliert werden mit
	
	mcs program.cs cguru.cs cbehringer.cs -C:"interop.MIDIOXLib.dll"
	
dann ensteht eine .exe, die dann benutzt werden kann.




X:MIDIYOKE:_________________________________________________________

benötigte ports



	 
der event port steurt folgende devices an

                                              ableton connect   konfigdatei-port            
	MIDI-YOKE 1        instrument 1           track in          midiyokeOS
	MIDI-YOKE 2        instrument 2           track in          automatisch
	MIDI-YOKE 3        instrument 3           track in          midiyakeOS
	MIDI-YOKE 4        instrument 4           track in          automatisch 
	MIDI-YOKE 8        steuerungsdaten live   remote in         bcrlive     
	USBDEVICE x        ein behringer                            bcrLinks
	USBDEVICE x        der andere                               bcrRechts
      
der output vom host wird über abgefangen über 

	MIDIYOKE 5 

und wird an folgende devices direkt drangehängt

    USBDEVICE x          
    USBDEVICE c

XI:EINBETTUNG DES GURU-DRUMCOMPUTER:____________________________________

in der version 1.4 ist die unterst�tzung zweier guru 1.5.12 maschinen vorgesehen.
wie im midiyoke kapitel bereits behandelt, ist es vorgesehen, das jedes guru �ber 
einen eigenen midi-yoke port angesprochen werden kann. detaillierte informationen
zur midi implemenatation der guru findet sich in einer gesonderten midi implementation
chart von fxpansion.

je nachdem welches instrument gerade gew�hlt ist, l�sst der midi-manager die eingegangenen
signale entweder an den einen oder an den anderen guru senden. beide gurus werden also
gleich inplementiert und erforden dieselben einrichtungschritte in live.

zur steuerung der guru-drum computer sind 2 hardware-geraete in gebrauch

	1.zoom rythm track drumcomputer.
	dieser wird auf seinem basskanal daf�r benutzt, um bei dem gerade ausgew�hlten
	guru, auf dessen ausgew�hlter engine, verschiedene patterns anzutriggern, sowie
	um browser und aufnahmekommandos anzusteuern.

	2.akai mpd16
	diese wird auf ihrer ersten bank dazu genutzt, direkt die 16 drumpads der 
	ausgew�hlten engine anzusteuern. auf ihrer zweiten bank sollen mit ihr 8 scenes
	ansteuerbar sein, sowie 4 engines und 4 views erreichbar sein.

1.zoom________________________________________________________________
der zoom rythm track wird ueber seinen basskanal 9 benutzt. somit sind alle noten,
die im midimanager eingehen ueber ihr erstes datenbyte zu identifizieren. das erste
datenbyte enth�lt note on/off sowie die kanalinfo 9. das erste midi-datenbyte ist somit
f�r alle zoom befehle entweder 152 (on) oder 136 (off). die erste bank von unten f�ngt
dann an bei midi notennummer 12 fuer die note c. alle uebrigen noten und banke werden
dann einfach immer hochgez�hlt. somit lassen sich �ber identifizierte tasten entsprechende
guru funktionen ausl�sen.

konkret sind das:

	

	1.bank         -     12        -          trigger pattern 1 der aktuellen engine 
	1.bank         -     13        -          trigger pattern 2 der aktuellen engine 
	1.bank         -     14        -          trigger pattern 3 der aktuellen engine 
	1.bank         -     15        -          trigger pattern 4 der aktuellen engine 
	1.bank         -     16        -          trigger pattern 5 der aktuellen engine 
	1.bank         -     17        -          trigger pattern 6 der aktuellen engine 
	1.bank         -     18        -          trigger pattern 7 der aktuellen engine 
	1.bank         -     19        -          trigger pattern 8 der aktuellen engine 
	1.bank         -     20        -          nichts 
	1.bank         -     21        -          record 
	1.bank         -     22        -          undo (record) 
	1.bank         -     23        -          commit (record) 
	2.bank         -     24        -          trigger pattern 1 der aktuellen engine 
	2.bank         -     25        -          trigger pattern 2 der aktuellen engine 
	2.bank         -     26        -          trigger pattern 3 der aktuellen engine 
	2.bank         -     27        -          trigger pattern 4 der aktuellen engine 
	2.bank         -     28        -          trigger pattern 5 der aktuellen engine 
	2.bank         -     29        -          trigger pattern 6 der aktuellen engine 
	2.bank         -     30        -          trigger pattern 7 der aktuellen engine 
	2.bank         -     31        -          trigger pattern 8 der aktuellen engine
	2.bank         -     32        -          browser command 1 file up
	2.bank         -     33        -          browser command 1 file down
	2.bank         -     34        -          browser command cancel
	2.bank         -     35        -          browser command ok


2.mpd16_________________________________________________________________
die mpd ist wie gesagt in zwei baenke unterteilt, beide b�nke senden auf midikanal
16 und haben also als mididdatenbyte 1 immer die kennung 175 wegen note on.

die erste bank 

	48   49   50   51            pad13 pad14  pad15 pad16
	44	 45   46   47            pad9  pad10  pad11 pad12
	40   41   42   43            pad5  pad6   pad7  pad8
	36   37   38   39            pad1  pad2   pad3  pad4

die werte wurden bewusst so gew�hlt, das die nummern denen der gurumidiinpmlementation
entsprechen und nicht mehr umgerechnet werden m�ssen, da die erste bank f�r noten-triggering
gedacht ist und schnell reagiert werden muss. lediglich der kanal muss bei ausgabe aug einer
guru von kanal 16 auf 1-8 oder 11 abge�ndert werden.

die zweite bank

	60   61   62   63            scene    scene   scene  scene
	72   73   74   75            scene    scene   scene  scene
	84   85   86   87            engi1    engi2   engi3  engi4
    96   97   98   99            pattern  graph   pad    scenes

auch hier sind die werte an die midiimplementaion der guru angelehnt, die oberen 8 werden
zur auswahl der scenes benutzt, die dritte reihe zur wahl eine engine, die vierte zur view
wahl.















3.live








TODO
DAS PROGRAMM______________________________________________________________
















