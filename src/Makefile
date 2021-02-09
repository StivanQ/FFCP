build:
	javac *.java
	jar cfm MyJar.jar Manifest.txt *.class

xboard: jar
	xboard -debug -fcp "make run"

run:
	java -ea -jar MyJar.jar

run_free_no_ea:
	java -jar MyJar.jar

testare: jar
	xboard -debug -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n"

testare_negru: jar
	xboard -debug -scp "make run" -fcp fairymax -firstInitString "new\nrandom\nsd 2\n"

unuvunu: jar
	xboard -debug -fcp "java -jar MobEval.jar" -scp "java -jar NoMobEval.jar" -tc 1 -inc 1 -autoCallFlag true -mg 100 -sgf unuvunu.png -reuseFirst false -reuseSecond false

notare:
	xboard -debug -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 5 -inc 2 -autoCallFlag true -mg 200 -sgf 18052341.txt -reuseFirst false

notare_live:
	xboard -debug -fcp "java -ea -jar NUME.jar" -scp fairymax -secondInitString "new\nrandom\n" -tc 5 -inc 2 -autoCallFlag true -mg 200 -sgf partideDepth8mobEval.txt -reuseFirst false

notare_background:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\nsd 9\n" -tc 1 -inc 1 -autoCallFlag true -mg 200 -sgf partideDepth9NNN2.txt -reuseFirst false

notare_background2:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\nsd 10\n" -tc 1 -inc 1 -autoCallFlag true -mg 200 -sgf partideDepth10NNN2.txt -reuseFirst false

notare_background3:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 1 -inc 1 -autoCallFlag true -mg 200 -sgf partideDepthUnleashed.txt -reuseFirst false

notare_background4:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 1 -inc 1 -autoCallFlag true -mg 200 -sgf partideDepthUnleashed2.txt -reuseFirst false

notare_background5:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 1 -inc 1 -autoCallFlag true -mg 200 -sgf partideDepthUnleashed2.txt -reuseFirst false

ltc10:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 10 -inc 5 -autoCallFlag true -mg 200 -sgf LTC10.txt -reuseFirst false

ltc30:
	xboard -debug -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 30 -inc 15 -autoCallFlag true -mg 200 -sgf LTC30.txt -reuseFirst false

ltc60:
	xboard -fcp "make run" -scp fairymax -secondInitString "new\nrandom\n" -tc 60 -inc 30 -autoCallFlag true -mg 200 -sgf LTC60.txt -reuseFirst false

bullet:
	xboard -fcp "java -jar BonusPushedPawnsModified.jar" -scp fairymax -secondInitString "new\nrandom\n" -tc 1 -inc 0 -autoCallFlag true -mg 2000 -sgf BULLETBonusPawnsModified.txt -reuseFirst false

blitz:
	xboard -fcp "java -jar BonusPushedPawnsModified.jar" -scp fairymax -secondInitString "new\nrandom\n" -tc 5 -inc 2 -autoCallFlag true -mg 2000 -sgf BLITZBonusPawnsModified.txt -reuseFirst false

bullet1:
	xboard -fcp "java -ea -jar BonusPushedPawnsModified.jar" -scp fairymax -secondInitString "new\nrandom\nsd 4\n" -tc 1 -inc 0 -autoCallFlag true -mg 2000 -sgf BULLETTestDepth4.txt -reuseFirst false

bullet2:
	xboard -fcp "java -jar NoMobEval.jar" -scp fairymax -secondInitString "new\nrandom\n" -tc 1 -inc 0 -autoCallFlag true -mg 2000 -sgf BULLETNoMobEval.txt -reuseFirst false

bullet1v1:
	xboard -fcp "java -ea -jar SmallRandom.jar" -scp "java -ea -jar SmallRandom.jar" -tc 1 -inc 0 -autoCallFlag true -mg 2000 -sgf BULLETTestDepth4.txt -reuseFirst false -reuseSecond false

blitz1:
	xboard -fcp "java -ea -jar Versiune1605ThreeFold.jar" -scp fairymax -secondInitString "new\nrandom\n" -tc 5 -inc 2 -autoCallFlag true -mg 2000 -sgf BLITZ1605Draw.txt -reuseFirst false

blitz2:
	xboard -fcp "java -jar NoMobEval.jar" -scp fairymax -secondInitString "new\nrandom\n" -tc 5 -inc 2 -autoCallFlag true -mg 2000 -sgf BLITZNoMobEval.txt -reuseFirst false


old_vs_new:
	xboard -debug -fcp "java -jar MyJar_old.jar" -scp "java -jar MyJar_new.jar" -tc 1 -inc 1 -autoCallFlag true -mg 200 -sgf OldVSNew.txt -reuseFirst false -reuseSecond false

del_jar:
	rm -f MyJar.jar

del_class:
	rm -f *.class

clean:
	rm -f board.debug
	rm -f MyJar.jar
	rm -f *.class
