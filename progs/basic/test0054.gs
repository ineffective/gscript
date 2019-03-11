def main() {
	def m = Map[
		[ "mul", \(l, r) { return l * r; } ],
		[ "add", \(l, r) { return l + r; } ]
	];
	io.print("mul: ", m["mul"](3, 4), "\n");
	io.print("add: ", m["add"](2, 5), "\n");
	return m;
}

