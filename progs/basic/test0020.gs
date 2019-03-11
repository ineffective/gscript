def someCall(lam) {
	return lam();
}

def main() {
	def x = 10;
	return someCall(\() { return x; });
}

