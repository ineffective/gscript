// this tests whether function can be called recursively

def cnt(i) {
	io.print("value of i: ", i, "\n");
	if (i == 0) {
		io.print("i == 0, returning 0\n");
		return 0;
	}
	i = i - 1;
	return 1 + cnt(i);
}

def main() {
	return cnt(5);
}

