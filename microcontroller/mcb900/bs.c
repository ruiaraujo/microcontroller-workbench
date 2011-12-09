#include "bs.h"
#include <string.h>
#include "io.h"

#define TMS0 0
#define TMS1 1
#define TDI 2
#define TDO 3
#define MASK 4
#define SELTAP 5

unsigned char xdata buffer [BUFFER_SIZE];

static struct {
	unsigned char line;
	unsigned char instr; 
	unsigned char tam;
	unsigned char arg;
} instruction;


unsigned char tap_number;
unsigned int iter = 0;
unsigned int size;
//unsigned char parsing_state = parsing_line;

int line = 0;

//unsigned char buf=buffer[i];


void update_prog_size(unsigned int prog_size ){
	size = prog_size;
}


void run(){
	 
	 while(iter <= size ){
	 	switch(buffer[iter]){
	 		case TMS0: run_tms0(); iter++; break; 
			case TMS1: run_tms1(); iter++; break;
			case TDI: run_tdi();   iter++; break;
			case TDO: run_tdo();   iter++; break;
			case MASK: run_mask(); iter++; break;
			case SELTAP: run_seltap(); iter++; break;
	 	}
	}		
			
}

void run_tms0(){	
	unsigned char n_tms = buffer[++iter];
	iter++;
	while(n_tms-- >0){
	putstring(" TMS0:");
	while ( putdigit(buffer[iter]) ) wait();	
	}
}

void run_tms1(){	
	unsigned char n_tms = buffer[++iter];
	iter++;
	while(n_tms-- >0){
	putstring(" TMS1:");
	while ( putdigit(buffer[iter]) ) wait();	
	}
}

void run_tdi(){
	unsigned char n_bytes = buffer[++iter];
	iter++;
	putstring(" TDI:");
	while(n_bytes-- >0){
	while ( putdigit(buffer[iter]) ) wait();
	iter++;
	}
}	
	
void run_tdo(){
	unsigned char n_bytes = buffer[++iter];
	putstring(" TDO:");
	while(n_bytes-- >0){
	while ( putdigit(buffer[++iter]) ) wait();	
	}
}

void run_mask(){
	unsigned char n_bytes = buffer[iter];
	putstring(" MASK:");
	putstring(" ");
	while(n_bytes-- >0){
	while ( putdigit(buffer[iter]) ) wait();
	iter++;	
	}

}

void run_seltap(){
	putstring("SELTAP");
	putstring(" ");
	while ( putdigit(buffer[iter]) ) wait();

}

/*
>>>>>>> Stashed changes
unsigned char run_instr(){

	unsigned char response; 
	
	if(parsing_state == parsing_line){
<<<<<<< Updated upstream
		line=buffer[iter];;
=======
		line=(buffer[iter]<<8)|buffer[iter+1];
>>>>>>> Stashed changes
		iter++;
		parsing_state =	parsing_instr;
	}

	else if(parsing_state == parsing_instr){
		
				

	/*	switch(buffer[iter]){
			case '0': iter+2; response = tms0(); iter++;	break;		//i+2 porque neste caso já sabemos que o tam_Arg é 1	 i++ para apontar para a instrução seguinte
			case '1': iter+2; response = tms1(); iter++; 	break;
			case '2': iter++; response = tdi(); iter++; 	break;     //incrementar o i na funcao por cada byte shiftado
			case '3': iter++; response = tdo(); iter++;	break;
			case '4': iter++; response = mask(); iter++;	break;
			case '5': iter+2; response = seltap();	iter++; break;
		

		}
		   */






  
/*unsigned char tms0(){
	unsigned char n_tms =buffer[iter];
	unsigned char j;
>>>>>>> Stashed changes
	if(tap_number=='1'){
		for(j=0;j<n_tms;j++){
			TCK1=1;
			TMS1=0;
			TCK1=0;
			return 'a';
		}
	}

	else if(tap_number=='2'){
		 for(j=0;j<n_tms;j++){
			TCK2=1;
			TMS2=0;
			TCK2=0;
			return 'a';
		}
	
	}

	return 'n'; //acrescentar linha??como é que responde? é putstring?
}

unsigned char tms1(){
<<<<<<< Updated upstream
	int n_tms =atoi(&buffer[iter]);
=======
	int n_tms =buffer[iter];
>>>>>>> Stashed changes
	int j;
	if(tap_number=='1'){
		for(j=0;j<n_tms;j++){
			TCK1=1;
			TMS1=1;
			TCK1=0;
			return 'a';
		}
	}

	else if(tap_number=='2'){
		 for(j=0;j<n_tms;j++){
			TCK2=1;
			TMS2=1;
			TCK2=0;
			return 'a';
		}
	
	}
	return 'n';//erro; so ha duas taps
}

unsigned char seltap(){
	tap_number=buffer[iter];//TODO: see if the tap is supported 
		return 'a';
}

unsigned char tdi(){
				return 0;
<<<<<<< Updated upstream
 }
=======
 }	   */
