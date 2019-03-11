def get_lam(x, y) {
	return \() { return x * y; };
}
def main() {
	return \(a, b, c) {
		return a() + b() + c();
	} (get_lam(1, 2), get_lam(2, 3), get_lam(3, 4));
}

