package tanjun.calculator;

public class Number extends Symbol{
  public Number(double value) {
    super(value, String.valueOf(value));
    this.setTypeId(1);
    this.setAllowedPredecessors(new int[]{1, 2, 3, 4, 5});
    this.setAllowedSuccessors(new int[]{1, 2, 3, 4, 5});
    this.setNeedsClosing(false);
    this.setIsClosing(false);
    this.setIsValidEnding(true);
  }

  @Override
  public double calculateValue() {
    Symbol predecessor = this.getPredecessor();
    Symbol successor = this.getSuccessor();
    double calculatedValue = this.getValue();

    if (successor != null && successor.getTypeId() == 1) {
      calculatedValue *= Math.pow(10, successor.countDigits());
      calculatedValue = successor.calculateValue(calculatedValue);
    }

    System.out.println("Returning: " + calculatedValue);
    return calculatedValue;
  }


  @Override
  public double calculateValue(double currentValue) {
    Symbol successor = this.getSuccessor();
    double calculatedValue = currentValue + this.getValue();

    if (successor != null && successor.getTypeId() == 1) {
      calculatedValue *= Math.pow(10, successor.countDigits());
      calculatedValue = successor.calculateValue(calculatedValue);
    }

    System.out.println("Returning: " + calculatedValue);
    return calculatedValue;
  }
}
