#ifndef _IO_H_
#define _IO_H_

//initializes the serial port
void com_initialize (void);

//routine used by the serial interrupt
// to send the next char in the buffer
void send_more(void);

//places a char in the output buffer
// returns 0 if it succeeds
// !0 if the buffer is full
#undef putchar
char putchar(char c);
#define putchar_w(c) {while ( putchar(c) ) wait(); }

char putdigit(unsigned char c);

//just a wrapper over putchcar 
// to send a string.
// it will block if th buffer is full
void putstring(char * s);

char puthexdigit(unsigned char val);

void wait_a_lot(void);
void wait(void);
#endif
