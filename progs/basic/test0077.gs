def main() {
	def x = 1;
	return \() {
		return \() {
			return \() {
				return \() {
					io.print(x, "\n");
					return "OK!";
				}();
			}();
		}();
	}();
}

