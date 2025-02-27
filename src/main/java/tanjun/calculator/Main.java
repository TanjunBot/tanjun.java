package tanjun.calculator;

public class Main {
  public static void main(String[] args) {
    Number number_1 = new Number(5);
    Number number_2 = new Number(7);
    Add add_1 = new Add();
    Number number_4 = new Number(6);
    Number number_5 = new Number(32);
    number_1.setSuccessor(number_2);
    number_2.setSuccessor(add_1);
    add_1.setSuccessor(number_4);
    number_4.setSuccessor(number_5);

    System.out.println(add_1.getPredecessor().getValue());
    System.out.println(add_1.getSuccessor().getValue());

    System.out.println(add_1.calculateValue());
  }
}
