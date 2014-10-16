package com.example.taxcalc;

public class Calculator {
	private int maxDigit, maxValue;
	private int displayValue;
	private Operand leftValue, rightValue, currentValue;
	private Operator operator;
	
	public Calculator() {
		maxDigit = 10000;
		maxValue = 99999;
		clear();
	}
	
	public Calculator(int value) {
		this();
		displayValue = value;
	}
	
	public int put(Integer...params) {
		for(Integer i : params) {
			put(i.intValue());
		}
		
		return displayValue;
	}
	
	public int put(CharSequence c) {
		return put(c.toString());
	}
	
	public int put(String s) {
		return put(Integer.parseInt(s.trim()));
	}
	
	public int put(int num) {
		if(currentValue.value >= maxDigit) {
			return displayValue;
		}
		if(!currentValue.isTyping) {
			displayValue = currentValue.setValue(num);
		}
		else {
			displayValue = currentValue.append(num);
		}
		
		return displayValue;
	}
	
	public int put(Operator op) {
		if(currentValue.equals(leftValue)) {
			if(!leftValue.isTyping) {
				leftValue.setValue(displayValue);
			}
		}
		else {
			if(rightValue.isTyping) {
				displayValue = Math.min(leftValue.setValue(operator.calculate(leftValue, rightValue)), maxValue);
			}
		}
		currentValue = rightValue.reset();
		operator = op;
		
		return displayValue;
	}
	
	public int calculate() {
		if(operator == null) {
			return displayValue;
		}
		
		if(!leftValue.isTyping) {
			leftValue.setValue(displayValue);
		}		
		if(!rightValue.isTyping) {
			rightValue.setValue(displayValue);
		}
		
		displayValue = Math.min(operator.calculate(leftValue, rightValue), maxValue);
		operator = EQUAL.set(operator);
		currentValue = leftValue.reset();
		
		return displayValue;
	}
	
	public int clear() {
		this.leftValue = new Operand();
		this.rightValue = new Operand();
		this.currentValue = this.leftValue;
		this.operator = null;
		this.displayValue = 0;
		
		return displayValue;
	}
	
	public int delete() {		
		if(currentValue.isTyping) {
			displayValue = currentValue.setValue(currentValue.value / 10);
			
			if(displayValue == 0) {
				currentValue.reset();
			}
		}
		else {
			if(currentValue.equals(rightValue)) {
				operator = null;
				currentValue = leftValue;
			}
			displayValue = currentValue.value;
		}
		
		return displayValue;
	}
	
	public int getDisplayValue() {
		return displayValue;
	}
	
	public String getDisplayOperator() {
		return operator == null ? "" : operator.toString();
	}
	
	@Override
	public String toString() {
		return String.format("%,3d", displayValue);
	}
	
	public static final EqualOperator EQUAL = new EqualOperator();
	
	public static final Operator ADD   = new Operator(){
		@Override
		public int calculate(Operand leftValue, Operand rightValue){
			return leftValue.value + rightValue.value;
		}
		@Override
		public String toString() { return "+"; }
	};
	public static final Operator SUB = new Operator(){
		@Override
		public int calculate(Operand leftValue, Operand rightValue){
			return leftValue.value - rightValue.value;
		}
		@Override
		public String toString() { return "-"; }
	};
	public static final Operator MUL = new Operator(){
		@Override
		public int calculate(Operand leftValue, Operand rightValue){
			return leftValue.value * rightValue.value;
		}
		@Override
		public String toString() { return "*"; }
	};
	public static final Operator DIV = new Operator(){
		@Override
		public int calculate(Operand leftValue, Operand rightValue){
			return leftValue.value / rightValue.value;
		}
		@Override
		public String toString() { return "/"; }
	};
	
	private static class Operand {
		public boolean isTyping = false;
		public int value = 0;
		
		public int setValue(int value) {
			this.isTyping = true;
			this.value = value;
			return this.value;
		}
		public int append(int value) {
			this.isTyping = true;
			this.value = (this.value * 10) + value;
			return this.value;
		}
		public Operand reset() {
			isTyping = false;
			value = 0;
			return this;
		}
		public boolean equals(Operand operand) {
			return this.hashCode() == operand.hashCode();
		}
	}
	
	private static interface Operator {
		public int calculate(Operand leftValue, Operand rightValue);
	}
	private static class EqualOperator implements Operator {
		public Operator operator;
		@Override
		public int calculate(Operand leftValue, Operand rightValue) {
			return this.operator.calculate(leftValue, rightValue);
		}
		public Operator set(Operator op) {
			this.operator = op instanceof EqualOperator ? ((EqualOperator)op).operator : op;
			return this;
		}
		@Override
		public String toString() { return "="; }
	}
}
