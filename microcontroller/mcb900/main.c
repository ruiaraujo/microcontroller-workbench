
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
			 
#define DEBUG_PARSER 11

#define COD_PROG_INIT 'p'
#define COD_RUN 'r'
#define COD_STEP 't'
#define COD_STOP 's'

//#define DEBUG 1		 


unsigned int ptr=0;
unsigned char tam_arg =0;
static unsigned char state_parser;
static unsigned char response;

void main()
{
	P0M1 = 0;//quasi bidirectional port
	P0M2 = 0;
	P1M1 = 0x0; //input only
	P1M2 = 0x0;
 	P3M1 = 0;	
	P3M2 = 0x0;
	P2M1 = 0x05;
	P2M2 = 0xFA;
	P2 = 0; // clean Outputs
	TMOD = 0x11;
	com_initialize();
	EA = 1;
	state_parser = WAITING;
#ifdef DEBUG
	state_parser = RUN;
	buffer[0] = 1;	  
	buffer[1] = 1;
	buffer[2] = 5;
	buffer[3] = 5;
	buffer[4] = 1;
	buffer[5] = 1;
	buffer[6] = 0;
	buffer[7] = 1;
	buffer[8] = 1;
	buffer[9] = 1;
	buffer[10] = 1;
	buffer[11] = 2;
	buffer[12] = 0;
	buffer[13] = 1;
	buffer[14] = 2;
	buffer[15] = 2;
	buffer[16] = 1;
	buffer[17] = 0;
	buffer[18] = 3;
	buffer[19] = 1;
	buffer[20] = 0x81;
	buffer[21] = 4;
	buffer[22] = 1;
	buffer[23] = 0xff;
	buffer[24] = 0;
	buffer[25] = 1;
	buffer[26] = 7;
	buffer[27] = 1;
	buffer[28] = 1;
	buffer[29] = 3;
	buffer[30] = 0;
	buffer[31] = 1;
	buffer[32] = 2;
	buffer[33] = 2;	 // sdr Tdi
	buffer[34] = 3;
	buffer[35] = 0x00;
	buffer[36] = 0;
	buffer[37] = 0;
	buffer[38] = 3;
	buffer[39] = 3;
	buffer[40] = 0;
	buffer[41] = 0x0f;
	buffer[42] = 0;
	buffer[43] = 4;
	buffer[44] = 3;
	buffer[45] = 0xf7;
	buffer[46] = 0xff;
	buffer[47] = 0xff;
	buffer[48] = 0;
	buffer[49] = 1;
	buffer[50] = 17;
	buffer[51] = 1;
	buffer[52] = 1;
	buffer[53] = 2;
	update_prog_size(54);  
#endif
	while(1)
	{
		if(state_parser == RUN){
			run();
			state_parser = WAITING;
		}
		if ( state_parser == STEP ){
			step();
			state_parser = WAITING;
		}
		if ( state_parser == DEBUG_PARSER ){
			debug_parser();
			state_parser = WAITING;
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
		//P2 = key;
		if ( key < 10 )
			putdigit(key);
		else
			putchar(key); //if the buffer is full, we don't care
		if(state_parser == WAITING || state_parser == RUN ){
			switch(key){
				case COD_PROG_INIT:  state_parser=INI;ptr = 0; break;
				case COD_RUN: state_parser=RUN; break;
				case COD_STOP:	stop(); break;
				case 'l': state_parser = DEBUG_PARSER; break;
				case COD_STEP: state_parser = STEP; break;
			}
		}
		
		else if(state_parser == INI){								 
		 		if(key==':') {
					state_parser = PARSING_INSTR; 
				}
				if ( key == 'f' )
				{
					 state_parser=DEBUG_PARSER;
					 update_prog_size(ptr);
					 putchar(ptr);
					 ptr = 0;
				}	
		}													
		else if(state_parser == PARSING_INSTR){
		   		buffer[ptr++]=key;
				state_parser = PARSING_TAMARG;	
		}
		else if(state_parser ==  PARSING_TAMARG){
				tam_arg = key;
				buffer[ptr++]=key;
				state_parser = PARSING_ARG;
		}

		else if(state_parser == PARSING_ARG){
				buffer[ptr++]=key;
				--tam_arg;
				if( tam_arg <= 0 ){
					state_parser=INI;	
				}	
		}
		

		
	}
	 

}
