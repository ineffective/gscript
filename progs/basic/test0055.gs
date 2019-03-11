def add(lhs, rhs) { return lhs + rhs; }
def mul(lhs, rhs) { return lhs * rhs; }

def bind(map, name, l, r) {
	def y = map[name];
	io.print("y: ", y, "\n");
	return \() { return y(l, r); };
}

def main() {
	def m = Map[
		[ "mul", mul ],
		[ "add", add ]
	];
	io.print("m[mul]: ", m["mul"], "\n");
	def f = bind(m, "mul", 2, 3);
	m = null;
	io.print("f: ", f(), "\n");
	io.print("f: ", f(), "\n");
	return m;
}

