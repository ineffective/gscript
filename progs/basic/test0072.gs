def add(a, b) {
	return a + b;
}
def mul(a, b) {
	return a * b;

}

def main() {
	def ycomb = \(a, b) { return \(f) { return f(a, b); }; };
	def a = ycomb(add, mul);
	// T(a) = F(f) -> R(f)
	return a(\(a, b) { return a(1, 2) + b(3, 4); });
}

