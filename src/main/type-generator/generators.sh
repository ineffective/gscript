# this was used to create ArrayOfPrimitivesToIterable.java file from the following code (in file 'inputfile')
#if (cls == PRIMTYPE.class) {
#	return new Iterable<BOXTYPE>() {
#		@Override
#		public Iterator<BOXTYPE> iterator() {
#			return new Iterator<BOXTYPE>() {
#				private int iter = 0;
#				@Override
#				public boolean hasNext() {
#					if (iter < ((PRIMTYPE[])o).length) return true;
#					return false;
#				}
#				@Override
#				public BOXTYPE next() {
#					return ((PRIMTYPE[])o)[iter++];
#				}
#			};
#		}
#	};
#}

# definition of primitive types and respective boxing types (in file 'types')

#boolean Boolean
#byte Byte
#char Character
#double Double
#float Float
#int Integer
#long Long
#short Short
#void Void

rm out ; while read PRIMTYPE BOXTYPE ; do cat inputfile | sed 's!PRIMTYPE!'$PRIMTYPE'!g' | sed 's!BOXTYPE!'$BOXTYPE'!g' >> out ; done < types


