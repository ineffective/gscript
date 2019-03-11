def testDoubleLTInt(lhsdouble : Double, rhsint : Double) {
	if (lhsdouble < rhsint) {
		return "lhs < rhs";
	} else {
		return "lhs >= rhs";
	}
	return 3;
}

def main() {
	return testDoubleLTInt(10.0, 9);
}

