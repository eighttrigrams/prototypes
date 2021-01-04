#!/bin/bash



awk 'BEGIN { print "MixerConverter V1.0" 
	
	offset = 24
	RS="$"
	
	}
	{
	if (( $1 == "encoder" ) && ($2 >= 25) && ( $2 <= 56))  
		{
		
		if ($2 == "25") midiCC = 24   + offset
		if ($2 == "26") midiCC = 25   + offset
		if ($2 == "27") midiCC = 26   + offset
		if ($2 == "28") midiCC = 27   + offset
		if ($2 == "29") midiCC = 28   + offset
		if ($2 == "30") midiCC = 29   + offset
		if ($2 == "31") midiCC = 30   + offset
		if ($2 == "32") midiCC = 31   + offset
		
		if ($2 == "33") midiCC = 0    + offset
		if ($2 == "34") midiCC = 1    + offset
		if ($2 == "35") midiCC = 2    + offset
		if ($2 == "36") midiCC = 3   + offset
		if ($2 == "37") midiCC = 4   + offset
		if ($2 == "38") midiCC = 5   + offset
		if ($2 == "39") midiCC = 6   + offset
		if ($2 == "40") midiCC = 7   + offset
		
		if ($2 == "41") midiCC = 8   + offset
		if ($2 == "42") midiCC = 9   + offset
		if ($2 == "43") midiCC = 10   + offset
		if ($2 == "44") midiCC = 11   + offset
		if ($2 == "45") midiCC = 12   + offset
		if ($2 == "46") midiCC = 13   + offset
		if ($2 == "47") midiCC = 14   + offset
		if ($2 == "48") midiCC = 15   + offset
		
		if ($2 == "49") midiCC = 16   + offset
		if ($2 == "50") midiCC = 17   + offset
		if ($2 == "51") midiCC = 18   + offset
		if ($2 == "52") midiCC = 19   + offset
		if ($2 == "53") midiCC = 20   + offset
		if ($2 == "54") midiCC = 21   + offset
		if ($2 == "55") midiCC = 22   + offset
		if ($2 == "56") midiCC = 23   + offset
	
		ch = "13"
		mode = "absolute"
		onValue = 0
		offValue = 127
		led = "1dot"
		show = "off"		

		print "$" $1,$2
		print $3,$4,ch,midiCC,onValue,offValue,mode
		print $10,show
		print $12,led 
		}
	else	
	printf("$" $0)

	
	
	
	}' $1 > $2

