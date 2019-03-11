def main() {
	def x = 1;
	def y = \(x) { return x; };
	return y(2);
}

