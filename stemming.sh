#! /bin/bash

javac -d bin -cp lib/opennlp-tools-1.9.0.jar src/main/java/filter/*.java src/main/java/io/*.java src/main/java/utils/*.java src/main/java/Tokenizer.java src/main/java/Stemmer.java
java -cp "bin:lib/*" Stemmer
