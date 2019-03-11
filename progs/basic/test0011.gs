// we expect this to fail, since 'false' is not treated as an object. this should be most probably
// fixed, but not now!
def getFalse() {
	return false.getClass();
}
def main() {
	return getFalse();
}

