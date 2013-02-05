CSSInt_dir=cssInterpreter
#Lexing_dir=lexing
Parsing_dir=parsing

Class_path=.#:other
JavaC=javac
Comp=$(JavaC) -cp $(Class_path)

all: parsing #lexing

# lexing: parsing
# 	@echo "Building lexer..."
# 	(cd $(Lexing_dir); make)
# 	$(Comp) $(CSSInt_dir)/Lexer.java
# 	@echo

parsing:
	@echo "Building parser..."
	(cd $(Parsing_dir); make)
#	$(Comp) $(CSSInt_dir)/Parser.java
	@echo