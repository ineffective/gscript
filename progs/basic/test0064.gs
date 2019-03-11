def add(a, b) {
        return a + b;
}
def mul(a, b) {
	return a * b;
}
def f(a, b) {
        return \(l, r) { return a(l, r) + b(l, r); };
}

def main() {
        return f(add, mul)(1, 2);
}

