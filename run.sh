#!/usr/bin/env bash

manual() {
    echo "Usage: sh $0 [java source file name] [data .csv file] [minsup value in [0,1]]"
    echo "Example: sh $0 A1_G13_t2.java data/g13_processed_12k.csv 0.03"
}

work() {
    if [ -f $1 ]; then
        echo "File $1 exists!"
        javac $1
        classfile=${1%.java}
        java $classfile $2 $3
        rm *.class
    else
        echo "File $1 does NOT exist!"
    fi
}

wrong=0
if [ $# -ne 3 ]; then
    wrong=1
fi

if (( ${wrong} )); then
    manual
else
    work $@
fi