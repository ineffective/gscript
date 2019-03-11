def test(e) {
	out.write("got entity: ", e);
	out.write("STR: ", STR(e), " DEX: ", DEX(e), " INT: ", INT(e), " WIS: ", WIS(e));
	toHit = STR(e) / 2 + DEX(e) + INT(e) / 10 + WIS(e) / 10;
	defense = DEFENSE(e);
	out.write("TO HIT: ", toHit);
	for (x = 0; x < 100;  x = x + 1) {
		if (toHit - defense < dice.rand(100)) {
			out.write("HIT! DMG: ", dice.rand(12));
		} else {
			out.write("MISS");
		}
	}
	return true;
}

def PUT_ITEM(where, what) {
	out.write("putting ", NAME(what), " into ", NAME(where));
	if (STORAGE(where) == null) {
		return false;
	}
	if (WEIGHT(what) > CAPACITY(where)) {
		return false;
	}
	inventory = STORAGE(where);
	game.map().removeEntity(what);
	inventory.add(what);
	stuff_weight = 0.0;
	for (x = 0 ; x < inventory.size() ; x = x + 1) {
		stuff_weight = WEIGHT(inventory.get(x));
	}
	new_capacity = STR(where) * 100 - stuff_weight;
	out.write("NEW CAPACITY: ", new_capacity);
	return true;
}

def actPickDflt(who, what, extra) {
        gsobj.setField("name", "Storm");
        gsobj.setField("sex", "female");
        gsobj.setField("age", 37);
        gsobj.setField("strength", 125);
        gsobj.setField("inventory", []);
        gsobj.setField("describe", \() {
                out.write(self.name,  " is a ", self.age, " year old ", self.sex, ". \n");
                out.write("Strength : ", self.strength, "\n");
                out.write("Items cnt: ", self.inventory.size(), "\n");
        });
        gsobj.describe();
        out.write("W imię ojca i syna, jedziemy.\n");
		out.write("TO JSON: ", who.getJson().prettyPrint(gsobj));
	if (PUT_ITEM(who, what)) {
		out.write(NAME(what), " picked up.");
		return true;
	} else {
		out.write("You can't do that.");
		return false;
	}
}
def actExamineDflt(who, what, extra) {
	out.write(DESC(what));
	return true;
}

