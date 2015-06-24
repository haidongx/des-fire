### 
# @1.0 andreas bachmann
# 
#JAVAHOME = /share/giv1/j2sdk1.3.0rc
JAVAHOME = c:/jdk1.3_04
JAVAC = $(JAVAHOME)/bin/javac
MYCLASS = $(HOME)/diss/mc
CLASSPATH = $(JAVAHOME):$(MYCLASS)
#
SOURCES = Behave.java \
	BehaveDeriv.java \
	BehaveTest.java \
	BehaveDerivTest.java
#
TARGETS = Behave.class \
	BehaveDeriv.class \
	BehaveTest.class \
	BehaveDerivTest.class
#
#
all: $(TARGETS)
#
Behave.class: Behave.java
	$(JAVAC) -classpath $(CLASSPATH) Behave.java
#
BehaveDeriv.class: BehaveDeriv.java
	$(JAVAC) -classpath $(CLASSPATH) BehaveDeriv.java
#
BehaveTest.class: BehaveTest.java
	$(JAVAC) -classpath $(CLASSPATH) BehaveTest.java
#
BehaveDerivTest.class: BehaveDerivTest.java
	$(JAVAC) -classpath $(CLASSPATH) BehaveDerivTest.java
#