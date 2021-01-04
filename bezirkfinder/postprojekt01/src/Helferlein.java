
public class Helferlein {

	static String befreieZeile(String eingabeZeile)
	{   
		String tokenInhalt="";
	    String puffer="";
		int modus=0;
		char z;
	  	LeseSchleife:
		for (int i=0;i<eingabeZeile.length();i++)
	  	{
	  	  z=eingabeZeile.charAt(i);
	  	  switch (z)
	  	  {
	  	  case '<':
	  		if (modus==1)
	  		{
	  			tokenInhalt="";
	  			tokenInhalt=puffer;
	  			puffer="";
	  		}
	  	    break;
	  	  case '>':
	  		if (modus==0){
	  			modus=1;
	  			puffer="";
	  		}
	  		break;
	  	  case ' ':
	  		  break;
	  	  case '\n':break LeseSchleife;
	  	  default:
	  		  puffer=puffer+z;
	  	      break;
	  	  }
	
	  	}
		puffer="";
	  	puffer=tokenInhalt;
		return puffer;
	}
	
	
}
