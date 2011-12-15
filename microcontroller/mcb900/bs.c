#include "bs.h"
#include <string.h>
#include "io.h"

#define NORMAL 0
#define STATE_TDI 1

#define TMS0 0
#define TMS1 1
#define TDI 2
#define TDO 3
#define MASK 4
#define SELTAP 5

unsigned char xdata buffer [BUFFER_SIZE];

static struct {
	unsigned char number_bytes;
	unsigned char * argument;
	unsigned char shifts_done; 
} tdi, tdo, mask;


unsigned char tap_number;
unsigned int iter = 0;
unsigned int size;


unsigned char state;



void update_prog_size(unsigned int prog_size ){
	size = prog_size;
}

void stop(){//this is one way of stopping the run loop below.
   iter = size +1;
}

void step(){
        switch(buffer[iter]){
 		case TMS0: tms(0); break; 
		case TMS1: tms(1);break;
		case TDI: get_tdi();break;
		case TDO: get_tdo(); break;
		case MASK: get_mask(); break;
		case SELTAP: run_seltap(); break;
 	}
}

void run(){
	iter = 0;
	while(iter <= size ){
	 	switch(buffer[iter]){
     		case TMS0: tms(0); break; 
		    case TMS1: tms(1);break;
		    case TDI: get_tdi();break;
		    case TDO: get_tdo(); break;
		    case MASK: get_mask(); break;
		    case SELTAP: run_seltap(); break;
	 	}
	}	
	iter = 0;	
			
}


static void tap1_clock(unsigned char value ){
        if ( tap_number == 1 )
        {
                //TMS1 = value;
                TCK1 = 1;
                TCK1 = 0;
        }
        else
        {
                TMS2 = value;
                TCK2 = 1;
                TCK2 = 0;
        }
}

unsigned char number_bytes;
unsigned long int argument;

void tms(unsigned char value){
        unsigned char i;
	number_bytes = buffer[++iter];
	argument = 0;
	for ( i = 0; i < number_bytes; ++i )
	{
                argument = (argument<<8)|buffer[++iter];
	}
	while ( number_bytes-- > 0 )
	{
	        if ( state ==  NORMAL )
	        {
	            tap1_clock(  value  );
	        }
	        else if ( state == STATE_TDI )
	        {
                TDI1 = (*tdi.argument) & 0x01;
                *tdi.argument = (*tdi.argument) >> 1;
                ++tdi.shifts_done;
                if ( tdi.shifts_done >= 8  )
                {
                   if ( tdi.number_bytes != 0 ) 
                   {
                        tdi.shifts_done = 0;
                        --tdi.number_bytes;
	                     tap1_clock(  value  );
                   }
                   else
                    state =  NORMAL;
                }
                else
	               tap1_clock(  value  ); 
	        }
	}
}


void get_tdi(){
	tdi.number_bytes = buffer[++iter];
	tdi.argument  = &buffer[iter];
	tdi.shifts_done = 0;
	iter += tdi.number_bytes;
}	
	
void get_tdo(){
	tdo.number_bytes = buffer[++iter];
	tdo.argument  = &buffer[iter];
	tdo.shifts_done = 0;
	iter += tdo.number_bytes;
}

void get_mask(){
	mask.number_bytes = buffer[++iter];
	mask.argument  = &buffer[iter];
	mask.shifts_done = 0;
	iter += mask.number_bytes;
}

void run_seltap(){
	++iter; //Going over the number of bytes
    tap_number = buffer[++iter];
}
