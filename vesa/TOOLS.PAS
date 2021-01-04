unit tools;

interface

function hexb(b:byte):string;
function hexw(w:word):string;
function getb(p:pointer;var o:word):byte;
function getw(p:pointer;var o:word):word;


implementation

function hexb(b:byte):string;
const a:array[0..15]of char=('0','1','2','3','4',
                           '5','6','7','8','9',
                           'A','B','C','D','E','F');
begin
  hexb:=a[b shr 4]+a[b and $0f];
end;

function hexw(w:word):string;
begin
  hexw:=hexb(hi(w))+hexb(lo(w));
end;

function getb(p:pointer;var o:word):byte;
var b:byte;
    o2:word;
begin
o2:=o;
asm
  mov bx,word ptr p
  mov ax,word ptr p+2
  mov es,ax
  add bx,o2
  mov al,es:[bx]
  mov b,al
end;
getb:=b;
inc(o);
end;

function getw(p:pointer;var o:word):word;
var w:word;
    o2:word;
begin
o2:=o;
asm
  mov bx,word ptr p
  mov ax,word ptr p+2
  mov es,ax
  add bx,o2
  mov ax,es:[bx]
  mov w,ax
end;
getw:=w;
inc(o,2);
end;

begin end.