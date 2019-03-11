def MAKEITERABLE(o) { return o; }

def newline() { io.print("\n"); }

def main() {
	def OBJECT = List[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
	io.print("OBJECT.getClass() -> ", OBJECT.getClass(), "\n");
	// here starts implementation of for(def x | obj)
	def auto = MAKEITERABLE(OBJECT).iterator();
	def VAR = null;
	while (auto.hasNext()) {
		VAR = auto.next();
		io.print(VAR, " -> ");
	}
	io.print("\nKONIEC!\n\n");
	return 0;
}

