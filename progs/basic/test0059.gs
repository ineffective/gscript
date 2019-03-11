// FIXME: INVESTIGATE:
// Can not assign to constant

def main() {
	def m = Map[];
	m["a"] = \(a, b) { return a + b; };
	return m;
}

