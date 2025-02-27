package tanjun.calculator;

public class Add extends Symbol {
  public Add() {
    super(0, "+");

    this.setTypeId(2);
    this.setAllowedPredecessors(new int[]{1});
    this.setAllowedSuccessors(new int[]{1});
    this.setNeedsClosing(false);
    this.setIsClosing(false);
    this.setIsValidEnding(false);
  }

  @Override
  public double calculateValue() {
    Symbol successor = this.getSuccessor();
    Symbol predecessor = this.getPredecessor();

    double calculatedValue = 0;
    if (predecessor != null && predecessor.getTypeId() == 1) {
      Symbol firstNumber = predecessor;
      while (firstNumber.getPredecessor() != null && firstNumber.getPredecessor().getTypeId() == 1) {
        firstNumber = firstNumber.getPredecessor();
      }
      calculatedValue += firstNumber.calculateValue();
    }

    if (successor != null && successor.getTypeId() == 1) {
      calculatedValue += successor.calculateValue();
    }

    return calculatedValue;
  }
}
