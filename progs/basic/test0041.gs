def testFuncOne() {
	io.print("Hello, I am testFuncOne!\n");
	return null;
}

def add(lhs, rhs) { return lhs + rhs; }
def sub(lhs, rhs) { return lhs - rhs; }
def mul(lhs, rhs) { return lhs * rhs; }
def div(lhs, rhs) { return lhs / rhs; }

def apply(f, lhs, rhs) {
	return f(lhs, rhs);
}

def main() {
	io.print("Hello world!\n");
	io.print("Now we will count to three.\n");
	def i = 0;
	for (i = 0 ; i < 3 ; i = i + 1) {
		io.print(i + 1, "\n");
	}
	io.print("Now, we will call a function\n");
	testFuncOne();
	io.print("Now, having two variables, x = 3 and y = 5, we will do some stuff with them.\n");
	def x = 3;
	def y = 5;
	io.print("Now, we create lambda holding these variables in its scope. It also applies function passed to it to these arguments:\n");
	def l = \(f) { return f(x, y); };
	io.print("This is this lambda: ", l, "\n");
	io.print("Result of l(add): ", l(add), "\n");
	io.print("Result of l(sub): ", l(sub), "\n");
	io.print("Result of l(mul): ", l(mul), "\n");
	io.print("Result of l(div): ", l(div), "\n");
	io.print("Changing x and y to floats, 1.23 and 4.56 respectively");
	x = 1.23;
	y = 4.56;
	io.print("Now, we create lambda holding these variables in its scope. It also applies function passed to it to these arguments:\n");
	l = \(f) { return f(x, y); };
	io.print("This is this lambda: ", l, "\n");
	io.print("Result of l(add): ", l(add), "\n");
	io.print("Result of l(sub): ", l(sub), "\n");
	io.print("Result of l(mul): ", l(mul), "\n");
	io.print("Result of l(div): ", l(div), "\n");
	io.print("End of the program!\n");
	return add;
}

