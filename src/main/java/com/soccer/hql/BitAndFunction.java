package com.soccer.hql;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class BitAndFunction implements SQLFunction {

	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return true;
	}

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping)
			throws QueryException {
		 return org.hibernate.type.IntegerType.INSTANCE;
	}

	@Override
	public String render(Type firstArgumentType, List arguments,
			SessionFactoryImplementor factory) throws QueryException {
		    if(arguments.size() != 2){
			  throw new IllegalArgumentException("BitAndFunction requires 2 arguments!"); 
			 }
		     return "(" +arguments.get(0).toString() + " & " + arguments.get(1).toString()  + ")"; 
	}

}
