// bezirkfinder1.0.h : Hauptheaderdatei für die bezirkfinder1.0-Anwendung
//

#pragma once

#ifndef __AFXWIN_H__
	#error 'stdafx.h' muss vor dieser Datei in PCH eingeschlossen werden.
#endif

#include "resource.h"		// Hauptsymbole


// Cbezirkfinder10App:
// Siehe bezirkfinder1.0.cpp für die Implementierung dieser Klasse
//

class Cbezirkfinder10App : public CWinApp
{
public:
	Cbezirkfinder10App();

// Überschreibungen
	public:
	virtual BOOL InitInstance();

// Implementierung

	DECLARE_MESSAGE_MAP()
};

extern Cbezirkfinder10App theApp;
