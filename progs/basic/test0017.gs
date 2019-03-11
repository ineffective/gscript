def getVAL_A(entity) {
	return entity.hmap.get(TestEnum.VAL_A);
}
def do_it(who) {
	def inventory = getVAL_A(who);
	io.print("inventory: ", inventory, "\n");
	if (inventory == null) {
		io.print("NULL, FUCK!\n");
	} else {
		io.print("NOT NULL. FUCK!\n");
	}
	io.print("inventory 2: ", inventory, "\n");
	return inventory;
}

def main() {
	io.print("stuff.get(): ");
	return do_it(stuff);
}

