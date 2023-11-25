package drozdi.console;

public class ConsoleLogger {

    private String color = ConsoleColors.BLACK;

    final String WARNING = "WARNING";
    final String ERROR = "ERROR";
    final String INFO = "INFO";
    final String DEBUG = "DEBUG";

    public ConsoleLogger(String color){
        this.color = color;
    }

    private void print(String color,String message){
        System.out.println(color + message + ConsoleColors.RESET);
    }
    private void print(String message){
        print(color,message);
    }

    public void printError(String errorMessage) {
        print(ConsoleColors.RED, errorMessage);
    }

    public void info(String message) {
        print(color,INFO+ ": " + message);
    }

    public void error(String message) {
        print(color, ERROR+ ": " + message);
    }

    public void terror(String message) { //termianting error
        error(message);
        System.exit(42);
    }

  public void debug(String message) {
      print(color, DEBUG+ ": " + message);
  }

  public void warn(String message) {
        print(color, WARNING+ ": " + message);
  }
}

