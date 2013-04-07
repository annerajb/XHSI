

#if BYTE_ORDER == LITTLE_ENDIAN
float custom_htonf(float x) {
	float r;
    unsigned char *s1 = (unsigned char *) &x;
    unsigned char *s2 = (unsigned char *) &r;
    s2[0] = s1[3];
    s2[1] = s1[2];
    s2[2] = s1[1];
    s2[3] = s1[0];
	return r;
}
int custom_htoni(int x) {
	int r;
    unsigned char *s1 = (unsigned char *) &x;
    unsigned char *s2 = (unsigned char *) &r;
    s2[0] = s1[3];
    s2[1] = s1[2];
    s2[2] = s1[1];
    s2[3] = s1[0];
	return r;
}
float custom_ntohf(float x) {
	float r;
    unsigned char *s1 = (unsigned char *) &x;
    unsigned char *s2 = (unsigned char *) &r;
    s2[0] = s1[3];
    s2[1] = s1[2];
    s2[2] = s1[1];
    s2[3] = s1[0];
	return r;
}
int custom_ntohi(int x) {
	int r;
    unsigned char *s1 = (unsigned char *) &x;
    unsigned char *s2 = (unsigned char *) &r;
    s2[0] = s1[3];
    s2[1] = s1[2];
    s2[2] = s1[1];
    s2[3] = s1[0];
	return r;
}
#else
#define custom_htonf(x) x;
#define custom_htoni(x) x;
#define custom_ntohf(x) x;
#define custom_ntohi(x) x;
#endif
