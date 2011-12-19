#ifndef _BST_H_
#define _BSH_H_

#include <REG9351.H>
#include <stdlib.h>
#include <ctype.h>


sbit TDO1 = P2^2;
sbit TCK1 = P2^5;
sbit TDI1 = P2^3;
sbit TMS1 = P2^4;

sbit TDO2 = P2^0;
sbit TCK2 = P2^6;
sbit TDI2 = P2^1;
sbit TMS2 = P2^7;

#define BUFFER_SIZE 512

#define ACK 'a'

extern unsigned char xdata buffer[BUFFER_SIZE];
extern unsigned int iter;

extern unsigned char tap_number;


void update_prog_size(unsigned int size);
void tms(unsigned char);
void run();
void step();
void stop();
void get_tdi();
void get_tdo();
void get_mask();
void run_seltap();

void debug_parser();



#endif
