def main() {
	def x = null;
	if (3 < 4) {
		x = "OK";
	}
	if (3 > 4) {
		x = "BAD";
	}
	if (3 < 4) {
		x = "OK";
	} else {
		x = "BAD";
	}
	if (3 > 4) {
		x = "BAD";
	} else {
		x = "OK";
	}
	return x;
}
