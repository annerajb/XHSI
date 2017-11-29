#ifndef SENDER_H_
#define SENDER_H_

// packet senders
float	sendADCCallback(float, float, int, void *);
float	sendAvionicsCallback(float, float, int, void *);
float	sendAuxiliarySystemsCallback(float, float, int, void *);
float	sendEnginesCallback(float, float, int, void *);
float	sendStaticCallback(float, float, int, void *);
float	sendFmsCallback(float, float, int, void *);
float	sendTcasCallback(float, float, int, void *);

int sendRemoteCommand(int);

#endif
