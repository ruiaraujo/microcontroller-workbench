#ifndef _BST_H_
#define _BSH_H_

#include <REG9351.H>
#include <stdlib.h>
#include <ctype.h>


sbit TD01 = P0^1;
sbit TCK1 = P0^2;
sbit TDI1 = P0^0;
sbit TMS1 = P0^3;

sbit TD02 = P1^1;
sbit TCK2 = P1^2;
sbit TDI2 = P1^0;
sbit TMS2 = P1^3;
//sbit ICA     = P2^7;

#define BUFFER_SIZE 512

extern unsigned char xdata buffer[BUFFER_SIZE];
extern unsigned int iter;

extern unsigned char tap_number;


void update_prog_size(unsigned int size);
void run();
void run_tms0();
void run_tms1();
void run_tdi();
void run_tdo();
void run_mask();
void run_seltap();



#endif