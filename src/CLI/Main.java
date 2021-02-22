package CLI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static boolean IsArgument(String arg)
    {
        return !(arg.equals("|") || arg.equals("<") || arg.equals("<<"));
    }

    public static String runCommand(ArrayList<String> command, Terminal terminal){
        String result;
        switch (command.get(0)) {
            case "cd":
                result = terminal.cd(command.get(1));
                break;
            case "ls":
                if(command.size() > 1)
                {
                    result = terminal.ls(command.get(1));
                }
                else {
                    result = terminal.ls();
                }
                break;
            case "rm":
                result = terminal.rm(command.get(1));
                break;
            case "rmdir":
                result = terminal.rmdir(command.get(1));
                break;
            case "mkdir":
                result = terminal.mkdir(command.get(1));
                break;
            case "more":
                result = terminal.more(command.get(1));
                break;
            case "cat":
                result = terminal.cat(command.get(1));
                break;
            case "cp":
                result = terminal.cp(command.get(1), command.get(2));
                break;
            case "mv":
                result = terminal.mv(command.get(1), command.get(2));
                break;
            case "args":
                result = terminal.args(command.get(1));
                break;
            case "date":
                result = terminal.date();
                break;
            case "help":
                result = terminal.help();
                break;
            case "pwd":
                result = terminal.pwd();
                break;
            case "clear":
                result = terminal.clear();
                break;
            default:
                result = "exit";
        }
        return result;
    }


    public static String runPipCommand(ArrayList<String> command, String lastResult){
        Terminal terminal = new Terminal();
        String result;
        switch (command.get(0)) {
            case "cd":
                result = terminal.cd(lastResult);
                break;
            case "ls":
                result = terminal.ls(lastResult);
                break;
            case "rm":
                result = terminal.rm(lastResult);
                break;
            case "rmdir":
                result = terminal.rmdir(lastResult);
                break;
            case "mkdir":
                result = terminal.mkdir(lastResult);
                break;
            case "more":
                result = terminal.more(lastResult);
                break;
            case "cat":
                result = terminal.cat(lastResult);
                break;
            case "pwd":
                result = terminal.pwd();
                break;
            case "clear":
                result = terminal.clear();
                break;
            default:
                result = "exit";
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        Parser parserObject = new Parser();
        Terminal terminal = new Terminal();
        String result = "";
        while(!result.equals("exit")){
            System.out.print(terminal.getWorkDirectory()+ ">");
            ArrayList<ArrayList<String>> commands = parserObject.SplitInput();
            if(commands.size() == 0){
                System.out.println("this command not valid.");
            }else{
                if(!Parser.OperatorExist){
                    result = runCommand(commands.get(0),terminal);
                    if(!commands.get(0).get(0).equals("more")){
                        System.out.println(result);
                    }
                } else {
                    ArrayList<String> command = commands.get(0);
                    if(parserObject.operator.equals(">")){
                        if(!terminal.PathExist(commands.get(1).get(1))){
                            System.out.println("File not Exist.");
                        }else{
                            result = runCommand(command,terminal);
                            terminal.writeFile(commands.get(1).get(1),result);
                        }
                    }else if(parserObject.operator.equals(">>")){

                        if(!terminal.PathExist(commands.get(1).get(1))){
                            System.out.println("File not Exist.");
                        }else{
                            result = runCommand(command,terminal);
                            File file = new File(commands.get(1).get(1));
                            FileWriter fr = new FileWriter(file, true);
                            fr.write(result);
                            fr.close();

                        }
                    }
                    else{
                        result = runCommand(commands.get(0),terminal);
                        for (int i = 1; i < commands.size(); i++){
                            result = runPipCommand(commands.get(i),result);
                        }
                        System.out.println(result);
                    }
                }
            }
        }
    }
}
