def f2() {
	io.print("in f2\n");
	return 10;
}
def f1() {
	return f2;
}
def main() {
	def f = f1();
	def x = f();
	return x;
}

