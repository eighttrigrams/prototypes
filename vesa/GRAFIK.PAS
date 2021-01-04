{**************************************************}
{*****Die Unit stellt grafische Funktionen fr*****}
{*****die Vesa Modi zur Verfgung******************}
{**************************************************}
{*****geschrieben von: Daniel de Oliveira**********}
{*****Datum der letzten Žnderung: 17.05.1997*******}
{**************************************************}
unit grafik;

interface

{Hier werden die Sprite Daten abgelegt}
type rsprite=record
       xres,yres:word;
       where:pointer;
     end;

var pal:array[1..768]of byte;
    line:array[0..319]of byte;
    xres,yres,bank,bank2:word;
    bankproc:pointer;

procedure getpal;
{holt die DAC-Palette ins pal-Array}
procedure setpal;
{setzt die DAC-Palette mit den Daten aus pal}
procedure pcxtosprite(name:string;var sprite:rsprite);
{liest ein PCX-Bild ein und legt es in einem Sprite-Record ab}
procedure line8(x1,y1,x2,y2:word;col:byte);
{zeichnet eine Linie von P1(x1,y1) nach P2(x2,y2)}
procedure put8(x,y:word;col:byte);
{setzt einen Punkt nach P(x,y) mit der Farbe col}
function get8(x,y:word):byte;
{liefert die Farbe des Punktes P(x,y)}
procedure show(sprite:rsprite;x,y:word);
{gibt ein Sprite auf dem Bildschirm aus}
procedure showsprite(sprite:rsprite;x,y:word);
{wie oben, jedoch werden Pixel mit Hintergrundfarbe ignoriert}

implementation

procedure show(sprite:rsprite;x,y:word);
var hilfe:pointer;
    xreso:word;
    abstand:word;
    p:pointer;
begin
  hilfe:=sprite.where;
  xreso:=sprite.xres;
  abstand:=xres-xreso;
  p:=bankproc;
  asm
    push ds                 {DS sichern              }
    {+++++Erste Bank berechnen+++++}
    mov ax,xres             {Offset fr ersten Punkt }
    mul y                   {berechnen               }
    add ax,x
    jnc @no1
    inc dx
    @no1:
    xor bx,bx
    call dword ptr [p]
    {+++++Erste Bank berechnen+++++}
    {+++++Register vorbereiten+++++}
    mov di,ax               {Erster Offset           }
    mov ax,sprite.yres      {Anzahl y-Durchl„ufe     }
    lds si,dword ptr hilfe  {Quelle ins Datenseg     }
    mov bx,0a000h           {Videosegment vorbereiten}
    mov es,bx
    {+++++Register vorbereiten+++++}
    {++++++++++Hauptteil+++++++++++}
    @yloop:
    mov cx,xreso            {x-Z„hler initialisieren }
    @xloop:
    mov bl,ds:[si]
    mov es:[di],bl
    inc si
    add di,1                {inc di setzt Carry nicht}
    jnc @no2                {wenn šberlauf, dann     }
    inc dx                  {eine Bank weiter        }
    xor bx,bx
    call dword ptr [p]
    @no2:
    loop @xloop             {einen Punkt weiter      }
    add di,abstand          {sonst abstand addieren  }
    jnc @no3                {wenn šberlauf, dann     }
    inc dx                  {eine Bank weiter        }
    xor bx,bx
    call dword ptr [p]
    @no3:
    dec ax                  {y-Z„hler dekrementieren }
    jnz @yloop              {wenn nicht 0, weiter    }
    {++++++++++Hauptteil+++++++++++}
    pop ds                  {DS restaurieren         }
  end;
end;

procedure showsprite(sprite:rsprite;x,y:word);
var hilfe:pointer;
    xreso:word;
    abstand:word;
    p:pointer;
begin
  hilfe:=sprite.where;
  xreso:=sprite.xres;
  abstand:=xres-xreso;
  p:=bankproc;
  asm
    push ds                 {DS sichern              }
    {+++++Erste Bank berechnen+++++}
    mov ax,xres             {Offset fr ersten Punkt }
    mul y                   {berechnen               }
    add ax,x
    jnc @no1
    inc dx
    @no1:
    xor bx,bx
    call dword ptr [p]
    {+++++Erste Bank berechnen+++++}
    {+++++Register vorbereiten+++++}
    mov di,ax               {Erster Offset           }
    mov ax,sprite.yres      {Anzahl y-Durchl„ufe     }
    lds si,dword ptr hilfe  {Quelle ins Datenseg     }
    mov bx,0a000h           {Videosegment vorbereiten}
    mov es,bx
    {+++++Register vorbereiten+++++}
    {++++++++++Hauptteil+++++++++++}
    @yloop:
    mov cx,xreso            {x-Z„hler initialisieren }
    @xloop:
    mov bl,ds:[si]          {Punkt holen             }
    cmp bl,0
    je @uebergehen          {Punkt nur setzen, wenn  }
    mov es:[di],bl          {nicht Hintergrundfarbe  }
    @uebergehen:            {Z„hler trotzdem weiter  }
    inc si
    add di,1                {inc di setzt Carry nicht}
    jnc @no2                {wenn šberlauf, dann     }
    inc dx                  {eine Bank weiter        }
    xor bx,bx
    call dword ptr [p]
    @no2:
    loop @xloop             {einen Punkt weiter      }
    add di,abstand          {sonst abstand addieren  }
    jnc @no3                {wenn šberlauf, dann     }
    inc dx                  {eine Bank weiter        }
    xor bx,bx
    call dword ptr [p]
    @no3:
    dec ax                  {y-Z„hler dekrementieren }
    jnz @yloop              {wenn nicht 0, weiter    }
    {++++++++++Hauptteil+++++++++++}
    pop ds                  {DS restaurieren         }
  end;
end;

procedure put8(x,y:word;col:byte);assembler;
asm
  mov ax,0a000h               {Videosegment nach ES    }
  mov es,ax
  mov ax,y                    {y mit der horizontalem  }
  mov dx,xres                 {Aufl”sung multiplizieren}
  mul dx                      {und x dazu,             }
  add ax,x
  jnc @keinueberlauf          {auf eventuellen šberlauf}
  inc dx                      {mit Erh”hung von DX     }
@keinueberlauf:               {reagieren               }
  mov di,ax                   {Offset nach DI          }

  cmp dx,bank                 {DX mit voerhergehender  }
  je @gleichebank             {Bank vergleichen und nur}
  mov bank,dx                 {setzen, wenn die Bank   }
  xor bx,bx                   {ungleich der vorigen    }
  call dword ptr [bankproc]   {Zeit!!!                 }

@gleichebank:
  mov al,col                  {Farbe nach AL           }
  mov es:[di],al              {und Punkt setzen        }
end;

procedure line8(x1,y1,x2,y2:word;col:byte);assembler;
var ende:word;
asm
{+++++eventuelle Quelltextmodifikationen rckg„ngig+++++}
mov cl,93h
mov byte ptr cs:[@tausch1],cl
mov byte ptr cs:[@tausch2],cl
mov cx,4040h
mov word ptr cs:[@deltaxgross_incax],cx
mov cx,4343h
mov word ptr cs:[@deltaxgross_incbx],cx
{+++++eventuelle Quelltextmodifikationen rckg„ngig+++++}
  mov ax,0a000h
  mov es,ax
{+++++deltay berechnen und Modifikationen vornehmen+++++}
  mov ax,y2            {y1 von y2 subtrahieren                   }
  sub ax,y1
  jns @deltaypos       {wenn positiv, dann Sprung                }
  neg ax               {deltaY negieren                          }
  mov bl,4Bh           {dec BX                                   }
  mov byte ptr cs:[@deltaxgross_incbx],bl
  mov bl,48h           {dec AX                                   }
  mov byte ptr cs:[@deltaxklein_incax],bl
  @deltaypos:          {deltaY ist positiv                       }
{+++++deltay berechnen und Modifikationen vornehmen+++++}
{+++++deltax berechnen und Modifikationen vornehmen+++++}
  mov bx,x2            {x1 von x2 subtrahieren                   }
  sub bx,x1
  jns @deltaxpos       {wenn positiv, dann Sprung                }
  neg bx               {deltaX negieren                          }
  mov cl,48h           {dec AX                                   }
  mov byte ptr cs:[@deltaxgross_incax],cl
  mov cl,4Bh           {dec BX                                   }
  mov byte ptr cs:[@deltaxklein_incbx],cl
  @deltaxpos:          {deltaX ist positiv                       }
{+++++deltax berechnen und Modifikationen vornehmen+++++}
{+++++++Steigung berechnen, Variablen vorbereiten+++++++}
  cmp ax,bx            {deltay mit deltax vergleichen            }
  jbe @deltaxgross     {wenn kleiner,dann zu deltaxgross springen}
  xchg ax,bx           {damit deltaX durch deltaY geteilt wird   }
  mov cx,100           {(100*deltaX)/deltaY                      }
  mul cx               {mit 100 wegen Nachkommastellen           }
  div bx
  mov si,ax            {Steigung in SI sichern                   }
  mov ax,y2            {Endekoordinate (y2) in ende sichern      }
  mov ende,ax
  mov cl,90h           {nicht ben”tigtes noppen                  }
  mov byte ptr cs:[@deltaxgross_incax],cl
  mov byte ptr cs:[@deltaxgross_incbx],cl
  mov byte ptr cs:[@tausch1],cl
  mov byte ptr cs:[@tausch2],cl
  xor cx,cx            {Nachkommastelle von x auf 0 setzen       }
  mov bx,x1            {Vorkommastelle von x auf ersten x-Wert   }
  mov ax,y1            {Z„hler mit erster y-Stelle initialisieren}
  jmp @normal          {deltaxgross bergehen                    }
  @deltaxgross:        {deltaX ist gr”sser                       }
  mov cx,100           {(100*deltaY)/deltaX                      }
  mul cx               {mit 100 wegen Nachkommastellen           }
  div bx
  mov si,ax            {Steigung in SI sichern}
  mov ax,x2            {Endkoordinate (x2) in ende sichern       }
  mov ende,ax
  mov cl,90h           {nicht ben”tigtes noppen                  }
  mov byte ptr cs:[@deltaxklein_incax],cl
  mov byte ptr cs:[@deltaxklein_incbx],cl
  xor cx,cx            {Nachkommastelle von y auf 0 setzen       }
  mov bx,y1            {Vorkommastelle von y auf ersten y-Wert   }
  mov ax,x1            {Z„hler mit erster x-Stelle initialisieren}
  @normal:
{+++++++Steigung berechnen, Variablen vorbereiten+++++++}
{++++++++++++++++ersten Punkt zeichnen++++++++++++++++++}
  push ax
  push bx              {Register sichern                         }
  push cx
  mov cx,xres
@tausch1:
  xchg bx,ax           {im Normalfall ist ax=x, muá aber y sein  }
  mul cx               {wegen Multiplikation mit x-Aufl”sung     }
  add ax,bx            {x dazu                                   }
  jnc @noover1         {auf šberlauf prfen (32-Bit Ergebnis)    }
  inc dx               {dann Banknummer erh”hen                  }
  @noover1:
  cmp dx,bank          {prfen, ob Bank gleich der vorherigen    }
  je @gleich1
  xor bx,bx
  mov bank,dx          {sonst neue Bank sichern und wechseln     }
  call dword ptr [bankproc]
  @gleich1:
  mov di,ax            {Offset nach AX                           }
  mov al,col           {Farbe nach AL                            }
  mov es:[di],al       {...und abschicken                        }
  pop cx
  pop bx               {Register restaurieren                    }
  pop ax
{++++++++++++++++ersten Punkt zeichnen++++++++++++++++++}
{++++++++++++++++++++Linie zeichnen+++++++++++++++++++++}
@loop:                 {n„chster Punkt                           }

{++++++++++++++++++++Z„hler erh”hen+++++++++++++++++++++}
@deltaxgross_incax:
  inc ax
@deltaxklein_incax:
  inc ax

  add cx,si            {Steigung zu Nachkommastelle              }
  cmp cx,100           {auf šberlauf prfen, wenn nicht Sprung   }
  jb @kleiner
  sub cx,100           {Nachkommastelle um 100 verringern        }

{++++++++++++++++Vorkommastelle erh”hen+++++++++++++++++}
@deltaxgross_incbx:
  inc bx
@deltaxklein_incbx:
  inc bx

@kleiner:

{++++++++Punkt zeichnen, Dokumentation siehe oben+++++++}
  push ax
  push bx
  push cx
  mov cx,xres
@tausch2:
  xchg bx,ax
  mul cx
  add ax,bx
  jnc @noover2
  inc dx
  @noover2:
  cmp dx,bank
  je @gleich2
  mov bank,dx
  xor bx,bx
  call dword ptr [bankproc]
  @gleich2:
  mov di,ax
  mov al,col
  mov es:[di],al
  pop cx
  pop bx
  pop ax

  cmp ax,ende          {Z„hler auf Endekoordinate prfen         }
  jne @loop            {wenn nicht erreicht, dann n„chster Punkt }
{++++++++++++++++++++Linie zeichnen+++++++++++++++++++++}
end;

procedure getpal;
var i:word;
begin
  port[$3c7]:=0;
  for i:=1 to 768 do
    pal[i]:=port[$3c9] ;
end;

procedure setpal;
var i:word;
begin
  port[$3c8]:=0;
  for i:=1 to 768 do
    port[$3c9]:=pal[i];
end;

function get8(x,y:word):byte;
var col:byte;
begin
asm
  mov ax,0a000h                {Videosegment vorbereiten}
  mov es,ax
  mov ax,y                     {y holen und mit         }
  mov dx,xres                  {vertikaler Aufl”sung    }
  mul dx                       {multiplizieren          }
  add ax,x                     {dazu x                  }
  jnc @keinueberlauf           {eventuellen šberlauf    }
  inc dx                       {mit Erh”hung von DX     }
@keinueberlauf:                {abgleichen              }
  mov si,ax                    {Offset nach SI          }

  cmp dx,bank2                 {Wenn die Bank (read)    }
  je @gleichebank              {unglich der vorherigen  }
  mov bank2,dx                 {dann neue Bank setzen   }
  mov bx,1
  call dword ptr [bankproc]

@gleichebank:
  mov al,es:[si]               {Farbe von AL holen      }
  mov col,al                   {und in col sichern      }
end;
get8:=col;
end;

procedure pcxtosprite(name:string;var sprite:rsprite);
var f:file of byte;
    os:word;
    hilfspointer:pointer;
    a,b:byte;
    x,y,z:word;
    xresolution:word;
procedure holeaufloesung;
var hilfsword1,hilfsword2:word;
begin
  seek(f,4);
  read(f,a);
  read(f,b);
  hilfsword1:=(b shl 8)+a;              {xstart holen       }
  seek(f,8);
  read(f,a);
  read(f,b);
  hilfsword2:=(b shl 8)+a;              {xende holen        }
  sprite.xres:=hilfsword2-hilfsword1+1; {Aufl”sung berechnen}
  seek(f,6);
  read(f,a);
  read(f,b);
  hilfsword1:=(b shl 8)+a;              {ystart holen       }
  seek(f,10);
  read(f,a);
  read(f,b);
  hilfsword2:=(b shl 8)+a;              {yende holen        }
  sprite.yres:=hilfsword2-hilfsword1+1; {Aufl”sung berechnen}
end;
procedure holepalette;
var z:word;
begin
seek(f,filesize(f)-768);     {Hier befindet sich die Palette}
for z:=1 to 768 do
begin
  read(f,a);                         {Farb-Byte holen       }
  a:=a shl 2;                        {2 nach links schiften }
  pal[z]:=a;                         {und nach pal schreiben}
end;
end;
begin
  assign(f,name);
  reset(f);
  holeaufloesung;
  getmem(sprite.where,sprite.xres*sprite.yres);
  hilfspointer:=sprite.where;
  holepalette;
  xresolution:=sprite.xres;
seek(f,128);
for y:=0 to sprite.yres-1 do
begin
  x:=0;
  while not (x>(sprite.xres-1)) do
  begin
    read(f,a);
     if (a and 192)=192 then
     begin
       read(f,b);
       for z:=1 to (a and 63) do begin line[x]:=b; inc(x); end;
     end
     else
     begin
       line[x]:=(a and 63);
       inc(x);
     end;
  end;
  os:=y*sprite.xres;
  asm
      lea si,line
      les di,dword ptr hilfspointer
      add di,os
      mov cx,xresolution
      rep movsb
  end;
end;
  close(f);
end;

begin
end.