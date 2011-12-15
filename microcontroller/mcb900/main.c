
#include <REG9351.H>			// register definition
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include "io.h"
#include "bs.h"


#define INI 0
#define REC_INSTR 1
#define WAITING 2

#define PARSING_INSTR 4
#define PARSING_NARGS 5
#define PARSING_ARG 6
#define RUN 7
#define STEP 8

#define PARSING_TAMARG 9
#define FIM 10

#define COD_PROG_INIT 'p'

//#define DEBUG 1		 


int ptr=0;
unsigned char tam_arg =0;
static unsigned char state;
static unsigned char response;

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
	TMOD = 0x11;
	com_initialize();
	EA = 1;
	
	state = WAITING;

	while(1)
	{
		if(state == RUN){
			run();
		}
		if ( state == STEP ){
			step();
		}

	}
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
		if ( key < 10 )
			putdigit(key);
		else
			putchar(key); //if the buffer is full, we don't care
		if(state == WAITING || state == RUN ){
			switch(key){
				case COD_PROG_INIT:  state=INI;ptr = 0; break;
				case 'r': state=RUN; break;
				case 's': stop(); break;
				case 't': state = STEP; break;
			}
		}
		
		else if(state == INI){								 
		 		if(key==':') {	//TODO:KILL this 
					state = PARSING_INSTR; 
				}
				if ( key == 'f' )
				{
					 state=WAITING;
					 update_prog_size(ptr);
				}
				
		}
			

															
		else if(state == PARSING_INSTR){
		   		buffer[ptr++]=key;
				state = PARSING_TAMARG;	
		}
		else if(state ==  PARSING_TAMARG){
				tam_arg = key;
				buffer[ptr++]=key;
				state = PARSING_ARG;
		}

		else if(state == PARSING_ARG){
				buffer[ptr++]=key;
				--tam_arg;
				if( tam_arg <= 0 ){
					state=INI;	
				}	
		}
		

		
	}
	 

}
