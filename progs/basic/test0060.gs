def main() {
	def m = Map[];
	m["a"] = \(a, b) { return a + b; };
	return m["a"](1, 2);
}

