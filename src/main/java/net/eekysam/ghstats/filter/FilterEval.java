package net.eekysam.ghstats.filter;

import java.text.ParsePosition;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Iterator;

import net.eekysam.ghstats.data.RepoItem;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public class FilterEval extends AbstractEvaluator<Object>
{
	public static final Operator NOT = new Operator("!", 1, Operator.Associativity.RIGHT, 4);
	public static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 1);
	public static final Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);
	
	public static final Operator LESS = new Operator("<", 2, Operator.Associativity.LEFT, 2);
	public static final Operator GREATER = new Operator(">", 2, Operator.Associativity.LEFT, 2);
	public static final Operator LEQUAL = new Operator("<=", 2, Operator.Associativity.LEFT, 2);
	public static final Operator GEQUAL = new Operator(">=", 2, Operator.Associativity.LEFT, 2);
	public static final Operator EQUAL = new Operator("==", 2, Operator.Associativity.LEFT, 2);
	public static final Operator NEQUAL = new Operator("!=", 2, Operator.Associativity.LEFT, 2);
	
	public static final Operator ADD = new Operator("++", 2, Operator.Associativity.LEFT, 3);
	public static final Operator SUB = new Operator("--", 2, Operator.Associativity.LEFT, 3);
	public static final Operator DIFF = new Operator("<>", 2, Operator.Associativity.LEFT, 3);
	
	public final static Constant TRUE = new Constant("true");
	public final static Constant FALSE = new Constant("false");
	public final static Constant NOW = new Constant("now");
	
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
	private static Parameters PARAMS;
	
	static
	{
		PARAMS = new Parameters();
		
		PARAMS.add(NOT);
		PARAMS.add(AND);
		PARAMS.add(OR);
		
		PARAMS.add(LESS);
		PARAMS.add(GREATER);
		PARAMS.add(LEQUAL);
		PARAMS.add(GEQUAL);
		PARAMS.add(EQUAL);
		PARAMS.add(NEQUAL);
		
		PARAMS.add(ADD);
		PARAMS.add(SUB);
		PARAMS.add(DIFF);
		
		PARAMS.add(TRUE);
		PARAMS.add(FALSE);
		PARAMS.add(NOW);
		
		PARAMS.addFunctionBracket(BracketPair.PARENTHESES);
		PARAMS.addExpressionBracket(BracketPair.PARENTHESES);
	}
	
	public FilterEval()
	{
		super(PARAMS);
	}
	
	@Override
	protected Object toValue(String literal, Object evaluationContext)
	{
		if (literal.startsWith("."))
		{
			String varn = literal.substring(1).toUpperCase();
			FilterVar var;
			try
			{
				var = FilterVar.valueOf(varn);
			}
			catch (IllegalArgumentException e)
			{
				throw new IllegalArgumentException(String.format("Could not find a field with name %s", varn));
			}
			if (evaluationContext == null)
			{
				return FilterVar.NOT_LOADED;
			}
			if (evaluationContext instanceof RepoItem)
			{
				RepoItem repo = (RepoItem) evaluationContext;
				return repo.getVar(var);
			}
			else
			{
				throw new IllegalArgumentException("The current context does not have any fields!");
			}
		}
		if (literal.startsWith("\"") && literal.endsWith("\""))
		{
			return literal;
		}
		try
		{
			return Long.parseLong(literal);
		}
		catch (NumberFormatException e)
		{
		}
		try
		{
			TemporalAccessor time = TIME_FORMATTER.parse(literal);
			return Instant.ofEpochSecond(time.getLong(ChronoField.INSTANT_SECONDS));
		}
		catch (DateTimeParseException e)
		{
		}
		try
		{
			Duration dur = Duration.parse(literal);
			return dur;
		}
		catch (DateTimeParseException e)
		{
		}
		throw new IllegalArgumentException(String.format("Part of the filter could not be parsed: %s", literal));
	}
	
	@Override
	protected Object evaluate(Constant constant, Object evaluationContext)
	{
		if (constant == NOW)
		{
			return Instant.now();
		}
		else if (constant == TRUE)
		{
			return true;
		}
		else if (constant == FALSE)
		{
			return false;
		}
		throw new IllegalArgumentException();
	}
	
	@Override
	protected Object evaluate(Operator op, Iterator<Object> operands, Object evaluationContext)
	{
		if (op.getOperandCount() == 2)
		{
			Object o1 = operands.next();
			Object o2 = operands.next();
			Exception error = null;
			try
			{
				if (o1 == FilterVar.NOT_LOADED || o2 == FilterVar.NOT_LOADED)
				{
					return FilterVar.NOT_LOADED;
				}
				else if (o1 instanceof Number && o2 instanceof Number)
				{
					long n1 = ((Number) o1).longValue();
					long n2 = ((Number) o2).longValue();
					return this.doEvaluate(op, n1, n2);
				}
				else if (o1 instanceof Duration && o2 instanceof Duration)
				{
					Duration d1 = (Duration) o1;
					Duration d2 = (Duration) o2;
					return this.doEvaluate(op, d1, d2);
				}
				else if (o1 instanceof Instant && o2 instanceof Instant)
				{
					Instant i1 = (Instant) o1;
					Instant i2 = (Instant) o2;
					return this.doEvaluate(op, i1, i2);
				}
				else if (o1 instanceof Instant && o2 instanceof Duration)
				{
					Instant i1 = (Instant) o1;
					Duration d2 = (Duration) o2;
					if (op == ADD)
					{
						return d2.addTo(i1);
					}
					else if (op == SUB)
					{
						return d2.subtractFrom(i1);
					}
					throw new IllegalArgumentException(String.format("A duration and an instant can only be added (%s) or subtracted (%s).", ADD.getSymbol(), SUB.getSymbol()));
				}
				else if (o1 instanceof Duration && o2 instanceof Instant)
				{
					Instant i2 = (Instant) o2;
					Duration d1 = (Duration) o1;
					if (op == ADD)
					{
						return d1.addTo(i2);
					}
					throw new IllegalArgumentException(String.format("An instant and a duration can only be added (%s).", ADD.getSymbol()));
				}
				else if (o1 instanceof Boolean && o2 instanceof Boolean)
				{
					boolean b1 = (Boolean) o1;
					boolean b2 = (Boolean) o2;
					return this.doEvaluate(op, b1, b2);
				}
				else if (o1 instanceof String && o2 instanceof String)
				{
					String s1 = (String) o1;
					String s2 = (String) o2;
					return this.doEvaluate(op, s1, s2);
				}
			}
			catch (IllegalArgumentException e)
			{
				error = e;
			}
			throw new IllegalArgumentException(String.format("(%s) %s (%s) could not be evaluated.", o1, op.getSymbol(), o2), error);
		}
		else
		{
			Object o1 = operands.next();
			if (o1 instanceof Boolean)
			{
				if (op == FilterEval.NOT)
				{
					return !(Boolean) o1;
				}
			}
			throw new IllegalArgumentException(String.format("(%s) could not be evaluated with the %s operation.", o1, op.getSymbol()));
		}
	}
	
	private Object doEvaluate(Operator op, long o1, long o2)
	{
		if (op == ADD)
		{
			return o1 + o2;
		}
		else if (op == SUB)
		{
			return o1 - o2;
		}
		else if (op == DIFF)
		{
			return Math.abs(o1 - o2);
		}
		else
		{
			return this.doCompare(o1, o2, op, String.format("Numbers can only be used with the addition (%s), subtraction (%s), difference (%s), and comparison (%s, %s, %s, %s, etc) operators.", ADD.getSymbol(), SUB.getSymbol(), DIFF.getSymbol(), EQUAL.getSymbol(), NEQUAL.getSymbol(), LESS.getSymbol(), GREATER.getSymbol()));
		}
	}
	
	private Object doEvaluate(Operator op, Duration o1, Duration o2)
	{
		if (op == ADD)
		{
			return o1.plus(o2);
		}
		else if (op == SUB)
		{
			return o1.minus(o2);
		}
		else if (op == DIFF)
		{
			return o1.minus(o2).abs();
		}
		else
		{
			return this.doCompare(o1.getSeconds(), o2.getSeconds(), op, String.format("Durations can only be used with the addition (%s), subtraction (%s), difference (%s), and comparison (%s, %s, %s, %s, etc) operators.", ADD.getSymbol(), SUB.getSymbol(), DIFF.getSymbol(), EQUAL.getSymbol(), NEQUAL.getSymbol(), LESS.getSymbol(), GREATER.getSymbol()));
		}
	}
	
	private Object doEvaluate(Operator op, Instant o1, Instant o2)
	{
		if (op == DIFF)
		{
			return Duration.between(o1, o2);
		}
		else
		{
			return this.doCompare(o1.getEpochSecond(), o2.getEpochSecond(), op, String.format("Instants (date+time) can only be used with the difference (%s) and comparison (%s, %s, %s, %s, etc) operators.", DIFF.getSymbol(), EQUAL.getSymbol(), NEQUAL.getSymbol(), LESS.getSymbol(), GREATER.getSymbol()));
		}
	}
	
	private Object doEvaluate(Operator op, boolean o1, boolean o2)
	{
		if (op == AND)
		{
			return o1 && o2;
		}
		else if (op == OR)
		{
			return o1 || o2;
		}
		else
		{
			return this.doCompare(o1 ? 1 : 0, o2 ? 1 : 0, op, String.format("Booleans can only be used with the OR (%s), AND (%S), NOT (%s), and comparison (%s, %s, %s, %s, etc) operators.", OR.getSymbol(), AND.getSymbol(), NOT.getSymbol(), EQUAL.getSymbol(), NEQUAL.getSymbol(), LESS.getSymbol(), GREATER.getSymbol()));
		}
	}
	
	private Object doEvaluate(Operator op, String o1, String o2)
	{
		if (op == ADD)
		{
			return o1 + o2;
		}
		else if (op == EQUAL)
		{
			return o1.equalsIgnoreCase(o2);
		}
		else if (op == NEQUAL)
		{
			return !o1.equalsIgnoreCase(o2);
		}
		else
		{
			throw new IllegalArgumentException(String.format("Strings can only be added (%s) or compared with %s or %s.", ADD.getSymbol(), EQUAL.getSymbol(), NEQUAL.getSymbol()));
		}
	}
	
	private Object doCompare(long o1, long o2, Operator op, String onFail)
	{
		if (op == EQUAL)
		{
			return o1 == o2;
		}
		else if (op == LESS)
		{
			return o1 < o2;
		}
		else if (op == GREATER)
		{
			return o1 > o2;
		}
		else if (op == LEQUAL)
		{
			return o1 <= o2;
		}
		else if (op == GEQUAL)
		{
			return o1 >= o2;
		}
		else if (op == NEQUAL)
		{
			return o1 != o2;
		}
		throw new IllegalArgumentException(onFail);
	}
}
