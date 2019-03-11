def main() {
	def obj = List[ \(a, b) { return a + b; }, \(a, b) { return a - b; }, \(a, b) { return a * b ; }, \(a, b) { return a / b; }];
	io.print(obj[0](1, 2), "\n");
	io.print(obj[1](3, 2), "\n");
	io.print(obj[2](3, 5), "\n");
	io.print(obj[3](128, 31), "\n");
	return obj;
}

