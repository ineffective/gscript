def main() {
	def x = main.getCode();
	def i = 0;
	for (i = 0 ; i < x.length ; i = i + 1) {
		io.print("x[", i, "] = ", x[i], "\n");
	}
	return null;
}

