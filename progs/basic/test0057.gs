// FIXME: INVESTIGATE

def main() {
	def x = Array[0, 1, 2];
	x[0] = 3;
	x[1] = 2;
	x[2] = 1;
	def i = 0;
	for (i = 0 ; i < x.length ; i = i + 1) {
		io.print("x[", i, "] = ", x[i], "\n");
	}
}

