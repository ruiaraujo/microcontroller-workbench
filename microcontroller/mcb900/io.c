#include "io.h"
#include <string.h>
#include <REG9351.H>			// register definition	

#define TBUF_SIZE	48		/* DO NOT CHANGE */

static char tbuf [TBUF_SIZE];

static volatile unsigned char t_in = 0;
static volatile unsigned char t_out = 0;
static volatile unsigned char t_disabled = 0;

//an utility function to be used by putchar 
//calling code 
void wait(void){
	int i = 0;
	for (; i < 100; i++)  {  /* Delay for 100 Counts */
		;                       /* call wait function */
	}
}


char puthexdigit(unsigned char val){
	if ( val < 10 )
		return putchar('0'+val);
	else
		return putchar('7'+val);//'A'-10 == '7'
}

//just a wrapper over putchcar 
// to send a string.
// it will block if th buffer is full
void putstring( char * string ){
	int i = 0;
	while ( string[i] != '\0')
	{
		while ( putchar(string[i]) )  //wait for space in the buffer
			wait();
		++i;
	}

}


//routine used by the serial interrupt
// to send the next char in the buffer
void send_more(void){
	if (t_in != t_out)
		SBUF = tbuf [t_out++];
	else
	{
		t_disabled = 1;
		t_in = 0;
		t_out = 0;
	}
}


static void com_baudrate (void)
{
	EA = 0;                             /* Disable Interrupts */
	
	/*------------------------------------------------
	Clear transmit interrupt and buffer.
	------------------------------------------------*/
	TI = 0;				    /* clear transmit interrupt */
	t_in = 0;			    /* empty transmit buffer */
	t_out = 0;
	t_disabled = 1;			    /* disable transmitter */
	
	
	BRGR1 = 0x00; // 38400 bps
	BRGR0 = 0xB0;
	BRGCON = 0x03; // Select BRG as the clock source for UART
	
	EA = 1;                             /* Enable Interrupts */
}

//initializes the serial port
void com_initialize (void)
{
	/*------------------------------------------------
	Setup BGR to generate the proper baud rate.
	------------------------------------------------*/
	com_baudrate ();
	
	/*------------------------------------------------
	Clear com buffer indexes.
	------------------------------------------------*/
	EA = 0;                         /* Disable Interrupts */
	
	t_in = 0;
	t_out = 0;
	t_disabled = 1;
	
	
	/*------------------------------------------------
	Setup serial port registers.
	------------------------------------------------*/
	SM0 = 0; SM1 = 1;		/* serial port MODE 1 */
	SM2 = 0;
	REN = 1;			/* enable serial receiver */
	
	TI = 0;				/* clear transmit interrupt */
	RI = 0;				/* clear receiver interrupt */
	
	ES = 1;				/* enable serial interrupts */
	PS = 0;				/* set serial interrupts to low priority */
	
	EA = 1;             /* Enable Interrupts */
}

//places a char in the output buffer
// returns 0 if it succeeds
// !0 if the buffer is full
char putchar ( char character)
{
	/*------------------------------------------------
	If the buffer is full, return an error value.
	------------------------------------------------*/
	if ((TBUF_SIZE + 1) <= t_in )
	{
	  //P2 = 0xFF;	
	  return (-1);
	}
	/*------------------------------------------------
	Add the data to the transmit buffer.  If the
	transmit interrupt is disabled, then enable it.
	------------------------------------------------*/
//	EA = 0;                         /* Disable Interrupts */
	
	tbuf [t_in++] = character;
	
	if (t_disabled)			/* if transmitter is disabled */
	{
		t_disabled = 0;
		TI = 1;			/* enable it */
	}
	
//	EA = 1;                         /* Enable Interrupts */
	
	return (0);

}


char putdigit(unsigned char c)
{
	return putchar('0'+c);
}
