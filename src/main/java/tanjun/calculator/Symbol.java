package tanjun.calculator;

import java.util.Arrays;

public class Symbol {
  private double value;
  private String display;
  private byte strength;
  private boolean needsClosing;
  private boolean isClosing;
  private boolean isValidEnding;
  /*
    typeId 1 = numbers (0-9)
    typeId 2 = +
    typeId 3 = -
    typeId 4 = *
    typeId 5 = /
   */
  private int typeId;
  private int[] allowedSuccessors;
  private int[] allowedPredecessors;
  private Symbol predecessor;
  private Symbol successor;

  public Symbol(double value, String display) {
    this.value = value;
    this.display = display;
  }

  public double calculateValue() {
    return this.value;
  }

  public double calculateValue(double currentValue) {
    return this.value + currentValue;
  }

  public int countDigits() {
    if (this.typeId != 1) return 0;
    int count = 0;
    double num = this.getValue();
    while (num >= 1) {
      count++;
      num /= 10;
    }
    return count;
  }


  public double getValue() {
    return this.value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getDisplay() {
    return this.display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public byte getStrength() {
    return this.strength;
  }

  public void setStrength(byte strength) {
    this.strength = strength;
  }

  public boolean getNeedsClosing() {
    return this.needsClosing;
  }

  public void setNeedsClosing(boolean needsClosing) {
    this.needsClosing = needsClosing;
  }

  public boolean getIsClosing() {
    return this.isClosing;
  }

  public void setIsClosing(boolean isClosing) {
    this.isClosing = isClosing;
  }

  public boolean getIsValidEnding() {
    return this.isValidEnding;
  }

  public void setIsValidEnding(boolean isValidEnding) {
    this.isValidEnding = isValidEnding;
  }

  public int getTypeId() {
    return this.typeId;
  }

  public void setTypeId(int typeId) {
    this.typeId = typeId;
  }

  public int[] getAllowedSuccessors() {
    return this.allowedSuccessors;
  }

  public void setAllowedSuccessors(int[] allowedSuccessors) {
    this.allowedPredecessors = allowedSuccessors;
  }

  public int[] getAllowedPredecessors() {
    return this.allowedPredecessors;
  }

  public void setAllowedPredecessors(int[] allowedPredecessors) {
    this.allowedPredecessors = allowedPredecessors;
  }

  public Symbol getPredecessor() {
    return this.predecessor;
  }

  public void setPredecessor(Symbol predecessor) {
    if (!predecessorAllowed(predecessor)) return;
    if (this.predecessor != null) this.predecessor.setSuccessor(predecessor);
    predecessor.successor = this;
    this.predecessor = predecessor;
  }

  public Symbol getSuccessor() {
    return this.successor;
  }

  public void setSuccessor(Symbol successor) {
    if (!successorAllowed(successor)) return;
    if (this.successor != null) this.successor.setPredecessor(successor);
    successor.predecessor = this;
    this.successor = successor;
  }

  public boolean successorAllowed(Symbol successor) {
    for (int typeId : allowedSuccessors) {
      if (successor.getTypeId() == typeId) return true;
    }
    return false;
  }

  public boolean predecessorAllowed(Symbol predecessor) {
    for (int typeId : allowedPredecessors) {
      if (predecessor.getTypeId() == typeId) return true;
    }
    return false;
  }
}
