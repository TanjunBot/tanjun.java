package tanjun.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import tanjun.Tanjun;
import tanjun.database.CurrencyConversion;
import tanjun.database.currencyconversion.CurrencyOption;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Calculator {
  Tanjun tanjun;
  String expression;
  String displayExpression = "";
  Message message;
  int page = 0;
  final static int MAX_PAGES = 3;
  ArrayList<Double> history = new ArrayList<>();
  long messageId;

  CurrencyConversion originCurrency;
  CurrencyConversion destinationCurrency;

  String originTemperature;
  String destinationTemperature;

  String originWeight;
  String destinationWeight;

  String originDistance;
  String destinationDistance;

  String originVelocity;
  String destinationVelocity;

  Double valueA;
  Double valueB;
  Double valueC;
  Double valueX;
  Double valueY;

  public Calculator(Tanjun tanjun, String expression, Message message) {
    this.tanjun = tanjun;
    this.expression = expression != null? expression: "";
    this.message = message;
    this.messageId = message.getIdLong();
  }

  public void callback(ButtonInteractionEvent event) {
    String id = event.getComponentId();
    String[] parts = id.split("_");
    String sign = parts[2];

    if (!parts[0].equals("calc")) return;

    switch (sign) {
      case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "(", ")":
        expression += sign;
        displayExpression += sign;
        break;

      case "del":
        this.deleteCallback();
        break;

      case "ac":
        expression = "";
        displayExpression = "";
        break;

      case "multi":
        expression += "*";
        displayExpression += "×";
        break;

      case "divide":
        expression += "/";
        displayExpression += "÷";
        break;

      case "add":
        expression += "+";
        displayExpression += "+";
        break;

      case "minus":
        expression += "-";
        displayExpression += "-";
        break;

      case "%":
        expression += "%";
        displayExpression += "%";
        break;

      case "pi":
        expression += "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628" +
                "034825342117067982148086513282306647";
        displayExpression += "π";
        break;

      case "e":
        expression += "2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571" +
                "382178525166427427466391932003059921";
        displayExpression += "ℇ";
        break;

      case "sin":
        expression += "sin(";
        displayExpression += "sin(";
        break;

      case "cos":
        expression += "cos(";
        displayExpression += "cos(";
        break;

      case "tan":
        expression += "tan(";
        displayExpression += "tan(";
        break;

      case "asin":
        expression += "asin(";
        displayExpression += "asin(";
        break;

      case "acos":
        expression += "acos(";
        displayExpression += "acos(";
        break;

      case "atan":
        expression += "atan(";
        displayExpression += "atan(";
        break;

      case "square":
        expression += "^2";
        displayExpression += "^2";
        break;

      case "exponent":
        expression += "^";
        displayExpression += "^";
        break;

      case "sqrt":
        expression += "^1/2";
        displayExpression += "^1÷2";
        break;

      case "nthroot":
        expression += "^1/";
        displayExpression += "^1÷";
        break;

      case "ln":
        expression += "log(";
        displayExpression += "ln(";
        break;

      case "ld":
        expression += "log2(";
        displayExpression += "ld(";
        break;

      case "log10":
        expression += "log10(";
        displayExpression += "log₁₀(";
        break;

      case "X", "Y", "A", "B", "C":
        if (displayExpression.isEmpty()) {
          switch (sign) {
            case "X":
              if (valueX == null)
                displayExpression += sign + " := ";
              else
                displayExpression += sign;
              break;
            case "Y":
              if (valueY == null)
                displayExpression += sign + " := ";
              else
                displayExpression += sign;
              break;
            case "A":
              if (valueA == null)
                displayExpression += sign + " := ";
              else
                displayExpression += sign;
              break;
            case "B":
              if (valueB == null)
                displayExpression += sign + " := ";
              else
                displayExpression += sign;
              break;
            case "C":
              if (valueC == null)
                displayExpression += sign + " := ";
              else
                displayExpression += sign;
              break;
          }
        } else {
          displayExpression += sign;
          expression += sign;
        }
        break;

      case "define":
        displayExpression += " := ";
        expression = "";
        break;

      case "lfloor":
        displayExpression += "⌊";
        expression += "floor(";
        break;

      case "rfloor":
        displayExpression += "⌋";
        expression += ")";
        break;

      case "lceil":
        displayExpression += "⌈";
        expression += "ceil(";
        break;

      case "rceil":
        displayExpression += "⌉";
        expression += ")";
        break;

      case "next":
        page++;
        if (page > MAX_PAGES) {
          page = 0;
        }
        break;

      case "prev":
        page--;
        if (page < 0) {
          page = MAX_PAGES - 1;
        }
        break;

      case "=":
        double result = calculateResult();
        if (displayExpression.startsWith("X := ")) {
          valueX = result;
        }
        if (displayExpression.startsWith("Y := ")) {
          valueY = result;
        }
        if (displayExpression.startsWith("A := ")) {
          valueA = result;
        }
        if (displayExpression.startsWith("B := ")) {
          valueB = result;
        }
        if (displayExpression.startsWith("C := ")) {
          valueC = result;
        }
        history.add(result);
        expression = "";
        displayExpression = "";
        break;

      case "ccconv":
        originCurrency = null;
        destinationCurrency = null;
        event.editMessageEmbeds(this.currencyConversionEmbed().build())
                .setComponents(
                        this.currencyConversionOriginSelect(),
                        this.currencyConversionDestinationSelect(),
                        this.currencyConversionButtons()
                ).queue();
        return;

      case "tconv":
        originTemperature = null;
        destinationTemperature = null;
        event.editMessageEmbeds(this.temperatureConversionEmbed().build())
                .setComponents(
                        this.temperatureConversionOriginSelect(),
                        this.temperatureConversionDestinationSelect(),
                        this.temperatureConversionButtons()
                ).queue();
        return;

      case "wconv":
        originWeight = null;
        destinationWeight = null;
        event.editMessageEmbeds(this.weightConversionEmbed().build())
                .setComponents(
                        this.weightConversionOrigin(),
                        this.weightConversionDestination(),
                        this.weightConversionButtons()
                ).queue();
        return;

      case "dconv":
        originDistance = null;
        destinationDistance = null;
        event.editMessageEmbeds(this.weightConversionEmbed().build())
                .setComponents(
                        this.distanceConversionOrigin(),
                        this.distanceConversionDestination(),
                        this.distanceConversionButtons()
                ).queue();
        return;

      case "vconv":
        originVelocity = null;
        destinationVelocity = null;
        event.editMessageEmbeds(this.velocityConversionEmbed().build())
                .setComponents(
                        this.velocityConversionOrigin(),
                        this.velocityConversionDestination(),
                        this.velocityConversionButtons()
                ).queue();
        return;

      case "ccconf":
        double originRate = originCurrency.getValue();
        double destinationRate = destinationCurrency.getValue();

        double rate = originRate/destinationRate;

        displayExpression += "÷" + rate;
        expression += "/" + rate;
        break;

      case "tcconf":
        expression = convertTemperature(expression, originTemperature, destinationTemperature);
        displayExpression = convertTemperature(displayExpression, originTemperature, destinationTemperature)
                .replaceAll("/", "÷")
                .replaceAll("\\*", "×");

        break;

      case "wcconf":
        double originAsGrams = this.getWeightConversionToGram(originWeight);
        double destinationAsGrams = 1 / this.getWeightConversionToGram(destinationWeight);
        double weightFactor = 1.0 / (originAsGrams * destinationAsGrams);

        expression += "/" + weightFactor;
        displayExpression += "÷" + weightFactor;
        break;

      case "dcconf":
        double originAsMeters = this.getDistanceConversionToMeter(originDistance);
        double destinationAsMeters = 1 / this.getDistanceConversionToMeter(destinationDistance);
        double distanceFactor = 1.0 / (originAsMeters * destinationAsMeters);

        expression += "/" + distanceFactor;
        displayExpression += "÷" + distanceFactor;
        break;

      case "vcconf":
        double originAsMetersPerHours = this.getVelocityConversionToMetersPerHour(originVelocity);
        double destinationAsMetersPerHours = 1 / this.getVelocityConversionToMetersPerHour(destinationVelocity);
        double velocityFactor = 1.0 / (originAsMetersPerHours * destinationAsMetersPerHours);

        expression += "/" + velocityFactor;
        displayExpression += "÷" + velocityFactor;
        break;
    }

    EmbedBuilder embedBuilder = generateEmbed();

    if (page == 0) {
      event.editMessageEmbeds(embedBuilder.build())
              .setComponents(
                      this.actionRow1(),
                      this.actionRow2(),
                      this.actionRow3(),
                      this.actionRow4(),
                      this.actionRow5()
              )
              .queue();

    } else if (page == 1) {
      event.editMessageEmbeds(embedBuilder.build())
              .setComponents(
                      this.actionRow6(),
                      this.actionRow7(),
                      this.actionRow8(),
                      this.actionRow9(),
                      this.actionRow10()
              )
              .queue();
    } else if (page == 2) {
      event.editMessageEmbeds(embedBuilder.build())
              .setComponents(
                      this.actionRow11(),
                      this.actionRow12(),
                      this.actionRow13(),
                      this.actionRow14(),
                      this.actionRow15()
              )
              .queue();
    }
  }

  public void deleteCallback() {
    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    System.out.println(expression);

    switch (lastChar) {
      case ' ':
        expression = "";
        displayExpression = "";
        return;

      case 'π', 'ℇ':
        expression = expression.substring(0, expression.length() - 122);
        displayExpression = displayExpression.substring(0, displayExpression.length() - 1);
        return;

      case '⌊':
        expression = expression.substring(0, expression.length() - 5);
        displayExpression = displayExpression.substring(0, displayExpression.length() - 1);
        return;

      case '⌈':
        expression = expression.substring(0, expression.length() - 4);
        displayExpression = displayExpression.substring(0, displayExpression.length() - 1);
        return;

      case '(':
        if (expression.length() >= 6)
          switch (expression.substring(expression.length() - 6)) {
            case "log10(":
              expression = expression.substring(0, expression.length() - 6);
              displayExpression = displayExpression.substring(0, displayExpression.length() - 6);
              return;
          }

        if (expression.length() >= 5)
          switch (expression.substring(expression.length() - 5)) {
            case "asin(", "acos(", "atan(":
              expression = expression.substring(0, expression.length() - 5);
              displayExpression = displayExpression.substring(0, displayExpression.length() - 5);
              return;
            case "log2(":
              expression = expression.substring(0, expression.length() - 5);
              displayExpression = displayExpression.substring(0, displayExpression.length() - 3);
              return;
          }

        System.out.println(expression.length());
        if (expression.length() >= 4)
          switch (expression.substring(expression.length() - 4)) {
            case "sin(", "cos(", "tan(", "log(":
              System.out.println(expression);
              expression = expression.substring(0, expression.length() - 4);
              displayExpression = displayExpression.substring(0, displayExpression.length() - 3);
              System.out.println(expression);
              return;
          }
        break;

      default:
        expression = expression.substring(0, expression.length() - 1);
        displayExpression = displayExpression.substring(0, displayExpression.length() - 1);
        return;
    }
  }

  public void selectCallback(StringSelectInteractionEvent event) throws URISyntaxException, IOException {
    String id = event.getComponentId();
    String[] parts = id.split("_");
    String action = parts[2];

    if (!parts[0].equals("calc")) return;

    String selected = event.getValues().getFirst();

    switch (action) {
      case "ccori":
        originCurrency = CurrencyConversion.get(tanjun, tanjun.getDatabaseConnector(), selected);
        event.editMessageEmbeds(this.currencyConversionEmbed().build())
                .setComponents(
                        this.currencyConversionOriginSelect(),
                        this.currencyConversionDestinationSelect(),
                        this.currencyConversionButtons()
                ).queue();
        break;
      case "ccest":
        destinationCurrency = CurrencyConversion.get(tanjun, tanjun.getDatabaseConnector(), selected);
        event.editMessageEmbeds(this.currencyConversionEmbed().build())
                .setComponents(
                        this.currencyConversionOriginSelect(),
                        this.currencyConversionDestinationSelect(),
                        this.currencyConversionButtons()
                ).queue();
        break;

      case "tcdest":
        destinationTemperature = selected;
        event.editMessageEmbeds(this.temperatureConversionEmbed().build())
                .setComponents(
                        this.temperatureConversionOriginSelect(),
                        this.temperatureConversionDestinationSelect(),
                        this.temperatureConversionButtons()
                ).queue();
        break;

      case "tcori":
        originTemperature = selected;
        event.editMessageEmbeds(this.temperatureConversionEmbed().build())
                .setComponents(
                        this.temperatureConversionOriginSelect(),
                        this.temperatureConversionDestinationSelect(),
                        this.temperatureConversionButtons()
                ).queue();
        break;

      case "wcori":
        originWeight = selected;
        event.editMessageEmbeds(this.weightConversionEmbed().build())
                .setComponents(
                        this.weightConversionOrigin(),
                        this.weightConversionDestination(),
                        this.weightConversionButtons()
                ).queue();
        break;

      case "wcdest":
        destinationWeight = selected;
        event.editMessageEmbeds(this.weightConversionEmbed().build())
                .setComponents(
                        this.weightConversionOrigin(),
                        this.weightConversionDestination(),
                        this.weightConversionButtons()
                ).queue();
        break;

      case "dcori":
        originDistance = selected;
        event.editMessageEmbeds(this.distanceConversionEmbed().build())
                .setComponents(
                        this.distanceConversionOrigin(),
                        this.distanceConversionDestination(),
                        this.distanceConversionButtons()
                ).queue();
        break;

      case "dcdest":
        destinationDistance = selected;
        event.editMessageEmbeds(this.distanceConversionEmbed().build())
                .setComponents(
                        this.distanceConversionOrigin(),
                        this.distanceConversionDestination(),
                        this.distanceConversionButtons()
                ).queue();
        break;

      case "vcori":
        originVelocity = selected;
        event.editMessageEmbeds(this.velocityConversionEmbed().build())
                .setComponents(
                        this.velocityConversionOrigin(),
                        this.velocityConversionDestination(),
                        this.velocityConversionButtons()
                ).queue();
        break;

      case "vcdest":
        destinationVelocity = selected;
        event.editMessageEmbeds(this.velocityConversionEmbed().build())
                .setComponents(
                        this.velocityConversionOrigin(),
                        this.velocityConversionDestination(),
                        this.velocityConversionButtons()
                ).queue();
        break;
    }
  }

  public EmbedBuilder currencyConversionEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    embedBuilder.setTitle("Select Currencys");
    StringBuilder description = new StringBuilder();

    description.append("Select the currency you want to convert from and the currency you want to " +
            "convert into. Once you are finished, click the Confirm button.");

    if (originCurrency != null) {
      description.append("\nOrigin Currency: ")
              .append(originCurrency.getSymbol())
              .append(" (").append(originCurrency.getFullName())
              .append(")");
    }

    if (destinationCurrency != null) {
      description.append("\nOrigin Currency: ")
              .append(destinationCurrency.getSymbol())
              .append(" (")
              .append(destinationCurrency.getFullName())
              .append(")");
    }

    embedBuilder.setDescription(description.toString());

    return embedBuilder;
  }

  private EmbedBuilder temperatureConversionEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    embedBuilder.setTitle("Select Temperatures");

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Select the Temperature you want to convert from and the Temperature you want to convert " +
            "into. Once you are finished, click the Confirm button.");

    if (originTemperature != null)
      stringBuilder.append("\nOrigin Temperature: ")
              .append(originTemperature)
              .append(" (")
              .append(this.getTemperatureName(originTemperature))
              .append(")");

    if (destinationTemperature != null)
      stringBuilder.append("\nDestination Temperature: ")
              .append(destinationTemperature)
              .append(" (")
              .append(this.getTemperatureName(destinationTemperature))
              .append(")");

    embedBuilder.setDescription(stringBuilder.toString());

    return embedBuilder;
  }

  private EmbedBuilder weightConversionEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Select Weights");

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Select the Weights you want to convert from and the Weight you want to convert " +
            "into. Once you are finished, click the Confirm button.");

    if (originWeight != null)
      stringBuilder.append("\nOrigin Weight: ")
              .append(originWeight)
              .append(" (")
              .append(this.getWeightName(originWeight))
              .append(")");

    if (destinationWeight != null)
      stringBuilder.append("\nDestination Weight: ")
              .append(destinationWeight)
              .append(" (")
              .append(this.getWeightName(destinationWeight))
              .append(")");

    embedBuilder.setDescription(stringBuilder.toString());

    return embedBuilder;
  }

  private EmbedBuilder distanceConversionEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Select Distances");

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Select the Distances you want to convert from and the Distances you want to convert " +
            "into. Once you are finished, click the Confirm button.");

    if (originDistance != null)
      stringBuilder.append("\nOrigin Distance: ")
              .append(originDistance)
              .append(" (")
              .append(this.getDistanceName(originDistance))
              .append(")");

    if (destinationDistance != null)
      stringBuilder.append("\nDestination Distance: ")
              .append(destinationDistance)
              .append(" (")
              .append(this.getDistanceName(destinationDistance))
              .append(")");

    embedBuilder.setDescription(stringBuilder.toString());

    return embedBuilder;
  }

  private EmbedBuilder velocityConversionEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setTitle("Select Velocity");

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Select the Velocity you want to convert from and the Velocity you want to convert " +
            "into. Once you are finished, click the Confirm button.");

    if (originVelocity != null)
      stringBuilder.append("\nOrigin Velocity: ")
              .append(originVelocity)
              .append(" (")
              .append(this.getVelocityName(originVelocity))
              .append(")");

    if (destinationVelocity != null)
      stringBuilder.append("\nDestination Velocity: ")
              .append(destinationVelocity)
              .append(" (")
              .append(this.getVelocityName(destinationVelocity))
              .append(")");

    embedBuilder.setDescription(stringBuilder.toString());

    return embedBuilder;
  }

  private double calculateResult() {

    double result = 0;

    try {
      Expression expr = new ExpressionBuilder(expression)
              .variables("X", "Y", "A", "B", "C")
              .build()
              .setVariable("X", valueX == null? 0: valueX)
              .setVariable("Y", valueY == null? 0: valueY)
              .setVariable("A", valueA == null? 0: valueA)
              .setVariable("B", valueB == null? 0: valueB)
              .setVariable("C", valueC == null? 0: valueC);

      result = expr.evaluate();
    } catch (Exception ignored) {

    }

    return result;
  }

  public EmbedBuilder generateEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    embedBuilder.setTitle("Calculator");

    StringBuilder description = new StringBuilder("```\n" + (Objects.equals(displayExpression, "")
            ? "Use the buttons below to enter your expression"
            : displayExpression) + "\n```\n");

    if (!Objects.equals(displayExpression, "") && displayExpression != null) {
      double result = calculateResult();

      description.append("`").append(result).append("`\n");
    }

    if (history != null && !history.isEmpty()) {
      description.append("🕒Verlauf");
      for (double res : history) {
        description.append("\n`").append(res).append("`");
      }
    }

    if (valueA != null) {
      description.append("\nA := `").append(valueA).append("`");
    }

    if (valueB != null) {
      description.append("\nB := `").append(valueB).append("`");
    }

    if (valueC != null) {
      description.append("\nC := `").append(valueC).append("`");
    }

    if (valueX != null) {
      description.append("\nX := `").append(valueX).append("`");
    }

    if (valueY != null) {
      description.append("\nY := `").append(valueY).append("`");
    }

    embedBuilder.setDescription(description.toString());

    return embedBuilder;
  }

  private boolean numbersDisabled() {
    if (displayExpression.isEmpty()) return false;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "-", "+", "×", "÷", ".", "(", "⌈", "⌊", "%", "^", " "));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  private boolean functionsDisabled() {
    if (displayExpression.isEmpty()) return false;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("-", "+", "×", "÷", "(", "⌈", "⌊", "%", "^", " "));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  private boolean constantNumberDisabled() {
    if (displayExpression.isEmpty()) return false;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("-", "+", "×", "÷", ".", "(", "⌈", "⌊", "%", " "));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  private boolean deleteDisabled() {
    return displayExpression.isEmpty();
  }

  private boolean mathSymbolsDisabled() {
    if (displayExpression.isEmpty()) return true;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", ")", "π", "ℇ", "⌉", "⌋", "A", "B", "C", "X", "Y"));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  private boolean minusDisabled() {
    if (displayExpression.isEmpty()) return false;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", ")", "π", "ℇ", "⌉", "⌋", "-", " ", "A", "B", "C", "X", "Y"));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  private boolean decimalPointDisabled() {
    int i = displayExpression.length() - 1;
    boolean hadNumber = false;

    while (i >= 0) {
      char currentChar = displayExpression.charAt(i);
      if (currentChar == '.'){
        return true;
      }

      if (currentChar >= '0' && currentChar <= '9'){
        hadNumber = true;
      }

      if (currentChar == '×' || currentChar == '÷' || currentChar == '-' || currentChar == '+') {
        return !hadNumber;
      }

      i--;
    }
    return !hadNumber;
  }

  private boolean equalsDisabled() {
    if (displayExpression.isEmpty()) return true;

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);
    if (lastChar == ' ') return true;

    int i = 0;
    int bracketCount = 0;
    int floorBracketCount = 0;
    int ceilBracketCount = 0;

    while (i < displayExpression.length()) {
      char currentChar = displayExpression.charAt(i);

      if (currentChar == '(') bracketCount++;
      if (currentChar == ')') bracketCount--;
      if (currentChar == '⌈') ceilBracketCount++;
      if (currentChar == '⌉') ceilBracketCount--;
      if (currentChar == '⌊') floorBracketCount++;
      if (currentChar == '⌋') floorBracketCount--;

      i++;
    }
    return bracketCount != 0 || floorBracketCount != 0 || ceilBracketCount != 0;
  }

  public boolean openBracketDisabled() {
    if (displayExpression.isEmpty()) return false;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("-", "+", "×", "÷"));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  public boolean closeBracketDisabled(char openBracket, char closeBracket) {
    if (displayExpression.isEmpty()) return true;

    int i = 0;
    int bracketCount = 0;

    while (i < displayExpression.length()) {
      char currentChar = displayExpression.charAt(i);

      if (currentChar == openBracket) bracketCount++;
      if (currentChar == closeBracket) bracketCount--;

      i++;
    }

    if (bracketCount == 0) return true;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "π", "ℇ", ")", "⌋", "⌉"));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  public boolean exponentDisabled() {
    if (displayExpression.isEmpty()) return false;

    ArrayList<String> allowedPredecessors = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

    char lastChar = displayExpression.charAt(displayExpression.length() - 1);

    return !allowedPredecessors.contains(String.valueOf(lastChar));
  }

  public boolean variableDisabled(String variable) {
    return switch (variable) {
      case "X" -> (valueX == null && !displayExpression.isEmpty());
      case "Y" -> (valueY == null && !displayExpression.isEmpty());
      case "A" -> (valueA == null && !displayExpression.isEmpty());
      case "B" -> (valueB == null && !displayExpression.isEmpty());
      case "C" -> (valueC == null && !displayExpression.isEmpty());
      default -> true;
    };
  }

  public boolean defineDisabled() {
    if (displayExpression.isEmpty()) return true;
    if (displayExpression.length() > 1) return true;

    ArrayList<String> allowedChars = new ArrayList<>(List.of("A", "B", "C", "X", "Y"));

    char lastChar = displayExpression.charAt(0);

    return !allowedChars.contains(String.valueOf(lastChar));
  }

  public ActionRow actionRow1() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_7", "7").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_8", "8").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_9", "9").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_del", "⌫").withDisabled(deleteDisabled()),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_ac", "AC").withDisabled(deleteDisabled())
    );
  }

  public ActionRow actionRow2() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_4", "4").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_5", "5").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_6", "6").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_multi", "×").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_divide", "÷").withDisabled(mathSymbolsDisabled())
    );
  }

  public ActionRow actionRow3() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_1", "1").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_2", "2").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_3", "3").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_add", "+").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_minus", "-").withDisabled(minusDisabled())
    );
  }

  public ActionRow actionRow4() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_0", "0").withDisabled(numbersDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_.", ".").withDisabled(decimalPointDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_%", "%").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_(", "(").withDisabled(openBracketDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_)", ")").withDisabled(closeBracketDisabled('(', ')'))
    );
  }

  public ActionRow actionRow5() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_prev", "\uD83E\uDC08"),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_next", "\uD83E\uDC0A"),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_pi", "π").withDisabled(constantNumberDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_e", "ℇ").withDisabled(constantNumberDisabled()),
            Button.of(ButtonStyle.SUCCESS, "calc_" + messageId + "_=", "=").withDisabled(equalsDisabled())
    );
  }

  private ActionRow actionRow6() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_sin", "sin").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_cos", "cos").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_tan", "tan").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_del", "⌫").withDisabled(deleteDisabled()),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_ac", "AC").withDisabled(deleteDisabled())
    );
  }

  private ActionRow actionRow7() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_asin", "asin").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_acos", "acos").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_atan", "atan").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_multi", "×").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_divide", "÷").withDisabled(mathSymbolsDisabled())
    );
  }

  private ActionRow actionRow8() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_square", "x²").withDisabled(exponentDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_exponent", "xʸ").withDisabled(exponentDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_sqrt", "√").withDisabled(exponentDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_add", "+").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_minus", "-").withDisabled(minusDisabled())
    );
  }

  private ActionRow actionRow9() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_X", "X").withDisabled(variableDisabled("X")),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_Y", "Y").withDisabled(variableDisabled("Y")),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_A", "A").withDisabled(variableDisabled("A")),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_B", "B").withDisabled(variableDisabled("B")),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_C", "C").withDisabled(variableDisabled("C"))
    );
  }

  private ActionRow actionRow10() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_prev", "\uD83E\uDC08"),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_next", "\uD83E\uDC0A"),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_define", ":=").withDisabled(defineDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_nthroot", "ⁿ√").withDisabled(exponentDisabled()),
            Button.of(ButtonStyle.SUCCESS, "calc_" + messageId + "_=", "=").withDisabled(equalsDisabled())
    );
  }

  private ActionRow actionRow11() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_ld", "ld").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_ln", "ln").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_log10", "log₁₀").withDisabled(functionsDisabled()),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_del", "⌫").withDisabled(deleteDisabled()),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_ac", "AC").withDisabled(deleteDisabled())
    );
  }

  private ActionRow actionRow12() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_lfloor", "⌊").withDisabled(openBracketDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_rfloor", "⌋").withDisabled(closeBracketDisabled('⌊', '⌋')),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_nothin1", "/").asDisabled(),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_multi", "×").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_divide", "÷").withDisabled(mathSymbolsDisabled())
    );
  }

  private ActionRow actionRow13() {
    return ActionRow.of(
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_lceil", "⌈").withDisabled(openBracketDisabled()),
            Button.of(ButtonStyle.SECONDARY, "calc_" + messageId + "_rceil", "⌉").withDisabled(closeBracketDisabled('⌈', '⌉')),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_nothin2", "/").asDisabled(),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_add", "+").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_minus", "-").withDisabled(minusDisabled())
    );
  }

  private ActionRow actionRow14() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_ccconv", "€->$").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_tconv", "C->F").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_wconv", "g->oz").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_dconv", "m->ft").withDisabled(mathSymbolsDisabled()),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_vconv", "mph").withDisabled(mathSymbolsDisabled())
    );
  }

  private ActionRow actionRow15() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_prev", "\uD83E\uDC08"),
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_next", "\uD83E\uDC0A"),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_nothin8", "/").asDisabled(),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_nothin9", "/").asDisabled(),
            Button.of(ButtonStyle.SUCCESS, "calc_" + messageId + "_=", "=").withDisabled(equalsDisabled())
    );
  }

  private ActionRow currencyConversionOriginSelect() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_ccori");

    List<CurrencyOption> currencyOptions = CurrencyConversion.getAllCurrencyOptions();

    for (CurrencyOption option : currencyOptions) {
      menu.addOption(option.getSymbol(), option.getCode(), option.getName());
    }

    if (originCurrency != null)
      menu.setDefaultValues(originCurrency.getName());

    return ActionRow.of(menu.build());
  }

  private ActionRow currencyConversionDestinationSelect() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_ccest");

    List<CurrencyOption> currencyOptions = CurrencyConversion.getAllCurrencyOptions();

    for (CurrencyOption option : currencyOptions) {
      menu.addOption(option.getSymbol(), option.getCode(), option.getName());
    }

    if (destinationCurrency != null)
      menu.setDefaultValues(destinationCurrency.getName());
    
    return ActionRow.of(menu.build());
  }

  private ActionRow currencyConversionButtons() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_ccconf", "Confirm"),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_cccanc", "Cancel")
    );
  }

  private ActionRow temperatureConversionOriginSelect() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_tcori");

    getTemperatureOptions(menu);

    if (originTemperature != null)
      menu.setDefaultValues(originTemperature);

    return ActionRow.of(menu.build());
  }

  private ActionRow temperatureConversionDestinationSelect() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_tcdest");

    getTemperatureOptions(menu);

    if (destinationTemperature != null)
      menu.setDefaultValues(destinationTemperature);

    return ActionRow.of(menu.build());
  }

  private ActionRow temperatureConversionButtons() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_tcconf", "Confirm"),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_tccanc", "Cancel")
    );
  }

  private void getTemperatureOptions(StringSelectMenu.Builder menu) {
    menu.addOption("°C", "C", "Celsius");
    menu.addOption("°F", "F", "Fahrenheit");
    menu.addOption("K", "K", "Kelvin");
    menu.addOption("°R", "R", "Rankine");
    menu.addOption("°RE", "RE", "Réaumur");
    menu.addOption("°D", "D", "Delisle");
    menu.addOption("°N", "N", "Newton");
  }

  private String getTemperatureName(String value) {
    return switch (value) {
      case "F" -> "Fahrenheit";
      case "K" -> "Kelvin";
      case "R" -> "Rankine";
      case "RE" -> "Réaumur";
      case "D" -> "Delisle";
      case "N" -> "Newton";
      default -> "Celsius";
    };
  }

  public static String convertTemperature(String expression, String originScale, String destinationScale) {
    String originToKelvin = convertToKelvinString(originScale).replaceAll("x", expression);
    return convertFromKelvinString(destinationScale).replace("x", originToKelvin);
  }

  private static String convertToKelvinString(String scale) {
    return switch (scale.toUpperCase()) {
      case "C", "Z" -> "x+273.15";
      case "F" -> "x+459.67";
      case "K" -> "x";
      case "R", "RE", "D" -> "x-273.15";
      default -> throw new IllegalArgumentException("Unrecognized scale: " + scale);
    };
  }

  private static String convertFromKelvinString(String scale) {
    return switch (scale.toUpperCase()) {
      case "C" -> "(x)-273.15";
      case "F" -> "((x)-273.15)*9/5+32";
      case "K" -> "(x)";
      case "R" -> "(x)*1.801";
      case "RE" -> "((x)-273.15)*0.8";
      case "D" -> "373.15-(x)*2/3";
      case "N" -> "(x)*100/33+273,15";
      default -> throw new IllegalArgumentException("Unrecognized scale: " + scale);
    };
  }

  private void getWeightOptions(StringSelectMenu.Builder menu) {
    menu.addOption("mg", "mg", "Milligram");
    menu.addOption("g", "g", "Gram");
    menu.addOption("kg", "kg", "Kilogram");
    menu.addOption("ton", "ton", "Metric Ton");
    menu.addOption("oz", "oz", "Ounce");
    menu.addOption("lb", "lb", "Pound");
    menu.addOption("st", "st", "Stone");
    menu.addOption("US ton", "ust", "US Ton");
    menu.addOption("UK ton", "ukt", "UK Ton");
    menu.addOption("µg", "µg", "Microgram");
    menu.addOption("US qtr", "usq", "US Quarter");
    menu.addOption("UK qtr", "ukq", "UK Quarter");
    menu.addOption("US cwt", "usc", "US Hundredweight");
    menu.addOption("UK cwt", "ukc", "UK Hundredweight");
    menu.addOption("ct", "ct", "Carat");
    menu.addOption("gr", "gr", "Grain");
    menu.addOption("sh", "sh", "Shekel");
    menu.addOption("tal", "tal", "Talent");
    menu.addOption("mm", "mm", "Mina");
    menu.addOption("scr", "scr", "Scruple");
  }

  private ActionRow weightConversionOrigin() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_wcori");

    getWeightOptions(menu);

    if (originWeight != null)
      menu.setDefaultValues(originWeight);

    return ActionRow.of(menu.build());
  }

  private ActionRow weightConversionDestination() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_wcdest");

    getWeightOptions(menu);

    if (destinationWeight != null)
      menu.setDefaultValues(destinationWeight);

    return ActionRow.of(menu.build());
  }

  private ActionRow weightConversionButtons() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_wcconf", "Confirm"),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_wccanc", "Cancel")
    );
  }

  private String getWeightName(String weight) {
    return switch (weight) {
      case "mg" -> "Milligram";
      case "g" -> "Gram";
      case "kg" -> "Kilogram";
      case "ton" -> "Metric Ton";
      case "oz" -> "Ounce";
      case "lb" -> "Pound";
      case "st" -> "Stone";
      case "ust" -> "US Ton";
      case "ukt" -> "UK Ton";
      case "µg" -> "Microgram";
      case "usq" -> "US Quarter";
      case "ukq" -> "UK Quarter";
      case "usc" -> "US Hundredweight";
      case "ukc" -> "UK Hundredweight";
      case "ct" -> "Carat";
      case "gr" -> "Grain";
      case "sh" -> "Shekel";
      case "tal" -> "Talent";
      case "mm" -> "Mina";
      case "scr" -> "Scruple";
      default -> "gram";
    };
  }

  private double getWeightConversionToGram(String weight) {
    return switch (weight) {
      case "µg" -> 0.000001;
      case "mg" -> 0.001;
      case "kg" -> 1000;
      case "ton" -> 1_000_000;
      case "oz" -> 28.349523125;
      case "lb" -> 453.59237;
      case "st" -> 6350.29318;
      case "usq" -> 11339.80925;
      case "ukq" -> 12700;
      case "usc" -> 45359.237;
      case "ukc" -> 50802.34544;
      case "ust" -> 907184.74;
      case "ukt" -> 1016046.9088;
      case "ct" -> 0.2;
      case "gr" -> 0.06479891;
      case "sh" -> 11;
      case "tal" -> 26000;
      case "mn" -> 500;
      case "scr" -> 1.2959782;
      default -> 1;
    };
  }

  private void getDistanceOptions(StringSelectMenu.Builder menu) {
    menu.addOption("mm", "mm", "Millimeter");
    menu.addOption("cm", "cm", "Centimeter");
    menu.addOption("m", "m", "Meter");
    menu.addOption("km", "km", "Kilometer");
    menu.addOption("pm", "pm", "Picometer");
    menu.addOption("in", "in", "Inch");
    menu.addOption("ft", "ft", "Foot");
    menu.addOption("yd", "yd", "Yard");
    menu.addOption("mi", "mi", "Mile");
    menu.addOption("dm", "dm", "Decimeter");
    menu.addOption("μm", "μm", "Micrometer");
    menu.addOption("nm", "nm", "Nanometer");
    menu.addOption("nmi", "nmi", "Nautical Mile");
    menu.addOption("furlong", "furlong", "Furlong");
    menu.addOption("chain", "chain", "Chain");
    menu.addOption("rod", "rod", "Rod");
    menu.addOption("hand", "hand", "Hand");
    menu.addOption("league", "league", "League");
    menu.addOption("au", "au", "Astronomical Unit");
    menu.addOption("ly", "ly", "Light Year");
    menu.addOption("pc", "pc", "Parsec");
  }

  private ActionRow distanceConversionOrigin() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_dcori");

    getDistanceOptions(menu);

    if (originDistance != null)
      menu.setDefaultValues(originDistance);

    return ActionRow.of(menu.build());
  }

  private ActionRow distanceConversionDestination() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_dcdest");

    getDistanceOptions(menu);

    if (destinationDistance != null)
      menu.setDefaultValues(destinationDistance);

    return ActionRow.of(menu.build());
  }

  private ActionRow distanceConversionButtons() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_dcconf", "Confirm"),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_dccanc", "Cancel")
    );
  }

  private String getDistanceName(String distance) {
    return switch (distance) {
      case "mm" -> "Millimeter";
      case "cm" -> "Centimeter";
      case "dm" -> "Decimeter";
      case "km" -> "Kilometer";
      case "μm" -> "Micrometer";
      case "nm" -> "Nanometer";
      case "pm" -> "Picometer";
      case "in" -> "Inch";
      case "ft" -> "Foot";
      case "yd" -> "Yard";
      case "mi" -> "Mile";
      case "nmi" -> "Nautical Mile";
      case "furlong" -> "Furlong";
      case "chain" -> "Chain";
      case "rod" -> "Rod";
      case "hand" -> "Hand";
      case "league" -> "League";
      case "au" -> "Astronomical Unit";
      case "ly" -> "Light Year";
      case "pc" -> "Parsec";
      default -> "Meter";
    };
  }

  private double getDistanceConversionToMeter(String distance) {
    return switch (distance) {
      case "mm" -> 0.001;
      case "cm" -> 0.01;
      case "dm" -> 0.1;
      case "km" -> 1000.0;
      case "μm" -> 0.000001;
      case "nm" -> 0.000000001;
      case "pm" -> 0.000000000001;
      case "in" -> 0.0254;
      case "ft" -> 0.3048;
      case "yd" -> 0.9144;
      case "mi" -> 1609.344;
      case "nmi" -> 1852.0;
      case "furlong" -> 201.168;
      case "chain" -> 20.1168;
      case "rod" -> 5.0292;
      case "hand" -> 0.1016;
      case "league" -> 4828.032;
      case "au" -> 149_597_870_700.0;
      case "ly" -> 9_460_730_472_580_800.0;
      case "pc" -> 30_856_775_814_671_900.0;
      default -> 1.0;
    };
  }

  private void getVelocityOptions(StringSelectMenu.Builder menu) {
    menu.addOption("m/s", "mps", "Meters per Second");
    menu.addOption("m/min", "mpm", "Meters per Minute");
    menu.addOption("m/h", "mphr", "Meters per Hour");
    menu.addOption("km/s", "kmps", "Kilometers per Second");
    menu.addOption("km/min", "kmpm", "Kilometers per Minute");
    menu.addOption("km/h", "kmph", "Kilometers per Hour");
    menu.addOption("cm/s", "cmps", "Centimeters per Second");
    menu.addOption("cm/min", "cmpm", "Centimeters per Minute");
    menu.addOption("cm/h", "cmph", "Centimeters per Hour");
    menu.addOption("mm/s", "mmps", "Millimeters per Second");
    menu.addOption("mm/min", "mmpm", "Millimeters per Minute");
    menu.addOption("mm/h", "mmph", "Millimeters per Hour");
    menu.addOption("ft/s", "ftps", "Feet per Second");
    menu.addOption("ft/min", "ftpm", "Feet per Minute");
    menu.addOption("ft/h", "ftph", "Feet per Hour");
    menu.addOption("mi/s", "mips", "Miles per Second");
    menu.addOption("mi/min", "mipm", "Miles per Minute");
    menu.addOption("mph", "mph", "Miles per Hour");
    menu.addOption("in/s", "inps", "Inches per Second");
    menu.addOption("in/min", "inpm", "Inches per Minute");
    menu.addOption("in/h", "inph", "Inches per Hour");
    menu.addOption("kn", "kn", "Knots (Nautical Miles per Hour)");
    menu.addOption("c", "c", "Speed of Light");
    menu.addOption("mach", "mach", "Mach (Speed of Sound)");
  }

  private ActionRow velocityConversionOrigin() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_vcori");

    getVelocityOptions(menu);

    if (originVelocity != null)
      menu.setDefaultValues(originVelocity);

    return ActionRow.of(menu.build());
  }

  private ActionRow velocityConversionDestination() {
    StringSelectMenu.Builder menu = StringSelectMenu.create("calc_" + messageId + "_vcdest");

    getVelocityOptions(menu);

    if (destinationVelocity != null)
      menu.setDefaultValues(destinationVelocity);

    return ActionRow.of(menu.build());
  }

  private ActionRow velocityConversionButtons() {
    return ActionRow.of(
            Button.of(ButtonStyle.PRIMARY, "calc_" + messageId + "_vcconf", "Confirm"),
            Button.of(ButtonStyle.DANGER, "calc_" + messageId + "_vccanc", "Cancel")
    );
  }

  private String getVelocityName(String velocity) {
    return switch (velocity) {
      case "mpm" -> "Meters per Minute";
      case "mphr" -> "Meters per Hour";
      case "kmps" -> "Kilometers per Second";
      case "kmpm" -> "Kilometers per Minute";
      case "kmph" -> "Kilometers per Hour";
      case "cmps" -> "Centimeters per Second";
      case "cmpm" -> "Centimeters per Minute";
      case "cmph" -> "Centimeters per Hour";
      case "mmps" -> "Millimeters per Second";
      case "mmpm" -> "Millimeters per Minute";
      case "mmph" -> "Millimeters per Hour";
      case "ftps" -> "Feet per Second";
      case "ftpm" -> "Feet per Minute";
      case "ftph" -> "Feet per Hour";
      case "mips" -> "Miles per Second";
      case "mipm" -> "Miles per Minute";
      case "mph" -> "Miles per Hour";
      case "inps" -> "Inches per Second";
      case "inpm" -> "Inches per Minute";
      case "inph" -> "Inches per Hour";
      case "kn" -> "Knots (Nautical Miles per Hour)";
      case "c" -> "Speed of Light";
      case "mach" -> "Mach (Speed of Sound)";
      default -> "Meters per Second";
    };
  }

  private double getVelocityConversionToMetersPerHour(String velocity) {
    return switch (velocity) {
      case "mpm" -> 60.0;
      case "mphr" -> 1.0;
      case "kmps" -> 3_600_000.0;
      case "kmpm" -> 60_000.0;
      case "kmph" -> 1000.0;
      case "cmps" -> 36.0;
      case "cmpm" -> 0.6;
      case "cmph" -> 0.01;
      case "mmps" -> 3.6;
      case "mmpm" -> 0.06;
      case "mmph" -> 0.001;
      case "ftps" -> 1097.28;
      case "ftpm" -> 18.288;
      case "ftph" -> 0.3048;
      case "mips" -> 5_793_638.4;
      case "mipm" -> 96_560.64;
      case "mph" -> 1609.344;
      case "inps" -> 91.44;
      case "inpm" -> 1.524;
      case "inph" -> 0.0254;
      case "kn" -> 1852.0;
      case "c" -> 1_079_252_848_800_000.0;
      case "mach" -> 1234.8;
      default -> 3600.0;
    };
  }
}
