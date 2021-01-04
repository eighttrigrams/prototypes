// bezirkfinder1.0Dlg.h : Headerdatei
//

#pragma once
#include "afxwin.h"
#include ".\adressfeld.h"
#include ".\pfadliste.h"


// Cbezirkfinder10Dlg Dialogfeld
class Cbezirkfinder10Dlg : public CDialog
{
// Konstruktion
public:
	Cbezirkfinder10Dlg(CWnd* pParent = NULL);	// Standardkonstruktor

// Dialogfelddaten
	enum { IDD = IDD_BEZIRKFINDER10_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV-Unterstützung


// Implementierung
protected:
	HICON m_hIcon;
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	CEdit edit_kontrollfeld;
	CString kontrollmeldung;
	PfadList *PfadListe;
	AdressFeld *Adressen;
    BezirkFeld *Bezirke;
	CString transfer;
    bool nur_plz;        //der datensatz unterscheidet sich nur über plz s
    bool per_hnr;        //wenn plzeingabe, dann muss festgestellt werden wie
	                     //der befehl reingekommen ist

	int Cbezirkfinder10Dlg::_10hoch(int zahl);
	int Cbezirkfinder10Dlg::toInt(CString eingabe);
	void drucke_bezirk(BezirkFeld *feld,CString bez);
	afx_msg void OnEnChangeEdit1();
	CEdit edit_eingabe;
	CString eingabe;
	afx_msg void OnEnChangeEdit2();
	CEdit edit_hneingabe;
	CString hneingabe;
	CEdit edit_plzeingabe;
	CString plzeingabe;
	afx_msg void OnEnChangeEdit3();
	CEdit edit_kontrollfeld2;
	CString kontrollmeldung2;
	CEdit edit_bezirk;
	CEdit edit_welle;
	CEdit edit_modul;
	CListBox edit_list;
	afx_msg void OnLbnDblclkList1();
	CListBox edit_plzlist;
	afx_msg void OnLbnDblclkPlzlist();
	afx_msg void OnEnChangePlzeingabe();
	afx_msg void OnEnChangeHneingabe();
};
