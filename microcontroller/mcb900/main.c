
#include <REG9351.H>			// register definition
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include "io.h"



unsigned char state;
//#define DEBUG 1		 



void main()
{
	P0M1 = 0;//quasi bidirectional port
	P0M2 = 0;
	//All the rest are in ---
	P1M1 = 0;
	P1M2 = 0x0;
 	P3M1 = 0;	
	P3M2 = 0x0;
	P2M1 = 0;
	P2M2 = 0x0;
	P2 = 0; // clean LED
	 
	com_initialize();
	EA = 1;
	
	while(1)
	{
		;
	}
}
void timer0 (void) interrupt 1  {

}
void serial (void) interrupt 4  {

	char key = 0;
	if ( TI != 0 )
	{
		TI = 0;
		send_more();
	}
	if ( RI != 0 )
	{
		key = SBUF;
		RI = 0;
		putchar(key); //if the buffer is full, we don't care
		
	}	 

}
