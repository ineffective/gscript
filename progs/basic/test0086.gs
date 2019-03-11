def map(f, what) {
	def result = List[];

	for (def i | what)
		result.addLast(f(i));

	return result;
}

def main() {
	def ar = Array[List[0, 1, 2], List[2, 3, 4]];

	return map(\(a) { return map( \(b) { return b + "!"; }, a); }, ar);
}

