def accumulate(init, iterable) {
	def rv = init;
	for (def x | iterable) {
		rv += x ;
	}
	return rv;
}
def main() {
	def arr = Array[1, 2, 3, 4];
	return accumulate(0, arr);
}

