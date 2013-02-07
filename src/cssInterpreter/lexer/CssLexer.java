package cssInterpreter.lexer;


import cssInterpreter.lexer.Lexer;
import cssInterpreter.node.*;

public class CssLexer extends Lexer {
	private int count;
	private TCommentBegin comment;
	private StringBuffer text;

	// We define a constructor
	public CssLexer(java.io.PushbackReader in) {
		super(in);
	}

	// We define a filter that recognizes nested comments.
	protected void filter() { 
		// if we are in the comment state
		if (state.equals(State.COMMENT)) {
			
			// if we are just entering this state
			if (comment == null) { 
				
				// The token is supposed to be a comment.
				// We keep a reference to it and set the
				// count to one
				
				comment = (TCommentBegin) token;
				text = new StringBuffer(comment.getText());
				count = 1;
				token = null; // continue to scan the input.
				
			} else { 
				// we were already in the comment state
				
				text.append(token.getText()); // accumulate the text.
				
				if (token instanceof TCommentBegin)
					count++;
				else if (token instanceof TCommentEnd)
					count--;
				
				if (count != 0)
					token = null; // continue to scan the input.
				else {
					comment.setText(text.toString());
					token = comment; // return a comment with the full text.
					state = State.NORMAL; // go back to normal.
					comment = null; // We release this reference.
				}
			}
		}
	}
}
