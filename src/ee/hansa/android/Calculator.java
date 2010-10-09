package ee.hansa.android;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

class Calculator {
	public BigDecimal evaluate(String inputString) {
		String tok1, tok2;

		StringTokenizer tokens = new StringTokenizer(inputString, "+-/*#", true);

		// get the first token and put its value into result
		if ((tok1 = tokens.nextToken()).equals("+"))
			tok1 = tokens.nextToken();
		else if (tok1.equals("-"))
			tok1 = "-".concat(tokens.nextToken());
		
		BigDecimal result = new BigDecimal("0.00");
		result = result.add(new BigDecimal(tok1));

		// evaluate the expression
		while (tokens.hasMoreTokens()) {
			// get the operator token
			tok1 = tokens.nextToken();

			// get the second operand token
			if ((tok2 = tokens.nextToken()).equals("+"))
				tok2 = tokens.nextToken();
			else if (tok2.equals("-"))
				tok2 = "-".concat(tokens.nextToken());

			// evaluate the subexpression
			switch (tok1.charAt(0)) {
			case '+':
				result = result.add(new BigDecimal(tok2));
				break;
			case '-':
				result = result.subtract(new BigDecimal(tok2));
				break;
			case '#':
			case '/':
				result = result.divide(new BigDecimal(tok2), HALF_EVEN);
				break;
			case '*':
				result = result.multiply(new BigDecimal(tok2));
				break;
			}
		}
		
		return result;
	}
}
