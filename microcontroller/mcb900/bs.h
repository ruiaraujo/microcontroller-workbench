#ifndef _BST_H_
#define _BSH_H_

#include <REG9351.H>
#include <stdlib.h>
#include <ctype.h>


sbit TDO1 = P0^1;
sbit TCK1 = P0^2;
sbit TDI1 = P0^0;
sbit TMS1 = P0^3;

sbit TDO2 = P1^1;
sbit TCK2 = P1^2;
sbit TDI2 = P1^0;
sbit TMS2 = P1^3;

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



#endif
