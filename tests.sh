#!/bin/bash

loginf() {
	echo "INFO: "$@
}
result() {
	echo "RESULT: "$@
}
testok() {
	result "PASSED"
}
testfail() {
	result "FAILED"
}

makeTests() {
echo Making tests: $tests
for x in $tests
do
	echo Making test "$x"
	mkdir -p "progs/basic/.testresults"
	t=progs/basic/$x
	to=progs/basic/.testresults/${x}.out
	te=progs/basic/.testresults/${x}.err
	( gsr -f "$t" ) > "${to}" 2>"${te}"
	dos2unix "$to" "$te" 2>/dev/null 1>/dev/null
done
}

runTests() {
for x in $tests
do
	echo ==== TEST $x ====
	t=progs/basic/$x
	to=progs/basic/.testresults/${x}.out
	te=progs/basic/.testresults/${x}.err
	( gsr -f "${t}" ) 2>test.err >test.out
	dos2unix test.err test.out >/dev/null 2>/dev/null
	if cmp test.err "$te" && cmp test.out "$to"
	then
		testok
	else
		echo ====================
		echo expected out:
		cat $to
		echo found out:
		cat test.out
		echo expected err:
		cat $te
		echo found err:
		cat test.err
		echo ====================
		testfail
	fi
done
}

progs=progs/basic/*.gs
function getTests() {
	{
		cd progs/basic/
		for x in *.gs ; do echo $x ; done
	}
}
tests=$(getTests)
if [ "$1" = "maketests" ]
then
	maketests=1
	shift
fi
if [ $# -gt 0 ]
then
	tests=$@
fi

if [ "$maketests" = 1 ]
then
	makeTests
else
	runTests
fi

rm -f test.out test.err
