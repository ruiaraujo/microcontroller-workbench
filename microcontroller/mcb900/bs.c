#include "bs.h"
#include <string.h>
#include "io.h"

#define NORMAL 0
#define STATE_TDI 1
#define STATE_TDO 2

//#define DEBUG 1
#define COD_TMS0 0
#define COD_TMS1 1
#define COD_TDI 2
#define COD_TDO 3
#define COD_MASK 4
#define COD_SELTAP 5
#define COD_RUNTEST 6


#define APPLY_MASK(X) ((X) & (*data_to_shift.mask))

#define ERROR_ACK 'e'
#define TDO 'f'
#define TDI 't'
#define SIZE 's'
#define NO_ERROR_TDO 'x'


unsigned char xdata buffer [BUFFER_SIZE];

static struct {
	unsigned char number_bytes;
	unsigned char * tdi;
	unsigned char * tdo;
	unsigned char * mask;
	unsigned char shifts_done; 
} data_to_shift;

unsigned char tdo_read = 0;

unsigned char tap_number = 1;
unsigned int iter = 0;
unsigned int size;


static unsigned char state  = NORMAL;


unsigned char number_bytes;
unsigned char argument_byte;
unsigned long int argument;

unsigned char has_error= 0;
unsigned char should_stop= 0;
unsigned char i_hate_tdo = 0 ;




void update_prog_size(unsigned int prog_size ){
	size = prog_size;
	iter = 0;
	has_error = 0;
	should_stop = 0;
    state = NORMAL;
}

void stop(){//this is one way of stopping the run loop below.
   iter = size +1;
}
  
void step(){   
	if ( iter >= size )
	{
	#ifdef DEBUG
		putstring("itr: ");
		puthexdigit_w(iter/100);
		puthexdigit_w((iter%100)/10 );
		puthexdigit_w(iter%10);
	#endif
	
		iter = 0;
		has_error = 0;
		should_stop = 0;
	    state = NORMAL;
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
	while(iter < size ){ 
		wait();
		step();
		if ( should_stop == 1 )
			break;
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
	#ifdef DEBUG
	putstring("itr: ");
	puthexdigit_w(iter/100);
	puthexdigit_w((iter%100)/10 );
	puthexdigit_w(iter%10);
	#endif
	iter = 0;
	
	has_error = 0;
	should_stop = 0;
    state = NORMAL;		
}


static void tap1_clock(unsigned char value ){
        if ( tap_number == 1 )
        {
                TMS1 = value;
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
					 tdo_read = tdo_read|(i_hate_tdo<<data_to_shift.shifts_done);
				}
                if ( tap_number == 1 )
					TDI1 = argument_byte & 0x01;
				else
					TDI2 = argument_byte & 0x01;
                argument_byte = argument_byte >> 1;
                ++data_to_shift.shifts_done;
                if ( data_to_shift.shifts_done >= 8  )
                {
                   if ( data_to_shift.number_bytes > 1 ) 
                   {
				   		if ( state == STATE_TDO )
						{
					   		if ( APPLY_MASK(*data_to_shift.tdo) != APPLY_MASK(tdo_read) )
							{
								has_error = 1;
								putchar_w(ERROR_ACK);
							}
							else
							{
								putchar_w(NO_ERROR_TDO);  
							}							
							#ifdef DEBUG
							putchar_w(data_to_shift.number_bytes);
							putchar_w(*data_to_shift.tdo);
							#endif
							putchar_w(tdo_read);
							
							putchar_w(TDO);	
							#ifdef DEBUG
								putchar_w(ERROR_ACK);
								putchar_w(data_to_shift.number_bytes);
								putchar_w(*data_to_shift.tdo);
							#endif
							putchar_w(*data_to_shift.tdo);
							tdo_read = 0;
							data_to_shift.tdo++;
							data_to_shift.mask++;
						}
						putchar_w(TDI);
				#ifdef DEBUG
						putchar_w(ERROR_ACK);
						putchar_w(data_to_shift.number_bytes);
						putchar_w(*data_to_shift.tdi);
				#endif
						putchar_w(*data_to_shift.tdi);
                        data_to_shift.shifts_done = 0;
                        --data_to_shift.number_bytes;
						data_to_shift.tdi++;
						argument_byte = *data_to_shift.tdi;
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
						if ( APPLY_MASK(*data_to_shift.tdo) != APPLY_MASK(tdo_read) )
						{
							has_error = 1;
							putchar_w(ERROR_ACK);
						}
						else
						{
							putchar_w(NO_ERROR_TDO);  
						}							
						#ifdef DEBUG
						putchar_w(data_to_shift.number_bytes);
						putchar_w(*data_to_shift.tdo);
						#endif
						putchar_w(tdo_read);
						
						putchar_w(TDO);	
						#ifdef DEBUG
							putchar_w(ERROR_ACK);
							putchar_w(data_to_shift.number_bytes);
							putchar_w(*data_to_shift.tdo);
						#endif
						putchar_w(*data_to_shift.tdo);
						tdo_read = 0;
					}
					putchar_w(TDI);	 
				#ifdef DEBUG
					putchar_w(ERROR_ACK);
					putchar_w(data_to_shift.number_bytes);
					putchar_w(*data_to_shift.tdi);
				#endif
					putchar_w(*data_to_shift.tdi);
					state = NORMAL; // if TMS = 1 we are not shifting anymore.
					if ( has_error == 1 )
					{
						should_stop = 1;
					}
					else
					{
						should_stop = 0;
					}

				}
			 
	        }

	}
	putchar_w(ACK);
	++iter;
}


void get_tdi(){
	data_to_shift.number_bytes = buffer[++iter];
	++iter;
	data_to_shift.tdi  = &buffer[iter];
	argument_byte = *data_to_shift.tdi;
	data_to_shift.shifts_done = 0;
	iter += data_to_shift.number_bytes;
	state = STATE_TDI;
	putchar_w(SIZE);
	putchar_w(data_to_shift.number_bytes);
	putchar_w(ACK);
}	
	
void get_tdo(){
	++iter;
	++iter;
	data_to_shift.tdo  = &buffer[iter];
	iter += data_to_shift.number_bytes;   
	putchar_w(ACK);
}

void get_mask(){
	++iter;
	++iter;
	data_to_shift.mask  = &buffer[iter];
	iter += data_to_shift.number_bytes; 
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
		putchar_w( buffer[iter] );
		++iter;
	}
		
	iter = 0;
}
