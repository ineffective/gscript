def main() {
	def v1 = 10;
	def v2 = 11;
	def v3 = 13;
	return \() {
		return 	\() {
			return \(p1) {
				io.print(v1, " " , v2, " ", v3, "\n");
				return \() {
					return v1 + v2 + v3 + p1;

				};
			}(1)() + 3;
		};
	}()();
}

