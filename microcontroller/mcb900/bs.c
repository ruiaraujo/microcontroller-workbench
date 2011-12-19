#include "bs.h"
#include <string.h>
#include "io.h"

#define NORMAL 0
#define STATE_TDI 1
#define STATE_TDO 2

#define COD_TMS0 0
#define COD_TMS1 1
#define COD_TDI 2
#define COD_TDO 3
#define COD_MASK 4
#define COD_SELTAP 5
#define COD_RUNTEST 6


#define APPLY_MASK(X) ((X) & (*mask.argument))

#define ERROR_ACK 'e'
#define TDO 'f'
#define TDI 't'

unsigned char xdata buffer [BUFFER_SIZE];

static struct {
	unsigned char number_bytes;
	unsigned char * argument;
	unsigned char shifts_done; 
} tdi, tdo, mask;

unsigned char tdo_read = 0;

unsigned char tap_number = 1;
unsigned int iter = 0;
unsigned int size;


static unsigned char state  = NORMAL;



void update_prog_size(unsigned int prog_size ){
	size = prog_size;
	iter = 0;
}

void stop(){//this is one way of stopping the run loop below.
   iter = size +1;
}
  
void step(){   
	if ( iter >= size )
	{
	
		putstring("itr: ");
		puthexdigit_w(iter/100);
		puthexdigit_w((iter%100)/10 );
		puthexdigit_w(iter%10);
		return;
	}
	#ifdef DEBUG
	putdigit(buffer[iter]);
    #endif
	switch(buffer[iter]){
 		case COD_TMS0: tms(0); break; 
		case COD_TMS1: tms(1);break;
		case COD_TDI: get_tdi();break;
		case COD_TDO: get_tdo(); break;
		case COD_MASK: get_mask(); break;
		case COD_SELTAP: run_seltap(); break;
 	}
}

void run(){
	iter = 0;
    state = NORMAL;
	while(iter < size ){ 
		wait();
		step();
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
					 ;
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		
		wait();
		wait();
		wait();
		wait();
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		wait();
		wait();
		wait();
		wait();
		
		
		wait();
		wait();
		wait();
		wait();

	}
	putstring("itr: ");
	puthexdigit_w(iter/100);
	puthexdigit_w((iter%100)/10 );
	puthexdigit_w(iter%10);
	iter = 0;
    state = NORMAL;		
}


static void tap1_clock(unsigned char value ){
        if ( tap_number == 1 )
        {
                TMS1 = value;
                TCK1 = 1;
		//		wait();
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
unsigned char argument_byte;
unsigned long int argument;


unsigned char i_hate_tdo = 0 ;

void tms(unsigned char value){
        unsigned char i;
		
  //  putstring("TMS R ");
//	putchar_w('0'+value);
	number_bytes = buffer[++iter];
//	putdigit(number_bytes/100);
//	putdigit(number_bytes/10);
//	putdigit(number_bytes%10);
//	putchar('\n');
	argument = 0;
	for ( i = 0; i < number_bytes; ++i )
	{
                argument = (argument<<8)|buffer[++iter];
	}
//	putdigit(argument%10);
//	putchar('\n');
	while ( argument-- > 0 )
	{
	        if ( state ==  NORMAL )
	        {
			 #ifdef DEBUG
	            putstring("TMS ");
				putchar_w('0'+value);
			#endif
	            tap1_clock(  value  );
	        }
	        else if ( state == STATE_TDI || state == STATE_TDO )
	        {
				if ( state == STATE_TDO )
				{
					 //tdo_read = tdo_read << 1 ;
					 i_hate_tdo = (tap_number==1?TDO1:TDO2);
					 tdo_read = tdo_read|(i_hate_tdo<<tdi.shifts_done);
				}
                if ( tap_number == 1 )
					TDI1 = argument_byte & 0x01;
				else
					TDI2 = argument_byte & 0x01;
                argument_byte = argument_byte >> 1;
                ++tdi.shifts_done;
                if ( tdi.shifts_done >= 8  )
                {
                   if ( tdi.number_bytes > 1 ) 
                   {
				   		if ( state == STATE_TDO )
						{
					   		if ( APPLY_MASK(*tdo.argument) != APPLY_MASK(tdo_read) )
							{
								putchar_w(ERROR_ACK);
								putchar_w(tdi.number_bytes);
								putchar_w(*tdo.argument);
								putchar_w(tdo_read);
							}
							else
							{  
								putchar_w(TDO);
								putchar_w(tdi.number_bytes);
								putchar_w(*tdo.argument);
							}
							tdo_read = 0;
							tdo.argument++;
							mask.argument++;
						}
						putchar_w(TDI);
						putchar_w(tdi.number_bytes);
						putchar_w(*tdo.argument);
                        tdi.shifts_done = 0;
                        --tdi.number_bytes;
						tdi.argument++;
						argument_byte = *tdi.argument;
                   }
                }
				 #ifdef DEBUG
	            putstring("TMS ");
				putchar_w('0'+value);
				 #endif
				tap1_clock(  value  );
				if ( value == 1 )
				{
					if ( state == STATE_TDO )
					{
						if ( APPLY_MASK(*tdo.argument) != APPLY_MASK(tdo_read) )
						{
							putchar_w(ERROR_ACK);
							putchar_w(tdi.number_bytes);
							putchar_w(*tdo.argument);
							putchar_w(tdo_read);
						}
						else
						{
							putchar_w(TDO);
							putchar_w(tdi.number_bytes);
							putchar_w(*tdo.argument);
						}
						tdo_read = 0;
					}
					putchar_w(TDI);
					putchar_w(tdi.number_bytes);
					putchar_w(*tdo.argument);
					state = NORMAL; // if TMS = 1 we are not shifting anymore.
				}
			 
	        }

	}
	putchar_w(ACK);
	++iter;
}


void get_tdi(){
	tdi.number_bytes = buffer[++iter];
	++iter;
	tdi.argument  = &buffer[iter];
	argument_byte = *tdi.argument;
	tdi.shifts_done = 0;
	iter += tdi.number_bytes;
	state = STATE_TDI;
	putchar_w(ACK);
}	
	
void get_tdo(){
	tdo.number_bytes = buffer[++iter];
	++iter;
	tdo.argument  = &buffer[iter];
	tdo.shifts_done = 0;
	tdo_read = 0;
	iter += tdo.number_bytes;   
	putchar_w(ACK);
}

void get_mask(){
	mask.number_bytes = buffer[++iter];
	++iter;
	mask.argument  = &buffer[iter];
	mask.shifts_done = 0;
	iter += mask.number_bytes; 
	state = STATE_TDO;
	putchar_w(ACK);
}

void run_seltap(){
	++iter; //go over the identifier
    tap_number = buffer[++iter];
	++iter;
	putchar_w(ACK);
//	putdigit(tap_number);
}

void debug_parser(){
 	iter = 0;
	while(iter < size ){
		puthexdigit_w( buffer[iter] );
		++iter;
	}	
	iter = 0;
}
