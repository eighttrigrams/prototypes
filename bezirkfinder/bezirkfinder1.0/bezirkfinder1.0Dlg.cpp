// bezirkfinder1.0Dlg.cpp : Implementierungsdatei
//

#include "stdafx.h"
#include "bezirkfinder1.0.h"
#include "bezirkfinder1.0Dlg.h"
#include ".\bezirkfinder1.0dlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// Cbezirkfinder10Dlg Dialogfeld



Cbezirkfinder10Dlg::Cbezirkfinder10Dlg(CWnd* pParent /*=NULL*/)
	: CDialog(Cbezirkfinder10Dlg::IDD, pParent)
	, kontrollmeldung(_T(""))
	, eingabe(_T(""))
	, hneingabe(_T(""))
	, plzeingabe(_T(""))
	, kontrollmeldung2(_T(""))
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
	
	Adressen=new AdressFeld;
	PfadListe=new PfadList(Adressen);
	Bezirke=new BezirkFeld;	
}

void Cbezirkfinder10Dlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_EDIT2, edit_eingabe);
	DDX_Text(pDX, IDC_EDIT2, eingabe);
	DDX_Control(pDX, IDC_HNEINGABE, edit_hneingabe);
	DDX_Text(pDX, IDC_HNEINGABE, hneingabe);
	DDX_Control(pDX, IDC_PLZEINGABE, edit_plzeingabe);
	DDX_Text(pDX, IDC_PLZEINGABE, plzeingabe);
	DDX_Control(pDX, IDC_EDIT3, edit_kontrollfeld2);
	DDX_Text(pDX, IDC_EDIT3, kontrollmeldung2);
	DDX_Control(pDX, IDC_EDIT4, edit_bezirk);
	DDX_Control(pDX, IDC_EDIT5, edit_welle);
	DDX_Control(pDX, IDC_EDIT6, edit_modul);
	DDX_Control(pDX, IDC_LIST1, edit_list);
	DDX_Control(pDX, IDC_PLZLIST, edit_plzlist);
}

BEGIN_MESSAGE_MAP(Cbezirkfinder10Dlg, CDialog)
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP
	ON_EN_CHANGE(IDC_EDIT1, OnEnChangeEdit1)
	ON_EN_CHANGE(IDC_EDIT2, OnEnChangeEdit2)
	ON_EN_CHANGE(IDC_EDIT3, OnEnChangeEdit3)
	ON_LBN_DBLCLK(IDC_LIST1, OnLbnDblclkList1)
	ON_LBN_DBLCLK(IDC_PLZLIST, OnLbnDblclkPlzlist)
	ON_EN_CHANGE(IDC_PLZEINGABE, OnEnChangePlzeingabe)
	ON_EN_CHANGE(IDC_HNEINGABE, OnEnChangeHneingabe)
END_MESSAGE_MAP()


// Cbezirkfinder10Dlg Meldungshandler

BOOL Cbezirkfinder10Dlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	SetIcon(m_hIcon, TRUE);			// Großes Symbol verwenden
	SetIcon(m_hIcon, FALSE);		// Kleines Symbol verwenden

    GetDlgItem(IDC_HNEINGABE)->ShowWindow(FALSE);
    GetDlgItem(IDC_PLZEINGABE)->ShowWindow(FALSE);
	GetDlgItem(IDC_PLZLIST)->ShowWindow(FALSE);
  
    do{edit_list.AddString(Adressen->eintrag[PfadListe->get_index()].strasse);
    }while (PfadListe->inc_iterator());
	

	return TRUE;  // Geben Sie TRUE zurück, außer ein Steuerelement soll den Fokus erhalten
}

void Cbezirkfinder10Dlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // Gerätekontext zum Zeichnen

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Symbol in Clientrechteck zentrieren
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Symbol zeichnen
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

// Die System ruft diese Funktion auf, um den Cursor abzufragen, der angezeigt wird, während der Benutzer
//  das minimierte Fenster mit der Maus zieht.
HCURSOR Cbezirkfinder10Dlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

void Cbezirkfinder10Dlg::OnEnChangeEdit1()
{
}

void Cbezirkfinder10Dlg::OnEnChangeEdit2()//händler für die strasseneingabe
{ drucke_bezirk(Bezirke,"loesche");
  nur_plz=true;
  per_hnr=false;
  edit_kontrollfeld2.SetWindowText("");
  edit_plzeingabe.SetWindowText("");
  edit_hneingabe.SetWindowText("");
  GetDlgItem(IDC_HNEINGABE)->ShowWindow(FALSE);
  GetDlgItem(IDC_PLZEINGABE)->ShowWindow(FALSE);
  GetDlgItem(IDC_PLZLIST)->ShowWindow(FALSE);


  edit_eingabe.GetWindowText(eingabe);
  PfadListe->Refresh(eingabe);
  edit_list.ResetContent();
  edit_plzlist.ResetContent();
  do{edit_list.AddString(Adressen->eintrag[PfadListe->get_index()].strasse);
  }while (PfadListe->inc_iterator());
  


  if (PfadListe->eineinziger==true){
	 //b=0;
	 drucke_bezirk(Bezirke,Adressen->eintrag[PfadListe->i_anfang()].bezirk);
	 edit_kontrollfeld2.SetWindowText("Bezirk gefunden! Bezirk gefunden!");
  }else
  if (PfadListe->gefunden==true){   
	 
	 for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++)
		 if (Adressen->eintrag[i].modus!="x") nur_plz=false;	
	 if (nur_plz) {GetDlgItem(IDC_PLZEINGABE)->ShowWindow(TRUE);
	               edit_kontrollfeld2.SetWindowText("Bitte Plz eingeben!");
				   for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++)
		              edit_plzlist.AddString(Adressen->eintrag[i].plz);
				   GetDlgItem(IDC_PLZLIST)->ShowWindow(TRUE);
	               }
	 else         {GetDlgItem(IDC_HNEINGABE)->ShowWindow(TRUE);
	               edit_kontrollfeld2.SetWindowText("weitere angaben jetzt");}
  }
}

void Cbezirkfinder10Dlg::drucke_bezirk(BezirkFeld *feld,CString bez){
  if (bez=="loesche"){
  edit_bezirk.SetWindowText("");
  edit_welle.SetWindowText("");
  edit_modul.SetWindowText("");
  return;}
  edit_bezirk.SetWindowText(bez);
  for (int i=0;i<feld->anzahl();i++){
	  if (feld->get_bezirk(i)==bez){
		  edit_welle.SetWindowText(feld->get_welle(i));
		  edit_modul.SetWindowText(feld->get_modul(i));
	  }
  }
}



void Cbezirkfinder10Dlg::OnEnChangeEdit3()
{}

void Cbezirkfinder10Dlg::OnLbnDblclkList1()
{edit_list.GetText(edit_list.GetCurSel(), transfer);
 edit_eingabe.SetWindowText(transfer);}

void Cbezirkfinder10Dlg::OnLbnDblclkPlzlist()
{edit_plzlist.GetText(edit_plzlist.GetCurSel(), transfer);
	edit_plzeingabe.SetWindowText(transfer);}

void Cbezirkfinder10Dlg::OnEnChangePlzeingabe()
{   drucke_bezirk(Bezirke,"loesche");
	if (!per_hnr)
    for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++){
			edit_plzeingabe.GetWindowText(transfer);
			if (Adressen->eintrag[i].plz==transfer) {
				 drucke_bezirk(Bezirke,Adressen->eintrag[i].bezirk);
				 edit_kontrollfeld2.SetWindowText("Bezirk gefunden! Bezirk gefunden!");
				 break; 
			}
		}
	else
	for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++){
	     edit_plzeingabe.GetWindowText(transfer);
		 if ((Adressen->eintrag[i].plz==transfer)&&
		     (Adressen->eintrag[i].modus=="x")){
                 drucke_bezirk(Bezirke,Adressen->eintrag[i].bezirk);
				 edit_kontrollfeld2.SetWindowText("Bezirk gefunden! Bezirk gefunden!");
				 break;
		 }
	}
}
int Cbezirkfinder10Dlg::toInt(CString eingabe){
  int laenge=eingabe.GetLength();
  int zahl=0;  char tmp;
  int zaehler=laenge;
  for(;(zaehler>0);zaehler--){  //-1 noch zurück wennn fehler vorhanden 
    tmp=eingabe.GetAt(zaehler-1);
	if ((tmp<48)||(tmp>57)) return -1;
    zahl+=_10hoch(laenge-zaehler)*(tmp-48);
  }        
  return zahl;
}

int Cbezirkfinder10Dlg::_10hoch(int zahl){
    if (zahl==0) return 1;
    int i=1;
    int basis=10;   
    for (;i<zahl;i++) basis*=10;
    return basis;
}  

void Cbezirkfinder10Dlg::OnEnChangeHneingabe()
{  
	per_hnr=false; bool einzeln=false;
	bool bearbeitet;bearbeitet=false;
	GetDlgItem(IDC_PLZLIST)->ShowWindow(FALSE);
	GetDlgItem(IDC_PLZEINGABE)->ShowWindow(FALSE);
	edit_plzeingabe.SetWindowText("");
	edit_plzlist.ResetContent();
    drucke_bezirk(Bezirke,"loesche");
	CString auslese; int vergleich;
	edit_hneingabe.GetWindowText(auslese);
	vergleich=toInt(auslese);
	if (vergleich==-1) return;
    
	for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++)
		if (Adressen->eintrag[i].modus=="e")
		  if (toInt(Adressen->eintrag[i].hnvon)==vergleich)
		  {drucke_bezirk(Bezirke,Adressen->eintrag[i].bezirk);bearbeitet=true;einzeln=true;}

    if (!einzeln)
	if (vergleich%2==1){
	for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++)
		if (Adressen->eintrag[i].modus=="u")
          if ((toInt(Adressen->eintrag[i].hnvon)<=vergleich)&&
			  (toInt(Adressen->eintrag[i].hnbis)>=vergleich))
          {drucke_bezirk(Bezirke,Adressen->eintrag[i].bezirk);bearbeitet=true;}
	}else{
	for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++)
	if (Adressen->eintrag[i].modus=="g")
        if ((toInt(Adressen->eintrag[i].hnvon)<=vergleich)&&
		    (toInt(Adressen->eintrag[i].hnbis)>=vergleich))
          {drucke_bezirk(Bezirke,Adressen->eintrag[i].bezirk);bearbeitet=true;}
	}
    if (!bearbeitet){
		per_hnr=true;
        GetDlgItem(IDC_PLZLIST)->ShowWindow(TRUE);
	    GetDlgItem(IDC_PLZEINGABE)->ShowWindow(TRUE);
		for (int i=PfadListe->i_anfang();i<PfadListe->i_ende()+1;i++)
		  if (Adressen->eintrag[i].modus=="x")
			  edit_plzlist.AddString(Adressen->eintrag[i].plz);

	}

}
