def someCall(lmbd) {
	return \() {
		def y = lmbd(1);
		return y() + 3;
	};
}

def main() {
	def v1 = 10;
	def v2 = 11;
	def v3 = 13;

	def x = \(p1) {
		io.print(v1, " " , v2, " ", v3, "\n");
		return \() {
			return v1 + v2 + v3 + p1;

		};
	};

	def tmp = \() {
		return someCall(x);
	};
	return tmp()();
}

