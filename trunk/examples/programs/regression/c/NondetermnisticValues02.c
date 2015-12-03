//#Safe
/* 
 * Author: heizmann@informatik.uni-freiburg.de
 * Date: 2015-11-12
 * 
 * Check that nondeterministic value for short is in the range of short.
 */

extern short someMethodThatReturnsASignedShort();

int main() {
	if (sizeof(short) == 2 && sizeof(int) > 2) {
		short a = someMethodThatReturnsASignedShort();
		int b = a;
		if (b > 32767) {
			//@ assert \false;
		}
	}
	return 0;
}