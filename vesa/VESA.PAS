{***************************************************}
{*****Die Unit stellt die Vesa Bios-Funktionen******}
{*****und -Datenstrukturen zur VerfÅgung************}
{***************************************************}
{*****geschrieben von: Daniel de Oliveira***********}
{*****Datum der letzten énderung: 17.05.1997********}
{***************************************************}
unit vesa;

interface

{Diesen Code liefern erfolgreich ausgefÅhrte Vesa-Funktionen}
const korrekt=$004f;

{Datenpuffer, der von der Procedure getvesainfo benutzt wird}
type rvesainfo=record
       signature:array[0..3]of char;
       version  :word;
       oemstring:pointer;
       abilities:array[0..3]of byte;
       videolist:pointer;
       totalmem :word;
       undefined:array[20..255]of byte;
     end;

{Datenpuffer, der von der Procedure getmodeinfo benutzt wird}
type rmodeinfo=record
       attributes:word;
       attwindowA:byte;
       attwindowB:byte;
       granularity:word;
       windowmem:word;
       segA:word;
       segB:word;
       bankproc:pointer;
       vlength:word;
       xres:word;
       yres:word;
       xcharsize:byte;
       ycharsize:byte;
       planes:byte;
       bitsperpixel:byte;
       banks:byte;
       memmodel:byte;
       banksize:byte;
       NrOfPages:byte;
       reserved:byte;
       RedBits,RedPos:Byte;
       GreenBits,GreenPos:Byte;
       BlueBits,BluePos:Byte;
       FreeBits,FreePos:Byte;
       DirectColorInfo: Byte;
       flatoffset:longint;
       offsetnotsee:longint;
       sizeofnotsee:word;
       undefined: ARRAY[50..255] OF Byte;
     end;

var status:word;
{Variable zur Wiedergabe des Status}

procedure vesasetmode(mode:word);
{Funktion setzt Videomodus}

function vesagetmode:word;
{Funktion ermittelt aktuellen Videomodus}

procedure setwindowpos(window:byte;granularitaetseinheiten:word);
{setzt die Bank eines Speicherfensters (in Granularityunits)}

procedure setvirtual(pixels:word);
{setzt virtuelle Bildschirmbreite}

procedure setstart(x,y:word);
{setzt die Adresse, ab der dargestellt wird}

procedure getvesainfo(var info:rvesainfo);
{holt Informationen Åber die VGA-Karte}

procedure getmodeinfo(var minfo:rmodeinfo;mode:word);
{holt Informationen Åber einen bestimmten Videomodus ein}

implementation

var os,sg:word;
{Hilfsvariablen}

procedure vesasetmode(mode:word);assembler;
asm
  mov ax,4f02h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  mov bx,mode       {Modus nach BX                           }
  int 10h           {Vesa-Interrupt ausfÅhren                }
  mov status,ax     {Status von AX holen und sichern         }
end;

function vesagetmode:word;
var mode:word;
begin
asm
  mov ax,4f03h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  int 10h           {Vesa-Interrupt ausfÅhren                }
  mov status,ax     {Status von AX holen und sichern         }
  mov mode,bx       {Modus aus BX holen und sichern          }
end;
  vesagetmode:=mode;
end;

procedure setwindowpos(window:byte;granularitaetseinheiten:word);assembler;
asm
  mov ax,4f05h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  mov bh,0          {Vesa-Unterfunktionsnummer               }
  mov bl,window     {Fensternummer-1                         }
  mov dx,granularitaetseinheiten
  int 10h           {Vesa-Interrupt ausfÅhren                }
  mov ax,status     {Status aus AX holen und sichern         }
end;

procedure setvirtual(pixels:word);assembler;
asm
  mov ax,4f06h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  mov bl,0          {Vesa-Unterfunktionsnummer               }
  mov cx,pixels     {Anzahl der Pixel in virtueller Zeile    }
  int 10h           {VGA-Interrupt ausfÅhren                 }
  mov status,ax     {Status aus AX holen und sichern         }
end;

procedure setstart(x,y:word);assembler;
asm
  mov ax,4f07h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  mov bx,0          {BH mu· 0 sein, BL Unterfunktionsnummer  }
  mov dx,y          {erste darzustellende Zeile nach DX      }
  mov cx,x          {erster darzust. Punkt in dieser Zeile   }
  int 10h           {VGA-Interrupt ausfÅhren                 }
  mov status,ax     {Status aus AX holen und sichern         }
end;

procedure getvesainfo(var info:rvesainfo);
begin
os:=ofs(info);
sg:=seg(info);
asm
  mov ax,4f00h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  mov bx,os         {Segment und Offset von 256 Byte         }
  mov di,bx         {Datenpuffer nach ES:DI                  }
  mov bx,sg
  mov es,bx
  int 10h           {VGA-Interrupt ausfÅhren                 }
  mov status,ax     {Status aus AX holen und sichern         }
end;
end;

procedure getmodeinfo(var minfo:rmodeinfo;mode:word);
begin
sg:=seg(minfo);
os:=ofs(minfo);
asm
  mov ax,4f01h      {AH Vesa-Kennung, AL Vesa-Funktionsnummer}
  mov cx,mode       {Modusnummer nach cx                     }
  mov bx,sg         {Segment und Offset von 256 Byte-        }
  mov es,bx         {Datenpuffer nach ES:DI                  }
  mov bx,os
  mov di,bx
  int 10h           {VGA-Interrupt ausfÅhren                 }
  mov status,ax     {Status aus AX holen und sichern         }
end;
end;


begin
end.