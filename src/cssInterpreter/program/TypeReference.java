package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;

public interface TypeReference {
	Type getType() throws CompilerException;
	//String getTypeName();
	String toString();
}
