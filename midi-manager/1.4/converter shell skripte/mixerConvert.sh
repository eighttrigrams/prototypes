#!/bin/bash



awk 'BEGIN { print "MixerConverter V1.0" 
	
	channel = "13"
	RS="$"
	}
	{
	if (( $1 == "encoder" ) && ($2 >= 1) && ( $2 <= 24))  
		{
		
		if ($2 == "1") midiCC = 0
		if ($2 == "2") midiCC = 1
		if ($2 == "3") midiCC = 2
		if ($2 == "4") midiCC = 3
		if ($2 == "5") midiCC = 4
		if ($2 == "6") midiCC = 5
		if ($2 == "7") midiCC = 6
		if ($2 == "8") midiCC = 7
		
		if ($2 == "9") midiCC =  64
		if ($2 == "10") midiCC = 65
		if ($2 == "11") midiCC = 66
		if ($2 == "12") midiCC = 67
		if ($2 == "13") midiCC = 68
		if ($2 == "14") midiCC = 69
		if ($2 == "15") midiCC = 70
		if ($2 == "16") midiCC = 71
		
		if ($2 == "17") midiCC = 8
		if ($2 == "18") midiCC = 9
		if ($2 == "19") midiCC = 10
		if ($2 == "20") midiCC = 11
		if ($2 == "21") midiCC = 12
		if ($2 == "22") midiCC = 13
		if ($2 == "23") midiCC = 14
		if ($2 == "24") midiCC = 15
		
		ch = channel
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,$7,$8,$9
		print $10,$11
		print $12,$13 
		}
	else if (( $1 == "button") && ( $2 >= 1 ) && ( $2 <= 8))
	#die buttons - erste gruppe
		{
		if ($2 == "1") midiCC = 0		
		if ($2 == "2") midiCC = 1		
		if ($2 == "3") midiCC = 2		
		if ($2 == "4") midiCC = 3		
		if ($2 == "5") midiCC = 4		
		if ($2 == "6") midiCC = 5		
		if ($2 == "7") midiCC = 6		
		if ($2 == "8") midiCC = 7		
		
		ch = "16"

		offValue = 0
		onValue = 127

		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 9 ) && ( $2 <= 16))
	#die buttons - zweite gruppe	
		{
		if ($2 == "9") midiCC = 0		
		if ($2 == "10") midiCC = 1		
		if ($2 == "11") midiCC = 2		
		if ($2 == "12") midiCC = 3		
		if ($2 == "13") midiCC = 4		
		if ($2 == "14") midiCC = 5		
		if ($2 == "15") midiCC = 6		
		if ($2 == "16") midiCC = 7		
		
		ch = "16"

		offValue = 0
		onValue = 127
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 17 ) && ( $2 <= 24))
	#die buttons - dritte gruppe		
		{
		if ($2 == "17") midiCC = 16		
		if ($2 == "18") midiCC = 17		
		if ($2 == "19") midiCC = 18		
		if ($2 == "20") midiCC = 19	
		if ($2 == "21") midiCC = 20		
		if ($2 == "22") midiCC = 21		
		if ($2 == "23") midiCC = 22		
		if ($2 == "24") midiCC = 23		
		
		ch = channel
		
		offValue = 127
		onValue = 0
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 25 ) && ( $2  <= 32))
	#die buttons - vierte gruppe
		{
		if ($2 == "25") midiCC = 0		
		if ($2 == "26") midiCC = 1		
		if ($2 == "27") midiCC = 2		
		if ($2 == "28") midiCC = 3	
		if ($2 == "29") midiCC = 4		
		if ($2 == "30") midiCC = 5		
		if ($2 == "31") midiCC = 6		
		if ($2 == "32") midiCC = 7		
		
		ch = "16"

		offValue = 0
		onValue = 127
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 33 ) && ( $2  <= 40))
	#buttons - die obere reihe
		{
		if ($2 == "33") midiCC = 16		
		if ($2 == "34") midiCC = 17		
		if ($2 == "35") midiCC = 18		
		if ($2 == "36") midiCC = 19	
		if ($2 == "37") midiCC = 20		
		if ($2 == "38") midiCC = 21		
		if ($2 == "39") midiCC = 22		
		if ($2 == "40") midiCC = 23		
		
		ch = channel
		offValue = 127
		onValue = 0
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 41 ) && ( $2  <= 48))
	#buttons - die untere reihe
		{
		if ($2 == "41") midiCC = 56		
		if ($2 == "42") midiCC = 57		
		if ($2 == "43") midiCC = 58		
		if ($2 == "44") midiCC = 59	
		if ($2 == "45") midiCC = 60		
		if ($2 == "46") midiCC = 61		
		if ($2 == "47") midiCC = 62		
		if ($2 == "48") midiCC = 63		
		
		ch = channel
		offValue = 0
		onValue = 127
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 53 ) && ( $2  <= 56))
	#buttons - die oberen vierer
		{
		if ($2 == "53") midiCC = 96		
		if ($2 == "54") midiCC = 97		
		if ($2 == "55") midiCC = 98		
		if ($2 == "56") midiCC = 99	
		
		ch = "16"
		offValue = 0
		onValue = 127
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else if (( $1 == "button") && ( $2 >= 49 ) && ( $2  <= 52))
	#buttons - die unteren vierer
		{
		if ($2 == "49") midiCC = 100		
		if ($2 == "50") midiCC = 101		
		if ($2 == "51") midiCC = 102		
		if ($2 == "52") midiCC = 103	
		
		ch = "16"
		offValue = 0
		onValue = 127
		
		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,$9
		print $10,$11
		}
	else	
	printf("$" $0)

	
	
	
	}' $1 > $2

